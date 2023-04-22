package com.example.trevorsandroidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.trevorsandroidapplication.database.UserDatabase;
import com.example.trevorsandroidapplication.database.WeightDatabase;
import com.example.trevorsandroidapplication.pages.SignInPage;

public class WeightAppMain extends AppCompatActivity {
    private UserDatabase userDatabase;
    private WeightDatabase weightDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Registering class instance variables (dependencies)
        userDatabase = new UserDatabase(getBaseContext());
        weightDatabase = new WeightDatabase(getBaseContext());

        // Registering initial sign in page instance
        new SignInPage(this).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (userDatabase != null) {
            userDatabase.close();
        }

        if (weightDatabase != null) {
            weightDatabase.close();
        }
    }

    public UserDatabase getUserDatabase() {
        return userDatabase;
    }

    public WeightDatabase getWeightDatabase() {
        return weightDatabase;
    }
}