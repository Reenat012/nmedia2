package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

class PostRepositoryMemoryInImpl : PostRepository {

    private var nextId: Long = 0
    private var posts = listOf(
        Post(
            id = nextId++,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 999,
            reposts = 999,
            views = 3_123_123,
            video = "http://www.youtube.com/watch?v=8PORS-t9oOM"
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

    override fun repost(id: Long) {
        posts = posts.map { if (it.id != id) it else it.copy(reposts = it.reposts + 1) }
        data.value = posts
    }

    override fun getPost(id: Long): Post {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Post> {
        TODO()
    }

    override fun likeById(id: Long): Post {
//        posts = posts.map { if (it.id != id) it else it.copy(likedByMe = !it.likedByMe, likes = if (!it.likedByMe) it.likes + 1 else it.likes - 1) }
//        data.value = posts
        TODO()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id } //оставляем только те посты, id которых не равны удаленному
        data.value = posts
    }


    override fun save(post: Post) : Post{
        TODO()
    }

    override fun openPostById(id: Long): Post {
        return posts[id.toInt()]
    }
}