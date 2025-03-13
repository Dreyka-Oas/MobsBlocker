package oas.work.mobs_blocker.procedures;

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

/**
 *  This class implements a procedure to prevent specific mobs from spawning in the game.
 *  It reads a list of blocked entity names from a configuration file ("mobs.oas")
 *  and cancels the entity spawn event if the entity's name is found in the list.
 */
@EventBusSubscriber
public class StopMobProcedure {
    /**
     *  This method is subscribed to the `EntityJoinLevelEvent`, which is fired whenever an entity joins a level (spawns).
     *  It triggers the mob blocking logic when an entity attempts to spawn.
     *  @param event The EntityJoinLevelEvent provided by Forge.
     */
    @SubscribeEvent
    public static void onEntitySpawned(EntityJoinLevelEvent event) {
        execute(event, event.getEntity());
    }

    /**
     *  First `execute` method overload. This method is designed to be called directly from other parts of the mod if needed,
     *  allowing for manual triggering of the mob blocking procedure.
     *  @param entity The entity to check for blocking.
     */
    public static void execute(Entity entity) {
        execute(null, entity);
    }

    /**
     *  Second and main `execute` method overload. This method contains the core logic for checking if an entity should be blocked.
     *  It retrieves the entity's name, reads the list of blocked mobs from the configuration file,
     *  and cancels the entity spawn event if a match is found.
     *  @param event The event that triggered this procedure (can be null if called directly).
     *  @param entity The entity to be checked and potentially blocked.
     */
    private static void execute(@Nullable Event event, Entity entity) {
        // Ensure the entity is not null before proceeding.
        if (entity == null)
            return;

        // Get the unique name (ResourceLocation) of the entity's type as a string.
        String entityName = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();

        // Define the file path to the configuration file "mobs.oas" located in the "config/oas_work/" directory.
        File configFile = new File(Paths.get("config", "oas_work", "mobs.oas").toString());

        // Check if the configuration file exists. If not, print an error message and exit the procedure.
        if (!configFile.exists()) {
            System.err.println("Configuration file 'mobs.oas' not found.");
            return;
        }

        // Attempt to read the configuration file line by line.
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String mobLine;
            // Read each line from the file until the end is reached.
            while ((mobLine = reader.readLine()) != null) {
                mobLine = mobLine.trim(); // Remove leading and trailing whitespace from the line.
                if (!mobLine.isEmpty()) { // Check if the line is not empty after trimming.
                    // Compare the current entity's name with the entity name read from the configuration file.
                    if (entityName.equals(mobLine)) {
                        // If a match is found, it means this entity should be blocked.
                        if (event instanceof ICancellableEvent _cancellable) {
                            // If the event is cancellable (EntityJoinLevelEvent is), cancel it to prevent the entity from spawning.
                            _cancellable.setCanceled(true);
                            System.out.println("Entity " + entityName + " has been blocked from spawning.");
                        }
                        break; // Exit the loop as the entity is blocked and further checks are unnecessary.
                    }
                }
            }
        } catch (IOException e) {
            // If an IOException occurs during file reading, print an error message with details.
            System.err.println("Error reading configuration file 'mobs.oas': " + e.getMessage());
        }
    }
}