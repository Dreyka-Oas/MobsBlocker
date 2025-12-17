<div align="center">

# Mobs Blocker
### Contrôlez les apparitions de créatures sur votre serveur

[![NeoForge](https://img.shields.io/badge/NeoForge-1.20.1+-orange?style=for-the-badge)](https://neoforged.net/)
[![CurseForge](https://img.shields.io/badge/CurseForge-Download-orange?style=for-the-badge&logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/mobblocker)
[![Modrinth](https://img.shields.io/badge/Modrinth-Download-blue?style=for-the-badge&logo=modrinth)](https://modrinth.com/mod/mobsblocker)
[![GitHub](https://img.shields.io/badge/GitHub-Source-black?style=for-the-badge&logo=github)](https://github.com/Dreyka-Oas/Mobs-Blocker)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](https://opensource.org/licenses/MIT)

**Mobs Blocker** est un outil d'administration simple et efficace pour empêcher l'apparition (spawn) de n'importe quelle créature (mob) sur votre serveur via une simple commande. Gardez le contrôle de votre monde sans redémarrage ni configuration complexe.

[Signaler un bug ou suggérer une fonctionnalité](https://github.com/Dreyka-Oas/Mobs-Blocker/issues)

</div>

---

## Comment ça marche

**Mobs Blocker** fournit un mécanisme de liste noire (blacklist) en temps réel. En utilisant la commande `/spawnblocker`, vous pouvez ajouter ou retirer n'importe quelle créature du jeu.

Le mod intercepte chaque tentative d'apparition d'entité sur le serveur. Si l'identifiant de l'entité correspond à un élément de la liste noire, l'événement est simplement annulé. La créature n'apparaît jamais, sans aucun impact sur les performances du serveur.

---

## Fonctionnalités Principales

- **Blocage Instantané**: Ajoutez ou retirez des créatures de la liste noire et les changements sont appliqués immédiatement, sans redémarrage.
- **Commandes Simples**: Une interface de commande intuitive (`add`, `remove`, `list`) facile à maîtriser.
- **Auto-complétion Intégrée**: Suggère automatiquement les identifiants des créatures pour éviter les erreurs de frappe.
- **Haute Performance**: Utilise une structure de données optimisée (`HashSet`) pour une vérification quasi-instantanée, garantissant zéro lag.
- **Léger et Côté Serveur**: Aucune installation requise pour les joueurs.

---

## Guide des Commandes

Toutes les commandes nécessitent un niveau de permission d'opérateur (niveau 4).

### Ajouter une créature à la liste noire

Pour bloquer l'apparition des Creepers :
```mcfunction
/spawnblocker add minecraft:creeper
```

### Retirer une créature de la liste noire

Pour autoriser à nouveau l'apparition des Creepers :
```mcfunction
/spawnblocker remove minecraft:creeper
```

### Afficher la liste des créatures bloquées

Pour voir toutes les créatures actuellement dans la liste noire :
```mcfunction
/spawnblocker list
```

---

## Détails Techniques

- **Côté Serveur Uniquement**: Le mod s'installe uniquement sur le serveur.
- **Gestion d'Événements**: Le système se base sur l'événement `EntityJoinLevelEvent` de NeoForge pour intercepter les apparitions.
- **Stockage en Mémoire**: La liste des créatures bloquées est conservée en mémoire vive. **Attention : cette liste sera réinitialisée à chaque redémarrage du serveur.**

---

<div align="center">
  <br>
  <p>Un outil simple pour un contrôle total.</p>
</div>
