package com.example.poimapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.poi.presentation.PoiMap
import com.example.poimapp.ui.poi.PoiDetails
import kotlinx.serialization.Serializable

@Serializable
object MainMapRoute

@Serializable
data class PoiDetailsRoute(val idList: List<String>)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = MainMapRoute) {
        composable<MainMapRoute> {
            PoiMap(
                modifier = Modifier.fillMaxSize(),
                onItemsClicked = { poiList ->
                    navController.navigate(PoiDetailsRoute(poiList.map { it.id }))
                }
            )
        }


        composable<PoiDetailsRoute>(
            //TODO: move to dialogs later
//            dialogProperties = DialogProperties(
//                usePlatformDefaultWidth = false,
//                decorFitsSystemWindows = true,
//                securePolicy = SecureFlagPolicy.SecureOn
//            ),
        ) {
            val arg: PoiDetailsRoute = it.toRoute()
            PoiDetails(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .navigationBarsPadding().statusBarsPadding(),
                idList = arg.idList
            )
        }

    }
}