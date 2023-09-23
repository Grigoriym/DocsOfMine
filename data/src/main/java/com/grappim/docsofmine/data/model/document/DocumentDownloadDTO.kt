package com.grappim.docsofmine.data.model.document

import com.grappim.docsofmine.data.model.group.GroupDTO
import com.grappim.docsofmine.data.serializers.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@kotlinx.serialization.Serializable
data class DocumentDownloadDTO(
    val id: Long,
    val name: String,
    val group: GroupDTO,
    val filesUri: List<DocumentFileUriDownloadDTO>,
    @kotlinx.serialization.Serializable(OffsetDateTimeSerializer::class)
    val createdDate: OffsetDateTime
)