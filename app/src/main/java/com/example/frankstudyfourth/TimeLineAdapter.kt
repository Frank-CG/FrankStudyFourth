package com.example.frankstudyfourth

import android.content.Intent
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frankstudyfourth.model.MeetingModel
import com.example.frankstudyfourth.utils.DateTimeUtils
import com.example.frankstudyfourth.utils.VectorDrawableUtils

import com.github.vipulasri.timelineview.TimelineView

import kotlinx.android.synthetic.main.item_timeline.view.*

/**
 * Created by Vipul Asri on 13-12-2018.
 */

class TimeLineAdapter(private val mFeedList: List<MeetingModel>) : RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val  layoutInflater = LayoutInflater.from(parent.context)
        return TimeLineViewHolder(layoutInflater.inflate(R.layout.item_timeline, parent, false), viewType)
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
            if(timeLineModel.entityStatus.toInt() != -999){
                val intent = Intent(holder.timeline.context, VideoActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, timeLineModel)
                }
                holder.timeline.context.startActivity(intent)
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

        init {
            timeline.initLine(viewType)
        }
    }

}
