package com.dimoiu.cosmin.gnbtransactions;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetTransactionsAsyncTask extends AsyncTask<String, Void, HashMap<String,List<Transaction>>>{

    public interface GetTransactionResponse {
        void getTransactionsFinish(HashMap<String,List<Transaction>> output);
    }

    private GetTransactionResponse delegate;

    public GetTransactionsAsyncTask(GetTransactionResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected HashMap<String,List<Transaction>> doInBackground(String... strings) {

        HashMap<String,List<Transaction>> transactions= new HashMap<>();
        try {
            JsonFeed jsonFeed=new JsonFeed(strings[0]);
            JSONArray jsonArray = new JSONArray(jsonFeed.readJSONFeed());
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (transactions.get(jsonObject.getString("sku")) == null) {
                   List<Transaction> list=new ArrayList<>();
                   list.add(new Transaction(jsonObject.getString("sku"),jsonObject.getDouble("amount"),jsonObject.getString("currency"),Exchange.convertToEuro(jsonObject.getString("currency"),jsonObject.getDouble("amount"))));
                   transactions.put(jsonObject.getString("sku"),list);
                }
                else{
                    List<Transaction> list=transactions.get(jsonObject.getString("sku"));
                    list.add(new Transaction(jsonObject.getString("sku"),jsonObject.getDouble("amount"),jsonObject.getString("currency"),Exchange.convertToEuro(jsonObject.getString("currency"),jsonObject.getDouble("amount"))));
                    transactions.put(jsonObject.getString("sku"),list);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    protected void onPostExecute(HashMap<String,List<Transaction>> output) {
        delegate.getTransactionsFinish(output);
    }
}
