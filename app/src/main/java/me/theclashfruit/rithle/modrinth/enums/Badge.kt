package me.theclashfruit.rithle.modrinth.enums

enum class Badge(val value: Int) {
    EarlyModpackAdopter(1 shl 1),
    EarlyResourcePackAdopter(1 shl 2),
    EarlyPluginAdopter(1 shl 3),
    AlphaTester(1 shl 4),
    Contributor(1 shl 5),
    Translator(1 shl 6);

    companion object {
        fun fromInt(value: Int): List<Badge> {
            return entries.filter { (value and it.value) != 0 }
        }

        fun toInt(badges: List<Badge>): Int {
            return badges.fold(0) { acc, badge -> acc or badge.value }
        }
    }
}