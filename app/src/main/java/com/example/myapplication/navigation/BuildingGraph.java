package com.example.myapplication.navigation;

import com.example.myapplication.model.Edge;
import com.example.myapplication.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingGraph {
    private Map<String, Node> nos = new HashMap<>();
    private Map<String, List<Edge>> adjacencias = new HashMap<>();

    public void adicionarNo(Node no) {
        nos.put(no.getId(), no);
        adjacencias.putIfAbsent(no.getId(), new ArrayList<>());
    }

    public void adicionarAresta(Edge aresta) {
        String origemId = aresta.getOrigem().getId();
        adjacencias.putIfAbsent(origemId, new ArrayList<>());
        adjacencias.get(origemId).add(aresta);
    }

    public Node buscarNo(String id) {
        return nos.get(id);
    }

    public List<Edge> obterVizinhos(String id) {
        return adjacencias.getOrDefault(id, new ArrayList<>());
    }

    public List<Node> obterTodosNos() {
        return new ArrayList<>(nos.values());
    }
}