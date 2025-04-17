package com.ghostdev.location.lostintravel.ui.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ghostdev.location.RecommendedPlacesQuery
import com.ghostdev.location.lostintravel.R
import com.ghostdev.location.lostintravel.ui.presentation.LoadingComponent
import com.ghostdev.location.lostintravel.utils.SetStatusBarColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeComponent(
    modifier: Modifier,
    viewmodel: HomeLogic = koinViewModel(),
    navigateToOnboarding: () -> Unit = {},
) {
    val state = viewmodel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewmodel.retrieveUserFullName(context)
        viewmodel.getRecommendedPlaces(context, false)
    }

    LaunchedEffect(state.value.isLoggedOut) {
        if (state.value.isLoggedOut) navigateToOnboarding()
    }

    HomeScreen(
        modifier = modifier,
        fullName = state.value.fullName,
        places = state.value.places,
        loading = state.value.isLoading,
        error = state.value.error,
        onLogoutClick = {
            viewmodel.logout(context)
        },
        onRefresh = {
            viewmodel.getRecommendedPlaces(context, true)
            println("Refreshing")
        },
        isRefreshing = state.value.isRefreshing
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    error: String? = null,
    fullName: String = "",
    places: List<RecommendedPlacesQuery.RecommendedPlace> = emptyList(),
    onLogoutClick: () -> Unit = {},
) {
    SetStatusBarColor(color = Color.White, darkIcons = true)
    val scrollState = rememberScrollState()
    val lazyListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(Color.White)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Header(
                    name = fullName,
                    onLogoutClick = onLogoutClick
                )

                Spacer(
                    modifier = Modifier
                        .height(48.dp)
                )

                Text(
                    text = "Frequently Visited",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!loading && !error.isNullOrEmpty()) {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    flingBehavior = rememberSnapFlingBehavior(
                        lazyListState = lazyListState,
                        snapPosition = SnapPosition.Center
                    )
                ) {
                    items(places.size) { index ->
                        val price = String.format("%.0f", places[index].price)
                        PlacesItem(
                            title = places[index].leadingDestinationTitle ?: "",
                            location = places[index].subDestinationTitle ?: "",
                            price = "$${price}",
                            desImageUrl = places[index].imageUrl ?: ""
                        )
                    }
                }
            }
        }

        if (loading && !isRefreshing) {
            LoadingComponent(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
@Preview
private fun Header(
    name: String = "",
    onLogoutClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val bgColor = remember(name) {
            listOf(
                Color(0xFF37474F),
                Color(0xFF263238),
                Color(0xFF3E2723),
                Color(0xFF1B1B1B),
                Color(0xFF004D40)
            ).random()
        }

        val firstLetter = name.firstOrNull()?.uppercase() ?: "?"

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
//                AsyncImage(
//                    model = profileImageUrl,
//                    placeholder = painterResource(R.drawable.ic_person),
//                    contentDescription = "Profile Image",
//                    modifier = Modifier
//                        .size(56.dp)
//                        .clip(CircleShape),
//                    contentScale = ContentScale.Crop
//                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstLetter,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Green, CircleShape)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Welcome $name,",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )

                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                )

                Text(
                    text = "Where do you want to go?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Thin,
                    color = Color.DarkGray
                )
            }
        }

        IconButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = Color(0x0D007AFF),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Logout",
                tint = Color.Black
            )
        }
    }
}

@Composable
@Preview
private fun PlacesItem(
    modifier: Modifier = Modifier,
    desImageUrl: String = "",
    title: String = "",
    location: String = "",
    price: String = ""
) {
    val cardHeight = 200.dp
    val imageHeightFraction = 2f / 3f

    Card(
        modifier = modifier
            .width(200.dp)
            .height(cardHeight)
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier) {
            AsyncImage(
                model = desImageUrl,
                placeholder = painterResource(R.drawable.placeholder_place),
                contentDescription = "$title image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(imageHeightFraction)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f - imageHeightFraction)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = price,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4299E1),
                        modifier = Modifier.wrapContentWidth()
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFFFB1616),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "/person",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }
        }
    }
}