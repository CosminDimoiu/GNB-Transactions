package com.dimoiu.cosmin.gnbtransactions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        List<String> transactions= (ArrayList<String>)getIntent().getSerializableExtra("transactions");
        int amount=getIntent().getIntExtra("amount",0);
        TextView textView=findViewById(R.id.textView);
        textView.setText("Total amount:  "+amount+"  Euro");
        ListView listView=findViewById(R.id.transactions);
        ArrayAdapter adapter=new ArrayAdapter(this,R.layout.listview_items,R.id.list_text,transactions);
        listView.setAdapter(adapter);
    }
}
