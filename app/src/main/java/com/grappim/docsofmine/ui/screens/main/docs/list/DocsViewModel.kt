package com.grappim.docsofmine.ui.screens.main.docs.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.docsofmine.model.document.DocumentListUI
import com.grappim.docsofmine.uikit.utils.toColor
import com.grappim.docsofmine.utils.WhileViewSubscribed
import com.grappim.docsofmine.utils.dateTime.DateTimeUtils
import com.grappim.docsofmine.utils.files.mime.MimeTypes
import com.grappim.domain.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DocsViewModel @Inject constructor(
    documentRepository: DocumentRepository,
    private val dateTimeUtils: DateTimeUtils
) : ViewModel() {

    val documents = documentRepository.getAllDocumentsFlow()
        .map { list ->
            list.map { document ->
                val formattedCreatedDate = dateTimeUtils
                    .formatToDemonstrate(document.createdDate, true)
                DocumentListUI(
                    id = document.id.toString(),
                    name = document.name,
                    createdDate = formattedCreatedDate,
                    preview = document
                        .filesUri
                        .find {
                            it.mimeType.contains(MimeTypes.Image.PREFIX)
                        }?.uriString
                        ?: document.filesUri.find {
                            it.previewUriString != null
                        }?.previewUriString ?: "",
                    groupColor = document.group.color.toColor()
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileViewSubscribed,
            initialValue = emptyList()
        )

}