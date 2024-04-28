package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.apply {
            val content = getStringExtra("content")
            binding.content.setText(content)
        }

        binding.bottomSave.setOnClickListener {
            val text = binding.content.text.toString() //получаем введенный текст
            if (text.isBlank()) {
                setResult(RESULT_CANCELED) //если текст пустой
            } else {
                setResult(RESULT_OK, Intent().apply {
                    putExtra(Intent.EXTRA_TEXT, text)
                })
            }
            finish()
        }
    }
}

object NewPostContract : ActivityResultContract<String, String?>() {
    override fun createIntent(context: Context, input: String): Intent =
        Intent(context, NewPostActivity::class.java).putExtra("content", input)

    override fun parseResult(resultCode: Int, intent: Intent?) =
        intent?.getStringExtra(Intent.EXTRA_TEXT)
}
