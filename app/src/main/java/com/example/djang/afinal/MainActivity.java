package com.example.djang.afinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "FinalProject:Main";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set up a queue for our Volley requests
        requestQueue = Volley.newRequestQueue(this);

        // Load the main layout for our activity
        setContentView(R.layout.activity_main);

        Button randomButton = findViewById(R.id.buttonRandom);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Random Button was pressed.");
                startAPICall(true);
            }
        });

        Button saveButton = findViewById(R.id.buttonGenerate);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Generate Button was pressed.");
                startAPICall(false);
            }
        });

        Spinner dropdown = findViewById(R.id.dropdownOptions);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(myAdapter);
    }

    private void startAPICall(boolean random) {
        try {
            String url = "http://numbersapi.com/";

            Spinner spinner = findViewById(R.id.dropdownOptions);
            String type = spinner.getSelectedItem().toString().toLowerCase();
            if (random) url += "random/";
            else {
                EditText settingsView = findViewById(R.id.settingInput);
                String number = settingsView.getText().toString();
                // check that number is valid (mm/dd, m/dd, m/d, mm/d; yyyy; number) for the type
                url += number + "/";
            }

            url += type + "?json";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "http://numbersapi.com/random/math?json",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            //apiCallDone(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    ((TextView) findViewById(R.id.textDisplay)).setText(getString(R.string.ApiError));
                    Log.w(TAG, error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
