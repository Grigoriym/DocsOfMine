package com.grappim.docuvault.feature.group.repoimpl.mappers

import com.grappim.docuvault.common.async.IoDispatcher
import com.grappim.docuvault.feature.group.db.model.GroupFieldEntity
import com.grappim.docuvault.feature.group.domain.GroupField
import com.grappim.docuvault.feature.group.repoapi.mappers.GroupFieldMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupFieldMapperImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GroupFieldMapper {
    override suspend fun toGroupFieldEntityList(list: List<GroupField>): List<GroupFieldEntity> =
        withContext(ioDispatcher) {
            list.map { groupField -> toGroupFieldEntity(groupField) }
        }

    override suspend fun toGroupFieldEntity(groupField: GroupField): GroupFieldEntity =
        withContext(ioDispatcher) {
            GroupFieldEntity(
                name = groupField.name,
                value = groupField.value,
                groupId = groupField.groupId
            )
        }
}
