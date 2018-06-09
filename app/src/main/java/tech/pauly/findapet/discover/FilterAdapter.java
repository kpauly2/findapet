package tech.pauly.findapet.discover;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.databinding.ItemFilterBreedBinding;
import tech.pauly.findapet.databinding.ItemFiltersBinding;


public class FilterAdapter extends RecyclerView.Adapter<FilterBreedViewHolder> {

    private static final int FILTERS = 0;
    private static final int BREED = 1;

    private List<String> breedItems = new ArrayList<>();
    private FilterViewModel viewModel;

    @Inject
    FilterAdapter() {}

    @NonNull
    @Override
    public FilterBreedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case FILTERS:
                return new FilterBreedViewHolder(ItemFiltersBinding.inflate(inflater, parent, false));
            case BREED:
                return new FilterBreedViewHolder(ItemFilterBreedBinding.inflate(inflater, parent, false));
            default:
                throw new IllegalStateException("Filter breed list view type not supported");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FilterBreedViewHolder holder, int position) {
        if (position == 0) {
            holder.bind(viewModel);
        } else {
            holder.bind(viewModel, breedItems.get(position - 1));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return FILTERS;
        } else {
            return BREED;
        }
    }

    @Override
    public int getItemCount() {
        return breedItems.size() + 1;
    }

    public void setViewModel(FilterViewModel viewModel) {
        this.viewModel = viewModel;
        notifyDataSetChanged();
    }

    public void setBreedItems(List<String> items) {
        breedItems.addAll(items);
        notifyDataSetChanged();
    }
}
