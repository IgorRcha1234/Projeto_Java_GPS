
package com.example.myapplication.navigation;

import com.example.myapplication.model.Node;

public class BlocoDGraphFactory {

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

// Entrada principal
        grafo.adicionarNo(
                criarNo("ENTRADA", 725, 1760)
        );

// Corredor central
        grafo.adicionarNo(
                criarNo("C_INFERIOR", 725, 1653)
        );

        grafo.adicionarNo(
                criarNo("C_CENTRAL", 725, 1432)
        );

        grafo.adicionarNo(
                criarNo("C_SUPERIOR", 725, 1252)
        );

// Corredor lateral esquerdo
        grafo.adicionarNo(
                criarNo("C_ESQUERDA", 146, 1432)
        );

// Portas do Lab 01
        grafo.adicionarNo(
                criarNo("LAB01_PORTA1", 848, 1252)
        );

        grafo.adicionarNo(
                criarNo("LAB01_PORTA2", 848, 1653)
        );

// Porta do Lab 02
        grafo.adicionarNo(
                criarNo("LAB02_PORTA", 146, 1492)
        );

// Porta do Lab 03
        grafo.adicionarNo(
                criarNo("LAB03_PORTA", 146, 1377)
        );


        // 5. Conexões do corredor principal
        grafo.conectar("ENTRADA", "C_INFERIOR");

        grafo.conectar("C_INFERIOR", "C_CENTRAL");

        grafo.conectar("C_CENTRAL", "C_SUPERIOR");

        // 6. Acessos ao Laboratório 01
        grafo.conectar("C_INFERIOR", "LAB01_PORTA2");

        grafo.conectar("C_SUPERIOR", "LAB01_PORTA1");

        // 7. Corredor lateral
        grafo.conectar("C_CENTRAL", "C_ESQUERDA");

        // 8. Acessos aos Labs 02 e 03
        grafo.conectar("C_ESQUERDA", "LAB02_PORTA");

        grafo.conectar("C_ESQUERDA", "LAB03_PORTA");

        return grafo;
    }
}
