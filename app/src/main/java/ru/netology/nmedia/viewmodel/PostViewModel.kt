package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
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
    val state: LiveData<FeedModelState>
        get() = _state

    //переменная для подсчета скрытых новых постов
    var countHidden = 0

    //неизменяемое состояние экрана
    val data: LiveData<FeedModel> = repository.data.map {
        FeedModel(posts = it, empty = it.isEmpty())
    }
        .asLiveData(Dispatchers.Default) //получаем LiveData из Flow на дефолтном потоке, потому что мапинг не требует ожидания

    private val _data = MutableLiveData<FeedModel>()

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    //подписка на количество новых постов
    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewer(it.posts.firstOrNull()?.id ?: 0L)
            .asLiveData(Dispatchers.Default)
    }

    fun load() {
        viewModelScope.launch {
            //создаем фоновый поток
            //вызываем значок загрузки
            //postValue безопасный метод в фоновом потоке вместо value, безопасно пробрасывает результат на основной поток MainThread
            _state.postValue(FeedModelState())

            _state.value = try {
                //получаем только посты сохраненные в локальной БД
                repository.getAllVisible()
                //если все хорошо, все флаги выключены
                FeedModelState()
            } catch (e: Exception) {
                //если ошибка, выбрасываем предупреждение
                FeedModelState(error = true)
            }
        }
    }

    fun getHiddenCount() {
        viewModelScope.launch {
            //обновляем переменную count числом новых постов
            countHidden = repository.getHiddenCount().firstOrNull() ?: 0
        }
    }

    fun refreshPosts() {
        //создаем корутину
        viewModelScope.launch {
            //создаем фоновый поток
            //вызываем значок загрузки
            //postValue безопасный метод в фоновом потоке вместо value, безопасно пробрасывает результат на основной поток MainThread
            _state.postValue(FeedModelState(refreshing = true))

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
                    repository.saveAsync(it.copy(content = text)) // <----
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
        //получаем состояние likedByMe поста по id
        val likedByMe = data.value?.posts?.find { it.id == id }?.likedByMe ?: return
        viewModelScope.launch {
            //если true - dislike, false - like
            try {
                if (likedByMe) {
                    repository.disLikeByIdAsync(id)
                    //обновлять данные вручную не требуется так как они обновляются автоматически благодаря подписке к локальной базе данных
                } else {
                    repository.likeByIdAsync(id)
                }
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Long) {
        // Оптимистичная модель
        viewModelScope.launch {
            val old = _data.value?.posts.orEmpty()

            try {
                repository.removeByIdAsync(id)
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
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

