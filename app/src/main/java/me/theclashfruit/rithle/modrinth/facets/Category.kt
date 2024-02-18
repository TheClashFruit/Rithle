package me.theclashfruit.rithle.modrinth.facets

class Category {
    companion object {
        // Categories

        const val ADVENTURE = "categories:adventure"
        const val CURSED = "categories:cursed"
        const val DECORATION = "categories:decoration"
        const val ECONOMY = "categories:economy"
        const val EQUIPMENT = "categories:equipment"
        const val FOOD = "categories:food"
        const val GAME_MECHANICS = "categories:game-mechanics"
        const val LIBRARY = "categories:library"
        const val MAGIC = "categories:magic"
        const val MANAGEMENT = "categories:management"
        const val MINIGAME = "categories:minigame"
        const val MOBS = "categories:mobs"
        const val OPTIMIZATION = "categories:optimization"
        const val SOCIAL = "categories:social"
        const val STORAGE = "categories:storage"
        const val TECHNOLOGY = "categories:technology"
        const val TRANSPORTATION = "categories:transportation"
        const val UTILITY = "categories:utility"
        const val WORLDGEN = "categories:worldgen"

        // Data Pack

        const val DATA_PACK = "categories:datapack"

        // Mod Loaders

        const val FABRIC = "categories:fabric"
        const val FORGE = "categories:forge"
        const val NEOFORGE = "categories:neoforge"
        const val QUILT = "categories:quilt"
        const val LITELOADER = "categories:liteloader"
        const val MODLOADER = "categories:modloader"
        const val RIFT = "categories:rift"

        // Plugin Loaders

        const val BUKKIT = "categories:bukkit"
        const val SPIGOT = "categories:spigot"
        const val PAPER = "categories:paper"
        const val PURPUR = "categories:purpur"
        const val SPONGE = "categories:sponge"
        const val BUNGEECORD = "categories:bungeecord"
        const val WATERFALL = "categories:waterfall"
        const val VELOCITY = "categories:velocity"
        const val FOLIA = "categories:folia"

        // All Loaders

        val ALL_MOD_LOADERS = listOf(
            FABRIC,
            FORGE,
            NEOFORGE,
            QUILT,
            LITELOADER,
            MODLOADER,
            RIFT
        )

        val ALL_PLUGIN_LOADERS = listOf(
            BUKKIT,
            SPIGOT,
            PAPER,
            PURPUR,
            SPONGE,
            BUNGEECORD,
            WATERFALL,
            VELOCITY,
            FOLIA
        )
    }
}