package com.example.afinal;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spinnerRole;
    private AppDatabase db;
    private UserDao userDao;

    private String currentName = "";
    private String currentRole = "کاربر عادی";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getDatabase(this);
        userDao = db.userDao();

        etName = findViewById(R.id.etName);
        spinnerRole = findViewById(R.id.spinnerRole);

        String[] roles = new String[]{"کاربر عادی", "مدیر سیستم", "کاربر مهم"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        loadUserData();

        etName.setOnKeyListener((v, keyCode, event) -> {
            currentName = etName.getText().toString();
            return false;
        });

        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentRole = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadUserData() {
        new Thread(() -> {
            UserEntity user = userDao.getUser();

            runOnUiThread(() -> {
                if (user != null) {
                    currentName = user.getUserName();
                    currentRole = user.getUserRole();

                    etName.setText(currentName);

                    int pos = ((ArrayAdapter) spinnerRole.getAdapter()).getPosition(currentRole);
                    if (pos >= 0) spinnerRole.setSelection(pos);
                }
            });
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveUserData();
    }

    private void saveUserData() {
        new Thread(() -> {
            String nameToSave = etName.getText().toString().trim();

            UserEntity user = new UserEntity(nameToSave, currentRole);
            user.setId(1);

            userDao.insert(user);

        }).start();
    }
}