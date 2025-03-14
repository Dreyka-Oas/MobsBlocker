# Block Specific Mobs from Spawning 🚫

Take control of creature spawns in your Minecraft world! This simple and effective mod lets you block specific mob spawns using an easy-to-edit configuration file. Say goodbye to unwanted creatures!

***

<details>
  <summary>⚙️ Easy Configuration</summary>

## ⚙️ Easy Configuration

**Quick steps to block mobs:**

1.  **`mobs.oas` File:** Automatically created in `config/oas_work/` on first launch.
2.  **Mob List:** Edit `mobs.oas` and add full Minecraft entity names (one per line, e.g., `minecraft:sheep`).
3.  **Blocking Active:** The mod reads the file and prevents listed mobs from spawning.
4.  **Full Compatibility:** Works with all mods adding new creatures.

---

### 📂 Accessing the Configuration File

The `mobs.oas` file is generated here: `config/oas_work/mobs.oas`. If it doesn't exist, it will be created on the first game launch.

---

### ✏️ Content of the `mobs.oas` File

The file contains a list of full Minecraft entity names. **Example:**
Use code with caution.
minecraft:sheep
minecraft:creeper
minecraft:zombie

Simply add the entities you want to block, one per line.

---

### 🚫 Blocking Entities

The mod reads each line in `mobs.oas`. If an entity matches a listed name, its spawn is **automatically canceled ❌**.

---

### 🧩 Mod Compatibility

This mod is **fully compatible** with other mods that add entities. You can block both vanilla and modded mobs by using their full entity names (e.g., `modname:custom_entity`).

</details>

***

<details>
  <summary>🛠️ Simple Usage</summary>

## 🛠️ Simple Usage

1.  **Edit `mobs.oas`:** Add the names of entities to block.
2.  **Launch Minecraft:** Or restart your server. Listed mobs will no longer spawn.

</details>

***

## 🌟 Note

**Note:** Compatible with all mods adding entities, to easily block both vanilla and modded mobs!
