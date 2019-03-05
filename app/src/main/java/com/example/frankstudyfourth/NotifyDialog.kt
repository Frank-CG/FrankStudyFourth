package com.example.frankstudyfourth

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

class NotifyDialog() : DialogFragment() {
    lateinit var title:String
    lateinit var message: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        builder.apply {
            setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
        }
        builder.setTitle(title)
        builder.setMessage(message)
        return builder.create()
    }
}