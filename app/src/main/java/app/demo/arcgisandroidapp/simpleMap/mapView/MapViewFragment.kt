package app.demo.arcgisandroidapp.simpleMap.mapView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.demo.arcgisandroidapp.databinding.FragMapViewBinding
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.LocationDisplay

class MapViewFragment : Fragment() {

    private lateinit var viewModel: MapViewViewModel
    private lateinit var viewBinding: FragMapViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MapViewViewModel::class.java)
        viewBinding = FragMapViewBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.mapView.map =
            ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16)
        viewBinding.mapView.isAttributionTextVisible = false
        viewBinding.mapView.onTouchListener =
            object : DefaultMapViewOnTouchListener(activity, viewBinding.mapView) {

                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    Toast.makeText(activity, "Map Single Tap Event", Toast.LENGTH_SHORT).show()
                    return super.onSingleTapConfirmed(e)
                }

                override fun onLongPress(e: MotionEvent?) {
                    super.onLongPress(e)
                    Toast.makeText(activity, "Map Long Tap Event", Toast.LENGTH_SHORT).show()
                }

            }
    }

    override fun onResume() {
        super.onResume()
        viewBinding.mapView.locationDisplay?.startAsync()
        viewBinding.mapView.locationDisplay?.autoPanMode = LocationDisplay.AutoPanMode.NAVIGATION
    }

    override fun onPause() {
        super.onPause()
        viewBinding.mapView.locationDisplay?.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding.mapView.dispose()
    }
}
