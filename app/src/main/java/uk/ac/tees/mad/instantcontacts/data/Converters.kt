package uk.ac.tees.mad.instantcontacts.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uk.ac.tees.mad.instantcontacts.domain.Call

class Converters {

    @TypeConverter
    fun fromCallList(callList: List<Call>?): String {
        val gson = Gson()
        return gson.toJson(callList)
    }

    @TypeConverter
    fun toCallList(callListString: String?): List<Call>? {
        if (callListString == null) return emptyList()
        val listType = object : TypeToken<List<Call>>() {}.type
        return Gson().fromJson(callListString, listType)
    }
}
