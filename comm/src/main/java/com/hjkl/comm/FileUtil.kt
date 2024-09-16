package com.hjkl.comm

import java.io.File


object FileUtil {

    fun getFolderPath(filePath: String): String {
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep)
    }

    fun getFolderName(filePath: String): String {
        val list = filePath.split(File.separator)
        return list[list.lastIndex - 1]
    }

    fun getFileName(filePath: String, isIncludeExtendName: Boolean = true): String {
        val lastSep = filePath.lastIndexOf(File.separator)
        if (isIncludeExtendName) {
            return filePath.substring(lastSep + 1)
        } else {
            val lastDot = filePath.lastIndexOf(".")
            return filePath.substring(lastSep + 1, lastDot)
        }
    }

    fun getFileExtendName(filePath: String): String {
        val lastDot = filePath.lastIndexOf(".")
        return filePath.substring(lastDot + 1)
    }
}