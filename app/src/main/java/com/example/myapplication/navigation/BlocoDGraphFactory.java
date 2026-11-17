
package com.example.myapplication.navigation;

import com.example.myapplication.model.Node;

public class BlocoDGraphFactory {

    // Conversão das coordenadas da planta
    private static Node criarNo(
            String id,
            float x,
            float y
    ) {

        float escalaX = 2080f / 1468f;
        float escalaY = 2900f / 2047f;

        return new Node(
                id,
                x * escalaX,
                y * escalaY
        );
    }

    public static BuildingGraph criarGrafo() {

        BuildingGraph grafo = new BuildingGraph();

        // =====================================
        // 1. ENTRADA PRINCIPAL
        // =====================================

        grafo.adicionarNo(
                criarNo("ENTRADA", 725, 1760)
        );


        // =====================================
        // 2. CORREDOR CENTRAL
        // =====================================

        grafo.adicionarNo(
                criarNo("C_INFERIOR", 725, 1653)
        );

        grafo.adicionarNo(
                criarNo("C_CENTRAL", 725, 1432)
        );

        grafo.adicionarNo(
                criarNo("C_SUPERIOR", 725, 1252)
        );


        // =====================================
        // 3. CORREDOR LATERAL ESQUERDO
        // =====================================

        grafo.adicionarNo(
                criarNo("C_ESQUERDA", 146, 1432)
        );


        // =====================================
        // 4. PORTAS DOS LABORATÓRIOS
        // =====================================

        grafo.adicionarNo(
                criarNo("LAB01_PORTA1", 848, 1252)
        );

        grafo.adicionarNo(
                criarNo("LAB01_PORTA2", 848, 1653)
        );

        grafo.adicionarNo(
                criarNo("LAB02_PORTA", 146, 1492)
        );

        grafo.adicionarNo(
                criarNo("LAB03_PORTA", 146, 1377)
        );


        // =====================================
        // 5. NOVOS NÓS - CORREDOR SUPERIOR
        // =====================================

        grafo.adicionarNo(
                criarNo("C_SUBIDA", 725, 765)
        );

        grafo.adicionarNo(
                criarNo("C_PASSAGEM", 650, 765)
        );

        grafo.adicionarNo(
                criarNo("C_ACESSOS", 650, 550)
        );


        // =====================================
        // 6. ESCADA E ELEVADOR
        // =====================================

        grafo.adicionarNo(
                criarNo("ESCADA_TERREO", 580, 550)
        );

        grafo.adicionarNo(
                criarNo("ELEVADOR_TERREO", 847, 550)
        );


        // =====================================
        // 7. CONEXÕES ORIGINAIS
        // =====================================

        grafo.conectar("ENTRADA", "C_INFERIOR");

        grafo.conectar("C_INFERIOR", "C_CENTRAL");

        grafo.conectar("C_CENTRAL", "C_SUPERIOR");

        // Laboratório 01
        grafo.conectar("C_INFERIOR", "LAB01_PORTA2");

        grafo.conectar("C_SUPERIOR", "LAB01_PORTA1");

        // Corredor lateral
        grafo.conectar("C_CENTRAL", "C_ESQUERDA");

        // Laboratórios 02 e 03
        grafo.conectar("C_ESQUERDA", "LAB02_PORTA");

        grafo.conectar("C_ESQUERDA", "LAB03_PORTA");


        // =====================================
        // 8. NOVAS CONEXÕES
        // =====================================

        grafo.conectar("C_SUPERIOR", "C_SUBIDA");

        grafo.conectar("C_SUBIDA", "C_PASSAGEM");

        grafo.conectar("C_PASSAGEM", "C_ACESSOS");

        // Rota convencional
        grafo.conectar("C_ACESSOS", "ESCADA_TERREO");

        // Rota acessível
        grafo.conectar("C_ACESSOS", "ELEVADOR_TERREO");

        return grafo;
    }
}
