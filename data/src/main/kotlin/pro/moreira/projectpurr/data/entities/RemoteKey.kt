package pro.moreira.projectpurr.data.entities

import androidx.room.Entity

@Entity(tableName = "remote_keys", primaryKeys = ["label"])
data class RemoteKey(
    val label: String,
    val nextKey: Int?,
)