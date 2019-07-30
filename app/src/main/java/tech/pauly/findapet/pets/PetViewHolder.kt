package tech.pauly.findapet.pets

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bleacherreport.velocidapterannotations.Bind
import com.bleacherreport.velocidapterannotations.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_pet.view.*
import tech.pauly.findapet.R
import tech.pauly.findapet.extensions.getDimension
import tech.pauly.findapet.extensions.getDrawable
import tech.pauly.findapet.pets.model.Pet

@ViewHolder(adapters = [PetListAdapter], layoutResId = R.layout.item_pet)
class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cornerRadius = getDimension(R.dimen.pet_card_radius) ?: 0
    private val transform = RequestOptions().transform(CenterCrop(), RoundedCorners(cornerRadius))

    @Bind
    fun bind(item: PetViewItem) {
        item.pet.listPhoto?.let { showImage(it) } ?: showPlaceholder()

        itemView.petName.text = item.pet.name
        itemView.petDetails.text = item.pet.details

        itemView.layoutParams.width = item.viewWidth
        itemView.petImage.layoutParams.height = item.viewWidth
    }

    private fun showPlaceholder() {
        itemView.petImage.background = getDrawable(R.drawable.shape_pet_placeholder)
    }

    private fun showImage(url: String) {
        Glide.with(itemView)
            .load(url)
            .apply(transform)
            .into(itemView.petImage)
    }
}

class PetViewItem(val pet: Pet, val viewWidth: Int)