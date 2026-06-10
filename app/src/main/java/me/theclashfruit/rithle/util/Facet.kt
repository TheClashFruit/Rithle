package me.theclashfruit.rithle.util

import me.theclashfruit.rithle.enums.Operation

class Facet(val id: String) {
    companion object {
        val ProjectType = Facet("project_types")

        /**
         * Loaders are lumped in with categories in search.
         */
        val Category = Facet("categories")

        val Version = Facet("game_versions")
        val ClientSide = Facet("client_side")
        val ServerSide = Facet("server_side")
        val OpenSource = Facet("open_source")
        val Title = Facet("title")
        val Author = Facet("author")
        val Follows = Facet("follows")
        val ProjectId = Facet("project_id")
        val License = Facet("license")
        val Downloads = Facet("downloads")
        val Color = Facet("color")
        val CreatedTimestamp = Facet("created_timestamp")
        val ModifiedTimestamp = Facet("modified_timestamp")

        fun builder(): Builder = Builder()
    }

    class Builder() {
        private val filters = mutableListOf<List<String>>()

        /**
         * Adds a group of filters connected by an OR operator.
         *
         * Example we want to filter mods that support either fabric or forge: `or(Facet.Category, "fabric", "forge")`
         *
         * @param facet The facet type.
         * @param values The values to filter on.
         *
         * @return [Builder]
         */
        fun or(facet: Facet, vararg values: Any): Builder {
            filters.add(values.map { "${facet.id}:${it.toString().lowercase()}" })

            return this
        }

        /**
         * Adds a filter criterion connected by an AND operator.
         *
         * Example we want to filter mods that are open source and client side: `and(Facet.OpenSource, true).and(Facet.ClientSide, true)`
         *
         * @param facet The facet type.
         * @param value The value to filter by.
         *
         * @return [Builder]
         */
        fun and(facet: Facet, value: Any): Builder {
            filters.add(listOf("${facet.id}:${value.toString().lowercase()}"))

            return this
        }

        /**
         * Adds a filter criterion to exclude certain results.
         *
         * Example we want to exclude mods that are not open source: `exclude(Facet.OpenSource, false)`
         *
         * @param facet The facet type.
         * @param value The value to filter by.
         *
         * @return [Builder]
         */
        fun exclude(facet: Facet, value: Any): Builder {
            filters.add(listOf("${facet.id}!=${value.toString().lowercase()}"))

            return this
        }

        /**
         * Adds a filter operation.
         *
         * Example we want to filter mods that have more than 100 downloads: `op(Facet.Downloads, Operation.Greater, 100)`
         *
         * @param facet The facet type.
         * @param operation The operation to perform.
         * @param value The value to filter by.
         *
         * @return [Builder]
         */
        fun op(facet: Facet, operation: Operation, value: Any): Builder {
            filters.add(listOf("${facet.id}${operation.value}${value.toString().lowercase()}"))

            return this
        }

        fun build(): List<List<String>> = filters
    }
}