package oas.work.mobsblocker.procedures;

import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

@EventBusSubscriber
public class StopMobProcedure {
    @SubscribeEvent
    public static void onEntitySpawned(EntityJoinLevelEvent event) {
        execute(event, event.getEntity());
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable Event event, Entity entity) {
        if (entity == null)
            return;

        // Obtenir le nom de l'entité (ex : minecraft:sheep)
        String entityName = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();

        // Obtenir le fichier 'mobs.oas' dans le dossier config/oas_work
        File configFile = new File(Paths.get("config", "oas_work", "mobs.oas").toString());

        // Vérifier si le fichier existe avant d'essayer de le lire
        if (!configFile.exists()) {
            System.err.println("Le fichier 'mobs.oas' est introuvable.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String mobLine;
            while ((mobLine = reader.readLine()) != null) {
                mobLine = mobLine.trim(); // Supprimer les espaces inutiles
                if (!mobLine.isEmpty()) {
                    // Comparer le nom de l'entité avec la ligne du fichier
                    if (entityName.equals(mobLine)) {
                        // Si une correspondance est trouvée, annuler l'événement
                        if (event instanceof ICancellableEvent _cancellable) {
                            _cancellable.setCanceled(true);
                            System.out.println("L'entité " + entityName + " a été bloquée.");
                        }
                        break; // Sortir de la boucle après avoir trouvé une correspondance
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier 'mobs.oas': " + e.getMessage());
        }
    }
}
