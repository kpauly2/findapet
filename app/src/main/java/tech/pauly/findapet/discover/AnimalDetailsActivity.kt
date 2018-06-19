package tech.pauly.findapet.discover

import android.databinding.DataBindingUtil
import android.os.Bundle
import dagger.android.AndroidInjection
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.ActivityAnimalDetailsBinding
import tech.pauly.findapet.shared.BaseActivity
import javax.inject.Inject

class AnimalDetailsActivity : BaseActivity() {

    @Inject
    internal lateinit var viewModel: AnimalDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityAnimalDetailsBinding>(this, R.layout.activity_animal_details)
        lifecycle.addObserver(viewModel)
        binding.viewModel = viewModel

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
