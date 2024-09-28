package oas.work.mobsblocker.procedures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class CreateFileProcedure {
    public static void execute() {
        // Utiliser Paths pour obtenir le chemin vers le répertoire config
        File configDir = new File(Paths.get("config", "oas_work").toString());

        // Créer le fichier mobs.oas
        createConfigFile(configDir);
    }

    private static void createConfigFile(File configDir) {
        // Vérifier si le répertoire n'existe pas et le créer si nécessaire
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, "mobs.oas");

        // Vérifier si le fichier existe déjà
        if (!configFile.exists()) {
            try (FileWriter writer = new FileWriter(configFile)) {
                // Contenu du fichier texte avec la liste des mobs
                String mobsList = "minecraft:sheep\nminecraft:creeper\n";
                writer.write(mobsList);
                System.out.println("Fichier 'mobs.oas' créé avec succès dans le dossier 'oas_work'.");
            } catch (IOException e) {
                System.err.println("Erreur lors de la création du fichier 'mobs.oas': " + e.getMessage());
            }
        } else {
            System.out.println("Le fichier 'mobs.oas' existe déjà.");
        }
    }
}
