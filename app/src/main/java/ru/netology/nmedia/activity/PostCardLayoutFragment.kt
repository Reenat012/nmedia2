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
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.ActivityPostCardLayoutBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class PostCardLayoutFragment : Fragment() {
    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
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

        val holder = PostViewHolder(binding, onInteractionListener)
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == postId } ?: return@observe
            holder.bind(post)
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