package com.hjkl.comm

import android.content.Context
import android.widget.Toast

object ToastUtil {

    private var mToast: Toast? = null

    fun toast(msg: String) {
        toast(AppUtil.getContext(), msg)
    }

    fun toast(context: Context, msg: String) {
        if (mToast != null) {
            mToast?.cancel()
            mToast = null
        }
        mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        mToast?.show()
    }

}