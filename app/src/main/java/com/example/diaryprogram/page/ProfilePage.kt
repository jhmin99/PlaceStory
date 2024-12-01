package com.example.diaryprogram.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.diaryprogram.R
import com.example.diaryprogram.api.ApiClient.apiService
import com.example.diaryprogram.api.ApiService
import com.example.diaryprogram.api.UserApi.loadUserProfile
import com.example.diaryprogram.appbar.AppBar
import com.example.diaryprogram.data.UserProfileResponseDto

// 프론트 완료
@Composable
fun ProfilePage(navHostController: NavHostController, userId: Long) {
    var userProfile by remember { mutableStateOf<UserProfileResponseDto?>(null) }
    val customfont = FontFamily(Font(R.font.nanumbarunpenr))

    DisposableEffect(userId) {
        loadUserProfile(apiService, userId) { profile ->
            userProfile = profile
        }
        onDispose { }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.linearGradient(
                colors = listOf(Color(0xFF070301), Color(0xFF886B5F)),
                start = Offset(0f, 0f),
                end = Offset(0f, 3000f)
            ) // 그라데이션 세팅
        )
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { navHostController.popBackStack() },
                modifier = Modifier.size(50.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.whiteback),
                    contentDescription = "백버튼",
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(modifier = Modifier.width(48.dp)) // 아이콘과 텍스트 간 여백 추가
            Text(
                text = "PLACE STORY",
                color = Color.White,
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.nanumbarunpenb))
            )
        }
    }
        Column (modifier = Modifier.padding(30.dp)) {

            Spacer(modifier = Modifier.height(100.dp))

            Box(modifier = Modifier
                .width(361.dp)
                .height(443.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(color = colorResource(id = R.color.light_daisy))
                .align(Alignment.CenterHorizontally)
            ) {
                Column(modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row (modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center){
                        userProfile?.profileImage?.let { image ->
                            Image(
                                painter = rememberAsyncImagePainter(image.url),
                                contentDescription = "User Profile Image",
                                modifier = Modifier.size(100.dp)
                                    .clip(CircleShape)
                            )
                        } ?: Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Default Profile Icon",
                            modifier = Modifier.size(100.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    userProfile?.let {
                        Text(
                            text = it.name,
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.nanumbarunpenb)),
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(
                        color = Color.White,
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Row {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .width(70.dp)
                                .height(70.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color = colorResource(id = R.color.box_daisy))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.heart),
                                    contentDescription = "총 좋아요",
                                    modifier = Modifier.size(40.dp)
                                )

                                Text(
                                    text = "${userProfile?.totalLikesCount}", // api 연동 필요
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontFamily = customfont
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .width(70.dp)
                                .height(70.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color = colorResource(id = R.color.box_daisy))
                                .clickable {
                                    navHostController.navigate("browseMine")
                                }
                        )
                        {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "작성한 일기", // api 연동 필요
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontFamily = customfont
                                )

                                Text(
                                    text = "${userProfile?.totalDiaryCount}", // api 연동 필요
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontFamily = customfont
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .width(70.dp)
                                .height(70.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(color = colorResource(id = R.color.box_daisy))
                                .clickable {
                                    navHostController.navigate("following")
                                }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center, // 수직 중앙 정렬
                                horizontalAlignment = Alignment.CenterHorizontally // 수평 중앙 정렬
                            ) {
                                Text(
                                    text = "팔로잉", // API 연동 필요
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontFamily = customfont
                                )
                                Text(
                                    text = "${userProfile?.totalFollowCount}", // API 연동 필요
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontFamily = customfont
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(onClick = {navHostController.navigate("setting")},
                        modifier = Modifier.width(300.dp).height(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.box_daisy) // 버튼 배경색 설정
                        )
                        ) {
                        Text(text = "프로필 편집")
                    }
                    Spacer(modifier = Modifier.height(15.dp))

                    Button(onClick = {navHostController.navigate("subscribe")},
                        modifier = Modifier.width(300.dp).height(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.box_daisy) // 버튼 배경색 설정
                        )
                    ){
                        Text(text = "구독 관리")
                    }
                }
            }
        }

        AppBar(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 30.dp),
            navHostController = navHostController,
            option=5
        )
    }
}

