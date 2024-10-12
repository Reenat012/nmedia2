package ru.netology.nmedia.activity

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentViewPhotoBinding
import ru.netology.nmedia.loadWithoutCircle
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class ViewPhotoFragment : Fragment() {

    val viewModel: PostViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //надуваем разметку
        val binding = FragmentViewPhotoBinding.inflate(
            inflater,
            container,
            false
        )

        // Получаем данные
        val uri: Uri? by lazy {
            requireArguments().getParcelable<Uri>("uriKey")
        }

        //показываем фото
        binding.photoViewIv.loadWithoutCircle(uri.toString())

        return binding.root
    }
}