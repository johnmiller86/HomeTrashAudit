/**
 * Activity for home screen.
 * @author John D. Miller
 * @version 1.0.2
 * @since 12/06/2015
 */
package com.johnmillercoding.hometrashaudit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.johnmillercoding.hometrashaudit.R;
import com.johnmillercoding.hometrashaudit.services.SessionManager;

import java.io.File;


public class HomeScreenActivity extends AppCompatActivity
{

    // Session Manager
    private SessionManager session;

    //Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Options Item Selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if (id == R.id.menuitem_logout){
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Instantiating Session
        session = new SessionManager(getApplicationContext());

        // First time run
        File file = getBaseContext().getFileStreamPath("Settings.txt");
        if (!file.exists())
        {
            Intent i = new Intent(getApplicationContext(), FirstTimeActivity.class);
            startActivity(i);
        }

        // Button Listeners
        Button newAuditButton = (Button)findViewById(R.id.newAuditButton);
        assert newAuditButton != null;
        newAuditButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HomeTrashAuditActivity.class);
                startActivity(i);
            }
        });

        Button viewJournalButton = (Button) findViewById(R.id.viewJournalButton);
        assert viewJournalButton != null;
        viewJournalButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), JournalActivity.class);
                File file = getBaseContext().getFileStreamPath("Journal.txt");
                if(file.exists())
                {
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(HomeScreenActivity.this, "You have not yet created a journal!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button viewTotalsButton = (Button)findViewById(R.id.viewTotalsButton);
        assert viewTotalsButton != null;
        viewTotalsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TotalsActivity.class);

                File file = getBaseContext().getFileStreamPath("Journal.txt");
                if(file.exists())
                {
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(HomeScreenActivity.this, "You must create a journal before viewing totals!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from SQLite users table
     * */
    private void logoutUser() {

        session.setLoggedIn(false);
        session.setUnit("");
        session.setUsername("");

        // Launching the login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
