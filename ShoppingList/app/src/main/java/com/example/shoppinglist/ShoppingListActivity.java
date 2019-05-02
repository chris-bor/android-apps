package com.example.shoppinglist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

    private Set<String> listItems;
    private Set<String> spinnerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shopping_list);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences.Editor editor = getSharedPreferences("", MODE_PRIVATE).edit();
        editor.putString("", "Jakaś inna wiadomość");
        editor.commit(); // UWAGA Może przywiesić wątek, można użyć .apply()
    }
}
