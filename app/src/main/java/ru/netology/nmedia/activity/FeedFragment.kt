package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.PostCardLayoutFragment.Companion.idArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment(
) : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    val viewModel: PostViewModel by activityViewModels()

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater, //класс в android, используемый для создания view
        container: ViewGroup?, //специальный тип в android, используемый как контейнер для других видов
        savedInstanceState: Bundle?
    ): View {
        //надуваем разметку
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        //теперь имеем возможность обращаться к группе элементов
        val groupVideo = view?.findViewById<Group>(R.id.group_video)

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    })
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
//                binding.content.setText("") //удаляем текст после добавления
//                AndroidUtils.hideKeyboard(binding.content) //убираем клавиатуру после добавления поста
//                group.visibility = View.GONE
            }

            override fun onRepost(post: Post) {
                //всплывающее окно поделиться
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)
                }

                //делаем диалог более стилизованным(красивым)
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.choser_share_post))
                startActivity(shareIntent)

                //подсчет количества репостов
                viewModel.repost(post.id)
            }

            @SuppressLint("QueryPermissionsNeeded")
            override fun playVideoInUri(post: Post) {
                viewModel.playVideo(post)
                //получаем url
                val url = post.video.toString()
                //создаем интент
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                startActivity(intent)
            }

            override fun openPost(post: Post) {
                viewModel.openPost(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_postCardLayoutFragment,
                    Bundle().also { it.idArg = post.id })
            }

            override fun openImage(post: Post) {
                val uriPhoto = Uri.parse("http://10.0.2.2:9999/media/${post.attachment?.url}")
                findNavController().navigate(
                    R.id.action_feedFragment_to_viewPhotoFragment,
                    bundleOf("uriKey" to uriPhoto) //передаем uri изображения с помощью ключа
                )
            }
        })

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        binding.list.adapter = adapter
//        viewModel.data.observe(viewLifecycleOwner) { model ->
//            val newPost = model.posts.size > adapter.currentList.size
//            adapter.s(model.posts) {
//                if (newPost) {
//                    binding.list.smoothScrollToPosition(0) //сверху сразу будет отображаться новый пост
//                }
//            } //при каждом изменении данных мы список постов записываем обновленный список постов
//
//            binding.emptyPosts.isVisible = model.empty
//        }

        binding.buttonNewPosts.setOnClickListener {
            //метод, который будет скрытые посты видимыми
            lifecycleScope.launch { viewModel.changeHiddenPosts() }

//            viewModel.refreshPosts()

            binding.buttonNewPosts.visibility = View.GONE
            binding.buttonTop.visibility = View.GONE

        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {

                }
            }
        })

        //получаем сгенерированные сервером посты
        viewModel.newerCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.buttonNewPosts.visibility = View.VISIBLE
                binding.buttonTop.visibility = View.VISIBLE
            }
//            Log.d("FeedFragment", "Newer count: $it")
//            if (it > 0) {
//                binding.buttonNewPosts.visibility = View.VISIBLE
//                binding.buttonTop.visibility = View.VISIBLE
//            }
        }

        viewModel.navigateFeedFragmentToProposalFragment.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_feedFragment_to_proposalFragment)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.retry_loading) {
                        viewModel.load()
                    }
                    .show()
            }
            binding.progressBar.isVisible = state.loading
            binding.refresh.isRefreshing = state.refreshing
        }

        //клик на кнопку добавить пост
        binding.bottomSave.setOnClickListener {
            if (appAuth.data.value != null) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else {
                findNavController().navigate(R.id.action_feedFragment_to_proposalFragment)
            }
        }

        binding.refresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }

//        binding.buttonRetry.setOnClickListener {
//            viewModel.load()
//        }//

        return binding.root
    }

}









