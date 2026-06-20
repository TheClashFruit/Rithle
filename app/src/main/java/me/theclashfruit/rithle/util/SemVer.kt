package me.theclashfruit.rithle.util

class SemVer(
    val version: String
) {
    private val semverRegex = Regex(
        "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)" +
                "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)" +
                "(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?" +
                "(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"
    )

    private val groups = (
            semverRegex
                .matchEntire(
                    version.removePrefix("v")
                ) ?: error("Not a valid semver string!")
        )
        .groupValues

    val major = groups[1]
    val minor = groups[2]
    val patch = groups[3]
    val preRelease = groups.getOrNull(4)?.takeIf { it.isNotEmpty() }
    val build = groups.getOrNull(5)?.takeIf { it.isNotEmpty() }

    fun compare(other: SemVer): Int  {
        major.toInt().compareTo(other.major.toInt()).let { if (it != 0) return it }
        minor.toInt().compareTo(other.minor.toInt()).let { if (it != 0) return it }
        patch.toInt().compareTo(other.patch.toInt()).let { if (it != 0) return it }

        if (preRelease == null && other.preRelease != null) return 1
        if (preRelease != null && other.preRelease == null) return -1
        if (preRelease == null && other.preRelease == null) return 0

        val theseIds = preRelease!!.split(".")
        val otherIds = other.preRelease!!.split(".")

        val len = maxOf(theseIds.size, otherIds.size)
        for (i in 0 until len) {
            val a = theseIds.getOrNull(i)
            val b = otherIds.getOrNull(i)

            if (a == null) return -1
            if (b == null) return 1

            val aNum = a.toLongOrNull()
            val bNum = b.toLongOrNull()

            val cmp = when {
                aNum != null && bNum != null -> aNum.compareTo(bNum)
                aNum != null && bNum == null -> -1
                aNum == null && bNum != null -> 1
                else -> a.compareTo(b)
            }

            if (cmp != 0) return cmp
        }

        return 0
    }

    operator fun compareTo(other: SemVer): Int = compare(other)

    override fun toString(): String {
        return version.removePrefix("v")
    }
}