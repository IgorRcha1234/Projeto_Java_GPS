package com.example.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RouteActivity extends AppCompatActivity {

    private TextView tvSummaryOrigem, tvSummaryDestino, tvViewportLabel, tvMapDescription, tvInstructions;
    private Button btnTabCampus, btnTabBlocoD, btnAlterarRota;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        tvSummaryOrigem = findViewById(R.id.tvSummaryOrigem);
        tvSummaryDestino = findViewById(R.id.tvSummaryDestino);
        tvViewportLabel = findViewById(R.id.tvViewportLabel);
        tvMapDescription = findViewById(R.id.tvMapDescription);
        tvInstructions = findViewById(R.id.tvInstructions);

        btnTabCampus = findViewById(R.id.btnTabCampus);
        btnTabBlocoD = findViewById(R.id.btnTabBlocoD);
        btnAlterarRota = findViewById(R.id.btnAlterarRota);
        btnBack = findViewById(R.id.btnBack);

        String origem = getIntent().getStringExtra("ORIGEM");
        String destino = getIntent().getStringExtra("DESTINO");

        if (origem != null) tvSummaryOrigem.setText(origem);
        if (destino != null) {
            tvSummaryDestino.setText(destino);
            tvInstructions.setText("Rota pronta para iniciar. As orientações até o " + destino + " aparecerão nesta área.");
        }

        btnTabCampus.setOnClickListener(v -> selectTab(true));
        btnTabBlocoD.setOnClickListener(v -> selectTab(false));

        btnAlterarRota.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());
    }

    private void selectTab(boolean isCampus) {
        if (isCampus) {
            btnTabCampus.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnTabCampus.setTextColor(Color.parseColor("#0F172A"));
            btnTabBlocoD.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F1F5F9")));
            btnTabBlocoD.setTextColor(Color.parseColor("#64748B"));

            tvViewportLabel.setText("VISÃO DO CAMPUS");
            tvMapDescription.setText("A visualização do campus será exibida aqui.");
        } else {
            btnTabBlocoD.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnTabBlocoD.setTextColor(Color.parseColor("#0F172A"));
            btnTabCampus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F1F5F9")));
            btnTabCampus.setTextColor(Color.parseColor("#64748B"));

            tvViewportLabel.setText("BLOCO D · TÉRREO");
            tvMapDescription.setText("A planta interna do Bloco D será exibida aqui.");
        }
    }
}