package com.example.frankstudyfourth

import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.frankstudyfourth.model.MeetingModel
import com.example.frankstudyfourth.utils.DateTimeUtils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.TrackSelectionView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import java.util.*
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory
import kotlinx.android.synthetic.main.activity_video_player.*
import java.text.SimpleDateFormat
import java.time.Duration


class VideoPlayerActivity : AppCompatActivity(), View.OnClickListener{

    companion object {
        private const val KEY_PLAY_WHEN_READY = "play_when_ready"
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
    }

    private val TAG = "VideoPlayerActivity"
    private var player: SimpleExoPlayer? = null
    private val playerView: PlayerView by lazy { findViewById<PlayerView>(R.id.player_view) }
    private val audioOnlyView: TextView by lazy { findViewById<TextView>(R.id.audioOnly) }


    private var shouldAutoPlay: Boolean = true
    private var trackSelector: DefaultTrackSelector? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()

    private var playWhenReady: Boolean = false
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0
    private val progressBar: ProgressBar by lazy { findViewById<ProgressBar>(R.id.progress_bar) }
    private val ivHideControllerButton: ImageView by lazy { findViewById<ImageView>(R.id.exo_controller) }
    private val ivSettings: ImageView by lazy { findViewById<ImageView>(R.id.settings) }
    private val ivSubtitle: ImageView by lazy { findViewById<ImageView>(R.id.subtitles)}

    private var audioOnly:Boolean = false


    private lateinit var mediaUrlStr: String
        //"http://annie.sliq.local/hls/live2/playlist.m3u8"
        //"https://parlvulive.azureedge.net/HOC230-DUO-5/WB_Chamber/VL/EN/Playlist.m3u8?DVR"
        //"http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"

    private lateinit var meetingModel: MeetingModel


    private lateinit var defaultHlsExtractorFactory: DefaultHlsExtractorFactory

    private lateinit var language: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        if (savedInstanceState != null) {
            with(savedInstanceState) {
                playWhenReady = getBoolean(KEY_PLAY_WHEN_READY)
                currentWindow = getInt(KEY_WINDOW)
                playbackPosition = getLong(KEY_POSITION)
            }
        }

        meetingModel = intent.getParcelableExtra<MeetingModel>(AlarmClock.EXTRA_MESSAGE)
        var meetingId = meetingModel.id
        Log.d(TAG, "MeetingId=$meetingId")

        language = Locale.getDefault().isO3Language.substring(0,2)
        if(language != "en" && language != "fr"){
            language = "fl"
        }
        audioOnly = meetingModel.audioOnly
        mediaUrlStr = getUrl(language,meetingModel)

        initMeetingInfo()


        shouldAutoPlay = true
        mediaDataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"),
                bandwidthMeter as TransferListener)

        defaultHlsExtractorFactory = DefaultHlsExtractorFactory(FLAG_ALLOW_NON_IDR_KEYFRAMES)
    }

    override fun onBackPressed(){
        releasePlayer()
        Log.d("onBackPressed","releasePlayer")
        super.onBackPressed()
    }

    private fun initMeetingInfo(){
        meetingTitle.text = meetingModel.title
        meetingLocation.text = meetingModel.location
        meetingDescription.text = meetingModel.description

        var originalFormat = "yyyy-MM-dd'T'HH:mm:ss"
        var scheduled = DateTimeUtils.parseDateTime(meetingModel.scheduledStart, originalFormat, "E, MMM d, yyyy")
        scheduled += "\n" + DateTimeUtils.parseDateTime(meetingModel.scheduledStart, originalFormat, "h:mm a")
        scheduled += " - " + DateTimeUtils.parseDateTime(meetingModel.scheduledEnd, originalFormat, "h:mm a")
        val formatter = SimpleDateFormat(originalFormat, Locale.US)
        var dateStart = formatter.parse(meetingModel.scheduledStart)
        var dateEnd = formatter.parse(meetingModel.scheduledEnd)
        var diff = (dateEnd.time - dateStart.time)/1000/60
        var diffStr = ""
        if(diff > 60){
            var min = diff % 60
            diffStr += "$min Minutes"
            var hour = diff / 60
            if(hour > 24){
                var day = hour / 24
                hour %= 24
                diffStr = "$day Days $hour Hours $diffStr"
            }else{
                diffStr = "$hour Hours $diffStr"
            }
        }
        scheduled += "\n$diffStr"
        meetingScheduled.text = scheduled

        var actual = DateTimeUtils.parseDateTime(meetingModel.actualStart, originalFormat, "E, MMM d, yyyy")
        actual += "\n" + DateTimeUtils.parseDateTime(meetingModel.actualStart, originalFormat, "h:mm a")
        actual += " - " + DateTimeUtils.parseDateTime(meetingModel.actualEnd, originalFormat, "h:mm a")

        dateStart = formatter.parse(meetingModel.actualStart)
        dateEnd = formatter.parse(meetingModel.actualEnd)
        diff = (dateEnd.time - dateStart.time)/1000/60
        diffStr = ""
        if(diff > 60){
            var min = diff % 60
            diffStr += "$min Minutes"
            var hour = diff / 60
            if(hour > 24){
                var day = hour / 24
                hour %= 24
                diffStr = "$day Days $hour Hours $diffStr"
            }else{
                diffStr = "$hour Hours $diffStr"
            }
        }
        actual += "\n$diffStr"
        meetingActual.text = actual
    }

    private fun getUrl(lang: String, meeting: MeetingModel): String {
        var rst = ""
        var audioOnly = audioOnly.toString().toLowerCase()
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

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23 || player == null) initializePlayer()
    }

    public override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            Log.d("onStop","releasePlayer")
            releasePlayer()
        }
    }

    public fun reInitializePlayer(curPage:Int, curSelection: Int){
        Log.d("VideoPlayerActivity","reInitializePlayer")
        var langArr = arrayOf("auto","en","fr","fl")
        this.audioOnly = curPage == 0
        this.mediaUrlStr = getUrl(langArr[curSelection],meetingModel)
        updateStartPosition();
//        onStop();
//        initializePlayer();

        if(audioOnly){
            audioOnlyView.visibility = View.VISIBLE
//            playerView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        }else{
            audioOnlyView.visibility = View.INVISIBLE
        }

        val mediaSource = HlsMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(Uri.parse(mediaUrlStr))

        val haveStartPosition = currentWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player!!.seekTo(currentWindow, playbackPosition)
        }

        player!!.prepare(mediaSource, !haveStartPosition, false)

    }

    override fun onClick(v: View) {

        if(v.id == R.id.settings){

            Log.d("VideoPlayerActivity.onClick","settings")
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev!!)
            }
            ft.addToBackStack(null)

            // Create and show the dialog.
            val dialogFragment = SettingDialogView()
            dialogFragment.setAudioOnly(meetingModel.audioOnly)
            dialogFragment.setLanguage(language)
            dialogFragment.show(ft, "dialog")
        }

        if (v.id == R.id.subtitles) {

            val mappedTrackInfo = trackSelector?.currentMappedTrackInfo

            if (mappedTrackInfo != null) {

                Log.d("VideoPlayerActivity.Track","[rendererCount="+mappedTrackInfo.rendererCount.toString()+"]")
                for(i in 0 until mappedTrackInfo.rendererCount){
                    var logMsg = ""
                    logMsg += "[rendererTrackGroups ["+i+"] Length="+mappedTrackInfo.getTrackGroups(i).length+"]\n"
                    var trackGroups = mappedTrackInfo.getTrackGroups(i)
                    for(j in 0 until trackGroups.length){
                        logMsg += "\t[TrackGroups[$j]:\n"
                        var trackGroupLength = trackGroups.get(j).length
                        for(k in 0 until trackGroupLength){
                            var trackFormat = trackGroups.get(j).getFormat(k)
                            logMsg += "\t\t[TrackFormat[$k]:"+Format.toLogString(trackFormat)
                        }
                    }
                    Log.d("VideoPlayerActivity.Track",logMsg)
                }

                val title = getString(R.string.caption)
                val rendererIndex = ivSubtitle.tag as Int
                Log.d("VideoPlayerActivity.Track", "rendererIndex=$rendererIndex")
                val dialogPair = TrackSelectionView.getDialog(this, title, trackSelector, 2)
                dialogPair.second.setShowDisableOption(false)
                dialogPair.second.setAllowAdaptiveSelections(true)
                dialogPair.first.show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        updateStartPosition()

        with(outState) {
            putBoolean(KEY_PLAY_WHEN_READY, playWhenReady)
            putInt(KEY_WINDOW, currentWindow)
            putLong(KEY_POSITION, playbackPosition)
        }

        super.onSaveInstanceState(outState)
    }


    private fun initializePlayer() {

        playerView.requestFocus()

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)

        trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        lastSeenTrackGroupArray = null

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

        playerView.player = player

        with(player!!) {
            addListener(PlayerEventListener())
            playWhenReady = shouldAutoPlay
        }

        // Use Hls, Dash or other smooth streaming media source if you want to test the track quality selection.
        /*val mediaSource: MediaSource = HlsMediaSource(Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"),
                mediaDataSourceFactory, mainHandler, null)*/

        if(audioOnly){
            audioOnlyView.visibility = View.VISIBLE
        }else{
            audioOnlyView.visibility = View.INVISIBLE
        }
        val mediaSource = HlsMediaSource.Factory(mediaDataSourceFactory)
                .createMediaSource(Uri.parse(mediaUrlStr))

        val haveStartPosition = currentWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player!!.seekTo(currentWindow, playbackPosition)
        }

        player!!.prepare(mediaSource, !haveStartPosition, false)
        updateButtonVisibilities()

        ivHideControllerButton.setOnClickListener { playerView.hideController() }
    }

    private fun releasePlayer() {
        if (player != null) {
            updateStartPosition()
            shouldAutoPlay = player!!.playWhenReady
            player!!.release()
            player = null
            trackSelector = null
        }
    }

    private fun updateStartPosition() {

        with(player!!) {
            playbackPosition = currentPosition
            currentWindow = currentWindowIndex
            playWhenReady = playWhenReady
        }
    }

    private fun updateButtonVisibilities() {

        ivSubtitle.visibility = View.GONE
        if (player == null) {
            return
        }
        ivSettings.setOnClickListener(this)

        val mappedTrackInfo = trackSelector!!.currentMappedTrackInfo ?: return

        for (i in 0 until mappedTrackInfo.rendererCount) {
            val trackGroups = mappedTrackInfo.getTrackGroups(i)
            if (trackGroups.length != 0) {
                if (player!!.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                    ivSubtitle.visibility = View.VISIBLE
                    ivSubtitle.setOnClickListener(this)
                    ivSubtitle.tag = i
                }
            }
        }
    }


    private inner class PlayerEventListener : Player.DefaultEventListener() {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE       // The player does not have any media to play yet.
                -> progressBar.visibility = View.VISIBLE
                Player.STATE_BUFFERING  // The player is buffering (loading the content)
                -> progressBar.visibility = View.VISIBLE
                Player.STATE_READY      // The player is able to immediately play
                -> progressBar.visibility = View.GONE
                Player.STATE_ENDED      // The player has finished playing the media
                -> progressBar.visibility = View.GONE
            }
            updateButtonVisibilities()
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            updateButtonVisibilities()
            // The video tracks are no supported in this device.
            if (trackGroups !== lastSeenTrackGroupArray) {
                val mappedTrackInfo = trackSelector!!.currentMappedTrackInfo
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO) == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        Toast.makeText(this@VideoPlayerActivity, "Error unsupported track", Toast.LENGTH_SHORT).show()
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }

}