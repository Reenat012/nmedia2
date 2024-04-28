package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityIntentHandlerBinding

class IntentHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntentHandlerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //проверяем входящий intent через savecall на случай если там записано null
        intent?.let {
            if (intent.action == Intent.ACTION_SEND) {
                val text = it.getStringExtra(Intent.EXTRA_TEXT)

                //проверяем не пуста ли строка
                if (text.isNullOrBlank())
                    Snackbar.make(
                        binding.root, //ссылка на корневой view
                        R.string.empty, //сообщение об ошибке
                        Snackbar.LENGTH_INDEFINITE /*константа сколько времени показывать увкдомление*/).
                        //нажимаем ОК и выходим из activity
                            setAction(android.R.string.ok) {
                                finish()
                            }
                        .show() //иначе snackbar показан не будет
            }
        }

    }
}