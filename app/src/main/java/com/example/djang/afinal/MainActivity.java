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

import org.json.JSONException;
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
                try {
                    startAPICall(false);
                } catch (Exception ignored) { }
            }
        });

        // create a dropdown list of options for the type of fact
        Spinner dropdown = findViewById(R.id.dropdownOptions);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(myAdapter);
    }

    private void startAPICall(final boolean random) {
        try {
            String url = "http://numbersapi.com/";

            Spinner spinner = findViewById(R.id.dropdownOptions);
            String type = spinner.getSelectedItem().toString().toLowerCase();
            if (random) url += "random/";
            else {
                EditText settingsView = findViewById(R.id.settingInput);
                String number = settingsView.getText().toString();
                TextView out = findViewById(R.id.textDisplay);
                switch (type) {
                    case "date":
                        if (number.matches("\\d*?/\\d*")) {
                            String[] nums = number.split("/");
                            int mo = Integer.parseInt(nums[0]);
                            int d = Integer.parseInt(nums[1]);
                            if (mo < 1 || mo > 12) {
                                out.setText(getString(R.string.invalidMonth));
                                throw new IllegalStateException();
                            }
                            switch (mo) {
                                case 1:
                                case 3:
                                case 5:
                                case 7:
                                case 8:
                                case 10:
                                case 12:
                                    if (d < 1 || d > 31) {
                                        out.setText(getString(R.string.invalidDay));
                                        throw new IllegalStateException();
                                    }
                                    break;
                                case 4:
                                case 6:
                                case 9:
                                case 11:
                                    if (d < 1 || d > 30) {
                                        out.setText(getString(R.string.invalidDay));
                                        throw new IllegalStateException();
                                    }
                                    break;
                                case 2:
                                    if (d < 1 || d > 29) {
                                        out.setText(getString(R.string.invalidDay));
                                        throw new IllegalStateException();
                                    }
                                    break;
                            }
                            break;
                        }
                    default:
                        try {
                            //noinspection ResultOfMethodCallIgnored, just want to check if it parses
                            Integer.parseInt(number);
                        } catch (NumberFormatException e) {
                            out.setText(getString(R.string.invalidNumber));
                            throw new IllegalStateException();
                        }
                }
                url += number + "/";
            }

            url += type + "?json";
            Log.d(TAG, url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            apiCallDone(response, random);
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

    private void apiCallDone(JSONObject response, boolean random) {
        try {
            if (random) {
                EditText in = findViewById(R.id.settingInput);
                in.setText(response.get("number").toString());
            }
            TextView out = findViewById(R.id.textDisplay);
            out.setText(response.get("text").toString());
        } catch (JSONException ignored) { }
    }
}
