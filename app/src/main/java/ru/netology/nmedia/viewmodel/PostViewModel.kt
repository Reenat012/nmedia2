package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.Post
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "http://10.0.2.2:9999/avatars/netology.jpg",
    likedByMe = false,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao()
    )
    private val _state = MutableLiveData<FeedModelState>() //изменяемое состояние экрана
    val state : LiveData<FeedModelState>
        get() = _state
    //неизменяемое состояние экрана
    val data: LiveData<FeedModel> = repository.data.map {
        FeedModel(posts = it, empty = it.isEmpty())
    }



    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    fun load() {
        viewModelScope.launch {
            //создаем фоновый поток
            //вызываем значок загрузки
            //postValue безопасный метод в фоновом потоке вместо value, безопасно пробрасывает результат на основной поток MainThread
            _state.postValue(FeedModelState(loading = true))

            _state.value = try {
                repository.getAll()
                //если все хорошо, все флаги выключены
                FeedModelState()
            } catch (e: Exception) {
                //если ошибка, выбрасываем предупреждение
                FeedModelState(error = true)
            }

        }
    }

    init {
        load()
    }

    //запрос списка постов

    val edited = MutableLiveData(empty) //хранит состояние редактированного поста

    fun changeContentAndSave(text: String) {
        viewModelScope.launch {
            edited.value?.let {
                if (it.content != text.trim()) { //проверяем не равен ли существующий текст вновь введенному (trim - без учета пробелом)
                    repository.saveAsync(it)
                }
                edited.postValue(empty) //postValue обновляем с фонового потока LiveData
            }
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
//        //получаем состояние likedByMe поста по id
//        val likedByMe = _data.value?.posts?.find { it.id == id }?.likedByMe
//        //если true - dislike, false - like
//        if (likedByMe == true) {
//            repository.disLikeByIdAsync(id, object : PostRepository.NmediaAllCallback<Post> {
//                override fun onSuccess(data: Post) {
//                    _data.postValue(_data.value?.posts?.map {
//                        if (it.id != id) it else it.copy(
//                            likedByMe = !it.likedByMe,
//                            likes = it.likes - 1
//                        )
//                    }
//                        ?.let { FeedModel(posts = it) })
//                }
//
//                override fun error(e: Exception) {
//                    kotlin.error(e)
//                }
//            })
//        } else {
//            repository.likeByIdAsync(id, object : PostRepository.NmediaAllCallback<Post> {
//                override fun onSuccess(data: Post) {
//                    _data.postValue(_data.value?.posts?.map {
//                        if (it.id != id) it else it.copy(
//                            likedByMe = !it.likedByMe,
//                            likes = it.likes + 1
//                        )
//                    }
//                        ?.let { FeedModel(posts = it) })
//                }
//
//                override fun error(e: Exception) {
//                    kotlin.error(e)
//                }
//            })
//        }
    }

    fun removeById(id: Long) {
//        // Оптимистичная модель
//        val old = _data.value?.posts.orEmpty()
////
//        repository.removeByIdAsync(id, object : PostRepository.NmediaAllCallback<Post> {
//            override fun onSuccess(data: Post) {
//                _data.postValue(_data.value?.posts?.filter { it.id != id }
//                    ?.let { FeedModel(posts = it) })
//            }
//
//            override fun error(e: Exception) {
//                _data.postValue(_data.value?.copy(posts = old))
//            }
//        })
    }

    fun edit(post: Post) {
        edited.value = post //редактируемый пост записываем в LiveData edited
    }

    fun playVideo(post: Post) {
        post.video
    }

    fun openPost(post: Post) {
//        repository.openPostById(post.id)
    }
}

