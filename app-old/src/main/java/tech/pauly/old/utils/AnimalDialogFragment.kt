package tech.pauly.old.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import tech.pauly.old.R
import tech.pauly.old.databinding.DialogAnimalBinding
import tech.pauly.old.shared.BaseDialogFragment
import tech.pauly.old.shared.events.DialogEvent

class AnimalDialogFragment : BaseDialogFragment() {

    lateinit var viewModel: AnimalDialogViewModel

    fun init(event: DialogEvent): AnimalDialogFragment {
        viewModel = AnimalDialogViewModel(event)
        viewModel.dismissSubject.quickSubscribe { dismiss() }
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity ?: return super.onCreateDialog(savedInstanceState)
        val layoutInflater = activity.layoutInflater
        val binding = DataBindingUtil.inflate<DialogAnimalBinding>(requireNotNull(layoutInflater), R.layout.dialog_animal, null, false)
        binding.viewModel = viewModel
        return Dialog(activity).apply {
            setContentView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}