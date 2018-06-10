package tech.pauly.findapet.discover;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.databinding.ItemFilterBreedBinding;
import tech.pauly.findapet.databinding.ItemBreedSearchBinding;
import tech.pauly.findapet.databinding.ItemFiltersBinding;


public class FilterAdapter extends RecyclerView.Adapter<FilterBreedViewHolder> {

    private static final int FILTERS = 0;
    private static final int BREED_SEARCH = 1;
    private static final int BREED = 2;

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
            case BREED_SEARCH:
                return new FilterBreedViewHolder(ItemBreedSearchBinding.inflate(inflater, parent, false));
            case BREED:
                return new FilterBreedViewHolder(ItemFilterBreedBinding.inflate(inflater, parent, false));
            default:
                throw new IllegalStateException("Filter breed list view type not supported");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FilterBreedViewHolder holder, int position) {
        switch (position) {
            case 0:
            case 1:
                holder.bind(viewModel);
                return;
            default:
                holder.bind(viewModel, breedItems.get(position - 2));
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return FILTERS;
            case 1:
                return BREED_SEARCH;
            default:
                return BREED;
        }
    }

    @Override
    public int getItemCount() {
        return breedItems.size() + 2;
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
