package app.demo.arcgisandroidapp.simpleEsriGraphics.formScreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import app.demo.arcgisandroidapp.room.GraphicDBModel
import app.demo.arcgisandroidapp.room.RoomDB
import app.demo.arcgisandroidapp.simpleEsriGraphics.activity.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class FormScreenViewModel : ViewModel() {

    val name: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val category: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val subCategory: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val tag: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val description: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    private val dao = RoomDB.getDB()!!.graphicDao()

    fun save(
        sharedViewModel: SharedViewModel,
        navController: NavController
    ) = viewModelScope.launch(Dispatchers.IO) {

        val model = GraphicDBModel(geometry = sharedViewModel.geometryJsonString)
        model.geometry = sharedViewModel.geometryJsonString
        model.name = name.value
        model.category = category.value
        model.subCategory = subCategory.value
        model.tag = tag.value
        model.description = description.value
        model.color = ""
        model.geometry = sharedViewModel.geometryJsonString
        model.attributes = JSONObject(HashMap<String, Any>() as Map<String, Any>).toString()
        model.meta = ""
        model.createdOn = System.currentTimeMillis().toString()
        model.lastUpdated = System.currentTimeMillis().toString()
        dao.insert(model)
        dao.getAll().forEach(::log)
//        navController.navigate(R.id.action_FormFragment_to_MapFragment)
        navController.popBackStack()
    }

    private fun log(model: GraphicDBModel) {
        Log.d("id", "${model.id}")
        Log.d("name", "${model.name}")
        Log.d("geometry", "${model.geometry}")
        Log.d("attributes", "${model.attributes}")
    }

}
