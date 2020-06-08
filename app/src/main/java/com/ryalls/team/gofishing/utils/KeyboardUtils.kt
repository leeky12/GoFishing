package com.ryalls.team.gofishing.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


class KeyboardUtils {

    fun closeKeyboard(context: Context, view: View?) {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}