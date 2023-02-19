package edu.northeastern.group12;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        progressCircle =(ProgressBar)findViewById(R.id.progressBar); // initialize the progress bar
        init(savedInstanceState);//initialize savedInstanceState

        //activity for back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebServiceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void init(Bundle savedInstanceState) {
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
        if(product_ndc!= null){ product_ndc_tv.setText(product_ndc);}
        if(product_type!= null){ product_type_tv.setText(product_type);}
        if(routes!= null){ routes_tv.setText(routes);}
        if(manufacturer_name!= null){ manufacturer_name_tv.setText(manufacturer_name);}
        if(pharm_class_epc!= null){ pharm_class_epc_tv.setText(pharm_class_epc);}
        if(active_ingredients!= null){ active_ingredients_tv.setText(active_ingredients);}
    }

    public void startProgressBar() {
        progressCircle.setVisibility(View.VISIBLE);
    }
}
