package ru.netology.nmedia.repositoryImpl//package ru.netology.nmedia.repository
//
//import UriDeserializer
//import UriSerializer
//import android.content.Context
//import android.net.Uri
//import androidx.lifecycle.MutableLiveData
//import com.google.gson.GsonBuilder
//import com.google.gson.reflect.TypeToken
//
//import ru.netology.nmedia.Post
//
//class PostRepositoryFilesImpl(private val context: Context) : PostRepository {
//
//    companion object {
//        private const val FILE_NAME = "posts.json"
//    }
//
//    val gson = GsonBuilder()
//        .registerTypeAdapter(Uri::class.java, UriSerializer())
//        .registerTypeAdapter(Uri::class.java, UriDeserializer())
//        .create()
//
//    private val typeToken = TypeToken.getParameterized(List::class.java, Post::class.java).type//обьясняем gson что мы хотим получить List(список) из Post
//    private var nextId: Long = 0
//    //переменная для хранения постов
//    private var posts = emptyList<Post>()
//        private set(value)  {
//            field = value
//            sync()
//        }
//    private var defaultPosts = listOf(
//        Post(
//            id = nextId++,
//            author = "Нетология. Университет интернет-профессий будущего",
//            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
//            published = "21 мая в 18:36",
//            likedByMe = false,
//            likes = 999,
//            reposts = 999,
//            views = 3_123_123,
//            video = "http://www.youtube.com/watch?v=8PORS-t9oOM",
//            authorAvatar = ""
//        ),
//        Post(
//            id = nextId++,
//            author = "Нетология",
//            content = "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
//            published = "21 мая в 18:36",
//            likedByMe = false,
//            likes = 999,
//            reposts = 999,
//            views = 3_123_123,
//            authorAvatar = ""
//        ),
//        Post(
//            id = nextId++,
//            author = "Hello",
//            content = "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
//            published = "21 мая в 18:36",
//            likedByMe = false,
//            likes = 999,
//            reposts = 999,
//            views = 3_123_123,
//            authorAvatar = ""
//        )
//    )
//
//    private val data = MutableLiveData(posts)
//
//    //блок для получения данных в виде String
//    init {
//        //получаем путь к файлу
//        val file = context.filesDir.resolve(FILE_NAME)
//        //проверяем существует ли файл по такому пути
//        if (file.exists()) {
//             context.openFileInput(FILE_NAME).bufferedReader().use {
//                 posts = gson.fromJson(it, typeToken)
//                 nextId = posts.maxOfOrNull { it.id }?.inc() ?: 1
//             }
//        } else {
//            //если null
//            posts = defaultPosts
//        }
//        //обновляем данные в data
//        data.value = posts
//    }
//
//    //блок для записи
//    private fun sync() {
//        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).bufferedWriter().use {
//            it.write(gson.toJson(posts))
//        }
//    }
//
//    override fun repost(id: Long) {
//        posts = posts.map { if (it.id != id) it else it.copy(reposts = it.reposts + 1) }
//        data.value = posts
//    }
//
//    override fun getPost(id: Long): Post {
//        TODO("Not yet implemented")
//    }
//
//    fun getAll(): List<Post> {
//        TODO()
//    }
//
//    override fun getAllAsync(callback: PostRepository.NmediaAllCallback<List<Post>>) {
//        TODO("Not yet implemented")
//    }
//
////    override fun likeById(id: Long): Post {
//////        posts = posts.map { if (it.id != id) it else it.copy(likedByMe = !it.likedByMe, likes = if (!it.likedByMe) it.likes + 1 else it.likes - 1) }
//////        data.value = posts
////        TODO()
////    }
//
//    override fun likeByIdAsync(id: Long, callback: PostRepository.NmediaAllCallback<Post>) {
//        TODO("Not yet implemented")
//    }
//
//    override fun disLikeByIdAsync(id: Long, callback: PostRepository.NmediaAllCallback<Post>) {
//        TODO("Not yet implemented")
//    }
//
//    override fun removeById(id: Long) {
//        TODO("Not yet implemented")
//    }
//
//    override fun removeByIdAsync(id: Long, callback: PostRepository.NmediaAllCallback<Post>) {
//        TODO("Not yet implemented")
//    }
//
//
//    override fun save(post: Post) : Post {
//        TODO()
//    }
//
//    override fun saveAsync(post: Post, callback: PostRepository.NmediaAllCallback<Post>) {
//        TODO("Not yet implemented")
//    }
//
//
//    override fun openPostById(id: Long): Post {
//        return posts[id.toInt()]
//    }
//}
//
