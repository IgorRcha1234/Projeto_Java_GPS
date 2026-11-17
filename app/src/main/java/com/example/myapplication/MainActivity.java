
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner spOrigem;
    private Spinner spDestino;

    private Button btnTracarRota;
    private Switch swAcessibilidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Componentes da interface
        spOrigem = findViewById(R.id.spOrigem);
        spDestino = findViewById(R.id.spDestino);

        btnTracarRota = findViewById(R.id.btnTracarRota);

        swAcessibilidade =
                findViewById(R.id.swAcessibilidade);

        // Entradas da universidade
        String[] origens = {
                "Entrada Bloco C",
                "Entrada Bloco B"
        };

        // Destinos dos dois andares
        String[] destinos = {

                // Térreo
                "Laboratório 01",
                "Laboratório 02",
                "Laboratório 03",

                // Segundo andar
                "Laboratório 04",
                "Laboratório 05",
                "Sala 201",
                "Sala 202",
                "Sala 203",
                "Biblioteca",
                "Sala de Estudos"
        };

        // Adapter de origem
        ArrayAdapter<String> adapterOrigem =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        origens
                );

        adapterOrigem.setDropDownViewResource(
                R.layout.spinner_item
        );

        // Adapter de destino
        ArrayAdapter<String> adapterDestino =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        destinos
                );

        adapterDestino.setDropDownViewResource(
                R.layout.spinner_item
        );

        spOrigem.setAdapter(adapterOrigem);
        spDestino.setAdapter(adapterDestino);

        // Botão traçar rota
        btnTracarRota.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RouteActivity.class
            );

            // Envia a origem
            intent.putExtra(
                    "ORIGEM",
                    spOrigem.getSelectedItem().toString()
            );

            // Envia o destino
            intent.putExtra(
                    "DESTINO",
                    spDestino.getSelectedItem().toString()
            );

            // Envia a preferência de acessibilidade
            intent.putExtra(
                    "ACESSIBILIDADE",
                    swAcessibilidade.isChecked()
            );

            startActivity(intent);
        });
    }
}
