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

    /* This is the main activity where are displayed all transactions. Here we use custom AsyncTasks to bring data from the given web services. */

public class MainActivity extends AppCompatActivity{
    GetExchangeRatesAsyncTask getExchangeRatesAsyncTask;
    GetTransactionsAsyncTask getTransactionsAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* First we try to read all exchange rates from the given web service using a dedicated AsyncTask. */
        getExchangeRatesAsyncTask= (GetExchangeRatesAsyncTask) new GetExchangeRatesAsyncTask((List<ExchangeRate> output) -> {
            Exchange.setExchangeRates(output);

            /* After that AsyncTask finishes the job in background, in post execute we try to get all the transactions from the given web service using another dedicated AsyncTask.
               We do that here because the second AsyncTask needs data given by the first one. This means it has to wait until the first one finishes.
               When the second AsyncTask finishes, in post execute we modify the UI with data retrieved from both AsyncTasks.*/
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
