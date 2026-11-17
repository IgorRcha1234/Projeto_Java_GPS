package com.example.myapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.model.Edge;
import com.example.myapplication.model.Node;
import com.example.myapplication.navigation.BuildingGraph;
import com.example.myapplication.navigation.CampusGraphFactory;

import java.util.List;

public class CampusMapView extends View {

    // =====================================
    // 1. MAPA EXTERNO E GRAFO
    // =====================================

    private Bitmap mapa;
    private BuildingGraph grafo;
    private List<Node> rotaAtual;

    // Pincéis
    private Paint pincelRota;
    private Paint pincelNos;
    private Paint pincelArestas;

    // Ativa ou desativa os testes do grafo
    private static final boolean MOSTRAR_GRAFO_DEBUG = false;


    // =====================================
    // 2. ZOOM
    // =====================================

    private float zoom = 1.0f;

    private static final float ZOOM_MIN = 1.0f;
    private static final float ZOOM_MAX = 4.0f;

    private ScaleGestureDetector detectorZoom;


    // =====================================
    // 3. MOVIMENTAÇÃO
    // =====================================

    private float deslocamentoX = 0f;
    private float deslocamentoY = 0f;

    private float ultimoX;
    private float ultimoY;

    private boolean arrastando = false;


    // =====================================
    // 4. CONSTRUTORES
    // =====================================

    public CampusMapView(Context context) {
        super(context);
        inicializar(context);
    }

    public CampusMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar(context);
    }


    // =====================================
    // 5. INICIALIZAR MAPA
    // =====================================

    private void inicializar(Context context) {

        // Carrega a planta externa do campus
        mapa = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.planta_campus
        );

        if (mapa != null) {

            Log.d(
                    "CAMPUS_DEBUG",
                    "Largura: " + mapa.getWidth()
                            + " | Altura: " + mapa.getHeight()
            );

            // Cria o grafo usando a resolução
            // real da imagem do campus
            grafo = CampusGraphFactory.criarGrafo(
                    mapa.getWidth(),
                    mapa.getHeight()
            );

        } else {

            Log.e(
                    "CAMPUS_DEBUG",
                    "Erro ao carregar o mapa do campus"
            );
        }


        // Pincel da rota azul
        pincelRota = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelRota.setColor(Color.BLUE);
        pincelRota.setStrokeWidth(7f);
        pincelRota.setStrokeCap(Paint.Cap.ROUND);
        pincelRota.setStrokeJoin(Paint.Join.ROUND);
        pincelRota.setStyle(Paint.Style.STROKE);


        // Pincel dos nós vermelhos
        pincelNos = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelNos.setColor(Color.RED);
        pincelNos.setStyle(Paint.Style.FILL);


        // Pincel das conexões amarelas
        pincelArestas = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelArestas.setColor(Color.YELLOW);
        pincelArestas.setStrokeWidth(4f);
        pincelArestas.setStyle(Paint.Style.STROKE);


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
    // 6. RECEBER A ROTA DO DIJKSTRA
    // =====================================

    public void definirRota(List<Node> rota) {

        rotaAtual = rota;

        // Atualiza o desenho na tela
        invalidate();
    }


    // Permite consultar as dimensões da imagem
    public int getLarguraMapa() {
        return mapa != null ? mapa.getWidth() : 0;
    }

    public int getAlturaMapa() {
        return mapa != null ? mapa.getHeight() : 0;
    }


    // =====================================
    // 7. CONTROLE DE TOQUE
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

                    float deltaX = x - ultimoX;
                    float deltaY = y - ultimoY;

                    deslocamentoX += deltaX;
                    deslocamentoY += deltaY;

                    limitarDeslocamento();

                    ultimoX = x;
                    ultimoY = y;

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
    // 8. LIMITAR DESLOCAMENTO
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
    // 9. DESENHAR O MAPA EXTERNO
    // =====================================

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mapa == null
                || getWidth() == 0
                || getHeight() == 0) {
            return;
        }

        float larguraTela = getWidth();
        float alturaTela = getHeight();

        float larguraMapa = mapa.getWidth();
        float alturaMapa = mapa.getHeight();

        // Escala inicial mantendo proporção
        float escalaBase = Math.min(
                larguraTela / larguraMapa,
                alturaTela / alturaMapa
        );

        float x = (
                larguraTela - larguraMapa * escalaBase
        ) / 2f;

        float y = (
                alturaTela - alturaMapa * escalaBase
        ) / 2f;

        canvas.save();

        // Movimentação
        canvas.translate(
                deslocamentoX,
                deslocamentoY
        );

        // Centralização
        canvas.translate(x, y);

        // Escala inicial
        canvas.scale(
                escalaBase,
                escalaBase
        );

        // Zoom adicional
        canvas.scale(
                zoom,
                zoom,
                larguraMapa / 2f,
                alturaMapa / 2f
        );


        // =====================================
        // DESENHAR IMAGEM DO CAMPUS
        // =====================================

        canvas.drawBitmap(
                mapa,
                0,
                0,
                null
        );


        // =====================================
        // DESENHAR ROTA AZUL
        // =====================================

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


        // =====================================
        // DESENHOS TEMPORÁRIOS DO GRAFO
        // =====================================

        if (MOSTRAR_GRAFO_DEBUG && grafo != null) {

            String[] ids = {
                    "ENTRADA_CENTRAL",
                    "ENTRADA_DIREITA",
                    "C_CENTRAL_INFERIOR",
                    "C_ENCONTRO",
                    "C_DIREITA",
                    "C_SUBIDA",
                    "C_FRENTE_D",
                    "BLOCO_D"
            };

            // Desenha as conexões
            for (String id : ids) {

                Node origem = grafo.buscarNo(id);

                if (origem == null) {
                    continue;
                }

                for (Edge aresta : grafo.obterVizinhos(id)) {

                    Node destino = aresta.getDestino();

                    // Evita desenhar a mesma conexão
                    // nos dois sentidos
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

            // Desenha os nós
            for (String id : ids) {

                Node no = grafo.buscarNo(id);

                if (no != null) {

                    canvas.drawCircle(
                            no.getX(),
                            no.getY(),
                            5f,
                            pincelNos
                    );
                }
            }

        }

        // Desenha os marcadores verde e vermelho
        desenharMarcadores(canvas);

        canvas.restore();
    }

    private void desenharMarcadores(Canvas canvas) {

        // Verifica se existe uma rota
        if (rotaAtual == null || rotaAtual.isEmpty()) {
            return;
        }

        // Primeiro e último nós da rota
        Node inicio = rotaAtual.get(0);
        Node destino = rotaAtual.get(rotaAtual.size() - 1);

        // Contorno branco dos marcadores
        Paint contorno = new Paint(Paint.ANTI_ALIAS_FLAG);
        contorno.setColor(Color.WHITE);
        contorno.setStyle(Paint.Style.FILL);

        // Marcador verde
        Paint pincelInicio = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelInicio.setColor(Color.rgb(22, 163, 74));
        pincelInicio.setStyle(Paint.Style.FILL);

        // Marcador vermelho
        Paint pincelDestino = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelDestino.setColor(Color.rgb(220, 38, 38));
        pincelDestino.setStyle(Paint.Style.FILL);

        float raio = 12f;
        float raioContorno = 15f;

        // INÍCIO: contorno branco e círculo verde
        canvas.drawCircle(
                inicio.getX(),
                inicio.getY(),
                raioContorno,
                contorno
        );

        canvas.drawCircle(
                inicio.getX(),
                inicio.getY(),
                raio,
                pincelInicio
        );

        // DESTINO: contorno branco e círculo vermelho
        canvas.drawCircle(
                destino.getX(),
                destino.getY(),
                raioContorno,
                contorno
        );

        canvas.drawCircle(
                destino.getX(),
                destino.getY(),
                raio,
                pincelDestino
        );
    }
}