package edu.northeastern.group12;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * group assignment for week 6
 */
public class WebServiceActivity extends AppCompatActivity {
    Button back; //back button, go to the main page and terminate the search
    Button search_openfda; //send request to web service
    private String drugName; //the name of drug users type in
    private String dosageForm; //the dosage form users type in
    private boolean searchGeneric; //if the users are searching for generics
    Spinner spinnerDrugForm; //spinner that let users choose the dosage form
    Spinner spinnerBrandGeneric; // spinner that let users choose brand name of generic name
    String searchBrandGeneric; //brand name or generic name
    String product_ndc; //GET from api
    String product_type;//GET from api
    String routes;//GET from api
    String manufacturer_name;//GET from api
    String pharm_class_epc;//GET from api
    String active_ingredients;//GET from api
    TextView product_ndc_tv; //text view that shows the product
    TextView product_type_tv;// //text view that shows the type of product
    TextView routes_tv; //text view that shows the routes
    TextView manufacturer_name_tv; //text view that shows the manufacturer name
    TextView pharm_class_epc_tv; //text view that shows the pharm class
    TextView active_ingredients_tv; //text view that shows the active ingredient
    ProgressBar progressCircle; //progressive bar
    String searchQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service);

        product_ndc_tv = findViewById(R.id.product_ndc);
        product_type_tv = findViewById(R.id.product_type);
        routes_tv = findViewById(R.id.routes);
        manufacturer_name_tv = findViewById(R.id.manufacturer_name);
        pharm_class_epc_tv = findViewById(R.id.pharm_class_epc);
        active_ingredients_tv = findViewById(R.id.active_ingredients);
        back = findViewById(R.id.backbutton);
        search_openfda = findViewById(R.id.search_openfda_button);
        spinnerDrugForm = findViewById(R.id.spinnerdrugform);
        spinnerBrandGeneric = findViewById(R.id.spinnerbrandgeneric);
        drugName = ((EditText) findViewById(R.id.drug_name)).getText().toString();
        progressCircle = (ProgressBar) findViewById(R.id.progressBar); // initialize the progress bar

        //initialize savedInstanceState
        init(savedInstanceState);

        final String serviceURL = "https://api.fda.gov/drug/ndc.json?api_key=vXJCXZb1koUtVO6Sk2sio3X7IQHUEBYqgEjwMfKS";
        String endpointURL = endpoint(serviceURL);

        search_openfda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendHttpRequest(endpointURL);
            }
        });

        //sendHttpRequest(serviceURL);

        //activity for back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebServiceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            drugName = savedInstanceState.getString("drugName");
            dosageForm = savedInstanceState.getString("dosageForm");
            searchBrandGeneric = savedInstanceState.getString("searchBrandGeneric");
            product_ndc = savedInstanceState.getString("product_ndc");
            product_type = savedInstanceState.getString("product_type");
            routes = savedInstanceState.getString("routes");
            manufacturer_name = savedInstanceState.getString("manufacturer_name");
            pharm_class_epc = savedInstanceState.getString("pharm_class_epc");
            active_ingredients = savedInstanceState.getString("active_ingredients");
        }
        if (product_ndc != null) {
            product_ndc_tv.setText(product_ndc);
        }
        if (product_type != null) {
            product_type_tv.setText(product_type);
        }
        if (routes != null) {
            routes_tv.setText(routes);
        }
        if (manufacturer_name != null) {
            manufacturer_name_tv.setText(manufacturer_name);
        }
        if (pharm_class_epc != null) {
            pharm_class_epc_tv.setText(pharm_class_epc);
        }
        if (active_ingredients != null) {
            active_ingredients_tv.setText(active_ingredients);
        }
    }


    private void sendHttpRequest(@NonNull String requestURL) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // try to do the HTTP request
                    progressCircle.setVisibility(View.VISIBLE);
                    URL url = new URL(requestURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    // try to get the response as inputStream
                    InputStream in = urlConnection.getInputStream();
                    String result = convertStreamToString(in);

                    System.out.println(result); // check response, testing only

                    // TODO: process the result

                    fetchData(result);

                    // disconnect at end
                    urlConnection.disconnect();
                    progressCircle.setVisibility(View.INVISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Start the thread
        thread.start();
    }

    private static String convertStreamToString(InputStream is) throws IOException {

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private String endpoint(String serviceURL) {
        dosageForm = spinnerDrugForm.getSelectedItem().toString();
        searchBrandGeneric = spinnerBrandGeneric.getSelectedItem().toString();
        searchGeneric = !searchBrandGeneric.equals("BRAND NAME");
        if (!searchGeneric) {
            searchQuery = "patient.drug.openfda.brand_name:\"" + drugName + "\"+AND+patient.drug.openfda.dosage_form:\"" + dosageForm + "\"";
        } else {
            searchQuery = "patient.drug.openfda.generic_name:\"" + drugName + "\"+AND+patient.drug.openfda.dosage_form:\"" + dosageForm + "\"";
        }
        String endpointURL = serviceURL + "&search=" + searchQuery;
        return endpointURL;
    }

    private void fetchData(String json) throws JSONException {
        if (!json.isEmpty()) {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            JSONObject results = jsonArray.getJSONObject(0);
            product_ndc = results.getString("product_ndc");
            product_type = results.getString("product_type");
            manufacturer_name = results.getJSONObject("openfda").getJSONArray("manufacturer_name").getString(0);
            routes = results.getString("route");
            //pharm_class_epc = results.getJSONObject("openfda").getJSONArray("pharm_class_epc").getString(0);
            active_ingredients = results.getJSONArray("active_ingredients").get(0).toString();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (product_ndc != null) {
                        product_ndc_tv.setText(product_ndc);
                    }
                    if (product_type != null) {
                        product_type_tv.setText(product_type);
                    }
                    if (manufacturer_name != null) {
                        manufacturer_name_tv.setText(manufacturer_name);
                    }
                    if (active_ingredients != null) {
                        active_ingredients_tv.setText(active_ingredients);
                    }
                    if (pharm_class_epc != null) {
                        pharm_class_epc_tv.setText(pharm_class_epc);
                    }
                    if (routes != null) {
                        routes_tv.setText(routes);
                    }
                }
            });
        }
    }
}
