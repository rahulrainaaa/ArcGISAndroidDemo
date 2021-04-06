package app.demo.arcgisandroidapp.simpleMap.sceneView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.demo.arcgisandroidapp.databinding.FragSceneViewBinding
import com.esri.arcgisruntime.mapping.ArcGISScene
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect
import com.esri.arcgisruntime.mapping.view.LightingMode

class SceneViewFragment : Fragment() {

    private lateinit var viewModel: SceneViewViewModel
    private lateinit var viewBinding: FragSceneViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SceneViewViewModel::class.java)
        viewBinding = FragSceneViewBinding.inflate(layoutInflater)

        val scene = ArcGISScene()
        scene.basemap = Basemap.createImagery()
        viewBinding.sceneView.scene = scene
        viewBinding.sceneView.isAttributionTextVisible = false

        viewBinding.sceneView.atmosphereEffect = AtmosphereEffect.REALISTIC
        viewBinding.sceneView.sunLighting = LightingMode.LIGHT_AND_SHADOWS

        return viewBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding.sceneView.dispose()
    }
}
