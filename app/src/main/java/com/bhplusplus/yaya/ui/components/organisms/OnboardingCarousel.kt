package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.bhplusplus.yaya.ui.components.molecules.OnboardingSlide
import com.bhplusplus.yaya.ui.components.molecules.PageIndicator

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun OnboardingCarousel(
    pages: List<OnboardingPage>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val page = pages[pageIndex]
            OnboardingSlide(
                title = page.title,
                description = page.description,
                icon = page.icon
            )
        }

        PageIndicator(
            pageCount = pages.size,
            currentPage = pagerState.currentPage
        )
    }
}
