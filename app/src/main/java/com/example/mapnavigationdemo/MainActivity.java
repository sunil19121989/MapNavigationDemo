package com.example.mapnavigationdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity {
    String Lat, Long, Name;
    TextView text_mode, text_car, text_train;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_mode = (TextView) findViewById(R.id.text_mode);
        text_car = (TextView) findViewById(R.id.text_car);
        text_train = (TextView) findViewById(R.id.text_train);
        if (checkNetworkConnection(this))
            new Modetask().execute();
        else {
            Toast.makeText(this, "No Network Available", Toast.LENGTH_LONG).show();
        }

        Button btn_navigate = (Button) findViewById(R.id.btn_navigate);
        btn_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);
                mapIntent.putExtra("Lat", Lat);
                mapIntent.putExtra("Long", Long);
                mapIntent.putExtra("Mode", Name);
                startActivity(mapIntent);
            }
        });
    }

    class SpinnerAdapter extends ArrayAdapter<SampleModel.Sample> {
        ArrayList<SampleModel.Sample> objects = null;

        public SpinnerAdapter(Context context, int resource,
                              ArrayList<SampleModel.Sample> objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        @Override
        public View getDropDownView(int position, View v, ViewGroup parent) {
            return getCustomView(position, v, parent);
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            return getCustomView(position, v, parent);
        }

        public View getCustomView(int position, View v, ViewGroup parent) {
            ViewHolder holder;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.item_list,
                        parent, false);
                holder = new ViewHolder();
                holder.txt_item = (TextView) v
                        .findViewById(R.id.txt_item);
                v.setTag(holder);
            } else
                holder = (ViewHolder) v.getTag();
            holder.txt_item.setText(objects.get(position).getName());
            return v;

        }


        class ViewHolder {
            TextView txt_item;
        }
    }

    public boolean checkNetworkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected() || mobile.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    class Modetask extends AsyncTask<Void, Void, Void> {
        ArrayList<SampleModel.Sample> ModelList = null;

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection c = null;
            try {
                URL u = new URL("http://express-it.optusnet.com.au/sample.json");
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Content-length", "0");
                c.setUseCaches(false);
                c.setAllowUserInteraction(false);
                c.setConnectTimeout(30000);
                c.setReadTimeout(30000);
                c.connect();
                int status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        InputStream is = new BufferedInputStream(c.getInputStream());
                        Reader reader = new InputStreamReader(is);
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        Type listType = new TypeToken<ArrayList<SampleModel.Sample>>() {
                        }.getType();
                        ModelList = (ArrayList<SampleModel.Sample>) gson.fromJson(reader, listType);

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null)
                    c.disconnect();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ModelList != null) {
                Spinner sp_sortlbl = (Spinner) findViewById(R.id.sp_sortlbl);
                setValues(ModelList, 0);
                sp_sortlbl.setAdapter(new SpinnerAdapter(MainActivity.this, R.layout.item_list, ModelList));
                sp_sortlbl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View v,
                                               int position, long id) {
                        setValues(ModelList, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
            }
        }
    }

    public void setValues(ArrayList<SampleModel.Sample> ModelList, int position) {
        Lat = ModelList.get(position).getLocationObj().getLatitude();
        Long = ModelList.get(position).getLocationObj().getLongitude();
        Name = ModelList.get(position).getName();
        if (!TextUtils.isEmpty(ModelList.get(position).getName()))
            text_mode.setText("Mode of Transport: " + ModelList.get(position).getName());
        if (!TextUtils.isEmpty(ModelList.get(position).getFromcentralObj().getCar()))
            text_car.setText("Car - " + ModelList.get(position).getFromcentralObj().getCar());
        if (!TextUtils.isEmpty(ModelList.get(position).getFromcentralObj().getTrain()))
            text_train.setText("Train - " + ModelList.get(position).getFromcentralObj().getTrain());
    }
}
