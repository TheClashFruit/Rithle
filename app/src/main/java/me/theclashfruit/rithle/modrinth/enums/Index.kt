package me.theclashfruit.rithle.modrinth.enums

enum class Index(private val value: String) {
    Relevance("relevance"),
    Downloads("downloads"),
    Follows("follows"),
    Newest("newest"),
    Updated("updated");

    override fun toString(): String {
        return value
    }
}