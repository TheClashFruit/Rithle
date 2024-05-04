package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class File(
    val hashes: Hash,
    val url: String,
    @SerializedName("filename") val fileName: String,
    val primary: Boolean,
    val size: Int,
    @SerializedName("file_type") val fileType: String?
)
