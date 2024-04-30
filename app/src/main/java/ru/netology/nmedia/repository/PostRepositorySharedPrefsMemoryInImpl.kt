package ru.netology.nmedia.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.Post

class PostRepositorySharedPrefsMemoryInImpl(context: Context) : PostRepository {
    companion object {
        private const val KEY = "posts"
    }

    private val gson = Gson() //подключаем библиотеку для хранения данных gson
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE) //создаем shred preferens
    private val typeToken = TypeToken.getParameterized(List::class.java, Post::class.java).type//обьясняем gson что мы хотим получить List(список) из Post
    private var nextId: Long = 0
    //переменная для хранения постов
    private var posts = emptyList<Post>()
        private set(value)  {
            field = value
            sync()
        }
    private var defaultPosts = listOf(
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 999,
            reposts = 999,
            views = 3_123_123,
            video = Uri.parse("http://www.youtube.com/watch?v=8PORS-t9oOM")
        ),
        Post(
            id = nextId++,
            author = "Нетология",
            content = "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 999,
            reposts = 999,
            views = 3_123_123
        ),
        Post(
            id = nextId++,
            author = "Hello",
            content = "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 999,
            reposts = 999,
            views = 3_123_123
        )
    )

    private val data = MutableLiveData(posts)

    //блок для получения данных в виде String
    init {
        prefs.getString(KEY, null )?.let {
            //получаем посты из gson в формате указанном в typeToken
            posts = gson.fromJson(it, typeToken)
            nextId = posts.maxOf { it.id } + 1
        } ?: run {
            //если null
            posts = defaultPosts
        }
        //обновляем данные в data
        data.value = posts
    }

    //блок для записи
    fun sync() {
        with(prefs.edit()) {
            putString(KEY, gson.toJson(posts))
            apply() //сохраняем данные на диск асинхронно
        }
    }

    override fun repost(id: Long) {
        posts = posts.map { if (it.id != id) it else it.copy(reposts = it.reposts + 1) }
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map { if (it.id != id) it else it.copy(likedByMe = !it.likedByMe, likes = if (!it.likedByMe) it.likes + 1 else it.likes - 1) }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id } //оставляем только те посты, id которых не равны удаленному
        data.value = posts
    }


    override fun save(post: Post) {
        posts = if (post.id == 0L) { //при id = 0 сохраняем новый пост
            listOf(
            post.copy(
                id = nextId++,
                author = "Me",
                published = "Now"
            )
        ) + posts}
        else { //при id равному id другого поста редактируем пост с равным id
            posts.map { if (post.id == it.id) it.copy(content = post.content) else it }
        }
        data.value = posts
    }
}