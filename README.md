<div align="center">

# Mobs Blocker
### Control Mob Spawning on Your Server

[![NeoForge](https://img.shields.io/badge/NeoForge-1.20.1+-orange?style=for-the-badge)](https://neoforged.net/)
[![GitHub](https://img.shields.io/badge/GitHub-Source-black?style=for-the-badge&logo=github)](https://github.com/Dreyka-Oas/Mobs-Blocker)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](https://opensource.org/licenses/MIT)

**Mobs Blocker** is a simple and effective admin tool to prevent any mob from spawning on your server via a simple command. Keep control of your world without restarts or complex configuration.

[Report a Bug or Suggest a Feature](https://github.com/Dreyka-Oas/Mobs-Blocker/issues)

</div>

---

## How It Works

**Mobs Blocker** provides a real-time blacklisting mechanism. Using the `/spawnblocker` command, you can add or remove any mob from the game.

The mod intercepts every entity spawn attempt on the server. If the entity's ID matches an item on the blacklist, the event is simply canceled. The mob never appears, with zero impact on server performance.

---

## Core Features

- **Instant Blocking**: Add or remove mobs from the blacklist with immediate effect—no restart required.
- **Simple Commands**: An intuitive command interface (`add`, `remove`, `list`) that is easy to master.
- **Built-in Autocomplete**: Automatically suggests mob IDs to prevent typos.
- **High Performance**: Uses an optimized data structure (`HashSet`) for near-instant checks, ensuring zero lag.
- **Lightweight & Server-Side**: No installation required for players.

---

## Command Guide

All commands require operator permission level 4.

### Add a Mob to the Blacklist

To block Creepers from spawning:
```mcfunction
/spawnblocker add minecraft:creeper
```

### Remove a Mob from the Blacklist

To allow Creepers to spawn again:
```mcfunction
/spawnblocker remove minecraft:creeper
```

### List Blacklisted Mobs

To see all currently blacklisted mobs:
```mcfunction
/spawnblocker list
```

---

## Technical Details

- **Server-Side Only**: The mod is only installed on the server.
- **Event-Driven**: The system hooks into NeoForge's `EntityJoinLevelEvent` to intercept spawns.
- **Persistent Storage**: The blacklist is stored in a JSON file (`config/spawnblocker.json`) and persists across server restarts. The list is loaded on startup and saved after each modification.

## Manual Configuration

You can manually configure the blacklist by editing the `spawnblocker.json` file located in your server's `config` directory. This file contains a JSON array of mob IDs (as strings) that should be blocked from spawning.

### File Location
- **Path**: `config/spawnblocker.json`
- **Format**: JSON array of strings, e.g., `["minecraft:creeper", "minecraft:zombie"]`

### Example Configuration
```json
[
  "minecraft:creeper",
  "minecraft:skeleton",
  "minecraft:spider"
]
```

### Steps to Configure
1. **Stop the Server**: Ensure the Minecraft server is stopped before editing the file to avoid conflicts.
2. **Edit the File**: Open `config/spawnblocker.json` with a text editor. Add or remove mob IDs in the array format. Use valid entity IDs from Minecraft (e.g., `minecraft:creeper`).
3. **Save and Restart**: Save the file and restart the server. The changes will be loaded automatically.

**Note**: While you can use in-game commands for dynamic changes, manual editing is useful for bulk configurations or when the server is offline. Always back up the file before editing.

---

<div align="center">
  <br>
  <p>A huge thank you to <strong><a href="https://mcreahub.pages.dev/">MCreaHub</a></strong> for facilitating the initial project setup. The mod has since been completely re-architected for exceptional performance.</p>
</div>
