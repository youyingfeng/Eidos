package org.eidos.reader.ui.utilities

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class Utilities {
    companion object {
        // public static extension functions for android miscellania like closing keyboards

        /* Extension functions to hide the keyboard from anywhere */
        // solution taken from: https://stackoverflow.com/questions/41790357/close-hide-the-android-soft-keyboard-with-kotlin
        fun Fragment.hideKeyboard() {
            view?.let { activity?.hideKeyboard(it) }
        }

        fun Activity.hideKeyboard() {
            hideKeyboard(currentFocus ?: View(this))
        }

        fun Context.hideKeyboard(view: View) {
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        /* Extension functions to set the title of the activity */
        // solution taken from: https://stackoverflow.com/questions/27918701/android-fragment-change-title
        fun Fragment.setActivityTitle(@StringRes id: Int) {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = getString(id)
        }

        fun Fragment.setActivityTitle(title: String) {
            (activity as AppCompatActivity?)!!.supportActionBar?.title = title
        }

    }
}