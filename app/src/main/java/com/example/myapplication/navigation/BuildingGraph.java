
package com.example.myapplication.navigation;

import com.example.myapplication.model.Node;
import com.example.myapplication.model.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingGraph {

    // Guarda os nós pelo identificador
    private final Map<String, Node> nos = new HashMap<>();

    // Guarda as conexões de cada nó
    private final Map<String, List<Edge>> adjacencias =
            new HashMap<>();

    // Adiciona um nó ao grafo
    public void adicionarNo(Node no) {
        if (nos.containsKey(no.getId())) {
            throw new IllegalArgumentException(
                    "Nó duplicado: " + no.getId()
            );
        }

        nos.put(no.getId(), no);
        adjacencias.put(no.getId(), new ArrayList<>());
    }

    // Conecta dois nós nos dois sentidos
    public void conectar(String idOrigem, String idDestino) {

        Node origem = nos.get(idOrigem);
        Node destino = nos.get(idDestino);

        if (origem == null || destino == null) {
            throw new IllegalArgumentException(
                    "Um dos nós não existe no grafo."
            );
        }

        adjacencias.get(idOrigem).add(
                new Edge(origem, destino)
        );

        adjacencias.get(idDestino).add(
                new Edge(destino, origem)
        );
    }

    // Busca um nó pelo identificador
    public Node buscarNo(String id) {
        return nos.get(id);
    }

    // Retorna as conexões de um nó
    public List<Edge> obterVizinhos(String id) {
        List<Edge> conexoes = adjacencias.get(id);

        if (conexoes == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(conexoes);
    }
}
