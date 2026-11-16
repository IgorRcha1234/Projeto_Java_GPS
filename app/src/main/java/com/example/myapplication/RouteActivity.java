package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Node;
import com.example.myapplication.navigation.BlocoDAndar2GraphFactory;
import com.example.myapplication.navigation.BlocoDGraphFactory;
import com.example.myapplication.navigation.BuildingGraph;
import com.example.myapplication.navigation.NavigationInstructions;
import com.example.myapplication.navigation.RouteCalculator;
import com.example.myapplication.view.IndoorMapView;

import java.util.Collections;
import java.util.List;

public class RouteActivity extends AppCompatActivity {

    private TextView tvSummaryOrigem;
    private TextView tvSummaryDestino;
    private TextView tvViewportLabel;
    private TextView tvInstructions;
    private Button btnAlterarRota;
    private IndoorMapView indoorMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        tvSummaryOrigem = findViewById(R.id.tvSummaryOrigem);
        tvSummaryDestino = findViewById(R.id.tvSummaryDestino);
        tvViewportLabel = findViewById(R.id.tvViewportLabel);
        tvInstructions = findViewById(R.id.tvInstructions);
        btnAlterarRota = findViewById(R.id.btnAlterarRota);
        indoorMapView = findViewById(R.id.indoorMapView);

        String destino = getIntent().getStringExtra("DESTINO");
        int andar = getIntent().getIntExtra("ANDAR", 0);

        Log.d("ROTA_DEBUG", "Destino recebido: " + destino + " | Andar: " + andar);

        if (destino != null && !destino.isEmpty()) {
            tvSummaryDestino.setText(destino);
        } else {
            tvSummaryDestino.setText("Laboratório 01");
            destino = "Laboratório 01";
        }

        if (andar == 2) {
            tvSummaryOrigem.setText("Acesso do 2º Andar");
            tvViewportLabel.setText("BLOCO D · 2º ANDAR");

            indoorMapView.mudarAndar(2);
            calcularRotaAndar2(destino);
        } else {
            tvSummaryOrigem.setText("Entrada do Bloco D");
            tvViewportLabel.setText("BLOCO D · TÉRREO");

            indoorMapView.mudarAndar(1);
            calcularRotaTerreo(destino);
        }

        btnAlterarRota.setOnClickListener(v -> finish());
    }

    private void calcularRotaTerreo(String destino) {
        BuildingGraph grafo = BlocoDGraphFactory.criarGrafo();
        RouteCalculator calculadora = new RouteCalculator(grafo);

        List<Node> caminho = Collections.emptyList();
        String laboratorio = "";

        String numero = destino == null ? "" : destino.replaceAll("[^0-9]", "");

        if (numero.equals("01") || numero.equals("1")) {
            laboratorio = "Laboratório 01";
            List<Node> rota1 = calculadora.calcularRota("ENTRADA", "LAB01_PORTA1");
            List<Node> rota2 = calculadora.calcularRota("ENTRADA", "LAB01_PORTA2");
            caminho = calcularDistancia(rota1) <= calcularDistancia(rota2) ? rota1 : rota2;
        } else if (numero.equals("02") || numero.equals("2")) {
            laboratorio = "Laboratório 02";
            caminho = calculadora.calcularRota("ENTRADA", "LAB02_PORTA");
        } else if (numero.equals("03") || numero.equals("3")) {
            laboratorio = "Laboratório 03";
            caminho = calculadora.calcularRota("ENTRADA", "LAB03_PORTA");
        }

        if (!caminho.isEmpty()) {
            indoorMapView.definirRota(caminho);
            String instrucoesInternas = NavigationInstructions.gerarInstrucoes(caminho, "entrada do Bloco D", laboratorio);
            tvInstructions.setText(instrucoesInternas);
        } else {
            indoorMapView.definirRota(null);
            tvInstructions.setText("Não foi possível calcular a rota até o laboratório selecionado.");
        }
    }

    private void calcularRotaAndar2(String destino) {
        BuildingGraph grafo = BlocoDAndar2GraphFactory.criarGrafo();
        RouteCalculator calculadora = new RouteCalculator(grafo);

        List<Node> caminho = Collections.emptyList();
        String salaSelecionada = destino != null ? destino : "Destino";

        String nome = destino == null ? "" : destino.toLowerCase();

        if (nome.contains("04")) {
            caminho = calculadora.calcularRota("ACESSO_2", "LAB_04");
        } else if (nome.contains("05")) {
            caminho = calculadora.calcularRota("ACESSO_2", "LAB_05");
        } else if (nome.contains("201")) {
            caminho = calculadora.calcularRota("ACESSO_2", "SALA_201");
        } else if (nome.contains("202")) {
            caminho = calculadora.calcularRota("ACESSO_2", "SALA_202");
        } else if (nome.contains("203")) {
            caminho = calculadora.calcularRota("ACESSO_2", "SALA_203");
        } else if (nome.contains("biblioteca")) {
            caminho = calculadora.calcularRota("ACESSO_2", "BIBLIOTECA");
        } else if (nome.contains("estudos")) {
            caminho = calculadora.calcularRota("ACESSO_2", "SALA_ESTUDOS");
        }

        if (!caminho.isEmpty()) {
            indoorMapView.definirRota(caminho);
            String instrucoesInternas = NavigationInstructions.gerarInstrucoes(caminho, "Acesso do 2º Andar", salaSelecionada);
            tvInstructions.setText(instrucoesInternas);
        } else {
            indoorMapView.definirRota(null);
            tvInstructions.setText("Não foi possível calcular a rota para este local no 2º andar.");
        }
    }

    private double calcularDistancia(List<Node> caminho) {
        if (caminho == null || caminho.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }
        double total = 0;
        for (int i = 0; i < caminho.size() - 1; i++) {
            Node atual = caminho.get(i);
            Node proximo = caminho.get(i + 1);
            double dx = proximo.getX() - atual.getX();
            double dy = proximo.getY() - atual.getY();
            total += Math.sqrt(dx * dx + dy * dy);
        }
        return total;
    }
}