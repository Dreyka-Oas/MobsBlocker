package oas.work.mobs_blocker.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.reflect.Field;
import java.util.*;

@Mod.EventBusSubscriber
public class SpawnblockerCommand {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("spawnblocker.json");
    public static final Set<String> BANNED_MOBS = new HashSet<>();
    private static boolean isInitialized = false;

    // --- SYSTÈME DE TRADUCTION SERVEUR ---
    private static final Map<String, Map<String, String>> LANG_MAP = new HashMap<>();

    static {
        // Anglais
        Map<String, String> en = new HashMap<>();
        en.put("add.success", "§aSuccessfully added §e%s§a to the block list.");
        en.put("remove.success", "§aSuccessfully removed §e%s§a from the block list.");
        en.put("error.exists", "§cError: The mob §e%s§c is already blocked.");
        en.put("error.not_found", "§cError: The mob §e%s§c is not in the list.");
        en.put("list.header", "§6Blocked Mobs: §f%s");
        en.put("list.empty", "§6The blocked mobs list is empty.");
        LANG_MAP.put("en_us", en);

        // Français
        Map<String, String> fr = new HashMap<>();
        fr.put("add.success", "§aAjouté : §e%s§a à la liste de blocage.");
        fr.put("remove.success", "§aRetiré : §e%s§a de la liste de blocage.");
        fr.put("error.exists", "§cErreur : Le mob §e%s§c est déjà bloqué.");
        fr.put("error.not_found", "§cErreur : Le mob §e%s§c n'est pas dans la liste.");
        fr.put("list.header", "§6Mobs Bloqués : §f%s");
        fr.put("list.empty", "§6La liste de blocage est vide.");
        LANG_MAP.put("fr_fr", fr);
    }

    // Utilisation de la réflexion pour lire la langue du joueur (champ privé en 1.19.2)
    private static String getPlayerLanguage(ServerPlayer player) {
        try {
            Field field = ServerPlayer.class.getDeclaredField("language");
            field.setAccessible(true);
            return (String) field.get(player);
        } catch (Exception e) {
            return "en_us";
        }
    }

    private static Component getMsg(CommandSourceStack source, String key, Object... args) {
        String lang = "en_us";
        if (source.getEntity() instanceof ServerPlayer player) {
            lang = getPlayerLanguage(player).toLowerCase();
        }
        Map<String, String> dict = LANG_MAP.getOrDefault(lang, LANG_MAP.get("en_us"));
        String text = dict.getOrDefault(key, key);
        try { return Component.literal(String.format(text, args)); } 
        catch (Exception e) { return Component.literal(text); }
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        if (!isInitialized) {
            loadConfig();
            isInitialized = true;
        }

        event.getDispatcher().register(Commands.literal("spawnblocker")
            .requires(s -> s.hasPermission(4))
            
            .then(Commands.literal("add")
                .then(Commands.argument("mob_id", ResourceLocationArgument.id())
                    .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(ForgeRegistries.ENTITY_TYPES.getKeys(), builder))
                    .executes(context -> {
                        String mobString = ResourceLocationArgument.getId(context, "mob_id").toString();
                        if (BANNED_MOBS.contains(mobString)) {
                            context.getSource().sendFailure(getMsg(context.getSource(), "error.exists", mobString));
                        } else {
                            BANNED_MOBS.add(mobString);
                            saveConfig();
                            // Pas de lambda () -> en 1.19.2
                            context.getSource().sendSuccess(getMsg(context.getSource(), "add.success", mobString), false);
                        }
                        return 1;
                    })
                )
            )
            
            .then(Commands.literal("remove")
                .then(Commands.argument("mob_id", ResourceLocationArgument.id())
                    .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(BANNED_MOBS.stream().map(ResourceLocation::new), builder))
                    .executes(context -> {
                        String mobString = ResourceLocationArgument.getId(context, "mob_id").toString();
                        if (BANNED_MOBS.contains(mobString)) {
                            BANNED_MOBS.remove(mobString);
                            saveConfig();
                            context.getSource().sendSuccess(getMsg(context.getSource(), "remove.success", mobString), false);
                        } else {
                            context.getSource().sendFailure(getMsg(context.getSource(), "error.not_found", mobString));
                        }
                        return 1;
                    })
                )
            )
            
            .then(Commands.literal("list")
                .executes(context -> {
                    if (BANNED_MOBS.isEmpty()) {
                        context.getSource().sendSuccess(getMsg(context.getSource(), "list.empty"), false);
                    } else {
                        String list = String.join(", ", BANNED_MOBS);
                        context.getSource().sendSuccess(getMsg(context.getSource(), "list.header", list), false);
                    }
                    return 1;
                })
            )
        );
    }

    // --- LOGIQUE DE BLOCAGE ---
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());
        if (id != null && BANNED_MOBS.contains(id.toString())) {
            event.setCanceled(true);
        }
    }

    private static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) return;
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Set<String> loaded = GSON.fromJson(reader, new TypeToken<HashSet<String>>(){}.getType());
            if (loaded != null) {
                BANNED_MOBS.clear();
                BANNED_MOBS.addAll(loaded);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void saveConfig() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(BANNED_MOBS, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }
}