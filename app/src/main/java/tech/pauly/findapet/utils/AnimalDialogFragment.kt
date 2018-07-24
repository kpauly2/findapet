package tech.pauly.findapet.utils

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.ViewGroup
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.DialogAnimalBinding
import tech.pauly.findapet.shared.events.DialogEvent

class AnimalDialogFragment : DialogFragment() {

    lateinit var viewModel: AnimalDialogViewModel

    fun init(event: DialogEvent): AnimalDialogFragment {
        viewModel = AnimalDialogViewModel(event)
        viewModel.dismissSubject.subscribe { _ -> dismiss()}
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = activity?.layoutInflater
        val binding = DataBindingUtil.inflate<DialogAnimalBinding>(requireNotNull(layoutInflater), R.layout.dialog_animal, null, false)
        binding.viewModel = viewModel
        return Dialog(activity).apply {
            setContentView(binding.root)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}