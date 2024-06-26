package com.grappim.docuvault.repo

import com.grappim.docuvault.common.async.IoDispatcher
import com.grappim.docuvault.data.db.dao.DocumentsDao
import com.grappim.docuvault.data.db.model.document.DocumentEntity
import com.grappim.docuvault.datetime.DateTimeUtils
import com.grappim.docuvault.feature.group.db.dao.GroupsDao
import com.grappim.docuvault.feature.group.repoapi.mappers.GroupMapper
import com.grappim.docuvault.repo.mappers.DocumentFileMapper
import com.grappim.docuvault.repo.mappers.DocumentMapper
import com.grappim.docuvault.repo.mappers.toDocument
import com.grappim.docuvault.repo.mappers.toEntity
import com.grappim.docuvault.repo.mappers.toFileDataEntityList
import com.grappim.domain.model.document.CreateDocument
import com.grappim.domain.model.document.Document
import com.grappim.domain.model.document.DraftDocument
import com.grappim.domain.repository.DocumentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val documentsDao: DocumentsDao,
    private val dateTimeUtils: DateTimeUtils,
    private val documentMapper: DocumentMapper,
    private val groupsDao: GroupsDao,
    private val groupMapper: GroupMapper,
    private val documentFileMapper: DocumentFileMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DocumentRepository {
    override suspend fun deleteDocumentFile(documentId: Long, fileName: String) {
        documentsDao.deleteDocumentFileByIdAndName(documentId, fileName)
    }

    override suspend fun getFullDocumentById(documentId: Long): Document =
        documentMapper.toDocument(documentsDao.getFullDocument(documentId))

    override suspend fun addDraftDocument(): DraftDocument = withContext(ioDispatcher) {
        val defaultGroup = groupsDao.getFirstGroup()
        val nowDate = dateTimeUtils.getDateTimeUTCNow()
        val folderDate = dateTimeUtils.formatToDocumentFolder(nowDate)
        val id = documentsDao.insert(
            DocumentEntity(
                name = "",
                documentFolderName = "",
                createdDate = nowDate,
                groupId = defaultGroup.groupEntity.groupId
            )
        )
        val folderName = "${id}_$folderDate"
        documentsDao.updateProductFolderName(folderName, id)
        DraftDocument(
            id = id,
            date = nowDate,
            documentFolderName = folderName,
            group = groupMapper.toGroup(defaultGroup)
        )
    }

    override fun getAllDocumentsFlow(): Flow<List<Document>> = documentsDao.getFullDocumentsFlow()
        .map { list -> documentMapper.toDocumentList(list) }

    override suspend fun addDocument(createDocument: CreateDocument) {
        val entity = documentMapper.toDocumentEntity(createDocument)
        val list = documentFileMapper.toFileDataEntity(createDocument)
        documentsDao.updateDocumentAndFiles(
            documentEntity = entity,
            list = list
        )
    }

    override suspend fun addDocument(document: Document) {
        documentsDao.insert(document.toEntity())
    }

    override suspend fun addDocuments(documents: List<Document>) = withContext(ioDispatcher) {
        documents.map { document ->
            async {
                documentsDao.insertDocumentAndFiles(
                    documentEntity = document.toEntity(),
                    list = document.toFileDataEntityList()
                )
            }
        }.awaitAll()
        Unit
    }

    override suspend fun removeDocumentById(id: Long) {
        documentsDao.deleteById(id)
    }

    override fun getDocumentById(id: Long): Flow<Document> = documentsDao.getDocumentById(id)
        .map { documentWithFilesEntity ->
            documentWithFilesEntity.documentEntity.toDocument(documentWithFilesEntity.files)
        }
}
