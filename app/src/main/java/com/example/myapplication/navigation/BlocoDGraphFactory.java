package com.example.myapplication.navigation;

import com.example.myapplication.model.Edge;
import com.example.myapplication.model.Node;

public class BlocoDGraphFactory {

    public static BuildingGraph criarGrafo() {
        BuildingGraph grafo = new BuildingGraph();

        // Adiciona os nós do térreo
        grafo.adicionarNo(new Node("ENTRADA", 500, 950));
        grafo.adicionarNo(new Node("C_INFERIOR", 500, 800));
        grafo.adicionarNo(new Node("C_CENTRAL", 500, 500));
        grafo.adicionarNo(new Node("C_SUPERIOR", 500, 200));
        grafo.adicionarNo(new Node("C_ESQUERDA", 250, 500));
        grafo.adicionarNo(new Node("LAB01_PORTA1", 200, 300));
        grafo.adicionarNo(new Node("LAB01_PORTA2", 200, 400));
        grafo.adicionarNo(new Node("LAB02_PORTA", 200, 600));
        grafo.adicionarNo(new Node("LAB03_PORTA", 200, 750));

        // Conexões (Arestas) usando o método auxiliar bidirecional
        conectar(grafo, "ENTRADA", "C_INFERIOR");
        conectar(grafo, "C_INFERIOR", "C_CENTRAL");
        conectar(grafo, "C_CENTRAL", "C_SUPERIOR");
        conectar(grafo, "C_CENTRAL", "C_ESQUERDA");
        conectar(grafo, "C_ESQUERDA", "LAB01_PORTA1");
        conectar(grafo, "C_ESQUERDA", "LAB01_PORTA2");
        conectar(grafo, "C_ESQUERDA", "LAB02_PORTA");
        conectar(grafo, "C_ESQUERDA", "LAB03_PORTA");

        return grafo;
    }

    private static void conectar(BuildingGraph grafo, String idOrigem, String idDestino) {
        Node origem = grafo.buscarNo(idOrigem);
        Node destino = grafo.buscarNo(idDestino);
        if (origem != null && destino != null) {
            grafo.adicionarAresta(new Edge(origem, destino));
            grafo.adicionarAresta(new Edge(destino, origem));
        }
    }
}