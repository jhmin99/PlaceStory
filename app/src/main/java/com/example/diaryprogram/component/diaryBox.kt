package com.example.diaryprogram.component

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.diaryprogram.R
import com.example.diaryprogram.api.ApiClient.apiService
import com.example.diaryprogram.api.DiaryApi.likeDiary
import com.example.diaryprogram.data.DiaryResponseDto
import com.example.diaryprogram.geo.getAddressFromLatLng
import com.example.diaryprogram.util.utils.base64ToImage


@Composable
fun DiaryBox(
    navController: NavHostController,
    userId: Long,
    diaryInfo: DiaryResponseDto,
    onDiaryClick: (Long) -> Unit,
    option: Int
) {
    val context = LocalContext.current
    var isClicked by remember { mutableStateOf(true) }
    var address by remember { mutableStateOf("...") }
    var isFollowing by remember { mutableStateOf(true) }

    LaunchedEffect(diaryInfo) {
        address = getAddressFromLatLng(
            context,
            diaryInfo.latitude,
            diaryInfo.longitude
        )

        isClicked=diaryInfo.isLiked
    }

    LaunchedEffect(isClicked) {

    }



    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .width(360.dp)
            .height(100.dp)
            .clickable(
                onClick = {
                    diaryInfo.diaryId.let { onDiaryClick(it) }
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(R.color.light_daisy))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            diaryInfo.profileImage?.let { image ->
                if (image.url == "default.jpg") {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Default Profile Icon",
                        modifier = Modifier.size(60.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (option == 0) {
                                    navController.navigate("other_profile_page/${diaryInfo.userId}")
                                } else if (option == 1) {
                                    isFollowing = false
                                    navController.navigate("other_profile_page/${diaryInfo.userId}")
                                }
                            }
                    )
                } else {
                    val bitmap = base64ToImage(image.url)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Diary Image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .clickable {
                                    navController.navigate("other_profile_page/${diaryInfo.userId}")
                                },
                            contentScale = ContentScale.Crop

                        )
                    }
                }
            } ?: Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Default Profile Icon",
                modifier = Modifier.size(60.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate("other_profile_page/${diaryInfo.userId}")
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = diaryInfo.diaryTitle,
                    fontFamily = FontFamily(Font(R.font.nanumbarunpenb)),
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(110.dp),
                    maxLines = 1
                )
                Text(
                    text = address,
                    fontFamily = FontFamily(Font(R.font.nanumbarunpenr)),
                    fontSize = 10.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(110.dp),
                    maxLines = 1
                )
                Text(
                    text = diaryInfo.date,
                    fontFamily = FontFamily(Font(R.font.nanumbarunpenr)),
                    fontSize = 10.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(110.dp),
                    maxLines = 1
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column {

                    if (isClicked) {
                        IconButton(
                            onClick = {
                                diaryInfo.diaryId?.let { diaryId ->
                                    likeDiary(
                                        apiService = apiService,
                                        userId = userId,
                                        diaryId = diaryId,
                                        onSuccess = {
                                            isClicked = false // 성공 시 좋아요 상태로 전환
                                        },
                                        onFailure = { throwable ->
                                            Toast.makeText(
                                                context,
                                                "좋아요 실패: ${throwable.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .size(50.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.heart),
                                contentDescription = "좋아요",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                diaryInfo.diaryId.let { diaryId ->
                                    likeDiary(
                                        apiService = apiService,
                                        userId = userId,
                                        diaryId = diaryId,
                                        onSuccess = {
                                            isClicked = true // 성공 시 좋아요 상태로 전환
                                        },
                                        onFailure = { throwable ->
                                            Toast.makeText(
                                                context,
                                                "좋아요 실패: ${throwable.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .size(50.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.emptyheart),
                                contentDescription = "좋아요",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }


                }
            }
        }
    }
}
