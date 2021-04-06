package app.demo.arcgisandroidapp.simpleEsriGraphics.formScreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.demo.arcgisandroidapp.R
import app.demo.arcgisandroidapp.databinding.FragmentFormBinding
import app.demo.arcgisandroidapp.simpleEsriGraphics.activity.SharedViewModel

class FormScreenFragment : Fragment() {

    private lateinit var viewModel: FormScreenViewModel
    private lateinit var binding: FragmentFormBinding
    private var sharedViewModel: SharedViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFormBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(FormScreenViewModel::class.java)
        sharedViewModel = activity?.let { ViewModelProvider(it).get(SharedViewModel::class.java) }
        binding.viewModel = viewModel
        binding.sharedViewModel = sharedViewModel
        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_FormFragment_to_MapFragment)
        }
        binding.btnSave.setOnClickListener {
            viewModel.save(sharedViewModel!!, findNavController())
        }
        Log.d("GeometryJsonString", sharedViewModel?.geometryJsonString)
        return binding.root
    }

}
