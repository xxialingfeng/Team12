package edu.northeastern.group12;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private static String[] queryResult;
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
    private final Handler textHandler = new Handler();


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

        search_openfda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drugName = ((EditText) findViewById(R.id.drug_name)).getText().toString();
                progressCircle.setVisibility(View.VISIBLE);
                runCallThread(view);
            }
        });

        spinnerDrugForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] drugForms = getResources().getStringArray(R.array.drugformspinner);
                dosageForm = drugForms[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerBrandGeneric.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] brandGeneric = getResources().getStringArray(R.array.brandgenericspinner);
                searchBrandGeneric = brandGeneric[position];
                searchGeneric = !searchBrandGeneric.equals("BRAND NAME");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchGeneric = true;
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("drugName", drugName);
        outState.putString("dosageForm", dosageForm);
        outState.putString("searchBrandGeneric", searchBrandGeneric);
        outState.putString("product_ndc", product_ndc);
        outState.putString("product_type", product_type);
        outState.putString("routes", routes);
        outState.putString("manufacturer_name", manufacturer_name);
        outState.putString("pharm_class_epc", pharm_class_epc);
        outState.putString("active_ingredients", active_ingredients);
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


    protected String[] sendHttpRequest() {
        String[] results = new String[6];
        URL url;
        String ndcURL;
        try {
            if (searchGeneric) {
                Log.e("searchGeneric ", "true");
                ndcURL = "https://api.fda.gov/drug/ndc.json?search=generic_name:\"" + drugName + "\"+AND+dosage_form:\"" + dosageForm + "\"&limit=1";
            } else {
                Log.e("searchGeneric ", "false");
                ndcURL = "https://api.fda.gov/drug/ndc.json?search=brand_name:\"" + drugName + "\"+AND+dosage_form:\"" + dosageForm + "\"&limit=1";
            }
            Log.e("ndcURL", ndcURL);
            url = new URL(ndcURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            final String response = convertStreamToString(inputStream);
            JSONObject jObject = new JSONObject(response);
            JSONObject drugResults = jObject.getJSONArray("results").getJSONObject(0);
            product_ndc = drugResults.getString("product_ndc");
            product_type = drugResults.getString("product_type").replace("[", "").replace("]", "").replace("\"", "");
            routes = drugResults.getString("route").replace("[", "").replace("]", "").replace("\"", "");
            JSONObject openfda = drugResults.getJSONObject("openfda");
            manufacturer_name = openfda.getString("manufacturer_name").replace("[", "").replace("]", "").replace("\"", "");
            pharm_class_epc = openfda.getJSONArray("pharm_class_epc").toString().replace("[", "").replace("]", "").replace("\"", "");
            JSONArray active_ingredients_array = drugResults.getJSONArray("active_ingredients");
            ArrayList<String> active_ingredients_list = new ArrayList<>();
            for (int i = 0; i < active_ingredients_array.length(); i++) {
                active_ingredients_list.add(active_ingredients_array.getJSONObject(i).getString("name").replace("\"", ""));
            }
            active_ingredients = active_ingredients_list.toString().replace("[", "").replace("]", "").replace("\"", "");
            if (active_ingredients_array.length() > 3) {
                active_ingredients_list = new ArrayList<>();
                active_ingredients_list.add(active_ingredients_array.getJSONObject(0).getString("name").replace("\"", ""));
                active_ingredients_list.add(active_ingredients_array.getJSONObject(1).getString("name").replace("\"", ""));
                active_ingredients_list.add(active_ingredients_array.getJSONObject(2).getString("name").replace("\"", ""));
                active_ingredients = active_ingredients_list.toString().replace("[", "").replace("]", "").replace("\"", "");
                active_ingredients = active_ingredients + " - (And more)";
            }

            results[0] = product_ndc;
            results[1] = product_type;
            results[2] = routes;
            results[3] = manufacturer_name;
            results[4] = pharm_class_epc;
            results[5] = active_ingredients;
            Log.e("results NDC: ", "product_ndc" + product_ndc);
            return results;

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        results[0] = "error";
        return results;

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


    class runnableThread implements Runnable {
        @Override
        public void run() {
            if (drugName != null) {
                queryResult = sendHttpRequest();
                boolean valid = isValidAPICall();
                if (valid) {
                    Log.e("openFDA", "valid api call");
                    textHandler.post(() -> {
                        product_ndc_tv.setText(product_ndc);
                        product_type_tv.setText(product_type);
                        routes_tv.setText(routes);
                        manufacturer_name_tv.setText(manufacturer_name);
                        pharm_class_epc_tv.setText(pharm_class_epc);
                        active_ingredients_tv.setText(active_ingredients);
                    });
                } else {
                    Log.e("openFDA", "invalid api call");
                    textHandler.post(() -> {
                        product_ndc_tv.setText("Invalid Search");
                        product_type_tv.setText("Invalid Search");
                        routes_tv.setText("Invalid Search");
                        manufacturer_name_tv.setText("Invalid Search");
                        pharm_class_epc_tv.setText("Invalid Search");
                        active_ingredients_tv.setText("Invalid Search");
                        Toast.makeText(WebServiceActivity.this, R.string.search_error, Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                textHandler.post(() -> {
                    product_ndc_tv.setText("Invalid Search");
                    product_type_tv.setText("Invalid Search");
                    routes_tv.setText("Invalid Search");
                    manufacturer_name_tv.setText("Invalid Search");
                    pharm_class_epc_tv.setText("Invalid Search");
                    active_ingredients_tv.setText("Invalid Search");
                    Toast.makeText(WebServiceActivity.this, R.string.search_error, Toast.LENGTH_SHORT).show();
                });
            }
            progressCircle.setVisibility(View.INVISIBLE);
        }
    }

    public void runCallThread(View view) {
        runnableThread callThread = new runnableThread();
        long endTime = System.currentTimeMillis()+10;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        }
        new Thread(callThread).start();
    }

    public static boolean isValidAPICall() {
        String error = queryResult[0];
        return error != null && !error.equals("error");
    }
}
