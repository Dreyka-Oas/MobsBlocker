<div align="center">

# Mobs Blocker
### Control Mob Spawning on Your Server

[![NeoForge](https://img.shields.io/badge/NeoForge-1.20.1+-orange?style=for-the-badge)](https://neoforged.net/)
[![CurseForge](https://img.shields.io/badge/CurseForge-Download-orange?style=for-the-badge&logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/mobblocker)
[![Modrinth](https://img.shields.io/badge/Modrinth-Download-blue?style=for-the-badge&logo=modrinth)](https://modrinth.com/mod/mobsblocker)
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
- **In-Memory Storage**: The blacklist is stored in RAM. **Warning: this list will be reset every time the server restarts.**

---

<div align="center">
  <br>
  <p>A huge thank you to <strong><a href="https://mcreahub.pages.dev/">MCreaHub</a></strong> for facilitating the initial project setup. The mod has since been completely re-architected for exceptional performance.</p>
</div>
