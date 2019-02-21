package com.example.frankstudyfourth

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.CompoundButton
import com.example.frankstudyfourth.model.MeetingModel
import kotlinx.android.synthetic.main.activity_setting_dialog.view.*

class SettingDialog : DialogFragment() {
    lateinit var meetingModel: MeetingModel
    lateinit var activity: VideoActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
//        var builder = AlertDialog.Builder(activity,R.style.DialogCustomTheme)
        var layoutInflater = activity!!.layoutInflater
        var view = layoutInflater.inflate(R.layout.activity_setting_dialog,null)

        if(meetingModel.audioOnly){
            view.videoStreamsList.visibility = View.GONE
            view.audioStreamsList.visibility = View.VISIBLE
            view.streamPicker.isChecked = false
            view.streamPicker.isClickable = false

            view.asen.setOnClickListener(View.OnClickListener {
                var videoPosition = activity.videoView.currentPosition
                activity.videoView.stopPlayback()
                activity.videoView.setVideoURI(Uri.parse(activity.GetUrl("en","true",activity.meetingModel)))
                activity.videoView.seekTo(videoPosition)
                activity.videoView.start()
            })

            view.asfr.setOnClickListener(View.OnClickListener {
                var videoPosition = activity.videoView.currentPosition
                activity.videoView.stopPlayback()
                activity.videoView.setVideoURI(Uri.parse(activity.GetUrl("fr","true",activity.meetingModel)))
                activity.videoView.seekTo(videoPosition)
                activity.videoView.start()
            })

            view.asfl.setOnClickListener(View.OnClickListener {
                var videoPosition = activity.videoView.currentPosition
                activity.videoView.stopPlayback()
                activity.videoView.setVideoURI(Uri.parse(activity.GetUrl("fl","true",activity.meetingModel)))
                activity.videoView.seekTo(videoPosition)
                activity.videoView.start()
            })

        }else{
            view.audioStreamsList.visibility = View.GONE
            view.videoStreamsList.visibility = View.VISIBLE
            view.streamPicker.isClickable = true
            view.streamPicker.isChecked = true
                view.streamPicker.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener(){ compoundButton: CompoundButton, isChecked: Boolean ->
                if(isChecked){
                    view.audioStreamsList.visibility = View.GONE
                    view.videoStreamsList.visibility = View.VISIBLE
                }else{
                    view.videoStreamsList.visibility = View.GONE
                    view.audioStreamsList.visibility = View.VISIBLE
                }
            })

            view.asen.setOnClickListener(View.OnClickListener {
                var videoPosition = activity.videoView.currentPosition
                activity.videoView.stopPlayback()
                activity.videoView.setVideoURI(Uri.parse(activity.GetUrl("en","true",activity.meetingModel)))
                activity.videoView.seekTo(videoPosition)
                activity.videoView.start()
            })

            view.vsen.setOnClickListener(View.OnClickListener {
                var videoPosition = activity.videoView.currentPosition
                activity.videoView.stopPlayback()
                activity.videoView.setVideoURI(Uri.parse(activity.GetUrl("en","false",activity.meetingModel)))
                activity.videoView.seekTo(videoPosition)
                activity.videoView.start()
            })

            view.asfr.setOnClickListener(View.OnClickListener {
                var videoPosition = activity.videoView.currentPosition
                activity.videoView.stopPlayback()
                activity.videoView.setVideoURI(Uri.parse(activity.GetUrl("fr","true",activity.meetingModel)))
                activity.videoView.seekTo(videoPosition)
                activity.videoView.start()
            })

            view.vsfr.setOnClickListener(View.OnClickListener {
                var videoPosition = activity.videoView.currentPosition
                activity.videoView.stopPlayback()
                activity.videoView.setVideoURI(Uri.parse(activity.GetUrl("fr","false",activity.meetingModel)))
                activity.videoView.seekTo(videoPosition)
                activity.videoView.start()
            })

            view.asfl.setOnClickListener(View.OnClickListener {
                var videoPosition = activity.videoView.currentPosition
                activity.videoView.stopPlayback()
                activity.videoView.setVideoURI(Uri.parse(activity.GetUrl("fl","true",activity.meetingModel)))
                activity.videoView.seekTo(videoPosition)
                activity.videoView.start()
            })

            view.vsfl.setOnClickListener(View.OnClickListener {
                var videoPosition = activity.videoView.currentPosition
                activity.videoView.stopPlayback()
                activity.videoView.setVideoURI(Uri.parse(activity.GetUrl("fl","false",activity.meetingModel)))
                activity.videoView.seekTo(videoPosition)
                activity.videoView.start()
            })
        }


        builder.setView(view)
        builder.setTitle("Please select a stream")
        println("MeetingId = "+meetingModel.id)
        println(meetingModel.streams.size)

        var dialog = builder.create()
//        var window = dialog.window
//        var size = Point()
//        window.windowManager.defaultDisplay.getSize(size)
//        window.setLayout(((size.x * 1.5).toInt()),WindowManager.LayoutParams.WRAP_CONTENT )
//        window.setBackgroundDrawableResource(R.color.colorDivider80);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        dialog.getWindow().setBackgroundDrawable(
//            ColorDrawable(resources.getColor(android.R.color.darker_gray))
//        );

        return dialog
    }


    fun onClickAudio(v: View){
        println(v.id)
    }

    fun onClickVideo(v: View){
        println(v.id)
    }

    fun switchStream(v: View){
        println(v.id)
    }
}
