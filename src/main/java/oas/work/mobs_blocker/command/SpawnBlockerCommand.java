package oas.work.mobs_blocker.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLPaths;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Mob;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@EventBusSubscriber
public class SpawnBlockerCommand {

    // --- CONFIGURATION ---
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("spawnblocker.json");
    public static final Map<String, Set<String>> BANNED_MOBS = new HashMap<>();
    
    private static final String GLOBAL_KEY = "global_settings";
    private static final String PRESET_PREFIX = "preset:";

    private static final List<String> ALL_RULES = Arrays.asList(
        "all", 
        "spawner", "!spawner", 
        "egg", "!egg", 
        "command", "!command",
        "natural", "!natural"
    );
    
    private static final Map<String, List<MobCategory>> PRESETS = new HashMap<>();
    static {
        PRESETS.put("monsters", Collections.singletonList(MobCategory.MONSTER));
        PRESETS.put("creatures", Collections.singletonList(MobCategory.CREATURE));
        PRESETS.put("ambient", Collections.singletonList(MobCategory.AMBIENT));
        PRESETS.put("water", Arrays.asList(MobCategory.WATER_CREATURE, MobCategory.UNDERGROUND_WATER_CREATURE));
        PRESETS.put("misc", Collections.singletonList(MobCategory.MISC));
    }

    // --- LOAD LOGIC ---
    private static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            BANNED_MOBS.clear();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Map<String, Set<String>> loaded = GSON.fromJson(reader, new TypeToken<HashMap<String, HashSet<String>>>(){}.getType());
            if (loaded != null) { 
                BANNED_MOBS.clear(); 
                BANNED_MOBS.putAll(loaded); 
            }
        } catch (Exception e) { 
            System.err.println("[SpawnBlocker] Error loading config: " + e.getMessage());
        }
    }

    // --- SAVE LOGIC ---
    private static void saveConfig() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(BANNED_MOBS, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- SMART SUGGESTIONS ---
    private static CompletableFuture<Suggestions> getRuleSuggestions(SuggestionsBuilder builder) {
        String input = builder.getRemaining().toLowerCase();
        String[] parts = input.split(" ");
        List<String> currentRules = Arrays.asList(parts);
        String lastPart = input.endsWith(" ") ? "" : parts[parts.length - 1];
        boolean hasInverse = currentRules.stream().anyMatch(r -> r.startsWith("!"));

        List<String> suggestions = ALL_RULES.stream().filter(rule -> {
            if (currentRules.contains("all") || currentRules.contains(rule)) return false;
            if (rule.equals("all") && !currentRules.isEmpty() && !input.equals(lastPart)) return false;
            if (hasInverse && !currentRules.contains(rule)) return false; 
            if (!hasInverse && !currentRules.isEmpty() && rule.startsWith("!")) return false;
            return rule.startsWith(lastPart);
        }).collect(Collectors.toList());

        String prefix = input.substring(0, input.length() - lastPart.length());
        for (String s : suggestions) { builder.suggest(prefix + s); }
        return builder.buildFuture();
    }

    // --- COMMAND REGISTRATION ---
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        loadConfig();
        event.getDispatcher().register(Commands.literal("spawnblocker")
            .requires(s -> s.hasPermission(4))
            
            // 1. ADD (Specific)
            .then(Commands.literal("add")
                .then(Commands.argument("mob_id", ResourceLocationArgument.id())
                    .suggests((c, b) -> SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.keySet(), b))
                    .then(Commands.argument("rules", StringArgumentType.greedyString())
                        .suggests((c, b) -> getRuleSuggestions(b))
                        .executes(c -> updateRules(c.getSource(), ResourceLocationArgument.getId(c, "mob_id").toString(), StringArgumentType.getString(c, "rules").split(" ")))
                    )
                )
            )
            
            // 2. PRESET
            .then(Commands.literal("preset")
                .then(Commands.argument("category", StringArgumentType.word())
                    .suggests((c, b) -> SharedSuggestionProvider.suggest(PRESETS.keySet(), b))
                    .then(Commands.argument("rules", StringArgumentType.greedyString())
                        .suggests((c, b) -> getRuleSuggestions(b))
                        .executes(c -> {
                            String cat = StringArgumentType.getString(c, "category");
                            if (!PRESETS.containsKey(cat)) return 0;
                            return updateRules(c.getSource(), PRESET_PREFIX + cat, StringArgumentType.getString(c, "rules").split(" "));
                        })
                    )
                )
            )
            
            // 3. GLOBAL
            .then(Commands.literal("global")
                .then(Commands.argument("rules", StringArgumentType.greedyString())
                    .suggests((c, b) -> getRuleSuggestions(b))
                    .executes(c -> updateRules(c.getSource(), GLOBAL_KEY, StringArgumentType.getString(c, "rules").split(" ")))
                )
            )
            
            // 4. RELOAD
            .then(Commands.literal("reload")
                .executes(c -> {
                    loadConfig();
                    c.getSource().sendSuccess(() -> Component.literal("§aConfiguration reloaded from disk!"), true);
                    return 1;
                })
            )

            // 5. RESET
            .then(Commands.literal("reset")
                .executes(c -> {
                    BANNED_MOBS.clear();
                    saveConfig();
                    c.getSource().sendSuccess(() -> Component.literal("§c⚠ COMPLETE configuration reset (File cleared)!"), true);
                    return 1;
                })
            )

            // 6. REMOVE
            .then(Commands.literal("remove")
                .then(Commands.argument("mob_id", ResourceLocationArgument.id())
                    .suggests((c, b) -> SharedSuggestionProvider.suggest(BANNED_MOBS.keySet().stream().filter(k -> !k.startsWith(PRESET_PREFIX) && !k.equals(GLOBAL_KEY)).collect(Collectors.toSet()), b))
                    .executes(c -> removeEntry(c.getSource(), ResourceLocationArgument.getId(c, "mob_id").toString()))
                )
                .then(Commands.literal("preset")
                    .then(Commands.argument("category", StringArgumentType.word())
                        .suggests((c, b) -> SharedSuggestionProvider.suggest(PRESETS.keySet(), b))
                        .executes(c -> removeEntry(c.getSource(), PRESET_PREFIX + StringArgumentType.getString(c, "category")))
                    )
                )
                .then(Commands.literal("global")
                    .executes(c -> removeEntry(c.getSource(), GLOBAL_KEY))
                )
            )
            
            // 7. LIST
            .then(Commands.literal("list")
                .executes(c -> {
                    if (BANNED_MOBS.isEmpty()) { 
                        c.getSource().sendSuccess(() -> Component.literal("§6No active rules."), false); 
                        return 1;
                    }
                    c.getSource().sendSuccess(() -> Component.literal("§6Active Blocking Rules:"), false);
                    
                    if(BANNED_MOBS.containsKey(GLOBAL_KEY)) {
                        c.getSource().sendSuccess(() -> Component.literal("§d★ GLOBAL §7: §b" + BANNED_MOBS.get(GLOBAL_KEY)), false);
                    }
                    
                    BANNED_MOBS.forEach((key, rules) -> {
                        if(key.startsWith(PRESET_PREFIX)) {
                            c.getSource().sendSuccess(() -> Component.literal("§e★ PRESET " + key.replace(PRESET_PREFIX, "").toUpperCase() + " §7: §b" + rules), false);
                        }
                    });

                    BANNED_MOBS.forEach((key, rules) -> {
                        if(!key.startsWith(PRESET_PREFIX) && !key.equals(GLOBAL_KEY))
                            c.getSource().sendSuccess(() -> Component.literal("§7- §f" + key + " §b" + rules), false);
                    });
                    return 1;
                })
            )
        );
    }

    // --- UPDATE LOGIC ---
    private static int updateRules(CommandSourceStack source, String targetId, String[] rulesArray) {
        Set<String> rulesSet = BANNED_MOBS.computeIfAbsent(targetId, k -> new HashSet<>());
        
        boolean newHasInverse = Arrays.stream(rulesArray).anyMatch(r -> r.startsWith("!"));
        if (newHasInverse && (rulesArray.length > 1 || !rulesSet.isEmpty())) {
            rulesSet.clear();
        }

        for (String rule : rulesArray) {
            if (!ALL_RULES.contains(rule)) continue;
            if (rule.equals("all") || rule.startsWith("!")) { 
                rulesSet.clear(); rulesSet.add(rule); break; 
            } else { 
                rulesSet.removeIf(r -> r.startsWith("!") || r.equals("all"));
                rulesSet.add(rule); 
            }
        }
        saveConfig();
        String displayName = targetId.equals(GLOBAL_KEY) ? "§dGLOBAL" : targetId.startsWith(PRESET_PREFIX) ? "§ePRESET " + targetId.replace(PRESET_PREFIX, "") : "§f" + targetId;
        source.sendSuccess(() -> Component.literal("§aRules updated for " + displayName + "§a: §b" + rulesSet), false);
        return 1;
    }

    private static int removeEntry(CommandSourceStack source, String key) {
        if (BANNED_MOBS.remove(key) != null) {
            saveConfig();
            source.sendSuccess(() -> Component.literal("§aRemoved rules for: §e" + key.replace(PRESET_PREFIX, "")), false);
        } else {
            source.sendFailure(Component.literal("§cNothing found to remove for: " + key));
        }
        return 1;
    }

    // --- BLOCKING LOGIC ---
    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Mob)) return; 
        
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()).toString();
        MobCategory category = event.getEntity().getType().getCategory();

        // 1. Specific
        Set<String> rules = BANNED_MOBS.get(id); 
        
        // 2. Preset
        if (rules == null) {
            for (Map.Entry<String, List<MobCategory>> entry : PRESETS.entrySet()) {
                if (entry.getValue().contains(category)) {
                    rules = BANNED_MOBS.get(PRESET_PREFIX + entry.getKey());
                    if (rules != null) break;
                }
            }
        }
        
        // 3. Global
        if (rules == null) {
            rules = BANNED_MOBS.get(GLOBAL_KEY);
        }

        if (rules == null || rules.isEmpty()) return;

        EntitySpawnReason reason = event.getSpawnType();
        String name = reason.name();

        boolean isSpawner = (reason == EntitySpawnReason.SPAWNER || name.contains("TRIAL_SPAWNER"));
        boolean isCommand = (reason == EntitySpawnReason.COMMAND);
        boolean isEgg = (reason == EntitySpawnReason.DISPENSER || name.contains("EGG") || name.contains("SPAWN_ITEM"));
        boolean isNatural = (!isSpawner && !isCommand && !isEgg);

        if (rules.contains("all")) { cancelSpawn(event); return; }

        for (String rule : rules) {
            if (rule.startsWith("!")) {
                boolean allowed = false;
                if (rule.equals("!spawner") && isSpawner) allowed = true;
                if (rule.equals("!egg") && isEgg) allowed = true;
                if (rule.equals("!command") && isCommand) allowed = true;
                if (rule.equals("!natural") && isNatural) allowed = true;
                if (!allowed) cancelSpawn(event);
                return;
            }
        }

        boolean shouldBlock = false;
        for (String rule : rules) {
            if (rule.equals("spawner") && isSpawner) shouldBlock = true;
            if (rule.equals("egg") && isEgg) shouldBlock = true;
            if (rule.equals("command") && isCommand) shouldBlock = true;
            if (rule.equals("natural") && isNatural) shouldBlock = true;
        }

        if (shouldBlock) cancelSpawn(event);
    }
    
    private static void cancelSpawn(FinalizeSpawnEvent event) {
        event.setCanceled(true);
        event.setSpawnCancelled(true);
        event.getEntity().discard();
    }

    // Failsafe for "all"
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Mob)) return;

        String id = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()).toString();
        MobCategory category = event.getEntity().getType().getCategory();

        Set<String> rules = BANNED_MOBS.get(id);
        if (rules == null) {
             for (Map.Entry<String, List<MobCategory>> entry : PRESETS.entrySet()) {
                if (entry.getValue().contains(category)) {
                    rules = BANNED_MOBS.get(PRESET_PREFIX + entry.getKey());
                    if (rules != null) break;
                }
            }
        }
        if (rules == null) rules = BANNED_MOBS.get(GLOBAL_KEY);

        if (rules != null && rules.contains("all")) {
            event.setCanceled(true);
            event.getEntity().discard();
        }
    }
}