package com.example.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Node;
import com.example.myapplication.navigation.BuildingGraph;
import com.example.myapplication.navigation.BlocoDGraphFactory;
import com.example.myapplication.navigation.BlocoDAndar2GraphFactory;
import com.example.myapplication.navigation.CampusGraphFactory;
import com.example.myapplication.navigation.NavigationInstructions;
import com.example.myapplication.navigation.RouteCalculator;

import com.example.myapplication.view.CampusMapView;
import com.example.myapplication.view.IndoorMapView;
import com.example.myapplication.view.Andar2MapView;

import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RouteActivity extends AppCompatActivity {

    // =====================================
    // 1. COMPONENTES DA INTERFACE
    // =====================================

    private TextView tvSummaryOrigem;
    private TextView tvSummaryDestino;
    private TextView tvViewportLabel;
    private TextView tvInstructions;
    private TextView tvAndarAtual;

    private Button btnTabCampus;
    private Button btnTabBlocoD;
    private Button btnToggleInstructions;
    private Button btnTerreo;
    private Button btnAndar2;

    private ImageButton btnBack;
    private ImageButton btnLayers;

    private LinearLayout floorSelector;
    private LinearLayout floorOptions;

    private ScrollView scrollInstructions;

    // Mapas
    private CampusMapView campusMapView;
    private IndoorMapView indoorMapView;
    private Andar2MapView andar2MapView;

    // Instruções
    private String instrucoesCampus = "";
    private String instrucoesInternas = "";
    private String instrucoesAndar2 =
            "O destino selecionado não está neste andar.";

    // Controle de navegação
    private int andarAtual = 0;
    private boolean modoAcessivel = false;


    // =====================================
    // 2. INICIALIZAÇÃO
    // =====================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_route);

        tvSummaryOrigem = findViewById(R.id.tvSummaryOrigem);
        tvSummaryDestino = findViewById(R.id.tvSummaryDestino);
        tvViewportLabel = findViewById(R.id.tvViewportLabel);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvAndarAtual = findViewById(R.id.tvAndarAtual);

        btnTabCampus = findViewById(R.id.btnTabCampus);
        btnTabBlocoD = findViewById(R.id.btnTabBlocoD);

        btnToggleInstructions =
                findViewById(R.id.btnToggleInstructions);

        btnBack = findViewById(R.id.btnBack);
        btnLayers = findViewById(R.id.btnLayers);

        btnTerreo = findViewById(R.id.btnTerreo);
        btnAndar2 = findViewById(R.id.btnAndar2);

        floorSelector = findViewById(R.id.floorSelector);
        floorOptions = findViewById(R.id.floorOptions);

        scrollInstructions =
                findViewById(R.id.scrollInstructions);

        // Carrega os três mapas
        campusMapView = findViewById(R.id.campusMapView);
        indoorMapView = findViewById(R.id.indoorMapView);
        andar2MapView = findViewById(R.id.andar2MapView);

        // Recebe os dados da MainActivity
        String origem = getIntent().getStringExtra("ORIGEM");
        String destino = getIntent().getStringExtra("DESTINO");

        modoAcessivel = getIntent().getBooleanExtra(
                "ACESSIBILIDADE",
                false
        );

        if (origem != null) {
            tvSummaryOrigem.setText(origem);
        }

        if (destino != null) {
            tvSummaryDestino.setText(destino);
        }

        // Calcula o percurso externo
        calcularRotaCampus(origem);

        // Verifica em qual andar está o destino
        if (identificarDestinoAndar2(destino) != null) {

            calcularRotaEntreAndares(destino);

        } else {

            calcularRotaInterna(destino);
        }

        // Botões das abas
        btnTabCampus.setOnClickListener(
                v -> selecionarAba(true)
        );

        btnTabBlocoD.setOnClickListener(
                v -> selecionarAba(false)
        );

        // Botão de camadas
        btnLayers.setOnClickListener(v -> {

            if (floorOptions.getVisibility() == View.VISIBLE) {

                floorOptions.setVisibility(View.GONE);

            } else {

                floorOptions.setVisibility(View.VISIBLE);
            }
        });

        // Seleção de andar
        btnTerreo.setOnClickListener(
                v -> selecionarAndar(0)
        );

        btnAndar2.setOnClickListener(
                v -> selecionarAndar(2)
        );

        // Abre ou fecha o painel de instruções
        btnToggleInstructions.setOnClickListener(v -> {

            boolean aberto =
                    scrollInstructions.getVisibility()
                            == View.VISIBLE;

            scrollInstructions.setVisibility(
                    aberto ? View.GONE : View.VISIBLE
            );

            btnToggleInstructions.setText(
                    aberto
                            ? "INSTRUÇÕES DE NAVEGAÇÃO  ▲"
                            : "INSTRUÇÕES DE NAVEGAÇÃO  ▼"
            );
        });

        // Volta para a tela inicial
        btnBack.setOnClickListener(
                v -> finish()
        );

        // Inicia no mapa externo
        selecionarAba(true);
    }


    // =====================================
    // 3. CALCULAR ROTA EXTERNA
    // =====================================

    private void calcularRotaCampus(String origem) {

        int largura = campusMapView.getLarguraMapa();
        int altura = campusMapView.getAlturaMapa();

        if (largura <= 0 || altura <= 0) {

            instrucoesCampus =
                    "Não foi possível carregar o mapa do campus.";

            campusMapView.definirRota(null);
            return;
        }

        BuildingGraph grafo =
                CampusGraphFactory.criarGrafo(largura, altura);

        RouteCalculator calculadora =
                new RouteCalculator(grafo);

        String idOrigem = identificarEntrada(origem);

        if (idOrigem == null) {

            instrucoesCampus = "Origem não reconhecida.";

            campusMapView.definirRota(null);
            return;
        }

        List<Node> caminho = calculadora.calcularRota(
                idOrigem,
                "BLOCO_D"
        );

        if (!caminho.isEmpty()) {

            campusMapView.definirRota(caminho);

            String nomeEntrada =
                    idOrigem.equals("ENTRADA_DIREITA")
                            ? "Entrada Bloco B"
                            : "Entrada Bloco C";

            instrucoesCampus =
                    NavigationInstructions.gerarInstrucoes(
                            caminho,
                            nomeEntrada,
                            "Bloco D"
                    );

            instrucoesCampus +=
                    "\nAo chegar, selecione a aba Bloco D "
                            + "para continuar o percurso.";

        } else {

            campusMapView.definirRota(null);

            instrucoesCampus =
                    "Não foi encontrado um caminho até o Bloco D.";
        }
    }


    // =====================================
    // 4. IDENTIFICAR ENTRADA
    // =====================================

    private String identificarEntrada(String origem) {

        if (origem == null) {
            return null;
        }

        String texto = normalizarTexto(origem);

        if (texto.contains("bloco b")
                || texto.contains("entrada direita")) {

            return "ENTRADA_DIREITA";
        }

        if (texto.contains("bloco c")
                || texto.contains("entrada central")) {

            return "ENTRADA_CENTRAL";
        }

        return null;
    }


    // =====================================
    // 5. ROTAS DOS LABORATÓRIOS DO TÉRREO
    // =====================================

    private void calcularRotaInterna(String destino) {

        BuildingGraph grafo =
                BlocoDGraphFactory.criarGrafo();

        RouteCalculator calculadora =
                new RouteCalculator(grafo);

        List<Node> caminho = Collections.emptyList();

        String laboratorio = "";

        String numero = destino == null
                ? ""
                : destino.replaceAll("[^0-9]", "");

        // Laboratório 01
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

            caminho = calcularDistancia(rota1)
                    <= calcularDistancia(rota2)
                    ? rota1 : rota2;

            // Laboratório 02
        } else if (numero.equals("02")
                || numero.equals("2")) {

            laboratorio = "Laboratório 02";

            caminho = calculadora.calcularRota(
                    "ENTRADA",
                    "LAB02_PORTA"
            );

            // Laboratório 03
        } else if (numero.equals("03")
                || numero.equals("3")) {

            laboratorio = "Laboratório 03";

            caminho = calculadora.calcularRota(
                    "ENTRADA",
                    "LAB03_PORTA"
            );
        }

        // Não existe percurso no segundo andar
        andar2MapView.definirRota(null);

        instrucoesAndar2 =
                "O destino selecionado está no térreo.";

        if (!caminho.isEmpty()) {

            indoorMapView.definirRota(caminho);

            instrucoesInternas =
                    NavigationInstructions.gerarInstrucoes(
                            caminho,
                            "entrada do Bloco D",
                            laboratorio
                    );

        } else {

            indoorMapView.definirRota(null);

            instrucoesInternas =
                    "Não foi possível calcular a rota interna.";
        }
    }


    // =====================================
    // 6. IDENTIFICAR DESTINOS DO ANDAR 2
    // =====================================

    private String identificarDestinoAndar2(String destino) {

        if (destino == null) {
            return null;
        }

        String texto = normalizarTexto(destino);
        String numero = texto.replaceAll("[^0-9]", "");

        if (texto.contains("biblioteca")) {
            return "BIBLIOTECA_PORTA";
        }

        if (texto.contains("estudos")) {
            return "ESTUDOS_PORTA";
        }

        switch (numero) {

            case "04":
            case "4":
                return "LAB04_PORTA";

            case "05":
            case "5":
                return "LAB05_PORTA";

            case "201":
                return "SALA201_PORTA";

            case "202":
                return "SALA202_PORTA";

            case "203":
                return "SALA203_PORTA";

            default:
                return null;
        }
    }


    // =====================================
    // 7. NAVEGAÇÃO ENTRE ANDARES
    // =====================================

    private void calcularRotaEntreAndares(String destino) {

        String idDestino =
                identificarDestinoAndar2(destino);

        if (idDestino == null) {
            return;
        }

        // Escolhe o tipo de acesso
        String acessoTerreo = modoAcessivel
                ? "ELEVADOR_TERREO"
                : "ESCADA_TERREO";

        String acessoAndar2 = modoAcessivel
                ? "ELEVADOR_ANDAR2"
                : "ESCADA_ANDAR2";

        String nomeAcesso = modoAcessivel
                ? "elevador"
                : "escada";


        // PRIMEIRO TRECHO: TÉRREO

        BuildingGraph grafoTerreo =
                BlocoDGraphFactory.criarGrafo();

        RouteCalculator calculadoraTerreo =
                new RouteCalculator(grafoTerreo);

        List<Node> rotaTerreo =
                calculadoraTerreo.calcularRota(
                        "ENTRADA",
                        acessoTerreo
                );


        // SEGUNDO TRECHO: ANDAR 2

        BuildingGraph grafoAndar2 =
                BlocoDAndar2GraphFactory.criarGrafo();

        RouteCalculator calculadoraAndar2 =
                new RouteCalculator(grafoAndar2);

        List<Node> rotaAndar2 =
                calculadoraAndar2.calcularRota(
                        acessoAndar2,
                        idDestino
                );


        // VALIDA OS DOIS PERCURSOS

        if (rotaTerreo.isEmpty() || rotaAndar2.isEmpty()) {

            indoorMapView.definirRota(null);
            andar2MapView.definirRota(null);

            instrucoesInternas =
                    "Não foi possível conectar os andares.";

            instrucoesAndar2 =
                    "Percurso indisponível.";

            return;
        }


        // DESENHA AS ROTAS

        indoorMapView.definirRota(rotaTerreo);
        andar2MapView.definirRota(rotaAndar2);


        // INSTRUÇÕES DO TÉRREO

        instrucoesInternas =
                NavigationInstructions.gerarInstrucoes(
                        rotaTerreo,
                        "entrada do Bloco D",
                        nomeAcesso
                );

        if (modoAcessivel) {

            instrucoesInternas +=
                    "\nUtilize o elevador para subir "
                            + "até o segundo andar.";

        } else {

            instrucoesInternas +=
                    "\nSuba pela escada até o segundo andar.";
        }

        instrucoesInternas +=
                "\nAo chegar, toque no ícone de camadas "
                        + "e selecione o andar 2.";


        // INSTRUÇÕES DO SEGUNDO ANDAR

        String nomeOrigem = modoAcessivel
                ? "saída do elevador"
                : "saída da escada";

        instrucoesAndar2 =
                NavigationInstructions.gerarInstrucoes(
                        rotaAndar2,
                        nomeOrigem,
                        destino
                );
    }


    // =====================================
    // 8. CALCULAR DISTÂNCIA
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
    // 9. NORMALIZAR TEXTOS
    // =====================================

    private String normalizarTexto(String texto) {

        return Normalizer.normalize(
                        texto,
                        Normalizer.Form.NFD
                ).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }


    // =====================================
    // 10. ALTERNAR ENTRE ABAS
    // =====================================

    private void selecionarAba(boolean isCampus) {

        int corSelecionada = Color.WHITE;
        int corInativa = Color.parseColor("#F1F5F9");

        int textoSelecionado = Color.parseColor("#0F172A");
        int textoInativo = Color.parseColor("#64748B");

        floorOptions.setVisibility(View.GONE);

        if (isCampus) {

            campusMapView.setVisibility(View.VISIBLE);
            indoorMapView.setVisibility(View.GONE);
            andar2MapView.setVisibility(View.GONE);

            floorSelector.setVisibility(View.GONE);

            tvAndarAtual.setText("Campus");
            tvViewportLabel.setText("VISÃO DO CAMPUS");

            atualizarInstrucoes(instrucoesCampus);

            btnTabCampus.setBackgroundTintList(
                    ColorStateList.valueOf(corSelecionada)
            );

            btnTabCampus.setTextColor(textoSelecionado);

            btnTabBlocoD.setBackgroundTintList(
                    ColorStateList.valueOf(corInativa)
            );

            btnTabBlocoD.setTextColor(textoInativo);

        } else {

            campusMapView.setVisibility(View.GONE);

            floorSelector.setVisibility(View.VISIBLE);

            selecionarAndar(andarAtual);

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


    // =====================================
    // 11. SELECIONAR ANDAR
    // =====================================

    private void selecionarAndar(int andar) {

        andarAtual = andar;

        campusMapView.setVisibility(View.GONE);

        floorSelector.setVisibility(View.VISIBLE);
        floorOptions.setVisibility(View.GONE);

        int azul = Color.parseColor("#2563EB");
        int branco = Color.WHITE;
        int escuro = Color.parseColor("#0F172A");

        if (andar == 0) {

            // Mostra o térreo
            indoorMapView.setVisibility(View.VISIBLE);
            andar2MapView.setVisibility(View.GONE);

            tvAndarAtual.setText("Térreo");

            tvViewportLabel.setText(
                    "BLOCO D - TÉRREO"
            );

            atualizarInstrucoes(instrucoesInternas);

            btnTerreo.setBackgroundTintList(
                    ColorStateList.valueOf(azul)
            );

            btnTerreo.setTextColor(branco);

            btnAndar2.setBackgroundTintList(
                    ColorStateList.valueOf(branco)
            );

            btnAndar2.setTextColor(escuro);

        } else {

            // Mostra o segundo andar
            indoorMapView.setVisibility(View.GONE);
            andar2MapView.setVisibility(View.VISIBLE);

            tvAndarAtual.setText("2º andar");

            tvViewportLabel.setText(
                    "BLOCO D - SEGUNDO ANDAR"
            );

            atualizarInstrucoes(instrucoesAndar2);

            btnAndar2.setBackgroundTintList(
                    ColorStateList.valueOf(azul)
            );

            btnAndar2.setTextColor(branco);

            btnTerreo.setBackgroundTintList(
                    ColorStateList.valueOf(branco)
            );

            btnTerreo.setTextColor(escuro);
        }
    }


    // =====================================
    // 12. ATUALIZAR INSTRUÇÕES
    // =====================================

    private void atualizarInstrucoes(String instrucoes) {

        tvInstructions.setText(instrucoes);

        scrollInstructions.post(
                () -> scrollInstructions.scrollTo(0, 0)
        );
    }
}