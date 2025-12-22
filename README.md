<div align="center">

# Mobs Blocker
### Advanced Spawn Control for Your Server

[![NeoForge](https://img.shields.io/badge/NeoForge-1.21+-orange?style=for-the-badge)](https://neoforged.net/)
[![GitHub](https://img.shields.io/badge/GitHub-Source-black?style=for-the-badge&logo=github)](https://github.com/Dreyka-Oas/Mobs-Blocker)
[![MCreaHub](https://img.shields.io/badge/Project_Setup-MCreaHub-blue?style=for-the-badge)](https://mcreahub.pages.dev/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](https://opensource.org/licenses/MIT)

**Mobs Blocker** has been completely re-architected to give you granular control over entity spawning. Stop mobs based on specific conditions (natural spawns, spawners, eggs) or apply rules globally across mob categories.

[Report a Bug or Suggest a Feature](https://github.com/Dreyka-Oas/Mobs-Blocker/issues)

</div>

**`⚠️ UPDATE NOTICE: This mod has been rewritten for performance and granularity (v1.2.0). Old config files (v1.1.0) are not compatible.`**

---

## 🚀 How It Works & Core Features

### The Logic System
Mobs Blocker intercepts every entity spawn attempt and checks rules in this specific order:
1.  **Specific Rules**: Is there a rule for this exact Entity ID? (e.g., `minecraft:zombie`)
2.  **Preset Rules**: If not, is there a rule for its Category? (e.g., `monsters`)
3.  **Global Rules**: If not, is there a Global rule set?

### Key Features
* **Granular Control**: Block mobs based on *how* they spawn (Natural, Spawners, Eggs, Commands).
* **Smart Presets**: Apply rules to entire categories (e.g., block all `monsters` or `water_creatures`).
* **Global Settings**: Apply a rule to *every* entity on the server instantly.
* **In-Game Management**: Full command system with auto-completion.
* **Zero Lag**: Uses optimized `HashSet` lookups and hooks into NeoForge events for minimal performance impact.
* **Server-Side Only**: Clients do not need to install this mod.

<br>

## 📜 The Rules System (Understanding Block Types)

When adding a rule, you define **what** to block. You can block specific sources or use "Inverse Mode" to allow *only* specific sources.

| Rule Keyword | Effect |
| :--- | :--- |
| `all` | Blocks **ALL** spawns for this target. |
| `natural` | Blocks natural spawns (darkness, biome generation). |
| `spawner` | Blocks mob spawners (dungeons, trial spawners). |
| `egg` | Blocks spawn eggs and dispensers. |
| `command` | Blocks `/summon` or command block spawns. |
| `!natural` | **Inverse**: Blocks everything *EXCEPT* natural spawns. |
| `!spawner` | **Inverse**: Blocks everything *EXCEPT* spawners. |
| `!command` | **Inverse**: Blocks everything *EXCEPT* commands. |

<br>

## 💻 Command Guide (Usage Examples)

All commands require **OP Level 4**.

### 1. Specific Mob Control (`add`)
Target a single entity type.

* **Block Zombies completely:**
  `/spawnblocker add minecraft:zombie all`

* **Block Creepers from spawning naturally (but allow spawners/eggs):**
  `/spawnblocker add minecraft:creeper natural`

* **Allow Skeletons ONLY from Spawners (Inverse Mode):**
  `/spawnblocker add minecraft:skeleton !spawner`

### 2. Category Presets (`preset`)
Apply rules to groups of mobs.
*Available categories:* `monsters`, `creatures`, `ambient`, `water`, `misc`.

* **Block all Monsters from spawning naturally:**
  `/spawnblocker preset monsters natural`

* **Disable all ambient creatures (Bats, etc.):**
  `/spawnblocker preset ambient all`

### 3. Global Settings (`global`)
Apply a fallback rule to the entire server.

* **Disable Natural Spawning server-wide (Adventure mode style):**
  `/spawnblocker global natural`

### 4. Management & Utilities
* **Remove a rule:** `/spawnblocker remove minecraft:zombie`
* **List active rules:** `/spawnblocker list`
* **Reload Config:** `/spawnblocker reload`
* **Reset Everything:** `/spawnblocker reset`

<br>

## 📂 Configuration File

The configuration is saved automatically to `config/spawnblocker.json`.

```json
{
  "minecraft:phantom": [ "all" ],
  "preset:monsters": [ "natural" ],
  "minecraft:zombie": [ "!spawner" ],
  "global_settings": [ "command" ]
}
```

<br>
<hr>
<br>

<details>
<summary><strong>📦 Legacy Documentation (v1.1.0 Only)</strong></summary>
<br>

> **Note:** This documentation applies **ONLY to version 1.1.0**. This version is no longer supported, but the documentation is kept for archival purposes.

<div align="center">

<h2>Mobs Blocker (Legacy v1.1.0)</h2>
<h3>Control Mob Spawning on Your Server</h3>

[![NeoForge](https://img.shields.io/badge/NeoForge-1.20.1+-orange?style=for-the-badge)](https://neoforged.net/)
[![GitHub](https://img.shields.io/badge/GitHub-Source-black?style=for-the-badge&logo=github)](https://github.com/Dreyka-Oas/Mobs-Blocker)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](https://opensource.org/licenses/MIT)

**Mobs Blocker** is a simple and effective admin tool to prevent any mob from spawning on your server via a simple command. Keep control of your world without restarts or complex configuration.

</div>

<hr>

<h2>How It Works</h2>

**Mobs Blocker** provides a real-time blacklisting mechanism. Using the `/spawnblocker` command, you can add or remove any mob from the game.

The mod intercepts every entity spawn attempt on the server. If the entity's ID matches an item on the blacklist, the event is simply canceled. The mob never appears, with zero impact on server performance.

<hr>

<h2>Core Features</h2>

<ul>
<li><strong>Instant Blocking</strong>: Add or remove mobs from the blacklist with immediate effect—no restart required.</li>
<li><strong>Simple Commands</strong>: An intuitive command interface (<code>add</code>, <code>remove</code>, <code>list</code>) that is easy to master.</li>
<li><strong>Built-in Autocomplete</strong>: Automatically suggests mob IDs to prevent typos.</li>
<li><strong>High Performance</strong>: Uses an optimized data structure (<code>HashSet</code>) for near-instant checks, ensuring zero lag.</li>
<li><strong>Lightweight & Server-Side</strong>: No installation required for players.</li>
</ul>

<hr>

<h2>Command Guide</h2>

All commands require operator permission level 4.

<h3>Add a Mob to the Blacklist</h3>
To block Creepers from spawning:
<pre><code>/spawnblocker add minecraft:creeper</code></pre>

<h3>Remove a Mob from the Blacklist</h3>
To allow Creepers to spawn again:
<pre><code>/spawnblocker remove minecraft:creeper</code></pre>

<h3>List Blacklisted Mobs</h3>
To see all currently blacklisted mobs:
<pre><code>/spawnblocker list</code></pre>

<hr>

<h2>Technical Details</h2>

<ul>
<li><strong>Server-Side Only</strong>: The mod is only installed on the server.</li>
<li><strong>Event-Driven</strong>: The system hooks into NeoForge's <code>EntityJoinLevelEvent</code> to intercept spawns.</li>
<li><strong>Persistent Storage</strong>: The blacklist is stored in a JSON file (<code>config/spawnblocker.json</code>) and persists across server restarts.</li>
</ul>

<h2>Manual Configuration</h2>

You can manually configure the blacklist by editing the <code>spawnblocker.json</code> file located in your server's <code>config</code> directory.

<h3>Example Configuration</h3>

<pre><code>[
  "minecraft:creeper",
  "minecraft:skeleton",
  "minecraft:spider"
]</code></pre>

<div align="center">
  <br>
  <p>A huge thank you to <strong><a href="https://mcreahub.pages.dev/">MCreaHub</a></strong> for facilitating the initial project setup.</p>
</div>

</details>
