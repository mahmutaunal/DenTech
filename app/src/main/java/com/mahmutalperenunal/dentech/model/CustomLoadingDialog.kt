package com.mahmutalperenunal.dentech.model

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import com.mahmutalperenunal.dentech.R

class CustomLoadingDialog(var activity: Activity) {

    private lateinit var alertDialog: AlertDialog

    @SuppressLint("InflateParams")
    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity, R.style.CustomAlertDialog)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_loading_dialog, null))
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window!!.setLayout(600,500)
    }

    fun dismissDialog() {
        alertDialog.dismiss()
    }
}