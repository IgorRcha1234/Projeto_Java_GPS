
package com.example.myapplication.model;

public class Edge {

    private final Node origem;
    private final Node destino;
    private final double peso;

    public Edge(Node origem, Node destino) {
        this.origem = origem;
        this.destino = destino;

        // Calcula o comprimento entre os dois nós
        double deltaX = destino.getX() - origem.getX();
        double deltaY = destino.getY() - origem.getY();

        this.peso = Math.sqrt(
                deltaX * deltaX + deltaY * deltaY
        );
    }

    public Node getOrigem() {
        return origem;
    }

    public Node getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }
}
