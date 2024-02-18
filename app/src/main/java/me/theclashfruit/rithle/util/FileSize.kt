package me.theclashfruit.rithle.util

class FileSize {
    companion object {
        private const val kb: Int = 1024
        private const val mb: Int = 1024 * kb
        private const val gb: Int = 1024 * mb

        /**
         * Convert human readable size to bytes.
         *
         * @param size Size to convert
         * @param unit Unit of the size
         * @return Size in bytes
         *
         * @sample oneKilobyte
         * @sample oneMegabyte
         * @sample oneGigabyte
         */
        fun toBytes(size: Int, unit: Int): Int {
            return when (unit) {
                1 -> size * kb
                2 -> size * mb
                3 -> size * gb
                else -> size
            }
        }

        /**
         * Convert bytes to human readable size.
         *
         * @param size Size to convert
         * @param unit Unit of the size
         * @return Size in human readable format
         */
        fun toHuman(size: Int, unit: Int): String {
            return when (unit) {
                1 -> "${size / kb} KB"
                2 -> "${size / mb} MB"
                3 -> "${size / gb} GB"
                else -> size.toString()
            }
        }

        /**
         * @return 1KB in bytes
         */
        fun oneKilobyte(): Int { return toBytes(1, SizeUnit.KB) }

        /**
         * @return 1MB in bytes
         */
        fun oneMegabyte(): Int { return toBytes(1, SizeUnit.MB) }

        /**
         * @return 1GB in bytes
         */
        fun oneGigabyte(): Int { return toBytes(1, SizeUnit.GB) }
    }

    class SizeUnit {
        companion object {
            /**
             * Size unit for kilobytes
             */
            const val KB: Int = 1;

            /**
             * Size unit for megabytes
             */
            const val MB: Int = 2;

            /**
             * Size unit for gigabytes
             */
            const val GB: Int = 3;
        }
    }
}