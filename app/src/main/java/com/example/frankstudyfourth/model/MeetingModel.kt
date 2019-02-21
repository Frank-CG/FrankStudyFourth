package com.example.frankstudyfourth.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Vipul Asri on 05-12-2015.
 */
@Parcelize
class MeetingModel(
        var title: String,
        var iconUri: String,
        var entityStatus: String,
        var entityStatusDesc: String,
        var location: String,
        var description: String,
        var thumbnailUri: String,
        var scheduledStart: String,
        var scheduledEnd: String,
        var hasArchiveStream: String,
        var actualStart: String,
        var actualEnd: String,
        var lastModifiedTime: String,
        var committeeId: String,
        var venueId: String,
        var assemblyProgress: String,
        var assemblyStatus: String,
        var foreignKey: String,
        var id: String,
        var tag: String,
        var streams: ArrayList<MeetingStreamModel>,
        var audioOnly: Boolean
) : Parcelable
