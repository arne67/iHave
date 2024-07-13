package com.susarne.ihave2.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
public class NameArrayAdapter extends ArrayAdapter<String> {
    private List<String> items;
    private List<String> filteredItems;

    public NameArrayAdapter(Context context, int resource, List<String> items) {
        super(context, resource);
        this.items = new ArrayList<>(items);  // Gem en kopi af de originale data
        this.filteredItems = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = items;
                    results.count = items.size();
                } else {
                    List<String> localFilteredItems = new ArrayList<>();
                    String filterString = constraint.toString().toLowerCase();

                    for (String item : items) {
                        if (item.toLowerCase().contains(filterString)) {
                            localFilteredItems.add(item);
                        }
                    }

                    results.values = localFilteredItems;
                    results.count = localFilteredItems.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems = (List<String>) results.values;
                clear();
                addAll(filteredItems);
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return (CharSequence) resultValue;
            }
        };
    }
}