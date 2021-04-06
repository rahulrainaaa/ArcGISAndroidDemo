package app.demo.arcgisandroidapp.room

import androidx.lifecycle.LiveData

class GraphicRepository(private val dao: GraphicDao) {

    suspend fun insert(model: GraphicDBModel) {
        dao.insert(model)
    }

    suspend fun delete(id: Int) {

    }
}