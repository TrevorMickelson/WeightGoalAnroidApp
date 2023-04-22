package com.example.trevorsandroidapplication.pages;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;

import com.example.trevorsandroidapplication.R;
import com.example.trevorsandroidapplication.WeightAppMain;
import com.example.trevorsandroidapplication.util.AppUtil;

public class SignInPage extends AppPage {
    private static final int MIN_INPUT_SIZE = 5;
    private final EditText userNameButton;
    private final EditText passwordButton;
    private final Button loginButton;

    public SignInPage(WeightAppMain main) {
        super(main);
        this.userNameButton = main.findViewById(R.id.username);
        this.passwordButton = main.findViewById(R.id.password);
        this.loginButton = main.findViewById(R.id.login);
    }

    @Override
    public void init() {
        loginButton.setOnClickListener(view -> {
            String currentName = userNameButton.getText().toString();
            if (!isValidInput(currentName)) {
                userNameButton.setError("Username invalid, try again!");
                return;
            }

            String currentPassword = passwordButton.getText().toString();
            if (!isValidInput(currentPassword)) {
                passwordButton.setError("Password invalid, try again!");
                return;
            }

            main.getUserDatabase().getUser(currentName, currentPassword).thenAccept(user -> {
                AppUtil.runSync(() -> {
                    main.setContentView(R.layout.data_grid);
                    new DataGridPage(main, user).init();
                });
            });
        });
    }

    private boolean isValidInput(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        return input.length() >= MIN_INPUT_SIZE;
    }
}

