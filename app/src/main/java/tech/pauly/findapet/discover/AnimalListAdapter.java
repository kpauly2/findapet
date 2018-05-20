package tech.pauly.findapet.discover;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.R;

public class AnimalListAdapter extends RecyclerView.Adapter<AnimalListItemViewHolder> {

    private List<AnimalListItemViewModel> animalItems = new ArrayList<>();

    @Inject
    AnimalListAdapter() {}

    @Override
    public AnimalListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AnimalListItemViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_animal_list, parent, false));
    }

    @Override
    public void onBindViewHolder(AnimalListItemViewHolder holder, int position) {
        holder.bind(animalItems.get(position));
    }

    @Override
    public int getItemCount() {
        return animalItems.size();
    }

    public void setAnimalItems(List<AnimalListItemViewModel> items) {
        animalItems.addAll(items);
        notifyDataSetChanged();
    }
}
