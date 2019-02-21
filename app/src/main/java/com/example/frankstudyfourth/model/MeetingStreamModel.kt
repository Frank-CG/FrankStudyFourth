package com.example.frankstudyfourth.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Vipul Asri on 05-12-2015.
 */
@Parcelize
class MeetingStreamModel(
    var globalEssenceFormatId: String,
    var isLive: String,
    var enabled: String,
    var audioOnly: String,
    var videoIndex: String,
    var audioIndex: String,
    var streamFormatId: String,
    var url: String,
    var lang: String,
    var streamAssemblerList: String,
    var preRoll: String,
    var duration: String,
    var id: String,
    var tag: String
): Parcelable