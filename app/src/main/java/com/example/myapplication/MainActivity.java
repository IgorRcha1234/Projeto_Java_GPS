package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner spOrigem, spDestino;
    private Button btnTracarRota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spOrigem = findViewById(R.id.spOrigem);
        spDestino = findViewById(R.id.spDestino);
        btnTracarRota = findViewById(R.id.btnTracarRota);

        String[] origens = {"Entrada Bloco C", "Entrada Bloco B"};
        String[] destinos = {"Laboratório 01", "Laboratório 02", "Laboratório 03"};

        // Utiliza o spinner_item.xml personalizado em vez do padrão do Android
        ArrayAdapter<String> adapterOrigem = new ArrayAdapter<>(this, R.layout.spinner_item, origens);
        adapterOrigem.setDropDownViewResource(R.layout.spinner_item);

        ArrayAdapter<String> adapterDestino = new ArrayAdapter<>(this, R.layout.spinner_item, destinos);
        adapterDestino.setDropDownViewResource(R.layout.spinner_item);

        spOrigem.setAdapter(adapterOrigem);
        spDestino.setAdapter(adapterDestino);

        btnTracarRota.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RouteActivity.class);
            intent.putExtra("ORIGEM", spOrigem.getSelectedItem().toString());
            intent.putExtra("DESTINO", spDestino.getSelectedItem().toString());
            startActivity(intent);
        });
    }
}