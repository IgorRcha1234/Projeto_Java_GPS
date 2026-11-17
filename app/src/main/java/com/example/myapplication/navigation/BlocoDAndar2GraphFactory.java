
package com.example.myapplication.navigation;

import com.example.myapplication.model.Node;

public class BlocoDAndar2GraphFactory {

    public static BuildingGraph criarGrafo() {

        BuildingGraph grafo = new BuildingGraph();

        // =====================================
        // 1. ACESSOS AO SEGUNDO ANDAR
        // =====================================

        grafo.adicionarNo(new Node("ESCADA_ANDAR2", 432, 362));
        grafo.adicionarNo(new Node("ELEVADOR_ANDAR2", 607, 362));


        // =====================================
        // 2. PONTOS DO CORREDOR CENTRAL
        // =====================================

        grafo.adicionarNo(new Node("C_01", 556, 204));
        grafo.adicionarNo(new Node("C_02", 556, 229));
        grafo.adicionarNo(new Node("C_03", 556, 362));
        grafo.adicionarNo(new Node("C_04", 556, 545));
        grafo.adicionarNo(new Node("C_05", 556, 577));
        grafo.adicionarNo(new Node("C_06", 556, 791));
        grafo.adicionarNo(new Node("C_07", 556, 1057));
        grafo.adicionarNo(new Node("C_08", 556, 1080));


        // =====================================
        // 3. PORTAS DOS DESTINOS
        // =====================================

        // Salas
        grafo.adicionarNo(new Node("SALA201_PORTA", 492, 229));
        grafo.adicionarNo(new Node("SALA202_PORTA", 619, 204));
        grafo.adicionarNo(new Node("SALA203_PORTA", 693, 545));

        // Laboratórios
        grafo.adicionarNo(new Node("LAB04_PORTA", 462, 577));
        grafo.adicionarNo(new Node("LAB05_PORTA", 462, 1080));

        // Biblioteca e sala de estudos
        grafo.adicionarNo(new Node("BIBLIOTECA_PORTA", 619, 791));
        grafo.adicionarNo(new Node("ESTUDOS_PORTA", 619, 1057));


        // =====================================
        // 4. CONEXÕES DA ESCADA E ELEVADOR
        // =====================================

        grafo.conectar("ESCADA_ANDAR2", "C_03");
        grafo.conectar("ELEVADOR_ANDAR2", "C_03");


        // =====================================
        // 5. CONEXÕES DO CORREDOR CENTRAL
        // =====================================

        grafo.conectar("C_01", "C_02");
        grafo.conectar("C_02", "C_03");
        grafo.conectar("C_03", "C_04");
        grafo.conectar("C_04", "C_05");
        grafo.conectar("C_05", "C_06");
        grafo.conectar("C_06", "C_07");
        grafo.conectar("C_07", "C_08");


        // =====================================
        // 6. CONEXÕES COM AS PORTAS
        // =====================================

        grafo.conectar("C_01", "SALA202_PORTA");
        grafo.conectar("C_02", "SALA201_PORTA");
        grafo.conectar("C_04", "SALA203_PORTA");

        grafo.conectar("C_05", "LAB04_PORTA");
        grafo.conectar("C_08", "LAB05_PORTA");

        grafo.conectar("C_06", "BIBLIOTECA_PORTA");
        grafo.conectar("C_07", "ESTUDOS_PORTA");

        return grafo;
    }
}
