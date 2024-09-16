package com.hjkl.comm

import androidx.annotation.StringRes

object ResUtil {

    private val resources by lazy { AppUtil.getContext().resources }

    fun getString(@StringRes id: Int): String {
        return resources.getString(id)
    }

    fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        return resources.getString(id, formatArgs)
    }
}