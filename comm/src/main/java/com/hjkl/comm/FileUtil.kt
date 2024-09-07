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
}