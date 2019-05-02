package com.example.shoppinglist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.ArraySet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingListActivity extends AppCompatActivity {

    @BindView(R.id.itemName_ET)
    EditText itemName;
    @BindView(R.id.itemList)
    ListView itemList;
    @BindView(R.id.itemSpinner)
    Spinner itemSpinner;

    private List<String> listItems;
    private Set<String> spinnerItems;

    private static final String LIST_ITEMS_KEY = "LIST_ITEMS_KEY";
    private static final String LIST_SPINNER_KEY = "LIST_SPINNER_KEY";
    private static final String SHOPPING_LIST_KEY = "SHOPPING_LIST_KEY";

    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shopping_list);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listItems = new ArrayList<>();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemName.getText() != null && !itemName.getText().toString().trim().isEmpty()) {
                    listItems.add(itemName.getText().toString());
                    itemName.setText("");
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sp = getSharedPreferences(SHOPPING_LIST_KEY, MODE_PRIVATE);
//        listItems  = sp.getStringSet(LIST_ITEMS_KEY, new ArraySet<String>());
        spinnerItems  = sp.getStringSet(LIST_SPINNER_KEY, new ArraySet<String>());

        listAdapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1
                , listItems);

        itemList.setAdapter(listAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getSharedPreferences(SHOPPING_LIST_KEY, MODE_PRIVATE).edit();
//        editor.putStringSet(LIST_ITEMS_KEY, listItems);
        editor.putStringSet(LIST_SPINNER_KEY, spinnerItems);
        editor.commit(); // UWAGA Może przywiesić wątek, można użyć .apply() działa w innym wątku, a nie chcemy odczytania przed zapisem
    }
}
