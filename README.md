STOP to Unwanted Spawns! 🛑

This server mod allows you to **block the spawning** of specific **mobs** in your Minecraft world! Tired of creepers exploding everywhere or hordes of sheep invading your base? This mod is for you! 🐑💥

**⚙️ How does it work?**

"Mobs Blocker" is a **server-side** mod that works **in the background** to control mob spawns. Here are the key steps:

1. **📝 Configuration:** When the server starts for the first time with the mod, a configuration file named `mobs.oas` is created in the `config/oas_work/` folder.
2. **📖 Reading the configuration file:** The mod reads this `mobs.oas` file which contains a **list of mobs to block**. Each mob to block must be added on a **new line** in this file, using its **full Minecraft identifier** (e.g., `minecraft:creeper`, `minecraft:sheep`).
3. **🛑 Blocking Spawns:** When Minecraft tries to spawn a mob, the mod **checks if its identifier is present in the list** in the `mobs.oas` file.
4. **⛔ Spawn Canceled:** If the mob is on the list, its **spawn is canceled**, and it will not appear in the world. Other mobs continue to spawn normally.

**🎮 How to use it?**

"Mobs Blocker" is designed to be **easy to configure**.

1. **🛠️ Configuration:**
    * **📂 Locate the configuration file:** Go to the `config/oas_work/` folder of your server. Open the `mobs.oas` file with a text editor.
    * **✏️ Modify the list:** **Add** the **Minecraft identifiers of the mobs** you want to block on each line. You can **delete** lines to allow certain mobs to spawn again. For example, to block creepers and sheep, the `mobs.oas` file should look like this:
       ```
       minecraft:sheep
       minecraft:creeper
       ```
    * **💾 Save** the `mobs.oas` file.
    * 🔄 **Changes are applied instantly, no server restart needed!**

**📌 Important Points:**

* 💻 **Server-Side ONLY:** This mod is **server-side only**. It must be installed **on the server** and not on players' games.
* ⚙️ **File-Based Configuration:** The list of blocked mobs is **directly editable** in the `mobs.oas` file, without complex commands.
* 📍 **Configuration File:** The `mobs.oas` file is located in the `config/oas_work/` folder of your server.
* 📝 **Minecraft Identifiers:** Use the **full Minecraft identifiers** of mobs (e.g., `minecraft:zombie`, `minecraft:pig`). You can find identifiers on sites like the Minecraft Wiki.
* 🤝 **Broad Compatibility:** This mod is **compatible with most other Minecraft mods**, ensuring easy integration into your existing server setup.
* ⚡ **Immediate Effect:** Modifications to the `mobs.oas` file are applied **instantly**, without requiring a server restart.
