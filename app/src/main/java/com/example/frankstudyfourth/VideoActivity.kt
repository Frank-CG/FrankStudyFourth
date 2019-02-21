package com.example.frankstudyfourth

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.VideoView
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.media.MediaPlayer.OnPreparedListener
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.example.frankstudyfourth.Protocols.ApiService
import com.example.frankstudyfourth.Protocols.BasicSettings.urlBaseHome
import com.example.frankstudyfourth.model.MeetingModel
import com.example.frankstudyfourth.model.MeetingStreamModel
import kotlinx.android.synthetic.main.activity_video.*
import java.util.*
import android.R.string.cancel
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog


class VideoActivity : AppCompatActivity() {

    var videourl = ""
    lateinit var videoView: VideoView
    lateinit var meetingModel: MeetingModel

    lateinit var thumbnail: Bitmap

    var audioOnly: String = "false"
    var language: String = "fl"
    var showlanguage: String = "en"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        videoView = findViewById(R.id.video_View);
        meetingModel = intent.getParcelableExtra<MeetingModel>(EXTRA_MESSAGE)
        Log.d("VideoActivity","Meeting Id="+meetingModel.id)

        language = Locale.getDefault().isO3Language.substring(0,2)
        if(language != "en" && language != "fr"){
            language = "fl"
        }else{
            showlanguage = language
        }
        println("Language:"+language)

        audioOnly = meetingModel.audioOnly.toString().toLowerCase()

        AsyncGetURL(this).execute()
    }

    fun GetUrl(lang: String, audioOnly: String, meeting: MeetingModel): String {
        var rst = ""
        var streams = meeting.streams
        if(streams.size > 0){
            for(i in 0..(streams.size-1)){
                var st = streams.get(i)
                if(st.audioOnly == audioOnly && st.lang == lang){
                    rst = st.url
                }
            }
        }
        return rst
    }

    private fun PlayVideo() {
        try {
            window.setFormat(PixelFormat.TRANSLUCENT)

            val mediaController = CcMediaController(this@VideoActivity)
            mediaController.setAnchorView(videoView)

            val video = Uri.parse(videourl)
            videoView?.setMediaController(mediaController)
            videoView?.setVideoURI(video)
            videoView?.requestFocus()
            videoView?.setVisibility(View.VISIBLE)

            if(this.audioOnly.toBoolean()){
                videoView!!.background = BitmapDrawable(resources,thumbnail)
            }

            if(videoView!!.isPlaying()){
                videoBuffering.visibility = View.GONE
            }

            videoView?.setOnPreparedListener(OnPreparedListener {
//                progressDialog?.dismiss()
                videoBuffering.visibility = View.GONE
                videoView!!.start()
            })
            videoView?.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                finish()
            })
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } catch (e: Exception) {
//            progressDialog?.dismiss()
            videoBuffering.visibility = View.GONE
            println("Video Play Error :" + e.toString())
            finish()
        }
    }


    @SuppressLint("StaticFieldLeak")
    class AsyncGetURL(private var activity: VideoActivity?) : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {
            var rst = ""
            var meetingId = activity?.meetingModel?.id
            var jsonMeetingStreamList = ApiService.GetStreamData(meetingId,activity!!.showlanguage)
            var jsonMeetingStreamArray = jsonMeetingStreamList.getJSONArray("Streams")
            var mStreamList = ArrayList<MeetingStreamModel>()
            var flag = true
            if(jsonMeetingStreamArray!!.length() > 0){
                for (j in 0..(jsonMeetingStreamArray.length()-1)){
                    var jsonMeetingStream = jsonMeetingStreamArray.getJSONObject(j)
                    mStreamList.add(
                        MeetingStreamModel(
                            jsonMeetingStream.getString("GlobalEssenceFormatId"),
                            jsonMeetingStream.getString("IsLive"),
                            jsonMeetingStream.getString("Enabled"),
                            jsonMeetingStream.getString("AudioOnly"),
                            jsonMeetingStream.getString("VideoIndex"),
                            jsonMeetingStream.getString("AudioIndex"),
                            jsonMeetingStream.getString("StreamFormatId"),
                            jsonMeetingStream.getString("Url"),
                            jsonMeetingStream.getString("Lang"),
                            jsonMeetingStream.getString("StreamAssemblerList"),
                            jsonMeetingStream.getString("PreRoll"),
                            jsonMeetingStream.getString("Duration"),
                            jsonMeetingStream.getString("Id"),
                            jsonMeetingStream.getString("Tag")
                        )
                    )
                    if(jsonMeetingStream.getString("AudioOnly") == "false"){
                        flag = false
                    }
                    if(jsonMeetingStream.getString("AudioOnly") == activity?.audioOnly &&
                        jsonMeetingStream.getString("Lang") == activity?.language){
                        rst = jsonMeetingStream.getString("Url")
                    }
                }
                activity?.meetingModel?.audioOnly = flag
                activity?.audioOnly = flag.toString().toLowerCase()
                activity?.meetingModel?.streams = mStreamList
                if(rst == ""){
                    rst = activity!!.GetUrl(activity!!.language, activity!!.audioOnly,activity!!.meetingModel)
                }
                var uri = urlBaseHome + activity!!.meetingModel.thumbnailUri
                activity!!.thumbnail = ApiService.GetThumbnail(uri)
            }
            return rst
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            println("URL="+result)
            if(result != null && result != ""){
                activity?.videourl = result
//                if(activity?.progressDialog == null){
//                    activity?.progressDialog = ProgressDialog.show(activity!!, "", "Buffering video...", true);
//                    activity?.progressDialog?.setCancelable(false);
//                }
                activity!!.videoBuffering.visibility = View.VISIBLE
                println("progressDialog!")
                activity?.PlayVideo();
            }else{
                activity?.videourl = ""
                println("Video Play Error: No Resource found!")
                noStreamAlert(activity!!)
            }
        }

        fun noStreamAlert(context:Context){
            val builder1 = AlertDialog.Builder(context)
            builder1.setMessage("The broadcast for this meeting has ended, streams are not available for now.")
            builder1.setCancelable(true)

            builder1.setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    activity?.finish()
            })

            val alert11 = builder1.create()
            alert11.show()
        }
    }

}
