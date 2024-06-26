package com.grappim.docuvault.uikit.widget

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
import com.grappim.docuvault.feature.group.domain.Group
import com.grappim.docuvault.uikit.theme.DocuVaultTheme
import com.grappim.docuvault.uikit.utils.toComposeColor

@Composable
fun PlatoGroupItem(group: Group, onGroupClick: (Group) -> Unit, isSelected: Boolean = false) {
    val backgroundColor = if (isSelected) {
        group.color.toComposeColor()
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
            Text(text = group.name)
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
private fun DomGroupItemPreview() {
    DocuVaultTheme {
        PlatoGroupItem(
            group = Group(id = 0, name = "Name", fields = listOf(), color = "FFCB77"),
            onGroupClick = {}
        )
    }
}
