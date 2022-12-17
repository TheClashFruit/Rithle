package me.theclashfruit.mrapi

class FilterBuilder {
    private val filterData: ArrayList<ArrayList<String>> = arrayListOf()

    fun addFilterItem(filter: String): FilterBuilder {
        filterData.add(arrayListOf(filter))

        return this
    }

    fun setProjectType(projectType: String): FilterBuilder {
        when(projectType) {
            "mod" -> filterData.add(arrayListOf("project_type:$projectType"))
            "modpack" -> filterData.add(arrayListOf("project_type:$projectType"))
            "resourcepack" -> filterData.add(arrayListOf("project_type:$projectType"))
            else -> throw NoSuchFieldError("Invalid Project Type!")
        }

        return this
    }

    fun build(): ArrayList<ArrayList<String>> {
        return filterData
    }
}