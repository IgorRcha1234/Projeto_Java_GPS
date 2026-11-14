
package com.example.myapplication.navigation;

import com.example.myapplication.model.Node;
import com.example.myapplication.model.Edge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class RouteCalculator {

    private final BuildingGraph grafo;

    // Representa um item da fila de prioridade
    private static class Estado {
        String id;
        double distancia;

        Estado(String id, double distancia) {
            this.id = id;
            this.distancia = distancia;
        }
    }

    public RouteCalculator(BuildingGraph grafo) {
        this.grafo = grafo;
    }

    public List<Node> calcularRota(
            String idOrigem,
            String idDestino
    ) {

        if (grafo.buscarNo(idOrigem) == null
                || grafo.buscarNo(idDestino) == null) {
            return Collections.emptyList();
        }

        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> anteriores = new HashMap<>();

        PriorityQueue<Estado> fila = new PriorityQueue<>(
                (a, b) -> Double.compare(
                        a.distancia,
                        b.distancia
                )
        );

        distancias.put(idOrigem, 0.0);
        fila.add(new Estado(idOrigem, 0.0));

        while (!fila.isEmpty()) {

            Estado estado = fila.poll();

            String atual = estado.id;

            double menorDistancia =
                    distancias.getOrDefault(
                            atual,
                            Double.POSITIVE_INFINITY
                    );

            // Ignora entradas antigas da fila
            if (estado.distancia > menorDistancia) {
                continue;
            }

            if (atual.equals(idDestino)) {
                break;
            }

            for (Edge aresta : grafo.obterVizinhos(atual)) {

                String vizinho =
                        aresta.getDestino().getId();

                double novaDistancia =
                        menorDistancia + aresta.getPeso();

                double distanciaRegistrada =
                        distancias.getOrDefault(
                                vizinho,
                                Double.POSITIVE_INFINITY
                        );

                if (novaDistancia < distanciaRegistrada) {

                    distancias.put(
                            vizinho,
                            novaDistancia
                    );

                    anteriores.put(vizinho, atual);

                    fila.add(
                            new Estado(vizinho, novaDistancia)
                    );
                }
            }
        }

        if (!distancias.containsKey(idDestino)) {
            return Collections.emptyList();
        }

        // Reconstrói a rota
        List<Node> caminho = new ArrayList<>();

        String atual = idDestino;

        while (atual != null) {
            caminho.add(grafo.buscarNo(atual));
            atual = anteriores.get(atual);
        }

        Collections.reverse(caminho);

        return caminho;
    }
}
