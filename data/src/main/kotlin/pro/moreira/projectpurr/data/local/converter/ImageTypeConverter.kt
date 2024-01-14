package pro.moreira.projectpurr.data.local.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import pro.moreira.projectpurr.data.entities.Image

@ProvidedTypeConverter
class ImageTypeConverter(private val moshi: Moshi) {

    @TypeConverter
    fun toJson(image: Image?): String? = moshi.adapter(Image::class.java).toJson(image)

    @TypeConverter
    fun fromJson(json: String?): Image? =
        json?.let { moshi.adapter(Image::class.java).fromJson(it) }
}