package me.theclashfruit.rithle.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Box
import com.composables.icons.lucide.Lucide
import me.theclashfruit.rithle.ui.theme.RithleTheme

@Composable
fun ProjectCard(
    title: String,
    author: String,
    description: String,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    onClick: () -> Unit
) {
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
        ) {}

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CardDefaults.shape),
                color = color
            ) {
                Icon(
                    imageVector = Lucide.Box,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Column() {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "by $author",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ProjectCardPreview() {
    RithleTheme {
        ProjectCard(
            title = "Project Name",
            author = "AuthorName",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur et nisi justo. Duis eget euismod ex.",
            onClick = {}
        )
    }
}