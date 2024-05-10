package ru.netology.nmedia.activity

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    companion object {
        var Bundle.textArg: String? by StringArg
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ActivityNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg
            ?.let ( binding.content::setText )

        binding.bottomSave.setOnClickListener {
            viewModel.changeContentAndSave(binding.content.text.toString())
            AndroidUtils.hideKeyboard(requireView()) //убираем клавиатуру
            findNavController().navigateUp() //откатываем навигацию назад
       }
        return binding.root
    }
}

//object NewPostContract : ActivityResultContract<String, String?>() {
//    override fun createIntent(context: Context, input: String): Intent =
//        Intent(context, NewPostActivity::class.java).putExtra("content", input)
//
//    override fun parseResult(resultCode: Int, intent: Intent?) =
//        intent?.getStringExtra(Intent.EXTRA_TEXT)
//}
