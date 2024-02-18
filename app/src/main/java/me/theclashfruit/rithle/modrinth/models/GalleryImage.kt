package me.theclashfruit.rithle.modrinth.models

data class GalleryImage(
    val url: String,
    val featured: Boolean,
    val title: String? = null,
    val description: String? = null,
    val created: String,
    val ordering: Int? = null
)
