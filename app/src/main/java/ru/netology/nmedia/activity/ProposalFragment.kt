package ru.netology.nmedia.activity

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R

@AndroidEntryPoint
class ProposalFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Заблокировано")
                .setIcon(R.drawable.ic_launcher_netology_foreground)
                .setCancelable(true)
                .setPositiveButton("Войти") { _, _ ->
                    findNavController().navigate(R.id.action_proposalFragment_to_authFragment)
                }
                .setNegativeButton(
                    "Отмена"
                ) { _, _ ->
                    findNavController().navigateUp()
                    //выходим из диалогового окна
                    return@setNegativeButton
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}