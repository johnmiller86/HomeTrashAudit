/**
 * Activity for date selection.
 * @author John D. Miller
 * @version 1.0.1
 * @since 12/06/2015
 */
package com.example.chefj.hometrashaudit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        // Ok button listener
        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePicker picker = (DatePicker) findViewById(R.id.datePicker);
                DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT);
                int day = picker.getDayOfMonth();
                int month = picker.getMonth();
                int year = picker.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                Date date = calendar.getTime();
                String dat = df.format(date);
                HomeTrashAuditActivity.dateText.setText(dat);
                finish();
            }
        });

        // Cancel button listener
        Button cancelButton = (Button) findViewById((R.id.cancelButton));
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
