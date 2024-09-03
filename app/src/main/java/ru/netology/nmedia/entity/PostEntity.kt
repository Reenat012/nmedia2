package ru.netology.nmedia.entity

import android.net.Uri
import android.widget.Toast
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import okhttp3.internal.ignoreIoExceptions
import ru.netology.nmedia.Attachment
import ru.netology.nmedia.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val hidden: Boolean = false, //свойство для сгенерированных записей
    @Embedded
    val attachment: AttachementEmbedded?
) {
    fun toDto() = Post(
        id = id,
        author = author,
        authorId = authorId,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        attachment = attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post, hidden: Boolean = false) =
            PostEntity(
                id = dto.id,
                author = dto.author,
                authorId = dto.authorId,
                authorAvatar = dto.authorAvatar,
                content = dto.content,
                published = dto.published,
                likedByMe = dto.likedByMe,
                likes = dto.likes,
                hidden = hidden,
                attachment = dto.attachment?.let {
                    AttachementEmbedded.fromDto(it)
                }
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(hidden: Boolean = false): List<PostEntity> = map { PostEntity.fromDto(it, hidden) }