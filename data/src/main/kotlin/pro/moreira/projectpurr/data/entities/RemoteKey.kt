package pro.moreira.projectpurr.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val id: Int,
    val label: String,
    val nextKey: Int?,
    val lastUpdate: Long,
)