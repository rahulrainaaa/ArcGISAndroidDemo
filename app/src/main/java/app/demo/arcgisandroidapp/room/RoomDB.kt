package app.demo.arcgisandroidapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GraphicDBModel::class], version = 1, exportSchema = false)
abstract class RoomDB : RoomDatabase() {

    abstract fun graphicDao(): GraphicDao

    companion object {

        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDB() = INSTANCE

        fun initDB(context: Context): RoomDB {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDB::class.java,
                    "app_database_room"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}