package com.example.myapplication.navigation;

import com.example.myapplication.model.Edge;
import com.example.myapplication.model.Node;
import com.example.myapplication.navigation.BuildingGraph;

public class BlocoDAndar2GraphFactory {

    public static BuildingGraph criarGrafo() {
        BuildingGraph grafo = new BuildingGraph();

        // Ponto de acesso do 2º Andar (Escada / Acesso)
        grafo.adicionarNo(new Node("ACESSO_2", 500, 900));

        // Corredor central
        grafo.adicionarNo(new Node("CORREDOR_CENTRAL", 500, 500));

        // Salas do lado esquerdo
        grafo.adicionarNo(new Node("LAB_04", 250, 400));
        grafo.adicionarNo(new Node("LAB_05", 250, 750));
        grafo.adicionarNo(new Node("SALA_201", 250, 150));

        // Salas do lado direito
        grafo.adicionarNo(new Node("SALA_202", 750, 150));
        grafo.adicionarNo(new Node("SALA_203", 750, 350));
        grafo.adicionarNo(new Node("BIBLIOTECA", 750, 550));
        grafo.adicionarNo(new Node("SALA_ESTUDOS", 750, 800));

        // Conexões (Arestas)
        conectar(grafo, "ACESSO_2", "CORREDOR_CENTRAL");
        conectar(grafo, "CORREDOR_CENTRAL", "LAB_04");
        conectar(grafo, "CORREDOR_CENTRAL", "LAB_05");
        conectar(grafo, "CORREDOR_CENTRAL", "SALA_201");
        conectar(grafo, "CORREDOR_CENTRAL", "SALA_202");
        conectar(grafo, "CORREDOR_CENTRAL", "SALA_203");
        conectar(grafo, "CORREDOR_CENTRAL", "BIBLIOTECA");
        conectar(grafo, "CORREDOR_CENTRAL", "SALA_ESTUDOS");

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