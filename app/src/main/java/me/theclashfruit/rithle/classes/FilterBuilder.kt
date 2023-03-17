package me.theclashfruit.rithle.classes

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
            "shader" -> filterData.add(arrayListOf("project_type:$projectType"))
            else -> throw NoSuchFieldError("Invalid Project Type!")
        }

        return this
    }

    fun build(): String {
        val fLick: ArrayList<String> = arrayListOf()

        if(filterData[0].isEmpty())
            filterData.removeAt(0)

        filterData.forEach {
            fLick.add(
                it.joinToString(
                    separator = "%22%2C%22",
                    prefix = "%5B%22",
                    postfix = "%22%5D"
                )
            )
        }

        return fLick.joinToString(
            separator = "%2C",
            prefix = "%5B",
            postfix = "%5D"
        )
    }
}