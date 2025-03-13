package oas.work.mobs_blocker.procedures;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Paths;

/**
 *  This class is responsible for creating the necessary configuration directory for the mod
 *  during the common setup phase of mod initialization. It ensures that the "oas_work" folder
 *  exists within the "config" directory, and then proceeds to trigger the file creation procedure.
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CreateFolderProcedure {

    /**
     *  This method is subscribed to the `FMLCommonSetupEvent`, which is triggered during the common setup phase of the mod.
     *  It ensures the execution of the folder creation logic when the mod is being initialized.
     *  @param event The FMLCommonSetupEvent provided by Forge.
     */
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        execute();
    }

    public static void execute() {
        execute(null);
    }

    /**
     *  This method contains the core logic to create the "oas_work" directory within the "config" directory.
     *  It checks if the directory already exists, and if not, attempts to create it.
     *  Upon successful directory creation or if the directory already exists, it then calls the procedure
     *  to create the configuration file within this newly created directory.
     *  @param event The event that triggered this procedure (can be null if called directly).
     */
    private static void execute(@Nullable Event event) {
        // Get the path to the configuration directory "oas_work" inside the main "config" directory.
        File configDir = new File(Paths.get("config", "oas_work").toString());

        // Check if the directory already exists.
        if (!configDir.exists()) {
            // If the directory does not exist, attempt to create it, including any necessary parent directories.
            boolean success = configDir.mkdirs();
            if (!success) {
                System.err.println("Failed to create directory 'oas_work'.");
            }
        } 

        // After ensuring the directory exists, call the procedure to create the configuration file within it.
        CreateFileProcedure.execute();
    }
}