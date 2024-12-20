package com.example.diaryprogram.navi

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.diaryprogram.page.BrowseFollowDiaryPage
import com.example.diaryprogram.page.BrowseMineDiaryPage
import com.example.diaryprogram.page.BrowsePublicDiaryPage
import com.example.diaryprogram.page.BrowseUserDiaryPage
import com.example.diaryprogram.page.DiaryPage
import com.example.diaryprogram.page.EditDiary
import com.example.diaryprogram.page.FollowPage
import com.example.diaryprogram.page.LoadingPage
import com.example.diaryprogram.page.LoginPage
import com.example.diaryprogram.page.MainPage
import com.example.diaryprogram.page.MapPage
import com.example.diaryprogram.page.MyDiaryPage
import com.example.diaryprogram.page.OtherProfilePage
import com.example.diaryprogram.page.ProfilePage
import com.example.diaryprogram.page.SettingPage
import com.example.diaryprogram.page.SubscribePage
import com.example.diaryprogram.page.WritePage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

/**
 *  앱의 전반적인 흐름 통제함
 */
@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    var currentLocation by remember {
        mutableStateOf(LatLng(0.0, 0.0))
    }
    val userId: Long = 3L

    NavHost(navController = navController, startDestination = "login") {
        //로그인 페이지
        composable(route = "login") {
            LoginPage(navController)
        }

        //메인 페이지
        composable(route = "main") {
            var isLocationLoaded by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                getUserLocation(
                    context,
                    onLocationReceived = { location ->
                        currentLocation = LatLng(location.latitude, location.longitude)
                        isLocationLoaded = true // 로딩 완료
                    },
                    onError = {
                        Log.e("MainScreen", "Failed to fetch current location.")
                        isLocationLoaded = false // 에러 발생 시에도 로딩 상태 종료
                    }
                )
            }

            if (isLocationLoaded){
                MainPage(navController,currentLocation)
            }
            else{
                LoadingPage()
            }
        }

        //지도 페이지
        composable(route = "map") {
            MapPage(navController, initialPosition = currentLocation, userId)
        }

        //설정 페이지
        composable(route="profile") {
            ProfilePage(navController,userId)
        }

        //일기 작성 페이지
        composable(route="write") {
            WritePage(navController,currentLocation, userId)
        }

        // 일기 조회 페이지
        composable(route = "browseMine") {
            BrowseMineDiaryPage(navController, userId)
        }

        composable(route = "browseUserDiaries/{userId}") { backStackEntry ->
            val otheruserId = backStackEntry.arguments?.getString("userId")?.toLongOrNull()
            if (otheruserId != null) {
                BrowseUserDiaryPage(navController, otheruserId)
            }
        }

        composable(route = "mydiary/{diaryId}") { backStackEntry ->
            val diaryId = backStackEntry.arguments?.getString("diaryId")?.toLongOrNull()
            if (diaryId != null) {
                MyDiaryPage(navController, userId, diaryId,0)
            } else {
                Log.e("NavigationError", "diaryId is null or invalid")
            }
        }


        composable(
            route = "geodiary/{diaryId}",
            arguments = listOf(navArgument("diaryId") { type = NavType.LongType }) // diaryId를 Long으로 처리
        ) { backStackEntry ->
            val diaryId = backStackEntry.arguments?.getLong("diaryId")
            if (diaryId != null) {
                MyDiaryPage(navController, userId, diaryId,1)
            } else {
                Log.e("NavigationError", "diaryId is null or invalid")
            }
        }

        composable(route = "editDiary/{diaryID}") {
            backStackEntry ->
            val diaryId = backStackEntry.arguments?.getString("diaryID")?.toLongOrNull()
            if (diaryId!=null){
                EditDiary(navController,userId,diaryId)
            }
            else {
                Log.e("NavigationError", "diaryId is null or invalid: $diaryId")
            }
        }


        // 팔로우 개인 조회
        composable(route = "browseFollow") {
            BrowseFollowDiaryPage(navController,userId)
        }

        composable(route = "browsePublic") {
            BrowsePublicDiaryPage(navController,userId)
        }

        composable(route = "publicdiary/{diaryId}/{isClicked}") { backStackEntry ->
            val diaryId = backStackEntry.arguments?.getString("diaryId")?.toLongOrNull()
            val isLiked = backStackEntry.arguments?.getString("isClicked")?.toBoolean()
            if (diaryId != null) {
                if (isLiked != null) {
                    DiaryPage(navController, userId, diaryId,1,isLiked)
                }
            } else {
                Log.e("NavigationError", "diaryId is null or invalid")
            }
        }
        composable(route = "followdiary/{diaryId}/{isClicked}") { backStackEntry ->
            val diaryId = backStackEntry.arguments?.getString("diaryId")?.toLongOrNull()
            val isLiked = backStackEntry.arguments?.getString("isClicked")?.toBoolean()
            Log.d("Navigation", "diaryId: $diaryId, isLiked: $isLiked")
            if (diaryId != null) {
                if (isLiked != null) {
                    DiaryPage(navController, userId, diaryId,0,isLiked)
                }
            } else {
                Log.e("NavigationError", "diaryId is null or invalid")
            }
        }


        // 프로필 편집 페이지
        composable(route = "setting") {
            SettingPage(navController,userId)
        }
        // 구독 페이지
        composable(route = "subscribe") {
            SubscribePage(navController,userId)
        } // 구현 X

        composable(route = "following") {
            FollowPage(navController,userId)
        }

        composable("other_profile_page/{userId}") { backStackEntry ->
            val otheruserId = backStackEntry.arguments?.getString("userId")?.toLongOrNull()

            if (otheruserId != null) {
                OtherProfilePage(navController, userId, otheruserId)
            } else {
                // 예외 처리
                navController.navigate("main")
            }
        }


    }
}

@SuppressLint("MissingPermission")
fun getUserLocation(
    context: Context,
    onLocationReceived: (Location) -> Unit,
    onError: (() -> Unit)? = null
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY, // 정확도를 높이기 위해 HIGH_ACCURACY 사용
        null // Location settings 객체는 기본값으로 null 사용
    ).addOnSuccessListener { location: Location? ->
        if (location != null) {
            onLocationReceived(location)
        } else {
            Log.e("getUserLocation", "Location is null.")
            onError?.invoke()
        }
    }.addOnFailureListener { exception ->
        Log.e("getUserLocation", "Error fetching location: ${exception.message}")
        onError?.invoke()
    }
}



