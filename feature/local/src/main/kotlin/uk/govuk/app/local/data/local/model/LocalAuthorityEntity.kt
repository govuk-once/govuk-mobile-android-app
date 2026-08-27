package uk.govuk.app.local.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_authority")
internal data class LocalAuthorityEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val url: String,
    val slug: String,
    @Embedded(prefix = "parent_") val parent: LocalAuthorityParentEntity?
)
