package ru.netology.nmedia.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.WallService
import ru.netology.nmedia.databinding.ActivityPostCardLayoutBinding
import ru.netology.nmedia.load

interface OnInteractionListener {
    fun onLike(post: Post)
    fun onRepost(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
    fun playVideoInUri(post: Post)
    fun openPost(post: Post)
}

typealias OnListener = (post: Post) -> Unit //можем вводить новые константы для типов, которые хотим использовать
typealias OnRemoveListener = (post: Post) -> Unit

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ActivityPostCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PostViewHolder(
    private val binding: ActivityPostCardLayoutBinding,
    private val onLInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    private val service = WallService()

    val urlNeto = "http://10.0.2.2:9999/avatars/netology.jpg"
    val urlSber = "http://10.0.2.2:9999/avatars/sber.jpg"
    val urlTcs = "http://10.0.2.2:9999/avatars/tcs.jpg"
    val url404 = "http://10.0.2.2:9999/avatars/404.png"

    @SuppressLint("QueryPermissionsNeeded")
    fun bind(post: Post) =
        binding.apply {
            post.authorAvatar?.let { ivAvatar.load(it) }
            tvAuthor.text = post.author
            tvPublished.text = post.published
            tvContent.text = post.content
            //следим за состоянием параметра likedByMe
            ivLikes.isChecked = post.likedByMe
            //текст будет записываться в атрибут text MaterialButton
            ivLikes.text = service.amount(post.likes)
            ivRepost.text = service.amount(post.reposts)

            ivLikes.setOnClickListener {
                onLInteractionListener.onLike(post)

            }
            ivRepost.setOnClickListener {
                onLInteractionListener.onRepost(post)

            }
            ivMenu.setOnClickListener {
                PopupMenu(
                    it.context,
                    it
                ).apply {//все, что внутри функции apply вызываются на объекте
                    //на котором apply была вызвана, т.е. PopupMenu
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.menu_edit -> {
                                onLInteractionListener.onEdit(post)
                                true
                            }

                            R.id.menu_remove -> {
                                onLInteractionListener.onRemove(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }


            if (post.video !== null) {
                //делаем видимой группу с элементами видео
                groupVideo.visibility = View.VISIBLE
                tvVideoPublished.text = post.video.toString()
                videoView.setImageResource(R.drawable.image_leo)
            }

            //условия для скрытия groupVideo
            if (post.video == null) {
                groupVideo.visibility = View.GONE
            }

            // обработчик нажатия на видео
            videoView.setOnClickListener {
                onLInteractionListener.playVideoInUri(post)
            }

            buttonPlay.setOnClickListener {
                onLInteractionListener.playVideoInUri(post)
            }

            //клик на контент -> перходим в отдельный пост
            tvContent.setOnClickListener {
                onLInteractionListener.openPost(post)
            }
        }
}

object PostDiffUtil : DiffUtil.ItemCallback<Post>() {
    //метод проверяет содержимое постов на равенство
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem

    //метод проверяет посты на равенство сравнивая id постов
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id

}