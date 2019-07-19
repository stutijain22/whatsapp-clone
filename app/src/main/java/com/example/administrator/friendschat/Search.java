package com.example.administrator.friendschat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView =  findViewById(R.id.searchView);
        listView =  findViewById(R.id.lv1);

        list = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if(list.contains(query)){
                    adapter.getFilter().filter(query);
                    Intent contact = new Intent(Search.this,Contacts.class);
                    startActivity(contact);
                }
                else
                {
                    Toast.makeText(Search.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //    adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Search.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}