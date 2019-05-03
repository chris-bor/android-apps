package com.example.androidjavaapps;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ApiActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.htmlDataButton)
    Button htmlDataButton;
    @BindView(R.id.resultTextView)
    TextView resultTextView;
    @BindView(R.id.jsonDataButton)
    Button jsonDataButton;

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.INVISIBLE);
        requestQueue = Volley.newRequestQueue(this);
        htmlDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        "https://www.google.pl/",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                resultTextView.setText(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                resultTextView.setText("!!!"+error.getMessage());
                            }
                        });
                requestQueue.add(stringRequest);
            }
        });
        jsonDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ThreadClass().execute("URL", "URL2", "URL3");
            }
        });
    }

    private class ThreadClass extends AsyncTask<String, Integer, Float> {
        @Override // operates in user thread (main thread)
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            resultTextView.setText("" + System.currentTimeMillis());
            super.onPreExecute();
        }

        @Override // operates in user thread
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override  // operates in own thread
        protected Float doInBackground(String... params) {
            String url = params[0];
            String a = url;
            for (int i = 0; i < 100000; i++) {
                a = a + url;
                if (i % 1000 == 0)
                    publishProgress(i / 1000);
            }
            return 0.0F;
        }

        @Override
        protected void onPostExecute(Float f) {
            progressBar.setVisibility(View.INVISIBLE);
            resultTextView.setText(resultTextView.getText() + " " + System.currentTimeMillis());
            super.onPostExecute(f);
        }

        @Override // cancel(true) in doInBackground is needed
        protected void onCancelled(Float f) {
            super.onCancelled(f);
        }
    }
}
