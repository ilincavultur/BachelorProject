package com.example.digitaldiaryba.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.digitaldiaryba.ui.saved_presentations.PresentationListItem
import com.example.digitaldiaryba.util.enums.EMediaType

@Entity(
    tableName = "media_object_table",
    foreignKeys = [
        ForeignKey(
            entity = AlbumObject::class,
            parentColumns = ["albumId"],
            childColumns = ["album_object_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
open class MediaObject(
    @PrimaryKey(autoGenerate = true)
    var mediaId: Int?,
    @ColumnInfo(name = "album_object_id", index = true)
    open var albumId: Int,
    open var type: EMediaType,
    open var name: String?,
    open var description: String?,
    open var date: String?,
    open var time: String? = System.currentTimeMillis().toString(),
    open var locationString: String?,
    open var decodedUri: String,
    open var uri: String,
    open var gsUri: String,
    open var filepath: String

) {

}
