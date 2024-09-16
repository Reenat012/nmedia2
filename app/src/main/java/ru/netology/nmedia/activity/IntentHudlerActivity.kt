package ru.netology.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment.Companion.textArg
import ru.netology.nmedia.databinding.ActivityIntentHandlerBinding

class IntentHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntentHandlerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //проверяем входящий intent через savecall на случай если там записано null
        intent?.let {
            if (intent.action !== Intent.ACTION_SEND) {
                return@let
            }

            //получаем текст
            val text = it.getStringExtra(Intent.EXTRA_TEXT)

            //проверяем не пуста ли строка
            if (text.isNullOrBlank())
                Snackbar.make(
                    binding.root, //ссылка на корневой view
                    R.string.empty, //сообщение об ошибке
                    Snackbar.LENGTH_INDEFINITE /*константа сколько времени показывать увкдомление*/
                ).
                    //нажимаем ОК и выходим из activity
                setAction(android.R.string.ok) {
                    finish()
                }
                    .show() //иначе snackbar показан не будет

            intent.removeExtra(Intent.EXTRA_TEXT) //убираем текст

            //получаем доступ к навигации активити
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment, //маршрут, по которому хотим перейти
                Bundle().apply {//аргументы, которые необходимо передать объекту
                    textArg = text
                }
            )
        }

        checkGoogleApiAvailability()

        //запросить разрешение на показ уведомлений
        requestNotificationsPermission()
    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@IntentHandlerActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@IntentHandlerActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@IntentHandlerActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

//        FirebaseMessaging.getInstance().token.addOnSuccessListener {
//            println(it)
//        }
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }
}
