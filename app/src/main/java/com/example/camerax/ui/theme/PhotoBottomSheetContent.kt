package com.example.camerax.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun PhotoBottomSheetContent(
    bitmaps : List<Bitmap>,
    modifier : Modifier = Modifier){
    if(bitmaps.isEmpty()){
        Box(
            modifier = modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ){
            Text("No Photos")
        }
    }else{
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            contentPadding = contentPadding(16.dp),
            modifier = modifier
        ) {
            items(bitmaps){bitmap->
                Image(bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = modifier.clip(RoundedCornerShape(16.dp)))

            }
        }

    }
}


