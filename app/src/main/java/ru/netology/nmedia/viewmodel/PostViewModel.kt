package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.Post
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.ModelPhoto
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repositoryImpl.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "http://10.0.2.2:9999/avatars/netology.jpg",
    likedByMe = false,
    published = "",
    authorId = 0
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

    //добавляем навигацию во viewModel
    private val _navigateFeedFragmentToProposalFragment = MutableLiveData<Boolean>()
    val navigateFeedFragmentToProposalFragment: LiveData<Boolean> =
        _navigateFeedFragmentToProposalFragment


    //неизменяемое состояние экрана
    val data: LiveData<FeedModel> =
        //впервую очередь смотрим на данные авторизации
        AppAuth.getInstanse().data.flatMapLatest { token ->
            val myId = token?.id

            //читаем базу данных
            repository.data.map { posts ->
                FeedModel(
                    posts.map {
                        it.copy(ownedByMe = it.authorId == myId)
                    }, empty = posts.isEmpty()
                )
            }
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

    //сохраняем фото в переменную
    private val _photo = MutableLiveData<ModelPhoto?>(null)
    val photo: LiveData<ModelPhoto?>
        get() = _photo

    fun setPhoto(uri: Uri, file: File) {
        _photo.value = ModelPhoto(uri, file)
    }

    fun clearPhoto() {
        _photo.value = null
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

    suspend fun changeHiddenPosts() {
        repository.changeHiddenPosts()
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
            edited.value?.let { post ->
                try {
                    //если текст не равен существующему
                    //добавлено фото
                    if (post.content != text.trim() && _photo.value != null) {
                        _photo.value?.let {
                            repository.saveWithAttachment(post.copy(content = text), it)
                        }
                    }
                    //если текст равен существующему(т.е. его не надо менять)
                    //добавлено фото
                    else if (post.content == text.trim() && _photo.value != null) {
                        _photo.value?.let {
                            repository.saveWithAttachment(post, it)
                        }
                    }
                    //если текст не равен существующему
                    //фото не добавлено
                    else if (post.content != text.trim() && _photo.value == null) {
                        repository.saveAsync(post.copy(content = text))
                    }

                    //postValue обновляем с фонового потока LiveData}
                    edited.postValue(empty)
                } catch (e: Exception) {
                    _state.value = FeedModelState(error = true)
                }
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
        //нужно создать условие
        //если пользователь вошел (если есть токен), можно лайкнуть
        //если нет, предложить авторизоваться

        if (AppAuth.getInstanse().data.value != null) {
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
        } else {
            //инициируем переход
            _navigateFeedFragmentToProposalFragment.value = true
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

