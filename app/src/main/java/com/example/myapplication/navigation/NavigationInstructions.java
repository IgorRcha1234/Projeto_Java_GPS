
package com.example.myapplication.navigation;

import com.example.myapplication.model.Node;

import java.util.List;

public class NavigationInstructions {

    // Ângulo mínimo para considerar uma curva
    private static final double ANGULO_MINIMO = 30.0;

    public static String gerarInstrucoes(
            List<Node> rota,
            String inicio,
            String destino) {

        if (rota == null || rota.size() < 2) {
            return "Não foi possível gerar as instruções.";
        }

        StringBuilder instrucoes = new StringBuilder();

        // Primeira instrução
        instrucoes.append("1. Comece em ")
                .append(inicio)
                .append(" e siga em frente pela linha azul.\n");

        int numeroPasso = 2;

        // Analisa três nós consecutivos
        for (int i = 1; i < rota.size() - 1; i++) {

            Node anterior = rota.get(i - 1);
            Node atual = rota.get(i);
            Node proximo = rota.get(i + 1);

            // Direção do primeiro segmento
            double dx1 = atual.getX() - anterior.getX();
            double dy1 = atual.getY() - anterior.getY();

            // Direção do segundo segmento
            double dx2 = proximo.getX() - atual.getX();
            double dy2 = proximo.getY() - atual.getY();

            // Evita segmentos sem comprimento
            double comprimento1 = Math.hypot(dx1, dy1);
            double comprimento2 = Math.hypot(dx2, dy2);

            if (comprimento1 == 0 || comprimento2 == 0) {
                continue;
            }

            // Produto vetorial: indica sentido da curva
            double cruzamento = dx1 * dy2 - dy1 * dx2;

            // Produto escalar: ajuda a calcular o ângulo
            double produtoEscalar = dx1 * dx2 + dy1 * dy2;

            double angulo = Math.toDegrees(
                    Math.atan2(
                            Math.abs(cruzamento),
                            produtoEscalar
                    )
            );

            // Ignora pequenas mudanças de direção
            if (angulo < ANGULO_MINIMO) {
                continue;
            }

            String orientacao;

            if (angulo > 150) {
                orientacao = "Faça o retorno";
            } else if (cruzamento > 0) {
                // No Canvas, Y aumenta para baixo
                orientacao = "Vire à direita";
            } else {
                orientacao = "Vire à esquerda";
            }

            instrucoes.append(numeroPasso)
                    .append(". ")
                    .append(orientacao)
                    .append(" e continue pela linha azul.\n");

            numeroPasso++;
        }

        // Última instrução
        instrucoes.append(numeroPasso)
                .append(". Chegue ao destino: ")
                .append(destino)
                .append(".");

        return instrucoes.toString();
    }
}
