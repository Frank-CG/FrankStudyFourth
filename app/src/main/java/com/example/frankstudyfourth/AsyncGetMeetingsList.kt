package com.example.frankstudyfourth

import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.example.frankstudyfourth.Protocols.ApiService
import com.example.frankstudyfourth.model.MeetingModel
import com.example.frankstudyfourth.model.MeetingStreamModel
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class AsyncGetMeetingsList(private var activity: MainActivity) : AsyncTask<String, String, JSONObject>() {

    override fun onPreExecute() {
        super.onPreExecute()
        var mDataList = ArrayList<MeetingModel>()
        var mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        activity.recyclerView!!.layoutManager = mLayoutManager
        var mAdapter = TimeLineAdapter(mDataList,activity)
        activity.recyclerView!!.adapter = mAdapter
//        activity.mInternetErrTv.visibility = View.GONE
//        activity.mNetworkLoseTv.visibility = View.GONE
        activity.mProgressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg params: String?): JSONObject {
        var jsonResult = JSONObject()
        var mMeetingList = ArrayList<MeetingModel>()

        var jsonMeetingList = ApiService.GetEventList(params[0],params[1], activity.showlanguage)

        var resultCode:Int = jsonMeetingList.get("responseCode") as Int
        var resultMessage:String = jsonMeetingList.get("responseMessage") as String
        jsonResult.put("resultCode",resultCode)
        jsonResult.put("resultMessage",resultMessage)

        if(resultCode == 200){
            var jsonMeetingArray = jsonMeetingList?.getJSONArray("ContentEntityDatas")

            if(jsonMeetingArray!!.length() > 0){
                for (i in 0..(jsonMeetingArray.length() -1)){
                    var jsonMeeting = jsonMeetingArray.getJSONObject(i)

                    mMeetingList.add(
                        MeetingModel(jsonMeeting.getString("Title"),
                            jsonMeeting.getString("IconUri"),
                            jsonMeeting.getString("EntityStatus"),
                            jsonMeeting.getString("EntityStatusDesc"),
                            jsonMeeting.getString("Location"),
                            jsonMeeting.getString("Description"),
                            jsonMeeting.getString("ThumbnailUri"),
                            jsonMeeting.getString("ScheduledStart"),
                            jsonMeeting.getString("ScheduledEnd"),
                            jsonMeeting.getString("HasArchiveStream"),
                            jsonMeeting.getString("ActualStart"),
                            jsonMeeting.getString("ActualEnd"),
                            jsonMeeting.getString("LastModifiedTime"),
                            jsonMeeting.getString("CommitteeId"),
                            jsonMeeting.getString("VenueId"),
                            jsonMeeting.getString("AssemblyProgress"),
                            jsonMeeting.getString("AssemblyStatus"),
                            jsonMeeting.getString("ForeignKey"),
                            jsonMeeting.getString("Id"),
                            jsonMeeting.getString("Tag"),
                            ArrayList<MeetingStreamModel>(),
                            false
                        )
                    )
                }
            }
        }
        jsonResult.put("jsonMeeting",mMeetingList)
        return jsonResult
    }

    override fun onPostExecute(jsonResult: JSONObject?) {
        super.onPostExecute(jsonResult)
        var rstCode:Int = jsonResult!!.get("resultCode") as Int
        println("rstCode=$rstCode")
        activity.mProgressBar.visibility = View.INVISIBLE
        if(rstCode == 200){
            var result:ArrayList<MeetingModel> = jsonResult!!.get("jsonMeeting") as ArrayList<MeetingModel>

            if (result == null) {
                Log.d(activity.tag,"Network Error!")
            } else {
                var flag = true
                if(result.size == 0){
                    flag = false
                    Log.d(activity.tag,"No Meetings!")
                    var desc = ""
                    if(activity.showlanguage == "fr"){
                        desc = "Aucune réunion trouvée!"
                    }else{
                        desc = "No meetings found!"
                    }
                    result.add(MeetingModel(
                        desc,
                        "",
                        "-999",
                        desc,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ArrayList(),
                        false
                    ))
                }
                var mLayoutManager:LinearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                activity.recyclerView!!.layoutManager = mLayoutManager
                var mAdapter = TimeLineAdapter(result,activity)
                activity.recyclerView!!.adapter = mAdapter

                if(flag){
                    AsyncGetStreamsList(activity).execute(mAdapter);
                }

            }
        } else if(rstCode == -1){
            var dialog = NotifyDialog()
            dialog.title = "Network Error"
            dialog.message = "Looks like lost your internet connection. Please check your settings and try again."
            dialog.show(activity.supportFragmentManager,"networkError")
        } else {
//            activity.mInternetErrTv.visibility = View.VISIBLE
        }
    }
}