package com.example.studybuddy.utils

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ToastNotifier @Inject constructor(
    @ApplicationContext private val
    context: Context
) {

    fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}