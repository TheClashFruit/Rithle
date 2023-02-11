package me.theclashfruit.rithle.models

import kotlinx.serialization.Serializable

@Serializable
data class GitHubAccessTokenModel (
    var access_token : String? = null,
    var token_type   : String? = null,
    var scope        : String? = null
)