package app.demo.arcgisandroidapp.room

import androidx.room.*

@Dao
interface GraphicDao {

    @Delete
    fun delete(model: GraphicDBModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: GraphicDBModel)

    @Query("SELECT * from graphic_table")
    fun getAll(): List<GraphicDBModel>
}