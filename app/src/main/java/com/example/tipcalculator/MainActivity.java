package com.example.tipcalculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {

    private EditText billAmountEditText;
    private TextView tipTextView;
    private TextView totalTextView;
    private TextView percentTextView;

    private Button percentUpButton;
    private Button percentDownButton;

    private SharedPreferences savedValues;

    private String billAmountString = "";
    private float tipPercent = .15f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //use `CTRL + Click` to see where these values are being assigned to.
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);

        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);

        //the .set... method matches the method used
        billAmountEditText.setOnEditorActionListener(this);

        //the .set... method matches the method used
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);

        //get the shared Preferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

    } //End onCreate

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
            //tipTextView.setText("$10.00");
            //totalTextView.setText("$100");
            calculateAndDisplay();
        }
        return false;

    }//End onEditorAction

    public void calculateAndDisplay(){
        //Get bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if(billAmountString.equals("")){
            billAmount = 0;
        } else{
            billAmount = Float.parseFloat(billAmountString);
        }

        //Calculate tip & total
        float tipAmount = (billAmount * tipPercent);
        float totalAmount = billAmount + tipAmount;

        //Display the results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));

        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));

    }//End calculateAndDisplay

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.percentDownButton:
                tipPercent -= .01f;
                calculateAndDisplay();
                break;
            case R.id.percentUpButton:
                tipPercent += .01f;
                calculateAndDisplay();
                break;
        }//End switch
    }//End onClick

    @Override
    public void onPause(){
        super.onPause();

        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();
    }

    @Override
    public void onResume(){
        super.onResume();

        //The method getString / getFloat uses a ("key", "defValue")
        //use get method to get the bill amount & tip percent strings
        billAmountString = savedValues.getString("billAmountString", "");
        //Editor does not allow double so use float
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);
        //set the bill amount on its widget
        billAmountEditText.setText(billAmountString);
        calculateAndDisplay();
    }

}//End MainActivity