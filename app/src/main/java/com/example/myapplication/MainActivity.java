package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- BOTÕES DO TÉRREO (Andar 1) ---
        configurarBotaoDestino(R.id.btnLab01, "Laboratório 01", 1);
        configurarBotaoDestino(R.id.btnLab02, "Laboratório 02", 1);
        configurarBotaoDestino(R.id.btnLab03, "Laboratório 03", 1);

        // --- BOTÕES DO SEGUNDO ANDAR (Andar 2) ---
        configurarBotaoDestino(R.id.btnLab04, "Laboratório 04", 2);
        configurarBotaoDestino(R.id.btnLab05, "Laboratório 05", 2);
        configurarBotaoDestino(R.id.btnSala201, "Sala 201", 2);
        configurarBotaoDestino(R.id.btnSala202, "Sala 202", 2);
        configurarBotaoDestino(R.id.btnSala203, "Sala 203", 2);
        configurarBotaoDestino(R.id.btnBiblioteca, "Biblioteca", 2);
    }

    private void configurarBotaoDestino(int buttonId, String nomeDestino, int andar) {
        Button btn = findViewById(buttonId);
        if (btn != null) {
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, RouteActivity.class);
                intent.putExtra("DESTINO", nomeDestino);
                intent.putExtra("ANDAR", andar);
                startActivity(intent);
            });
        }
    }
}