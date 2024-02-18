package me.theclashfruit.rithle.modrinth.facets

class FacetBuilder {
    private var facets: List<MutableList<String>> = listOf(
        mutableListOf(),
        mutableListOf()
    )

    fun addCategory(category: String): FacetBuilder {
        facets[0].add(category)

        return this
    }

    fun addCategories(category: List<String>): FacetBuilder {
        category.forEach {
            facets[0].add(it)
        }

        return this
    }

    fun addProjectType(projectType: String): FacetBuilder {
        facets[1].add(projectType)

        return this
    }

    fun build(): List<List<String>> {
        return facets
    }
}