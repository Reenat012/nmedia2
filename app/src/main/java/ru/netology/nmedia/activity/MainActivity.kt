package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.Group
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //clone1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //выывыв

        //теперь имеем возможность обращаться к группе элементов
        val groupVideo = findViewById<Group>(R.id.group_video)

        val viewModel: PostViewModel by viewModels()

        val newPostLauncher = registerForActivityResult(NewPostContract) {
            val result = it ?: return@registerForActivityResult
            viewModel.changeContentAndSave(result)
        }

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                newPostLauncher.launch(post.content)
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
                val shareIntent = Intent.createChooser(intent, getString(R.string.choser_share_post))
                startActivity(shareIntent)

                //подсчет количества репостов
                viewModel.repost(post.id)
            }

        })

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val newPost = posts.size > adapter.currentList.size
            adapter.submitList(posts) {
                if (newPost) {
                    binding.list.smoothScrollToPosition(0) //сверху сразу будет отображаться новый пост
                }
            } //при каждом изменении данных мы список постов записываем обновленный список постов
        }

        binding.bottomSave.setOnClickListener {
            //обращаемся к контракту
            newPostLauncher.launch("")
        }
    }
}




