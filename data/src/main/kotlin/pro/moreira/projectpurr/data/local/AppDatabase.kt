package pro.moreira.projectpurr.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.entities.RemoteKey
import pro.moreira.projectpurr.data.local.converter.ImageTypeConverter
import pro.moreira.projectpurr.data.local.dao.BreedDao
import pro.moreira.projectpurr.data.local.dao.RemoteKeyDao

@Database(
    version = 1,
    entities = [
        Breed::class,
        RemoteKey::class
    ],
    exportSchema = false,
)
@TypeConverters(ImageTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun breedDao(): BreedDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}