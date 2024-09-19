package ru.netology.nmedia.activity

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.PostCardLayoutFragment.Companion.idArg
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

        const val IMAGE_MAX_SIZE = 2048
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
            ?.let(binding.content::setText)

        //создаем лаунчер для интента фото
        val launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            //если ошибка
            if (it.resultCode == ImagePicker.RESULT_ERROR) {
                //показываем всплывающеее окно
                Toast.makeText(requireContext(), R.string.image_pick_error, Toast.LENGTH_SHORT)
                    .show()
                //возврата управления из лямбда-выражения, аннотированного ключевым словом suspend, обратно в точку вызова
                //это позволяет избежать вложенных обратных вызовов и делает код более читаемым и поддерживаемым
                return@registerForActivityResult
            }

            val uri = it.data?.data ?: return@registerForActivityResult

            viewModel.setPhoto(uri, uri.toFile())
        }

        //рекомендуемый подход к использованию меню от Google
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_add_post, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    if (menuItem.itemId == R.id.save) {
                        viewModel.changeContentAndSave(binding.content.text.toString())

                        findNavController().navigate(
                            R.id.action_newPostFragment_to_feedFragment,
                            Bundle().apply { })
                        AndroidUtils.hideKeyboard(requireView()) //убираем клавиатуру
                        true
                    } else {
                        false
                    }
            }, viewLifecycleOwner,
        )

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp() //откатываем навигацию назад
            viewModel.load()
        }

        //оформляем подписку на photo
        viewModel.photo.observe(viewLifecycleOwner) { model ->
            //если модель есть
            if (model != null) {
                //то заполняем preview тем, что там указано
                binding.previewPhoto.setImageURI(model.uri)
                //показывем preview
                binding.previewPhoto.visibility = View.VISIBLE
            } else {
                //скрываем видимость всего контейнера preview
                binding.previewPhoto.visibility = View.GONE
            }
        }

        //обработчик нажатия на кнопку clearPhoto
        binding.clearPhoto.setOnClickListener {
            //очищаем фото
            viewModel.clearPhoto()

            //убираем кнопку
            binding.clearPhoto.visibility = View.GONE
        }

        //обработчик нажатия на кнопку взять фото из камеры
        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop() //обрезка изображения после его выбора из галерии или камеры
                .cameraOnly()
                .maxResultSize(IMAGE_MAX_SIZE, IMAGE_MAX_SIZE)
                .createIntent {
                    launcher.launch(it)
                }

            //показываем кнопку очистить фото
            binding.clearPhoto.visibility = View.VISIBLE
        }

        //обработчик нажатия на кнопку взять фото из галереи
        binding.pickPhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop() //обрезка изображения после его выбора из галерии или камеры
                .galleryOnly()
                .maxResultSize(IMAGE_MAX_SIZE, IMAGE_MAX_SIZE)
                .createIntent {
                    launcher.launch(it)
                }

            //показываем кнопку очистить фото
            binding.clearPhoto.visibility = View.VISIBLE
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
