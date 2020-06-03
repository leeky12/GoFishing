package com.ryalls.team.gofishing.ui

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


object DisplayKeyboard {

    fun showSoftKeyboard(context : Context, view: View) {
 //       if (view.requestFocus()) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
  //      }
    }
}