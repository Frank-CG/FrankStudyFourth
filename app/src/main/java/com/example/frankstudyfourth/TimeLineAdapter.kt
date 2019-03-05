package com.example.frankstudyfourth

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frankstudyfourth.Protocols.ApiService
import com.example.frankstudyfourth.model.MeetingModel
import com.example.frankstudyfourth.model.MeetingStreamModel
import com.example.frankstudyfourth.utils.DateTimeUtils
import com.example.frankstudyfourth.utils.VectorDrawableUtils

import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.activity_video_player.view.*

import kotlinx.android.synthetic.main.item_timeline.view.*
import java.util.*

/**
 * Created by Vipul Asri on 13-12-2018.
 */

class TimeLineAdapter(private val mFeedList: List<MeetingModel>,private val activity: AppCompatActivity) : RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val  layoutInflater = LayoutInflater.from(parent.context)
        return TimeLineViewHolder(layoutInflater.inflate(R.layout.item_timeline, parent, false), viewType)
    }

    fun getMeetingList(): List<MeetingModel>{
        return mFeedList
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {

        val timeLineModel = mFeedList[position]

        if (timeLineModel.scheduledStart.isNotEmpty()) {
            holder.date.visibility = View.VISIBLE
            var time_start = DateTimeUtils.parseDateTime(timeLineModel.scheduledStart, "yyyy-MM-dd'T'HH:mm:ss", "h:mm a")
            var time_end = DateTimeUtils.parseDateTime(timeLineModel.scheduledEnd,"yyyy-MM-dd'T'HH:mm:ss","h:mm a")
            holder.date.text = time_start + " - " + time_end
        } else {
            holder.date.visibility = View.GONE
        }

        if(timeLineModel.description.isNotEmpty()){
            holder.desc.visibility = View.VISIBLE
            holder.desc.text = timeLineModel.description
        }else{
            holder.desc.visibility = View.GONE
        }

        if(timeLineModel.entityStatusDesc.isNotEmpty()){
            holder.status.visibility = View.VISIBLE
            holder.status.text = timeLineModel.entityStatusDesc
            when {
                timeLineModel.entityStatus.toInt() == 1 -> {    //In Progress
                    setMarker(holder, R.drawable.ic_marker_active, R.color.material_green_500)
                    holder.status.setBackgroundColor(holder.status.getResources().getColor(R.color.material_green_500))
                }
                timeLineModel.entityStatus.toInt() == -1 -> {   //Adjourned
                    setMarker(holder, R.drawable.ic_marker_active, R.color.material_blue_500)
                    holder.status.setBackgroundColor(holder.status.getResources().getColor(R.color.material_blue_500))
                }
                timeLineModel.entityStatus.toInt() == -3 -> {   //Canceled
                    setMarker(holder, R.drawable.ic_marker_inactive, R.color.material_pink_500)
                    holder.status.setBackgroundColor(holder.status.getResources().getColor(R.color.material_pink_500))
                }
                timeLineModel.entityStatus.toInt() == 0 -> {    //Not started
                    setMarker(holder, R.drawable.ic_marker_inactive, R.color.material_purple_500)
                    holder.status.setBackgroundColor(holder.status.getResources().getColor(R.color.material_purple_500))
                }
                timeLineModel.entityStatus.toInt() == 2 -> {    //Suspended
                    setMarker(holder, R.drawable.ic_marker_inactive, R.color.material_yellow_500)
                    holder.status.setBackgroundColor(holder.status.getResources().getColor(R.color.material_yellow_500))
                }
                timeLineModel.entityStatus.toInt() == 101 -> {    //In Camera
                    setMarker(holder, R.drawable.ic_marker_inactive, R.color.material_red_500)
                    holder.status.setBackgroundColor(holder.status.getResources().getColor(R.color.material_red_500))
                }
                timeLineModel.entityStatus.toInt() == -999 -> {
                    holder.status.visibility = View.GONE
                    setMarker(holder, R.drawable.ic_marker_inactive, R.color.material_red_500)
                }
                else -> {
                    setMarker(holder, R.drawable.ic_marker, R.color.material_grey_500)
                }
            }
        }else{
            holder.status.visibility = View.GONE
            setMarker(holder, R.drawable.ic_marker, R.color.colorPrimary)
        }

        holder.message.text = timeLineModel.title
        holder.card.setOnClickListener { v: View? ->
            Log.d("TimeLineClick",timeLineModel.title)
            if(ApiService.isNetworkAvailable(activity)){
                if(ApiService.checkWifiOnAndConnected(activity)){
                    if(timeLineModel.entityStatus.toInt() != -999){
                        if(timeLineModel.streams.size > 0){
                            var intent = Intent(holder.timeline.context, VideoPlayerActivity::class.java).apply {
                                putExtra(EXTRA_MESSAGE, timeLineModel)
                            }
                            holder.timeline.context.startActivity(intent)
                        }else{
                            holder.progressbar.visibility = View.VISIBLE
                        }
                    }
                }else{
                    var builder = AlertDialog.Builder(activity)
                    builder.apply {
                        setPositiveButton(R.string.ok_continue,
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss()
                                if(timeLineModel.entityStatus.toInt() != -999){
                                    if(timeLineModel.streams.size > 0){
                                        var intent = Intent(holder.timeline.context, VideoPlayerActivity::class.java).apply {
                                            putExtra(EXTRA_MESSAGE, timeLineModel)
                                        }
                                        holder.timeline.context.startActivity(intent)
                                    }else{
                                        holder.progressbar.visibility = View.VISIBLE
                                    }
                                }
                            })
                        setNegativeButton(R.string.cancel,
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss()
                            })
                    }
                    builder.setTitle("No WiFi Available")
                    builder.setMessage("Currently using mobile network,continuing to play will consume traffic!")
                    var dialog = builder.create()
                    dialog.show()
                }
            }else{
                var dialog = NotifyDialog()
                dialog.title = "Network Error"
                dialog.message = "No internet, please check your network settings, and try again."
                dialog.show(activity.supportFragmentManager,"networkError")
            }
        }
    }

    private fun setMarker(holder: TimeLineViewHolder, drawableResId: Int, colorFilter: Int) {
        holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, drawableResId, ContextCompat.getColor(holder.itemView.context, colorFilter))
    }

    override fun getItemCount() = mFeedList.size

    inner class TimeLineViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        val date = itemView.text_timeline_date
        val message = itemView.text_timeline_title
        val timeline = itemView.timeline
        var card = itemView.timeline_card
        var desc = card.text_timeline_desc
        var status = card.text_timeline_status
        var progressbar = card.meeting_progressBar

        init {
            timeline.initLine(viewType)
        }
    }

}
