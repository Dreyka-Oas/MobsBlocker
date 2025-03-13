package oas.work.mobs_blocker.procedures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 *  This class is responsible for creating the configuration file "mobs.oas"
 *  within the "oas_work" directory inside the "config" directory.
 *  This file is intended to store a list of mob entity names that should be blocked from spawning.
 */
public class CreateFileProcedure {

    /**
     *  This method is the main entry point for the file creation procedure.
     *  It calls the method to create the configuration file, ensuring that the necessary directory structure is in place.
     */
    public static void execute() {
        // Get the path to the configuration directory "oas_work" inside the main "config" directory.
        File configDir = new File(Paths.get("config", "oas_work").toString());

        // Call the method to create the configuration file within the specified directory.
        createConfigFile(configDir);
    }

    /**
     *  This method handles the actual creation of the "mobs.oas" configuration file.
     *  It first checks if the configuration directory exists, creates it if necessary,
     *  then checks if the configuration file itself exists, and creates it with default content if it does not.
     *  @param configDir The File object representing the configuration directory where the file should be created.
     */
    private static void createConfigFile(File configDir) {
        // Check if the configuration directory exists. If not, create it.
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        // Define the File object for the "mobs.oas" configuration file within the specified directory.
        File configFile = new File(configDir, "mobs.oas");

        // Check if the configuration file already exists.
        if (!configFile.exists()) {
            // If the file does not exist, attempt to create it and write default content to it.
            try (FileWriter writer = new FileWriter(configFile)) {
                // Default content for the configuration file, listing example mob entity names to be blocked.
                String mobsList = "minecraft:sheep\nminecraft:creeper\n";
                writer.write(mobsList);
            } catch (IOException e) {
                // If an IOException occurs during file creation or writing, print an error message to the console.
                System.err.println("Error creating configuration file 'mobs.oas': " + e.getMessage());
            }
        }
    }
}