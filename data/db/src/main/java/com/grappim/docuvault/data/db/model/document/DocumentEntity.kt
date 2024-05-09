package com.grappim.docuvault.data.db.model.document

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.grappim.docuvault.data.db.model.group.GroupEntity
import java.time.OffsetDateTime

@Entity(
    tableName = "document_table",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId")]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val documentId: Long = 0,
    val name: String,
    val createdDate: OffsetDateTime,
    val documentFolderName: String,
    val isCreated: Boolean = false,
    val groupId: Long
)
