package com.bhplusplus.yaya.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bhplusplus.yaya.data.models.ServiceImage

@Composable
fun ServiceDetailGallery(
    images: List<ServiceImage>,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        if (images.isNotEmpty()) {
            LazyRow(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(images) { index, image ->
                    AsyncImage(
                        model = image.image_url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .fillMaxHeight()
                            .clickable { onImageClick(index) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
            // Overlay degradado
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                            startY = 600f
                        )
                    )
            )
            // Indicador de cantidad
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "1 / ${images.size}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        }
    }
}
