package com.dimoiu.cosmin.gnbtransactions;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class GetExchangeRatesAsyncTask extends AsyncTask<String,Void,List<ExchangeRate>>{

    public interface GetExchangeRatesResponse {
        void getExchangeRatesFinish(List<ExchangeRate> exchangeRates);
    }

    private GetExchangeRatesResponse delegate;

    public GetExchangeRatesAsyncTask(GetExchangeRatesResponse delegate){
        this.delegate = delegate;
    }


    @Override
    protected List<ExchangeRate> doInBackground(String... strings) {

        List<ExchangeRate> exchangeRates=new ArrayList<>();
        try {
            JsonFeed jsonFeed=new JsonFeed(strings[0]);
            JSONArray jsonArray = new JSONArray(jsonFeed.readJSONFeed());
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                exchangeRates.add(new ExchangeRate(jsonObject.getString("from"),jsonObject.getString("to"),jsonObject.getDouble("rate")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return exchangeRates;
    }

    @Override
    protected void onPostExecute(List<ExchangeRate> exchangeRates) {
        delegate.getExchangeRatesFinish(exchangeRates);
    }
}
