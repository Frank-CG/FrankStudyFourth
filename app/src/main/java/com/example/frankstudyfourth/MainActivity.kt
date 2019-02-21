package com.example.frankstudyfourth

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.example.frankstudyfourth.Protocols.ApiService
import com.example.frankstudyfourth.model.MeetingModel
import com.example.frankstudyfourth.model.MeetingStreamModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.app_bar.*
import org.json.JSONObject
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val tag: String = "MainActivity"

    private lateinit var picker: DatePickerDialog
    private lateinit var mAdapter: TimeLineAdapter
    private val mDataList = ArrayList<MeetingModel>()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mTitle: TextView
    private lateinit var mInternetErrTv: TextView
    private lateinit var mNetworkLoseTv: TextView
    private lateinit var mProgressBar: ProgressBar

    private lateinit var selectedDate: Calendar

    private var showlanguage:String = "en";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(app_bar as Toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false);
        mTitle = toolbar_title as TextView
        mInternetErrTv = internet_error_retry
        mNetworkLoseTv = network_lose_retry
        mProgressBar = MyprogressBar

        selectedDate = Calendar.getInstance()

        showlanguage = Locale.getDefault().isO3Language.substring(0,2)
        if(showlanguage != "en" && showlanguage != "fr"){
            showlanguage = "en"
        }

        val cldr = Calendar.getInstance()
        val day = cldr.get(Calendar.DAY_OF_MONTH)
        val month = cldr.get(Calendar.MONTH)
        val year = cldr.get(Calendar.YEAR)
        picker = DatePickerDialog(this@MainActivity,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                println(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
            },
            year,
            month,
            day
        )


        picker.datePicker.init(year, month, day,
            DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                picker.getButton(DatePickerDialog.BUTTON_POSITIVE).visibility = View.GONE;
                picker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "CANCEL", picker)
                picker.hide()

                val startDate = Calendar.getInstance()
                startDate.set(Calendar.YEAR, year)
                startDate.set(Calendar.MONTH, monthOfYear)
                startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                selectedDate = startDate
                var endDate = startDate.clone() as Calendar
                endDate.add(Calendar.DATE,1)
                val formatter = SimpleDateFormat("yyyyMMdd")

                var titleFormatter = SimpleDateFormat("yyyy-MM-dd")
                mTitle.text = titleFormatter.format(startDate.time)
                AsyncTaskExample(this).execute(formatter.format(startDate.time),formatter.format(endDate.time))
            })

        if(ApiService.isNetworkAvailable(this)){
            setDataListItems()
        }else{
            mInternetErrTv.visibility = View.GONE
            mNetworkLoseTv.visibility = View.VISIBLE
        }

        //initRecyclerView()

        println("Default Language:"+ Locale.getDefault().displayLanguage)
        println("Default Language:"+ Locale.getDefault().isO3Language)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.appbar_menu, menu)

        return true
    }

    fun calendarShow(item: MenuItem){
        println(item.title)
        picker.show()
    }

    private fun setDataListItems() {
        var startDate = selectedDate
        var endDate = startDate.clone() as Calendar
        endDate.add(Calendar.DATE,1)
        var formatter = SimpleDateFormat("yyyyMMdd")

        var titleFormatter = SimpleDateFormat("yyyy-MM-dd")
        mTitle.text = titleFormatter.format(startDate.time)

        AsyncTaskExample(this@MainActivity).execute(formatter.format(startDate.time),formatter.format(endDate.time))
    }

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = mLayoutManager
        mAdapter = TimeLineAdapter(mDataList)
        recyclerView.adapter = mAdapter
        //var el = ApiService.GetEventList("20190210", "20190211")
        //Log.d("MainActivity",el.toString());
    }

    fun networkRetry(v: View){
        if(ApiService.isNetworkAvailable(this)){
            setDataListItems()
        }else{
            mInternetErrTv.visibility = View.GONE
            mNetworkLoseTv.visibility = View.VISIBLE
        }
    }

    @SuppressLint("StaticFieldLeak")
    class AsyncTaskExample(private var activity: MainActivity?) : AsyncTask<String, String, JSONObject>() {

        override fun onPreExecute() {
            super.onPreExecute()
            var mDataList = ArrayList<MeetingModel>()
            var mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            activity?.recyclerView?.layoutManager = mLayoutManager
            var mAdapter = TimeLineAdapter(mDataList)
            activity?.recyclerView?.adapter = mAdapter
            activity!!.mInternetErrTv.visibility = View.GONE
            activity!!.mNetworkLoseTv.visibility = View.GONE
            activity!!.mProgressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String?): JSONObject {
            var jsonResult = JSONObject()
            var mMeetingList = ArrayList<MeetingModel>()

            var jsonMeetingList = ApiService.GetEventList(params[0],params[1],activity!!.showlanguage)

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
            activity!!.mProgressBar.visibility = View.INVISIBLE
            if(rstCode == 200){
                var result:ArrayList<MeetingModel> = jsonResult!!.get("jsonMeeting") as ArrayList<MeetingModel>

                if (result == null) {
                    Log.d(activity?.tag,"Network Error!")
                } else {
                    if(result.size == 0){
                        Log.d(activity?.tag,"No Meetings!")
                        var desc = ""
                        if(activity!!.showlanguage == "fr"){
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
                    activity?.recyclerView?.layoutManager = mLayoutManager
                    var mAdapter = TimeLineAdapter(result)
                    activity?.recyclerView?.adapter = mAdapter
                }
            } else if(rstCode == -1){
                activity!!.mInternetErrTv.visibility = View.VISIBLE
            } else {
                activity!!.mInternetErrTv.visibility = View.VISIBLE
            }
        }
    }
}
