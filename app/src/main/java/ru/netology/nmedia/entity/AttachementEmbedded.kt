package ru.netology.nmedia.entity

import androidx.room.PrimaryKey
import ru.netology.nmedia.Attachment
import ru.netology.nmedia.enumeration.AttachmentType

data class AttachementEmbedded(
    @PrimaryKey(autoGenerate = true)
    val url: String,
    val type: AttachmentType,
) {

    fun toDto() = Attachment(url, type)
    companion object {
        fun fromDto(dto: Attachment) : AttachementEmbedded = with(dto) {
            AttachementEmbedded(
                url, type
            )
        }
    }
}