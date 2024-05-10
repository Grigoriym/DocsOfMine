package com.grappim.domain.model.document

import com.grappim.docuvault.feature.group.domain.Group
import java.time.OffsetDateTime

data class DraftDocument(
    val id: Long,
    val date: OffsetDateTime,
    val documentFolderName: String,
    val group: Group
)
