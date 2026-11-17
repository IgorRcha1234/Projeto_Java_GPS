
package com.example.myapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.model.Node;
import com.example.myapplication.model.Edge;
import com.example.myapplication.navigation.BuildingGraph;
import com.example.myapplication.navigation.BlocoDAndar2GraphFactory;

import java.util.List;

public class Andar2MapView extends View {

    // =====================================
    // 1. MAPA E GRAFO
    // =====================================

    private Bitmap mapa;
    private BuildingGraph grafo;
    private List<Node> rotaAtual;

    // Ativar visualização dos nós e arestas
    private static final boolean MOSTRAR_GRAFO_DEBUG = false;

    // Pincéis
    private Paint pincelRota;
    private Paint pincelNos;
    private Paint pincelArestas;
    private Paint pincelInicio;
    private Paint pincelDestino;
    private Paint pincelContorno;


    // =====================================
    // 2. ZOOM E MOVIMENTAÇÃO
    // =====================================

    private float zoom = 1.0f;

    private static final float ZOOM_MIN = 1.0f;
    private static final float ZOOM_MAX = 4.0f;

    private ScaleGestureDetector detectorZoom;

    private float deslocamentoX = 0f;
    private float deslocamentoY = 0f;

    private float ultimoX;
    private float ultimoY;

    private boolean arrastando = false;


    // =====================================
    // 3. CONSTRUTORES
    // =====================================

    public Andar2MapView(Context context) {
        super(context);
        inicializar(context);
    }

    public Andar2MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar(context);
    }


    // =====================================
    // 4. INICIALIZAÇÃO
    // =====================================

    private void inicializar(Context context) {

        // Carrega a planta do segundo andar
        mapa = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.planta_andar2
        );

        // Carrega o grafo
        grafo = BlocoDAndar2GraphFactory.criarGrafo();

        // Rota azul
        pincelRota = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelRota.setColor(Color.BLUE);
        pincelRota.setStrokeWidth(8f);
        pincelRota.setStrokeCap(Paint.Cap.ROUND);
        pincelRota.setStrokeJoin(Paint.Join.ROUND);
        pincelRota.setStyle(Paint.Style.STROKE);

        // Nós vermelhos
        pincelNos = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelNos.setColor(Color.RED);
        pincelNos.setStyle(Paint.Style.FILL);

        // Arestas amarelas
        pincelArestas = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelArestas.setColor(Color.YELLOW);
        pincelArestas.setStrokeWidth(5f);

        // Marcador de início
        pincelInicio = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelInicio.setColor(Color.rgb(22, 163, 74));

        // Marcador de destino
        pincelDestino = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelDestino.setColor(Color.rgb(220, 38, 38));

        // Contorno branco
        pincelContorno = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelContorno.setColor(Color.WHITE);

        // Detector de zoom
        detectorZoom = new ScaleGestureDetector(
                context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {

                    @Override
                    public boolean onScale(
                            ScaleGestureDetector detector) {

                        zoom *= detector.getScaleFactor();

                        zoom = Math.max(
                                ZOOM_MIN,
                                Math.min(zoom, ZOOM_MAX)
                        );

                        limitarDeslocamento();
                        invalidate();

                        return true;
                    }
                }
        );
    }


    // =====================================
    // 5. RECEBER A ROTA DO DIJKSTRA
    // =====================================

    public void definirRota(List<Node> rota) {

        rotaAtual = rota;
        invalidate();
    }


    // =====================================
    // 6. EVENTOS DE TOQUE
    // =====================================

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        detectorZoom.onTouchEvent(event);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:

                ultimoX = event.getX();
                ultimoY = event.getY();

                arrastando = true;
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:

                arrastando = false;
                return true;

            case MotionEvent.ACTION_POINTER_UP:

                arrastando = false;
                return true;

            case MotionEvent.ACTION_MOVE:

                if (event.getPointerCount() == 1
                        && arrastando
                        && !detectorZoom.isInProgress()) {

                    float x = event.getX();
                    float y = event.getY();

                    deslocamentoX += x - ultimoX;
                    deslocamentoY += y - ultimoY;

                    ultimoX = x;
                    ultimoY = y;

                    limitarDeslocamento();
                    invalidate();
                }

                return true;

            case MotionEvent.ACTION_UP:

                arrastando = false;
                performClick();
                return true;

            case MotionEvent.ACTION_CANCEL:

                arrastando = false;
                return true;
        }

        return true;
    }


    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }


    // =====================================
    // 7. LIMITES DA MOVIMENTAÇÃO
    // =====================================

    private void limitarDeslocamento() {

        if (mapa == null
                || getWidth() == 0
                || getHeight() == 0) {
            return;
        }

        float escalaBase = Math.min(
                (float) getWidth() / mapa.getWidth(),
                (float) getHeight() / mapa.getHeight()
        );

        float larguraFinal =
                mapa.getWidth() * escalaBase * zoom;

        float alturaFinal =
                mapa.getHeight() * escalaBase * zoom;

        float limiteX = Math.max(
                0f,
                (larguraFinal - getWidth()) / 2f
        );

        float limiteY = Math.max(
                0f,
                (alturaFinal - getHeight()) / 2f
        );

        deslocamentoX = Math.max(
                -limiteX,
                Math.min(deslocamentoX, limiteX)
        );

        deslocamentoY = Math.max(
                -limiteY,
                Math.min(deslocamentoY, limiteY)
        );
    }


    // =====================================
    // 8. DESENHAR MAPA E ROTA
    // =====================================

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (mapa == null
                || getWidth() == 0
                || getHeight() == 0) {
            return;
        }

        float larguraMapa = mapa.getWidth();
        float alturaMapa = mapa.getHeight();

        float escalaBase = Math.min(
                getWidth() / larguraMapa,
                getHeight() / alturaMapa
        );

        float x = (
                getWidth() - larguraMapa * escalaBase
        ) / 2f;

        float y = (
                getHeight() - alturaMapa * escalaBase
        ) / 2f;

        canvas.save();

        // Movimentação
        canvas.translate(
                deslocamentoX,
                deslocamentoY
        );

        // Centralização
        canvas.translate(x, y);

        // Escala
        canvas.scale(escalaBase, escalaBase);

        // Zoom
        canvas.scale(
                zoom,
                zoom,
                larguraMapa / 2f,
                alturaMapa / 2f
        );

        // Desenha a planta
        canvas.drawBitmap(mapa, 0, 0, null);

        // Desenha o grafo para testes
        if (MOSTRAR_GRAFO_DEBUG) {
            desenharGrafo(canvas);
        }

        // Desenha a rota azul
        if (rotaAtual != null && rotaAtual.size() >= 2) {

            for (int i = 0; i < rotaAtual.size() - 1; i++) {

                Node atual = rotaAtual.get(i);
                Node proximo = rotaAtual.get(i + 1);

                canvas.drawLine(
                        atual.getX(),
                        atual.getY(),
                        proximo.getX(),
                        proximo.getY(),
                        pincelRota
                );
            }
        }

        desenharMarcadores(canvas);

        canvas.restore();
    }


    // =====================================
    // 9. DESENHAR NÓS E ARESTAS
    // =====================================

    private void desenharGrafo(Canvas canvas) {

        String[] ids = {
                "ESCADA_ANDAR2",
                "ELEVADOR_ANDAR2",
                "C_01",
                "C_02",
                "C_03",
                "C_04",
                "C_05",
                "C_06",
                "C_07",
                "C_08",
                "SALA201_PORTA",
                "SALA202_PORTA",
                "SALA203_PORTA",
                "LAB04_PORTA",
                "LAB05_PORTA",
                "BIBLIOTECA_PORTA",
                "ESTUDOS_PORTA"
        };

        // Desenha as arestas amarelas
        for (String id : ids) {

            Node origem = grafo.buscarNo(id);

            if (origem == null) {
                continue;
            }

            for (Edge aresta : grafo.obterVizinhos(id)) {

                Node destino = aresta.getDestino();

                // Evita repetir a mesma aresta
                if (origem.getId().compareTo(
                        destino.getId()) >= 0) {
                    continue;
                }

                canvas.drawLine(
                        origem.getX(),
                        origem.getY(),
                        destino.getX(),
                        destino.getY(),
                        pincelArestas
                );
            }
        }

        // Desenha os nós vermelhos
        for (String id : ids) {

            Node no = grafo.buscarNo(id);

            if (no != null) {

                canvas.drawCircle(
                        no.getX(),
                        no.getY(),
                        7f,
                        pincelNos
                );
            }
        }
    }


    // =====================================
    // 10. MARCADORES DE INÍCIO E DESTINO
    // =====================================

    private void desenharMarcadores(Canvas canvas) {

        if (rotaAtual == null || rotaAtual.isEmpty()) {
            return;
        }

        Node inicio = rotaAtual.get(0);

        Node destino =
                rotaAtual.get(rotaAtual.size() - 1);

        // Início
        canvas.drawCircle(
                inicio.getX(),
                inicio.getY(),
                15f,
                pincelContorno
        );

        canvas.drawCircle(
                inicio.getX(),
                inicio.getY(),
                12f,
                pincelInicio
        );

        // Destino
        canvas.drawCircle(
                destino.getX(),
                destino.getY(),
                15f,
                pincelContorno
        );

        canvas.drawCircle(
                destino.getX(),
                destino.getY(),
                12f,
                pincelDestino
        );
    }
}
