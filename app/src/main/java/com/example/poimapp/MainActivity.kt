package com.example.poimapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.poi.presentation.PoiMap
import com.example.poi.presentation.PoiMapViewModel
import com.example.poimapp.ui.theme.PoimAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val poiViewModel: PoiMapViewModel by viewModel<PoiMapViewModel>()


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PoimAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val poiList by poiViewModel.mapStateFlow.collectAsStateWithLifecycle()
                    val isLoading by poiViewModel.isLoadingFlow.collectAsStateWithLifecycle()
                    PoiMap(
                        Modifier.fillMaxSize(),
                        poiList,
                        isLoading
                    ) {
                        poiViewModel.updateBoundingBox(it)
                    }
                }
            }
        }
    }
}