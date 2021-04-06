package app.demo.arcgisandroidapp.simpleEsriGraphics.mapScreen

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.demo.arcgisandroidapp.R
import app.demo.arcgisandroidapp.databinding.FragmentMapBinding
import app.demo.arcgisandroidapp.room.RoomDB
import app.demo.arcgisandroidapp.simpleEsriGraphics.activity.SharedViewModel
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.*
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragment class to show mapView for sketch editor and graphics demo.
 */
class MapScreenFragment : Fragment() {

    private val TAG = "MapScreenFragment"
    private var sharedViewModel: SharedViewModel? = null
    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        sharedViewModel = activity?.let { ViewModelProvider(it).get(SharedViewModel::class.java) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val map = ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 16)
        binding.mapView.map = map
        binding.mapView.isAttributionTextVisible = false
        binding.mapView.graphicsOverlays.add(GraphicsOverlay())
        binding.mapView.sketchEditor = SketchEditor()
        binding.mapView.sketchEditor.sketchStyle = getSketchStyle()

        binding.mapView.map.addLoadStatusChangedListener {

            if (it.newLoadStatus == LoadStatus.LOADED) {
                binding.fab.show()
                loadGraphics()
                binding.mapView.onTouchListener =
                    object : DefaultMapViewOnTouchListener(activity, binding.mapView) {
                        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                            val screen = android.graphics.Point(e!!.x.toInt(), e.y.toInt())
                            val tap = binding.mapView.screenToLocation(screen)
                            selectGeometry(tap)
                            return true

                        }
                    }
            } else if (it.newLoadStatus == LoadStatus.FAILED_TO_LOAD)
                Snackbar.make(binding.mapView, "Map failed to load.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Finish") { activity?.finish() }.show()
        }

        fab.hide()
        fab.setOnClickListener { startSketchEditor() }

        BottomSheetBehavior.from(navigationView).state = BottomSheetBehavior.STATE_HIDDEN
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_menu_point -> {
                    binding.mapView.sketchEditor.sketchStyle =
                        getSketchStyle(SketchCreationMode.POINT)
                    binding.mapView.sketchEditor.start(SketchCreationMode.POINT)
                    Toast.makeText(activity, "Create Point", Toast.LENGTH_SHORT).show()
                }
                R.id.item_menu_multi_point -> {
                    binding.mapView.sketchEditor.sketchStyle =
                        getSketchStyle(SketchCreationMode.MULTIPOINT)
                    binding.mapView.sketchEditor.start(SketchCreationMode.MULTIPOINT)
                    Toast.makeText(activity, "Create MultiPoint", Toast.LENGTH_SHORT).show()
                }
                R.id.item_menu_polyline -> {
                    binding.mapView.sketchEditor.start(SketchCreationMode.POLYLINE)
                    binding.mapView.sketchEditor.sketchStyle =
                        getSketchStyle(SketchCreationMode.POLYLINE)
                    Toast.makeText(activity, "Create Polyline", Toast.LENGTH_SHORT).show()
                }
                R.id.item_menu_polygon -> {
                    binding.mapView.sketchEditor.start(SketchCreationMode.POLYGON)
                    binding.mapView.sketchEditor.sketchStyle =
                        getSketchStyle(SketchCreationMode.POLYGON)
                    Toast.makeText(activity, "Create Polygon", Toast.LENGTH_SHORT).show()
                }
                R.id.item_menu_free_line -> {
                    binding.mapView.sketchEditor.start(SketchCreationMode.FREEHAND_LINE)
                    binding.mapView.sketchEditor.sketchStyle =
                        getSketchStyle(SketchCreationMode.FREEHAND_LINE)
                    Toast.makeText(activity, "Create Free Hand Line", Toast.LENGTH_SHORT).show()
                }
                R.id.item_menu_free_polygon -> {
                    binding.mapView.sketchEditor.start(SketchCreationMode.FREEHAND_POLYGON)
                    binding.mapView.sketchEditor.sketchStyle =
                        getSketchStyle(SketchCreationMode.FREEHAND_POLYGON)
                    Toast.makeText(activity, "Create Free Hand Polygon", Toast.LENGTH_SHORT).show()
                }
            }
            binding.mapView.sketchEditor.clearGeometry()
            bottomAppBar.visibility = View.VISIBLE
            BottomSheetBehavior.from(navigationView).state = BottomSheetBehavior.STATE_COLLAPSED
            true
        }

        bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        bottomAppBar.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.item_menu_save -> {
                    saveGraphic()
                }
                R.id.item_menu_undo -> {
                    if (binding.mapView.sketchEditor.canUndo()) binding.mapView.sketchEditor.undo()
                }
                R.id.item_menu_redo -> {
                    if (binding.mapView.sketchEditor.canRedo()) binding.mapView.sketchEditor.redo()
                }
                R.id.item_menu_clear -> {
                    binding.mapView.sketchEditor.clearGeometry()
                }
                R.id.item_menu_close -> {
                    binding.mapView.sketchEditor.stop()
                    bottomAppBar.visibility = View.GONE
                    bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                }
                else -> {
                    Toast.makeText(activity, "Null", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.locationDisplay.startAsync()
        binding.mapView.locationDisplay.autoPanMode = LocationDisplay.AutoPanMode.NAVIGATION
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.locationDisplay.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.dispose()
    }

    private fun showForm() = findNavController().navigate(R.id.action_MapFragment_to_FormFragment)

    private fun startSketchEditor() {

        if (bottomAppBar.isVisible) {
            BottomSheetBehavior.from(navigationView).state = BottomSheetBehavior.STATE_HALF_EXPANDED
        } else {
            Toast.makeText(activity, "Create Point", Toast.LENGTH_SHORT).show()
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            binding.mapView.sketchEditor.start(SketchCreationMode.POINT)
            binding.mapView.sketchEditor.sketchStyle = getSketchStyle(SketchCreationMode.POINT)
        }
    }

    private fun loadGraphics() = CoroutineScope(Dispatchers.Main).launch {

        val list = withContext(Dispatchers.IO) { RoomDB.getDB()?.graphicDao()?.getAll()!! }
        list.forEach { model ->
            val geometry = GeometryEngine.simplify(Geometry.fromJson(model.geometry))
            Toast.makeText(activity, "Fetched = ${list.size}", Toast.LENGTH_SHORT).show()

            if (geometry.geometryType == GeometryType.POINT) {
                val marker = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 12f)
                val graphic = Graphic(geometry, HashMap<String, Any>(), marker)
                binding.mapView.graphicsOverlays[0].graphics.add(graphic)
            } else if (geometry.geometryType == GeometryType.MULTIPOINT) {
                val marker = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.DKGRAY, 10f)
                val graphic = Graphic(geometry, HashMap<String, Any>(), marker)
                binding.mapView.graphicsOverlays[0].graphics.add(graphic)
            } else if (geometry.geometryType == GeometryType.POLYLINE) {
                val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2f)
                val graphic = Graphic(geometry, HashMap<String, Any>(), lineSymbol)
                binding.mapView.graphicsOverlays[0].graphics.add(graphic)
            } else if (geometry.geometryType == GeometryType.POLYGON) {
                val lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GRAY, 1f)
                val fillSymbol = SimpleFillSymbol(
                    SimpleFillSymbol.Style.DIAGONAL_CROSS,
                    Color.argb(80, 211, 211, 211), lineSymbol
                )
                val graphic = Graphic(geometry, HashMap<String, Any>(), fillSymbol)
                binding.mapView.graphicsOverlays[0].graphics.add(graphic)
            }
        }
    }

    private fun saveGraphic() {

        if (!binding.mapView.sketchEditor.isSketchValid) {
            Toast.makeText(activity, "Draw valid sketch", Toast.LENGTH_SHORT).show()
            return
        }
        val geometry = GeometryEngine.simplify(binding.mapView.sketchEditor.geometry)
        sharedViewModel?.geometryJsonString = geometry.toJson()
        showForm()
    }

    private fun getSketchStyle(mode: SketchCreationMode = SketchCreationMode.POLYGON): SketchStyle {

        val style = SketchStyle()
        if (mode == SketchCreationMode.POINT) {
            style.vertexSymbol =
                SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 14f)
        } else if (mode == SketchCreationMode.MULTIPOINT) {
            style.vertexSymbol =
                SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.DKGRAY, 14f)
            style.selectedVertexSymbol =
                SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.DKGRAY, 14f)
            style.isShowNumbersForVertices = true
        } else {
            style.vertexSymbol =
                SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.DKGRAY, 12f)
            style.lineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 1f)
            style.fillSymbol =
                SimpleFillSymbol(
                    SimpleFillSymbol.Style.DIAGONAL_CROSS,
                    Color.argb(80, 211, 211, 211), style.lineSymbol
                )
            style.selectedVertexSymbol =
                SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GRAY, 12f)
            style.midVertexSymbol =
                SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.DKGRAY, 8f)
            style.isShowNumbersForVertices = true
        }
        return style
    }

    fun selectGeometry(tap: Point) {

        val buffer = GeometryEngine.bufferGeodetic(
            tap, 3.0, LinearUnit(LinearUnitId.METERS),
            1.0, GeodeticCurveType.GREAT_ELLIPTIC
        )
        val layer = binding.mapView.graphicsOverlays[0]

        for (graphic in layer.graphics) {
            if (GeometryEngine.intersects(graphic.geometry, buffer)) {
                graphic?.run {
                    val sketchEditor = binding.mapView.sketchEditor
                    when (this.geometry.geometryType) {
                        GeometryType.POINT ->
                            sketchEditor.start(this.geometry, SketchCreationMode.POINT)
                        GeometryType.MULTIPOINT ->
                            sketchEditor.start(this.geometry, SketchCreationMode.MULTIPOINT)
                        GeometryType.POLYLINE ->
                            sketchEditor.start(this.geometry, SketchCreationMode.POLYLINE)
                        GeometryType.POLYGON ->
                            sketchEditor.start(this.geometry, SketchCreationMode.POLYGON)
                    }
                    Toast.makeText(activity, "Tap to edit geometry", Toast.LENGTH_SHORT).show()
                    bottomAppBar.visibility = View.VISIBLE
                    bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                }
                break
            } else {
                binding.mapView.setViewpointCenterAsync(tap, 500.0)
            }
        }
    }
}
