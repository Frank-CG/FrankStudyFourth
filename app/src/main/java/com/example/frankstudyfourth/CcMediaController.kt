package com.example.frankstudyfourth

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_setting_dialog.view.*


class CcMediaController(private var activity: VideoActivity) : MediaController(activity) {

    internal lateinit var mCCBtn: ImageButton
    internal lateinit var mLangDialog: SettingDialog

    override fun setAnchorView(view: View) {
        super.setAnchorView(view)

        val frameParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
        )
        frameParams.gravity = Gravity.LEFT or Gravity.TOP

        val v = makeCCView()
        addView(v, frameParams)

    }

    private fun makeCCView(): View {
        mCCBtn = ImageButton(activity)
        mCCBtn.setImageResource(R.drawable.ic_settings)

        mCCBtn.setOnClickListener {
            mLangDialog = makeDialog()
            mLangDialog.show(activity.supportFragmentManager,"my_dialog")
        }

        return mCCBtn
    }

    private fun makeDialog_old(): AlertDialog{
        val options = arrayOf("English", "French", "Floor")
        var urlOptions = arrayOf("en","fr","fl")
        val adapter = ArrayAdapter(activity, android.R.layout.select_dialog_item, options)
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Stream Selection")
        builder.setAdapter(adapter) { dialog, index ->
            println("Dialog:" + options[index])
            var videoPosition = activity.videoView.currentPosition
            activity.videoView.stopPlayback()
            activity.videoView.setVideoURI(Uri.parse(activity.GetUrl(urlOptions[index],"false",activity.meetingModel)))
            activity.videoView.seekTo(videoPosition)
            activity.videoView.start()
        }
        return builder.create()
    }

    private fun makeDialog(): SettingDialog{
        var dialog = SettingDialog()
        dialog.meetingModel = activity.meetingModel
        dialog.activity = activity



        return dialog
    }

}