package com.dimoiu.cosmin.gnbtransactions;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    GetExchangeRatesAsyncTask getExchangeRatesAsyncTask;
    GetTransactionsAsyncTask getTransactionsAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getExchangeRatesAsyncTask= (GetExchangeRatesAsyncTask) new GetExchangeRatesAsyncTask((List<ExchangeRate> output) -> {
            Exchange.setExchangeRates(output);

            getTransactionsAsyncTask= (GetTransactionsAsyncTask) new GetTransactionsAsyncTask((HashMap<String,List<Transaction>> result)->{
                HashMap<String,List<Transaction>> transactions=result;
                ListView listView=findViewById(R.id.transactions);
                ArrayAdapter adapter=new ArrayAdapter(this,R.layout.listview_items,R.id.list_text,transactions.keySet().toArray());
                listView.setAdapter(adapter);

                listView.setOnItemClickListener((adapterView, view, i, l) -> {

                    Intent intent=new Intent(this, TransactionActivity.class);
                    List<String> transactionsToString = new ArrayList<>();
                    int totalAmount=0;
                    for(int j=0;j<transactions.get(listView.getItemAtPosition(i).toString()).size();j++){
                        transactionsToString.add("Transaction number "+(j+1)+":   "+transactions.get(listView.getItemAtPosition(i).toString()).get(j).getAmountInEuro()+"  Euro");
                        totalAmount+=transactions.get(listView.getItemAtPosition(i).toString()).get(j).getAmountInEuro();
                    }

                    intent.putExtra("transactions", (Serializable) transactionsToString);
                    intent.putExtra("amount",totalAmount);
                    startActivity(intent);
                });

            }).execute("http://gnb.dev.airtouchmedia.com/transactions.json");

        }).execute("http://gnb.dev.airtouchmedia.com/rates.json");
    }
}
