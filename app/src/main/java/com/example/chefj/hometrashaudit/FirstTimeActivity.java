package com.example.chefj.hometrashaudit;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FirstTimeActivity extends AppCompatActivity {

    @Override
    public void onBackPressed()
    {
        // Do nothing disable back button until units are selected.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Creating settings file
                try {
                    FileOutputStream os = openFileOutput("Settings.txt", Context.MODE_PRIVATE);
                    PrintWriter output = new PrintWriter(os);
                    Spinner spinner = (Spinner) findViewById(R.id.unitSpinner);
                    output.println(spinner.getSelectedItem().toString());
                    output.flush();
                    output.close();
                    finish();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
