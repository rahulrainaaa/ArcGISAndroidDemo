package app.demo.arcgisandroidapp.simpleEsriGraphics.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.demo.arcgisandroidapp.databinding.ActivityEsriGraphicsBinding
import app.demo.arcgisandroidapp.room.RoomDB
import kotlinx.android.synthetic.main.activity_esri_graphics.*

class EsriGraphicsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityEsriGraphicsBinding.inflate(layoutInflater).root)
        setSupportActionBar(toolbar)
        RoomDB.initDB(this)
    }

}
