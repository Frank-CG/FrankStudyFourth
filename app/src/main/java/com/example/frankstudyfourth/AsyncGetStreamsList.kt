package com.example.frankstudyfourth

import android.os.AsyncTask
import com.example.frankstudyfourth.Protocols.ApiService
import com.example.frankstudyfourth.model.MeetingStreamModel
import com.google.android.exoplayer2.util.Log
import java.util.*

class AsyncGetStreamsList(private var activity: MainActivity) : AsyncTask<TimeLineAdapter, String, Boolean>()  {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: TimeLineAdapter): Boolean {
        var result = true
        var tlAd = params[0]
        var meetings = tlAd.getMeetingList()

        var showLanguage = Locale.getDefault().isO3Language.substring(0,2)
        if(showLanguage != "en" && showLanguage != "fr"){
            showLanguage = "en"
        }

        for(i in 0 until meetings.size){
            var meetingId = meetings[i].id
            var apiResult = ApiService.GetStreamData(meetingId,showLanguage)//.getJSONArray("Streams")
            var responseCode = apiResult.getInt("responseCode")
            if(responseCode == 200){
                var mStreamList = ArrayList<MeetingStreamModel>()
                var flag = true
                var streamArray = apiResult.getJSONArray("Streams")
                if(streamArray.length()>0){
                    for(j in 0 until streamArray.length()){
                        var stream  = streamArray.getJSONObject(j)
                        mStreamList.add(
                            MeetingStreamModel(
                                stream.getString("GlobalEssenceFormatId"),
                                stream.getString("IsLive"),
                                stream.getString("Enabled"),
                                stream.getString("AudioOnly"),
                                stream.getString("VideoIndex"),
                                stream.getString("AudioIndex"),
                                stream.getString("StreamFormatId"),
                                stream.getString("Url"),
                                stream.getString("Lang"),
                                stream.getString("StreamAssemblerList"),
                                stream.getString("PreRoll"),
                                stream.getString("Duration"),
                                stream.getString("Id"),
                                stream.getString("Tag")
                            )
                        )
                        if(stream.getString("AudioOnly") == "false"){
                            flag = false
                        }
                    }
                }else{
                    flag = false
                }
                meetings[i].audioOnly = flag
                meetings[i].streams = mStreamList
            }
            Log.d("AsyncGetStreamsList","responseCode=$responseCode")
        }
        return result
    }

    override fun onPostExecute(rstFlag: Boolean) {

    }
}