package com.example.shoppinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ShoppingListAdapter<String> extends ArrayAdapter {
    private List<String> objects;
    private Context  context;
    private int resource;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> spinnerItems;
    private Spinner spinner;

    public ShoppingListAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    public ShoppingListAdapter(@NonNull Context context, int resource, @NonNull List objects,
                               ArrayAdapter<String> spinnerAdapter, List<String> spinnerItems,
                               Spinner spinner) {
        this(context, resource, objects);
        this.spinnerAdapter = spinnerAdapter;
        this.spinnerItems = spinnerItems;
        this.spinner = spinner;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_shopping_list, parent, false);
        TextView name = rowView.findViewById(R.id.name_ET);
        name.setText(""+objects.get(position));
        CheckBox selected  = rowView.findViewById(R.id.selected_CB);
        selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerItems.add(objects.get(position));
                spinnerItems.remove("");
                spinnerItems.add((String) "");
                spinner.setSelection(spinnerItems.indexOf(""));
                spinnerAdapter.notifyDataSetChanged();
                objects.remove(position);
                notifyDataSetChanged();
            }
        });
        return rowView;
    }
}
