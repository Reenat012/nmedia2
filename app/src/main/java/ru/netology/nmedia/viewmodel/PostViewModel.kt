package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFilesImpl
import ru.netology.nmedia.repository.PostRepositoryImpl

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )
    val data = repository.getAll()
    val edited = MutableLiveData(empty) //хранит состояние редактированного поста
    fun changeContentAndSave(text: String) {
        edited.value?.let {
            if (it.content != text.trim()) { //проверяем не равен ли существующий текст вновь введенному (trim - без учета пробелом)
                repository.save(it.copy(content = text))
            }
            edited.value = empty
        }
    }

    //функция отмены редактирования и очистка поста
    fun cancelEdit() {
        edited.value = empty
    }

    fun cancelChangeContent() {
        edited.value = empty //удаляем редактированный пост из поля для редактирования
    }

    fun repost(id: Long) = repository.repost(id)
    fun likeById(id: Long) = repository.likeById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun edit(post: Post) {
        edited.value = post //редактируемый пост записываем в LiveData edited
    }

    fun playVideo(post: Post) {
        post.video
    }

    fun openPost(post: Post) {
        repository.openPostById(post.id)
    }
}

