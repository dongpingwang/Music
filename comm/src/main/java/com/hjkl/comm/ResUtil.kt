package com.hjkl.comm

import androidx.annotation.StringRes

object ResUtil {

    private val resources by lazy { AppUtil.getContext().resources }

    fun getString(@StringRes id: Int): String {
        return resources.getString(id)
    }
}