package app.demo.arcgisandroidapp.mainScreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.demo.arcgisandroidapp.R
import app.demo.arcgisandroidapp.simpleEsriGraphics.activity.EsriGraphicsActivity
import app.demo.arcgisandroidapp.simpleMap.SimpleMapActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val license = ArcGISRuntimeEnvironment.setLicense(getString(R.string.esri_license))
//        Log.d("ESRI LICENSE STATUS", license.licenseStatus.toString())
//        Log.d("EXTENSION STATUS", license.extensionsStatus.toString())
    }

    override fun onResume() {
        super.onResume()

        // App required permissions check.
        if (
            !hasPermission(Manifest.permission.INTERNET) ||
            !hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ||
            !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 111
            )
        }

        // GPS enable check.
        val locationServiceManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val gpsProvider = locationServiceManager.getProvider(LocationManager.GPS_PROVIDER)
//        val networkProvider = locationServiceManager.getProvider(LocationManager.NETWORK_PROVIDER)
        if (!locationServiceManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Turn Location ON", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun hasPermission(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    fun btnClickSimpleMapView(view: View) =
        startActivity(Intent(view.context, SimpleMapActivity::class.java))

    fun btnClickEsriGraphic(view: View) =
        startActivity(Intent(view.context, EsriGraphicsActivity::class.java))
}
