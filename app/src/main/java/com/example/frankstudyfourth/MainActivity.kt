package com.example.frankstudyfourth

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import com.example.frankstudyfourth.Protocols.ApiService
import com.example.frankstudyfourth.model.MeetingModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.app_bar.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(),SwipeRefreshLayout.OnRefreshListener {

    val tag: String = "MainActivity"

    private lateinit var picker: DatePickerDialog
    private lateinit var mAdapter: TimeLineAdapter
    private val mDataList = ArrayList<MeetingModel>()

    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var mTitle: TextView
    lateinit var mProgressBar: ProgressBar
    lateinit var showLanguage:String

    private lateinit var selectedDate: Calendar


    private val mSwipeRefreshLayout: SwipeRefreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(app_bar as Toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false);
        mTitle = toolbar_title as TextView
        mProgressBar = main_progressBar

        selectedDate = Calendar.getInstance()
        mTitle.text = SimpleDateFormat("yyyy-MM-dd").format(selectedDate.time)



        showLanguage = Locale.getDefault().isO3Language.substring(0,2)
        if(showLanguage != "en" && showLanguage != "fr"){
            showLanguage = "en"
        }

        val cldr = Calendar.getInstance()
        val day = cldr.get(Calendar.DAY_OF_MONTH)
        val month = cldr.get(Calendar.MONTH)
        val year = cldr.get(Calendar.YEAR)
        picker = DatePickerDialog(this@MainActivity,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                println(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                picker.getButton(DatePickerDialog.BUTTON_POSITIVE).visibility = View.GONE;
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

                if(dayOfMonth == 10){
                    val intent = Intent(this@MainActivity, VideoPlayerActivity::class.java)
                    startActivity(intent)
                }else{
                    val startDate = Calendar.getInstance()
                    startDate.set(Calendar.YEAR, year)
                    startDate.set(Calendar.MONTH, monthOfYear)
                    startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    selectedDate = startDate
                    val formatter = SimpleDateFormat("yyyyMMdd")

                    var titleFormatter = SimpleDateFormat("yyyy-MM-dd")
                    mTitle.text = titleFormatter.format(startDate.time)
                    AsyncGetMeetingsList(this@MainActivity).execute(formatter.format(startDate.time),formatter.format(startDate.time))
                }
            })
        picker.hide()

        mTitle.setOnClickListener(View.OnClickListener { v: View? ->
            picker.show()
        })

        if(ApiService.isNetworkAvailable(this)){
            setDataListItems()
        }else{
            var dialog = NotifyDialog()
            dialog.title = "Network Error"
            dialog.message = "No internet, please check your network settings, and try again."
            dialog.show(supportFragmentManager,"networkError")
        }

        println("Default Language:"+ Locale.getDefault().displayLanguage)
        println("Default Language:"+ Locale.getDefault().isO3Language)

        prepareSwipeRefreshLayout()
    }

    private fun prepareSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        networkRetry(mSwipeRefreshLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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

        var taskGetMeetingsList = AsyncGetMeetingsList(this@MainActivity).execute(formatter.format(startDate.time),formatter.format(startDate.time))
    }

    private fun networkRetry(v: View){
        mSwipeRefreshLayout.isRefreshing = false
        if(ApiService.isNetworkAvailable(this)){
            setDataListItems()
        }else{
            var dialog = NotifyDialog()
            dialog.title = "Network Error"
            dialog.message = "No internet, please check your network settings, and try again."
            dialog.show(supportFragmentManager,"networkError")
        }
    }



//    private fun initRecyclerView() {
//        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        recyclerView.layoutManager = mLayoutManager
//        mAdapter = TimeLineAdapter(mDataList,this)
//        recyclerView.adapter = mAdapter
//    }
}
