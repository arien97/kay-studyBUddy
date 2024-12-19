package com.example.studybuddy.features.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.studybuddy.R
import kotlinx.coroutines.launch

@Composable
fun OnboardingRoute(
    backAction: () -> Unit,
    nextAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val pageCount = 4
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        val pagerState = rememberPagerState(pageCount = { pageCount })
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text(
                    text = "Page: $page",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "Some content",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium
                )

            }
        }
        Row(Modifier.padding(horizontal = 16.dp)) {
            Indicators(pagerState, Modifier.weight(1f))
            Button(
                onClick = {
                    if (pagerState.currentPage == 0) {
                        backAction.invoke()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(56.dp),
                contentPadding = PaddingValues(0.dp),
                content = {
                    Image(
                        painterResource(R.drawable.ic_arrow_back),
                        contentDescription = null
                    )
                }
            )
            Button(
                onClick = {
                    if (pagerState.currentPage == pageCount - 1) {
                        nextAction.invoke()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(56.dp),
                contentPadding = PaddingValues(0.dp),
                content = {
                    Image(
                        painterResource(R.drawable.ic_arrow_forward),
                        contentDescription = null
                    )
                }
            )
        }
    }

}

@Composable
private fun Indicators(
    pagerState: androidx.compose.foundation.pager.PagerState,
    modifier: Modifier
) {
    Row(
        modifier
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color =
                if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(16.dp)
            )
        }
    }
}