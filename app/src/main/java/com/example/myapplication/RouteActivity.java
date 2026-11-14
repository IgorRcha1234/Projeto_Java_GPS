
package com.example.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Node;
import com.example.myapplication.navigation.BuildingGraph;
import com.example.myapplication.navigation.BlocoDGraphFactory;
import com.example.myapplication.navigation.CampusGraphFactory;
import com.example.myapplication.navigation.NavigationInstructions;
import com.example.myapplication.navigation.RouteCalculator;
import com.example.myapplication.view.CampusMapView;
import com.example.myapplication.view.IndoorMapView;

import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RouteActivity extends AppCompatActivity {

    // =====================================
    // COMPONENTES DA INTERFACE
    // =====================================

    private TextView tvSummaryOrigem;
    private TextView tvSummaryDestino;
    private TextView tvViewportLabel;
    private TextView tvInstructions;

    private Button btnTabCampus;
    private Button btnTabBlocoD;
    private Button btnAlterarRota;

    private ImageButton btnBack;

    // Mapas externo e interno
    private CampusMapView campusMapView;
    private IndoorMapView indoorMapView;

    // Instruções geradas automaticamente
    private String instrucoesCampus = "";
    private String instrucoesInternas = "";


    // =====================================
    // 1. INICIALIZAÇÃO
    // =====================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_route);

        // Componentes do XML
        tvSummaryOrigem = findViewById(R.id.tvSummaryOrigem);
        tvSummaryDestino = findViewById(R.id.tvSummaryDestino);
        tvViewportLabel = findViewById(R.id.tvViewportLabel);
        tvInstructions = findViewById(R.id.tvInstructions);

        btnTabCampus = findViewById(R.id.btnTabCampus);
        btnTabBlocoD = findViewById(R.id.btnTabBlocoD);
        btnAlterarRota = findViewById(R.id.btnAlterarRota);
        btnBack = findViewById(R.id.btnBack);

        campusMapView = findViewById(R.id.campusMapView);
        indoorMapView = findViewById(R.id.indoorMapView);

        // Recebe origem e destino da MainActivity
        String origem = getIntent().getStringExtra("ORIGEM");
        String destino = getIntent().getStringExtra("DESTINO");

        if (origem != null) {
            tvSummaryOrigem.setText(origem);
        }

        if (destino != null) {
            tvSummaryDestino.setText(destino);
        }

        // Calcula os dois percursos
        calcularRotaCampus(origem);
        calcularRotaInterna(destino);

        // Botão Campus
        btnTabCampus.setOnClickListener(
                v -> selecionarAba(true)
        );

        // Botão Bloco D
        btnTabBlocoD.setOnClickListener(
                v -> selecionarAba(false)
        );

        // Voltar à seleção de rota
        btnAlterarRota.setOnClickListener(
                v -> finish()
        );

        btnBack.setOnClickListener(
                v -> finish()
        );

        // Inicia mostrando o Campus
        selecionarAba(true);
    }


    // =====================================
    // 2. CALCULAR ROTA EXTERNA
    // =====================================

    private void calcularRotaCampus(String origem) {

        // Resolução real do mapa externo
        int largura = campusMapView.getLarguraMapa();
        int altura = campusMapView.getAlturaMapa();

        // Confirma que a imagem foi carregada
        if (largura <= 0 || altura <= 0) {

            instrucoesCampus =
                    "Não foi possível carregar o mapa do campus.";

            campusMapView.definirRota(null);
            return;
        }

        // Monta o grafo do Campus
        BuildingGraph grafo =
                CampusGraphFactory.criarGrafo(largura, altura);

        RouteCalculator calculadora =
                new RouteCalculator(grafo);

        // Identifica a entrada selecionada
        String idOrigem = identificarEntrada(origem);

        if (idOrigem == null) {

            instrucoesCampus =
                    "Origem não reconhecida. "
                            + "Confira a entrada selecionada.";

            campusMapView.definirRota(null);
            return;
        }

        // Calcula o percurso com Dijkstra
        List<Node> caminho = calculadora.calcularRota(
                idOrigem,
                "BLOCO_D"
        );

        if (!caminho.isEmpty()) {

            // Envia o caminho para desenhar a linha azul
            campusMapView.definirRota(caminho);

            // Identifica o nome da entrada
            String nomeEntrada =
                    idOrigem.equals("ENTRADA_DIREITA")
                            ? "Entrada Bloco B"
                            : "Entrada Bloco C";

            // Gera as instruções de direção
            instrucoesCampus =
                    NavigationInstructions.gerarInstrucoes(
                            caminho,
                            nomeEntrada,
                            "Bloco D"
                    );

            // Orienta a continuar pelo mapa interno
            instrucoesCampus +=
                    "\nDepois, selecione a aba Bloco D "
                            + "para seguir até o laboratório.";

        } else {

            campusMapView.definirRota(null);

            instrucoesCampus =
                    "Não foi encontrado um caminho "
                            + "até o Bloco D.";
        }
    }


    // =====================================
    // 3. IDENTIFICAR A ENTRADA
    // =====================================

    private String identificarEntrada(String origem) {

        if (origem == null) {
            return null;
        }

        // Remove acentos e padroniza o texto
        String texto = Normalizer.normalize(
                        origem,
                        Normalizer.Form.NFD
                ).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();

        // Entrada Bloco B = entrada direita
        if (texto.contains("bloco b")
                || texto.contains("entrada direita")) {

            return "ENTRADA_DIREITA";
        }

        // Entrada Bloco C = entrada central
        if (texto.contains("bloco c")
                || texto.contains("entrada central")) {

            return "ENTRADA_CENTRAL";
        }

        return null;
    }


    // =====================================
    // 4. CALCULAR ROTA INTERNA
    // =====================================

    private void calcularRotaInterna(String destino) {

        // Monta o grafo interno
        BuildingGraph grafo =
                BlocoDGraphFactory.criarGrafo();

        RouteCalculator calculadora =
                new RouteCalculator(grafo);

        List<Node> caminho = Collections.emptyList();

        String laboratorio = "";

        // Extrai o número do laboratório
        String numero = destino == null
                ? ""
                : destino.replaceAll("[^0-9]", "");

        // LABORATÓRIO 01 - DUAS PORTAS
        if (numero.equals("01") || numero.equals("1")) {

            laboratorio = "Laboratório 01";

            List<Node> rota1 = calculadora.calcularRota(
                    "ENTRADA",
                    "LAB01_PORTA1"
            );

            List<Node> rota2 = calculadora.calcularRota(
                    "ENTRADA",
                    "LAB01_PORTA2"
            );

            // Escolhe o percurso mais curto
            caminho = calcularDistancia(rota1)
                    <= calcularDistancia(rota2)
                    ? rota1 : rota2;

            // LABORATÓRIO 02
        } else if (numero.equals("02")
                || numero.equals("2")) {

            laboratorio = "Laboratório 02";

            caminho = calculadora.calcularRota(
                    "ENTRADA",
                    "LAB02_PORTA"
            );

            // LABORATÓRIO 03
        } else if (numero.equals("03")
                || numero.equals("3")) {

            laboratorio = "Laboratório 03";

            caminho = calculadora.calcularRota(
                    "ENTRADA",
                    "LAB03_PORTA"
            );
        }

        if (!caminho.isEmpty()) {

            // Desenha a rota azul no mapa interno
            indoorMapView.definirRota(caminho);

            // Gera as orientações do caminho
            instrucoesInternas =
                    NavigationInstructions.gerarInstrucoes(
                            caminho,
                            "entrada do Bloco D",
                            laboratorio
                    );

        } else {

            indoorMapView.definirRota(null);

            instrucoesInternas =
                    "Não foi possível calcular a rota "
                            + "até o laboratório selecionado.";
        }
    }


    // =====================================
    // 5. CALCULAR DISTÂNCIA DA ROTA
    // =====================================

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


    // =====================================
    // 6. ALTERNAR ENTRE AS ABAS
    // =====================================

    private void selecionarAba(boolean isCampus) {

        int corSelecionada = Color.WHITE;
        int corInativa = Color.parseColor("#F1F5F9");

        int textoSelecionado = Color.parseColor("#0F172A");
        int textoInativo = Color.parseColor("#64748B");

        if (isCampus) {

            // Mostra mapa externo
            campusMapView.setVisibility(View.VISIBLE);
            indoorMapView.setVisibility(View.GONE);

            tvViewportLabel.setText("VISÃO DO CAMPUS");

            // Mostra as instruções externas
            tvInstructions.setText(instrucoesCampus);

            // Destaca a aba Campus
            btnTabCampus.setBackgroundTintList(
                    ColorStateList.valueOf(corSelecionada)
            );

            btnTabCampus.setTextColor(textoSelecionado);

            btnTabBlocoD.setBackgroundTintList(
                    ColorStateList.valueOf(corInativa)
            );

            btnTabBlocoD.setTextColor(textoInativo);

        } else {

            // Mostra mapa interno
            campusMapView.setVisibility(View.GONE);
            indoorMapView.setVisibility(View.VISIBLE);

            tvViewportLabel.setText("BLOCO D · TÉRREO");

            // Mostra as instruções internas
            tvInstructions.setText(instrucoesInternas);

            // Destaca a aba Bloco D
            btnTabBlocoD.setBackgroundTintList(
                    ColorStateList.valueOf(corSelecionada)
            );

            btnTabBlocoD.setTextColor(textoSelecionado);

            btnTabCampus.setBackgroundTintList(
                    ColorStateList.valueOf(corInativa)
            );

            btnTabCampus.setTextColor(textoInativo);
        }
    }
}


