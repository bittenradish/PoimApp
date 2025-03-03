package com.example.poimapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.poi.presentation.PoiMap
import com.example.poi.presentation.PoiMapViewModel
import com.example.poimapp.ui.poi.PoiDetails
import com.example.poimapp.ui.poi.PoiDetailsViewModel
import com.example.poimapp.ui.poi.model.PoiDetailsState
import com.example.poimapp.ui.theme.PoimAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val poiViewModel: PoiMapViewModel by viewModel<PoiMapViewModel>()
    private val poiDetailsViewModel: PoiDetailsViewModel by viewModel<PoiDetailsViewModel>()


    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PoimAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val poiList by poiViewModel.mapStateFlow.collectAsStateWithLifecycle()
                    val isLoading by poiViewModel.isLoadingFlow.collectAsStateWithLifecycle()

                    val poiDetailsState by poiDetailsViewModel.stateFlow.collectAsStateWithLifecycle()
                    val sheetState = rememberModalBottomSheetState(
                        skipPartiallyExpanded = true
                    )
                    var showBottomSheet by remember { mutableStateOf(false) }

                    PoiMap(
                        Modifier.fillMaxSize(),
                        poiList,
                        isLoading,
                        onItemClicked = {
                            showBottomSheet = true
                            poiDetailsViewModel.requestItem(it)
                        },
                        cameraPositionChanged = {
                            poiViewModel.updateBoundingBox(it)
                        }
                    )

                    if (showBottomSheet) {
                        if (poiDetailsState is PoiDetailsState.PoiDetailsItem) {
                            PoiDetails(
                                Modifier,
                                item = poiDetailsState as PoiDetailsState.PoiDetailsItem,
                                sheetState,
                                onDismissRequest = {
                                    showBottomSheet = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}