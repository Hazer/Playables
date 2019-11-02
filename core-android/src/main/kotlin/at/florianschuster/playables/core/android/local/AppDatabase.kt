package at.florianschuster.playables.core.android.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import at.florianschuster.playables.core.model.Platform

@Database(entities = [RoomLocalGameData::class], version = 1)
@TypeConverters(value = [PlatformsTypeConverter::class])
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun localGameDataDao(): RoomLocalGameDataDao
}

internal class PlatformsTypeConverter {
    private val separator = ","

    @TypeConverter
    fun toPlatforms(platforms: String): List<Platform> =
        platforms.split(separator).map(Platform::valueOf)

    @TypeConverter
    fun fromPlatforms(platforms: List<Platform>): String =
        platforms.joinToString(separator = separator, transform = Platform::name)
}