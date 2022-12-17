package me.theclashfruit.mrapi

class FilterBuilder {
    private val filterData: ArrayList<ArrayList<String>> = arrayListOf(arrayListOf())

    fun addFilterItem(filter: String): FilterBuilder {
        filterData[0].add(
            filter
                .replace("'", "%27")
                .replace(":", "%3A")
        )

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

    fun build(): String {
        val fLick: ArrayList<String> = arrayListOf()

        filterData.forEach {
            fLick.add(
                it.joinToString(
                    separator = "\",\"",
                    prefix = "[\"",
                    postfix = "\"]"
                )
            )
        }

        return fLick.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]"
        )
    }
}