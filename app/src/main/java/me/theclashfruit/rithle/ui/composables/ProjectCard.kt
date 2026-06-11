package me.theclashfruit.rithle.ui.composables

import android.icu.text.CompactDecimalFormat
import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Box
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import me.theclashfruit.rithle.modrinth.serializables.ProjectResult
import me.theclashfruit.rithle.ui.theme.RithleTheme
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

@Composable
fun ProjectCard(
    project: ProjectResult,
    onClick: () -> Unit
) {
    val color = if (project.color != null)
        Color((project.color ?: 0) or 0xFF000000.toInt())
    else
        MaterialTheme.colorScheme.primaryContainer

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            color = color
        ) {
            if (project.featuredGallery != null)
                AsyncImage(
                    model = project.featuredGallery,
                    contentDescription = "${project.title}'s Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CardDefaults.shape),
                    color = if (project.iconUrl != null) CardDefaults.cardColors().containerColor else color
                ) {
                    if (!project.iconUrl.isNullOrEmpty())
                        AsyncImage(
                            model = project.iconUrl,
                            contentDescription = "${project.title}'s Icon",
                            modifier = Modifier.fillMaxSize()
                        )
                    else
                        Icon(
                            imageVector = Lucide.Box,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp)
                        )
                }

                Column {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "by ${project.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Download,
                            contentDescription = "Downloads",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatCount(project.downloads),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Heart,
                            contentDescription = "Followers",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatCount(project.follows),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.History,
                        contentDescription = "Updated",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = timeAgo(project.dateModified),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

fun formatCount(n: Int): String =
    CompactDecimalFormat
        .getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
        .format(n)

fun timeAgo(iso: String): String {
    val millis = Instant.parse(iso).toEpochMilli()
    return DateUtils.getRelativeTimeSpanString(
        millis,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}