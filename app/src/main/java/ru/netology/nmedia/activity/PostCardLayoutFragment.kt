package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.ActivityPostCardLayoutBinding
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.viewmodel.PostViewModel

class PostCardLayoutFragment : Fragment() {
    private val postViewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    companion object {
        var Bundle.idArg: Long by LongArg
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //надуваем разметку
        val binding = ActivityPostCardLayoutBinding.inflate(
            inflater,
            container,
            false
        )

        view?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it.findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val postId = arguments?.idArg

        postViewModel.data.observe(viewLifecycleOwner) { model ->
            //получаем нужный пост по id, если null выходим из метода
            val post = model.posts.find { it.id == postId } ?: return@observe
            val viewHolder = PostViewHolder(binding, object : OnInteractionListener {

                override fun onEdit(post: Post) {
                    postViewModel.edit(post)
                    findNavController().navigate(
                        R.id.action_postCardLayoutFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = post.content
                        })
                }

                override fun onLike(post: Post) {
                    postViewModel.likeById(post.id)
                }

                override fun onRemove(post: Post) {
                    postViewModel.removeById(post.id)
                    findNavController().navigateUp()
                }

                override fun onRepost(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, post.content)
                    }

                    val shareIntent = Intent.createChooser(intent, "Share Post")
                    startActivity(shareIntent)
                }

                override fun playVideoInUri(post: Post) {
                    postViewModel.playVideo(post)
                    //получаем url
                    val url = post.video.toString()
                    //создаем интент
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                    startActivity(intent)
                }

                override fun openPost(post: Post) {
                }

            })
            viewHolder.bind(post)
        }

        //проверяем есть ли фото на сервере
        if (postViewModel.photo.value != null) {
            //если есть делаем видимой photo_iv
            binding.photoIv.visibility = View.VISIBLE
        }

        //получаем изображение с сервера и присваиваем его photo_iv
        Glide.with(this)
            .load(postViewModel.photo.value?.uri)
            .into(binding.photoIv)

        binding.photoIv.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_viewPhotoFragment)
        }

        binding.videoView.setOnClickListener {
            //получаем ссылку
            val url = Uri.parse(binding.tvVideoPublished.toString())
            //создаем интент
            val intent = Intent(Intent.ACTION_VIEW, url)
        }
        return binding.root
    }
}