package com.grappim.docsofmine.uikit.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grappim.domain.model.group.Group
import com.grappim.docsofmine.uikit.theme.DocsOfMineTheme
import com.grappim.docsofmine.uikit.utils.toColor

@Composable
fun DomGroupItem(
    group: Group,
    onGroupClick: (Group) -> Unit,
    isSelected: Boolean = false
) {
    val backgroundColor = if (isSelected) {
        group.color.toColor()
    } else {
        Color.Gray
    }
    Card(
        modifier = Modifier
            .size(100.dp),
        backgroundColor = backgroundColor,
        onClick = {
            onGroupClick(group)
        }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = group.name,
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
private fun DomGroupItemPreview() {
    DocsOfMineTheme {
        DomGroupItem(
            group = Group(id = 0, name = "Name", fields = listOf(), color = "FFCB77"),
            onGroupClick = {

            }
        )
    }
}