package oas.work.mobs_blocker.command;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class SpawnBlockerCommand {

    public static final Set<String> BANNED_MOBS = new HashSet<>();
    
    // --- SYSTÈME DE TRADUCTION INTERNE ---
    private static final Map<String, Map<String, String>> LANG_MAP = new HashMap<>();

    static {
        // Dictionnaire ANGLAIS (Défaut)
        Map<String, String> en = new HashMap<>();
        en.put("add.success", "§aSuccessfully added §e%s§a to the block list.");
        en.put("remove.success", "§aSuccessfully removed §e%s§a from the block list.");
        en.put("error.exists", "§cError: The mob §e%s§c is already blocked.");
        en.put("error.not_found", "§cError: The mob §e%s§c is not in the list.");
        en.put("list.header", "§6Blocked Mobs: §f%s");
        en.put("list.empty", "§6The blocked mobs list is empty.");
        LANG_MAP.put("en_us", en);

        // Dictionnaire FRANÇAIS
        Map<String, String> fr = new HashMap<>();
        fr.put("add.success", "§aAjouté avec succès : §e%s§a à la liste de blocage.");
        fr.put("remove.success", "§aRetiré avec succès : §e%s§a de la liste de blocage.");
        fr.put("error.exists", "§cErreur : Le mob §e%s§c est déjà bloqué.");
        fr.put("error.not_found", "§cErreur : Le mob §e%s§c n'est pas dans la liste.");
        fr.put("list.header", "§6Mobs Bloqués : §f%s");
        fr.put("list.empty", "§6La liste de blocage est vide.");
        LANG_MAP.put("fr_fr", fr);
    }

    // Fonction qui choisit la bonne langue selon le joueur
    private static Component getMsg(CommandSourceStack source, String key, Object... args) {
        String lang = "en_us"; // Langue par défaut
        
        if (source.getEntity() instanceof ServerPlayer player) {
            // Récupère la langue du client (ex: "fr_fr")
            lang = player.clientInformation().language();
        }

        // Si la langue du joueur n'est pas dans notre liste, on prend l'anglais
        Map<String, String> dictionary = LANG_MAP.getOrDefault(lang, LANG_MAP.get("en_us"));
        
        // On récupère le texte et on remplit les %s
        String text = dictionary.getOrDefault(key, key);
        try {
            return Component.literal(String.format(text, args));
        } catch (Exception e) {
            return Component.literal(text);
        }
    }

    // --- ENREGISTREMENT COMMANDE ---
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("spawnblocker")
            .requires(s -> s.hasPermission(4))
            
            // --- ADD ---
            .then(Commands.literal("add")
                .then(Commands.argument("mob_id", ResourceLocationArgument.id())
                    .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.keySet(), builder))
                    .executes(context -> {
                        ResourceLocation mobId = ResourceLocationArgument.getId(context, "mob_id");
                        String mobString = mobId.toString();
                        
                        if (BANNED_MOBS.contains(mobString)) {
                            context.getSource().sendFailure(getMsg(context.getSource(), "error.exists", mobString));
                        } else {
                            BANNED_MOBS.add(mobString);
                            context.getSource().sendSuccess(() -> getMsg(context.getSource(), "add.success", mobString), false);
                        }
                        return 1;
                    })
                )
            )
            
            // --- REMOVE ---
            .then(Commands.literal("remove")
                .then(Commands.argument("mob_id", ResourceLocationArgument.id())
                    .suggests((context, builder) -> SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.keySet(), builder))
                    .executes(context -> {
                        ResourceLocation mobId = ResourceLocationArgument.getId(context, "mob_id");
                        String mobString = mobId.toString();

                        if (BANNED_MOBS.contains(mobString)) {
                            BANNED_MOBS.remove(mobString);
                            context.getSource().sendSuccess(() -> getMsg(context.getSource(), "remove.success", mobString), false);
                        } else {
                            context.getSource().sendFailure(getMsg(context.getSource(), "error.not_found", mobString));
                        }
                        return 1;
                    })
                )
            )
            
            // --- LIST ---
            .then(Commands.literal("list")
                .executes(context -> {
                    if (BANNED_MOBS.isEmpty()) {
                        context.getSource().sendSuccess(() -> getMsg(context.getSource(), "list.empty"), false);
                    } else {
                        String list = String.join(", ", BANNED_MOBS);
                        context.getSource().sendSuccess(() -> getMsg(context.getSource(), "list.header", list), false);
                    }
                    return 1;
                })
            )
        );
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()).toString();

        if (BANNED_MOBS.contains(entityId)) {
            event.setCanceled(true);
        }
    }
}