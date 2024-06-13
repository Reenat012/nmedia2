package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData<FeedModel>() //изменяемое состояние экрана
    val data: LiveData<FeedModel> //неизменяемое состояние экрана
        get() = _data

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    fun load() {
        //создаем фоновый поток
        //вызываем значок загрузки
        //postValue безопасный метод в фоновом потоке вместо value, безопасно пробрасывает результат на основной поток MainThread
        _data.postValue(FeedModel(loading = true))

        repository.getAllAsync(
            object : PostRepository.NmediaAllCallback<List<Post>> {
                override fun onSuccess(data: List<Post>) {
                    _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
                }

                override fun error(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            }
        )
    }

    init {
        load()
    }

    //запрос списка постов

    val edited = MutableLiveData(empty) //хранит состояние редактированного поста

    fun changeContentAndSave(text: String) {
        edited.value?.let {
            if (it.content != text.trim()) { //проверяем не равен ли существующий текст вновь введенному (trim - без учета пробелом)
                repository.saveAsync(
                    it.copy(content = text),
                    object : PostRepository.NmediaAllCallback<Post> {
                        override fun onSuccess(data: Post) {
                            _postCreated.postValue(Unit)
                        }

                        override fun error(e: Exception) {
                            kotlin.error(e)
                        }
                    })
            }
            edited.postValue(empty) //postValue обновляем с фонового потока LiveData
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
//    fun likeById(id: Long) {
//        thread {
//            repository.likeById(id)
//            repository.getPost(id) //обращаемся к репозиторию и обновляем пост
//        }
//    }

    fun likeById(id: Long) {
        val likedByMe = _data.value?.posts?.find { it.id == id }?.likedByMe ?: return

        repository.likeByIdAsync(id, object : PostRepository.NmediaAllCallback<Post> {
            override fun onSuccess(data: Post) {
                _data.postValue(FeedModel(posts = _data.value?.posts?.find { it.id == id }.copy(likedByMe = likedByMe))
            }

            override fun error(e: Exception) {
                kotlin.error(e)
            }
        })
    }

    fun removeById(id: Long) {
        // Оптимистичная модель
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id.toLong() != id }
            )
        )
        try {
            repository.removeById(id)
        } catch (e: IOException) {
            _data.postValue(_data.value?.copy(posts = old))
        }

    }

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

