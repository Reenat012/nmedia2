package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityPostCardLayoutBinding
import ru.netology.nmedia.databinding.FragmentViewPhotoBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class ViewPhotoFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

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

        //показываем фото
        binding.photoViewIv.setImageURI(viewModel.photo.value?.uri)

        return binding.root
    }
}