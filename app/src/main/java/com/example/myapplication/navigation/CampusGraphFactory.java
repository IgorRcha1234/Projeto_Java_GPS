
package com.example.myapplication.navigation;

import com.example.myapplication.model.Node;

public class CampusGraphFactory {

    // Dimensões da imagem de referência
    private static final float LARGURA_REF = 696f;
    private static final float ALTURA_REF = 350f;

    // Converte coordenadas para a resolução
    // real da imagem carregada pelo Android
    private static Node criarNo(
            String id,
            float x,
            float y,
            int largura,
            int altura
    ) {

        float escalaX = largura / LARGURA_REF;
        float escalaY = altura / ALTURA_REF;

        return new Node(
                id,
                x * escalaX,
                y * escalaY
        );
    }

    public static BuildingGraph criarGrafo(
            int larguraImagem,
            int alturaImagem
    ) {

        BuildingGraph grafo = new BuildingGraph();

        // ===================================
        // 1. AS DUAS ENTRADAS DO CAMPUS
        // ===================================

        grafo.adicionarNo(
                criarNo(
                        "ENTRADA_CENTRAL",
                        391, 333,
                        larguraImagem, alturaImagem
                )
        );

        grafo.adicionarNo(
                criarNo(
                        "ENTRADA_DIREITA",
                        651, 236,
                        larguraImagem, alturaImagem
                )
        );

        // ===================================
        // 2. CORREDORES DO CAMPUS
        // ===================================

        // Acesso saindo da entrada central
        grafo.adicionarNo(
                criarNo(
                        "C_CENTRAL_INFERIOR",
                        411, 333,
                        larguraImagem, alturaImagem
                )
        );

        // Encontro das duas rotas
        grafo.adicionarNo(
                criarNo(
                        "C_ENCONTRO",
                        411, 236,
                        larguraImagem, alturaImagem
                )
        );

        // Saída da entrada direita
        grafo.adicionarNo(
                criarNo(
                        "C_DIREITA",
                        635, 236,
                        larguraImagem, alturaImagem
                )
        );

        // Passagem entre os blocos
        grafo.adicionarNo(
                criarNo(
                        "C_SUBIDA",
                        411, 140,
                        larguraImagem, alturaImagem
                )
        );

        // Aproximação do Bloco D
        grafo.adicionarNo(
                criarNo(
                        "C_FRENTE_D",
                        375, 140,
                        larguraImagem, alturaImagem
                )
        );

        // ===================================
        // 3. DESTINO: BLOCO D
        // ===================================

        grafo.adicionarNo(
                criarNo(
                        "BLOCO_D",
                        375, 123,
                        larguraImagem, alturaImagem
                )
        );

        // ===================================
        // 4. CONEXÕES
        // ===================================

        // Caminho da entrada central
        grafo.conectar(
                "ENTRADA_CENTRAL",
                "C_CENTRAL_INFERIOR"
        );

        grafo.conectar(
                "C_CENTRAL_INFERIOR",
                "C_ENCONTRO"
        );

        // Caminho da entrada direita
        grafo.conectar(
                "ENTRADA_DIREITA",
                "C_DIREITA"
        );

        grafo.conectar(
                "C_DIREITA",
                "C_ENCONTRO"
        );

        // Trecho compartilhado até o Bloco D
        grafo.conectar(
                "C_ENCONTRO",
                "C_SUBIDA"
        );

        grafo.conectar(
                "C_SUBIDA",
                "C_FRENTE_D"
        );

        grafo.conectar(
                "C_FRENTE_D",
                "BLOCO_D"
        );

        return grafo;
    }
}