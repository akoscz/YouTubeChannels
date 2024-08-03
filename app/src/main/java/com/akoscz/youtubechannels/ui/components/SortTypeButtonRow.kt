package com.akoscz.youtubechannels.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akoscz.youtubechannels.ui.viewmodels.SortType

@Composable
fun SortTypeButtonRow(selectedSortType: SortType, updateSortType: (SortType) -> Unit) {
    Row(
        modifier = Modifier.padding(start = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clickable { updateSortType(SortType.NEWEST) }
                .background(
                    color =
                        if (selectedSortType == SortType.NEWEST) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Text(SortType.NEWEST.name.lowercase().replaceFirstChar { it.titlecase() },
                color =
                    if (selectedSortType == SortType.NEWEST) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .clickable { updateSortType(SortType.POPULAR) }
                .background(
                    color =
                        if (selectedSortType == SortType.POPULAR) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Text(SortType.POPULAR.name.lowercase().replaceFirstChar { it.titlecase() },
                color =
                    if (selectedSortType == SortType.POPULAR) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .clickable { updateSortType(SortType.OLDEST) }
                .background(
                    color =
                        if (selectedSortType == SortType.OLDEST) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Text(SortType.OLDEST.name.lowercase().replaceFirstChar { it.titlecase() },
                color =
                    if (selectedSortType == SortType.OLDEST) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview
@Composable
fun SortTypeButtonRowPreview() {
    SortTypeButtonRow(SortType.NEWEST) {}
}