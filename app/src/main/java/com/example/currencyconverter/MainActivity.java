package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    EditText amt;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView6);
        amt =findViewById(R.id.amt);
        final Button btnConvert = findViewById(R.id.button4);
        String[] arraySpinner = new String[] {
                "USD", "EUR", "GBP", "INR", "AUD", "CAD", "SGD", "CHF", "MYR", "JPY","CNY", "HKD", "BRL", "MXN", "ZAR", "RUB", "SAR", "AED", "IDR", "PHP"
        };
        Spinner s = findViewById(R.id.spinner3);
        Spinner s1 = findViewById(R.id.spinner5);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s1.setAdapter(adapter);
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = s.getSelectedItem().toString();
                String to = s1.getSelectedItem().toString();
                String amount = amt.getText().toString();

                if(amt.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please enter the amount", Toast.LENGTH_SHORT).show();
                }
                else {
                     new JsonTask().execute("https://api.fastforex.io/convert?from=" + from + "&to=" + to + "&amount=" + amount + "&api_key=aa218d96df-4905672943-rpeuwr");
                }
            }
        });

        }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            int n=result.length();
            int count=0,flag=0;
            String res = "";
            for(int i=0;i<n;i++) {
                if (result.charAt(i) == ':') {
                    count++;
                }
                if(count==4 && result.charAt(i)==',')
                {
                    flag=1;
                }
                if(count==4 && flag==0)
                {
                    res += result.charAt(i);
                }
            }
            textView.setText(res);
        }
    }
}