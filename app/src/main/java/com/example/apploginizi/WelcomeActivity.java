package com.example.apploginizi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    private TextView textViewWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        textViewWelcome = findViewById(R.id.textViewWelcome);

        String email = getIntent().getStringExtra("EMAIL");
        textViewWelcome.setText("¡Bienvenido, " + email + "!");
    }

    public void Return (View view) {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
