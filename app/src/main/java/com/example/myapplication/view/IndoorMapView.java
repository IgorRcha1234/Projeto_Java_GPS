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
import com.example.myapplication.model.Node;
import com.example.myapplication.model.Edge;
import com.example.myapplication.navigation.BuildingGraph;
import com.example.myapplication.navigation.BlocoDGraphFactory;
import com.example.myapplication.navigation.BlocoDAndar2GraphFactory;

import java.util.List;

public class IndoorMapView extends View {

    // =====================================
    // 1. VARIÁVEIS DO MAPA
    // =====================================

    private Bitmap mapa;

    // Grafo atual (Térreo ou 2º Andar)
    private BuildingGraph grafo;

    // Rota calculada pelo Dijkstra
    private List<Node> rotaAtual;

    // Pincéis de desenho
    private Paint pincelNos;
    private Paint pincelArestas;
    private Paint pincelRota;

    // Mostra ou esconde nós e arestas (Debug)
    private static final boolean MOSTRAR_GRAFO_DEBUG = true;


    // =====================================
    // 2. CONTROLE DE ZOOM
    // =====================================

    private float zoom = 1.0f;

    private static final float ZOOM_MIN = 1.0f;
    private static final float ZOOM_MAX = 4.0f;

    private ScaleGestureDetector detectorZoom;


    // =====================================
    // 3. CONTROLE DE MOVIMENTAÇÃO
    // =====================================

    private float deslocamentoX = 0f;
    private float deslocamentoY = 0f;

    private float ultimoX;
    private float ultimoY;

    private boolean arrastando = false;


    // =====================================
    // 4. CONSTRUTORES
    // =====================================

    public IndoorMapView(Context context) {
        super(context);
        inicializar(context);
    }

    public IndoorMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar(context);
    }


    // =====================================
    // 5. INICIALIZAÇÃO DO MAPA
    // =====================================

    private void inicializar(Context context) {

        // Carrega por padrão a imagem do Térreo do Bloco D
        mapa = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.planta_bloco_d
        );

        // Verifica as dimensões da imagem
        if (mapa != null) {
            Log.d("MAPA_DEBUG", "Largura: " + mapa.getWidth() + " | Altura: " + mapa.getHeight());
        } else {
            Log.e("MAPA_DEBUG", "Erro ao carregar a planta do Bloco D");
        }

        // Cria o grafo padrão do térreo
        grafo = BlocoDGraphFactory.criarGrafo();

        // Pincel para os nós vermelhos
        pincelNos = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelNos.setColor(Color.RED);
        pincelNos.setStyle(Paint.Style.FILL);

        // Pincel para as arestas amarelas
        pincelArestas = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelArestas.setColor(Color.YELLOW);
        pincelArestas.setStrokeWidth(7f);
        pincelArestas.setStyle(Paint.Style.STROKE);

        // Pincel para a rota azul
        pincelRota = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelRota.setColor(Color.BLUE);
        pincelRota.setStrokeWidth(12f);
        pincelRota.setStrokeCap(Paint.Cap.ROUND);
        pincelRota.setStrokeJoin(Paint.Join.ROUND);
        pincelRota.setStyle(Paint.Style.STROKE);

        // Configura o detector de zoom
        detectorZoom = new ScaleGestureDetector(
                context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {

                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        zoom *= detector.getScaleFactor();

                        // Limita o zoom entre 1x e 4x
                        zoom = Math.max(ZOOM_MIN, Math.min(zoom, ZOOM_MAX));

                        limitarDeslocamento();
                        invalidate();
                        return true;
                    }
                }
        );
    }


    // =====================================
    // 5.1. MÉTODO PARA ALTERNAR ANDAR E MAPA
    // =====================================

    public void mudarAndar(int andar) {
        if (andar == 2) {
            mapa = BitmapFactory.decodeResource(getResources(), R.drawable.planta_andar2);
            grafo = BlocoDAndar2GraphFactory.criarGrafo();
        } else {
            mapa = BitmapFactory.decodeResource(getResources(), R.drawable.planta_bloco_d);
            grafo = BlocoDGraphFactory.criarGrafo();
        }

        // Reseta o zoom e o deslocamento ao trocar de andar
        zoom = 1.0f;
        deslocamentoX = 0f;
        deslocamentoY = 0f;
        rotaAtual = null;

        invalidate();
    }


    // =====================================
    // 6. RECEBER A ROTA CALCULADA
    // =====================================

    public void definirRota(List<Node> rota) {
        rotaAtual = rota;
        invalidate();
    }


    // =====================================
    // 7. EVENTOS DE TOQUE
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
                if (event.getPointerCount() == 1 && arrastando && !detectorZoom.isInProgress()) {
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
    // 8. LIMITAR MOVIMENTAÇÃO DO MAPA
    // =====================================

    private void limitarDeslocamento() {
        if (mapa == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }

        float escalaBase = Math.min(
                (float) getWidth() / mapa.getWidth(),
                (float) getHeight() / mapa.getHeight()
        );

        float larguraFinal = mapa.getWidth() * escalaBase * zoom;
        float alturaFinal = mapa.getHeight() * escalaBase * zoom;

        float limiteX = Math.max(0f, (larguraFinal - getWidth()) / 2f);
        float limiteY = Math.max(0f, (alturaFinal - getHeight()) / 2f);

        deslocamentoX = Math.max(-limiteX, Math.min(deslocamentoX, limiteX));
        deslocamentoY = Math.max(-limiteY, Math.min(deslocamentoY, limiteY));
    }


    // =====================================
    // 9. DESENHAR O MAPA E AS ROTAS
    // =====================================

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mapa == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }

        float larguraTela = getWidth();
        float alturaTela = getHeight();

        float larguraMapa = mapa.getWidth();
        float alturaMapa = mapa.getHeight();

        float escalaBase = Math.min(
                larguraTela / larguraMapa,
                alturaTela / alturaMapa
        );

        float x = (larguraTela - larguraMapa * escalaBase) / 2f;
        float y = (alturaTela - alturaMapa * escalaBase) / 2f;

        canvas.save();

        canvas.translate(deslocamentoX, deslocamentoY);
        canvas.translate(x, y);
        canvas.scale(escalaBase, escalaBase);
        canvas.scale(zoom, zoom, larguraMapa / 2f, alturaMapa / 2f);

        // Desenha a planta (Térreo ou 2º Andar)
        canvas.drawBitmap(mapa, 0, 0, null);

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

        // Desenhos de teste do grafo (Debug)
        if (MOSTRAR_GRAFO_DEBUG) {
            for (Node origem : grafo.obterTodosNos()) {
                for (Edge aresta : grafo.obterVizinhos(origem.getId())) {
                    Node destino = aresta.getDestino();
                    if (origem.getId().compareTo(destino.getId()) >= 0) {
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

            for (Node no : grafo.obterTodosNos()) {
                canvas.drawCircle(no.getX(), no.getY(), 10f, pincelNos);
            }
        }

        desenharMarcadores(canvas);
        canvas.restore();
    }

    private void desenharMarcadores(Canvas canvas) {
        if (rotaAtual == null || rotaAtual.isEmpty()) {
            return;
        }

        Node inicio = rotaAtual.get(0);
        Node destino = rotaAtual.get(rotaAtual.size() - 1);

        Paint contorno = new Paint(Paint.ANTI_ALIAS_FLAG);
        contorno.setColor(Color.WHITE);
        contorno.setStyle(Paint.Style.FILL);

        Paint pincelInicio = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelInicio.setColor(Color.rgb(22, 163, 74));
        pincelInicio.setStyle(Paint.Style.FILL);

        Paint pincelDestino = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincelDestino.setColor(Color.rgb(220, 38, 38));
        pincelDestino.setStyle(Paint.Style.FILL);

        float raio = 12f;
        float raioContorno = 15f;

        canvas.drawCircle(inicio.getX(), inicio.getY(), raioContorno, contorno);
        canvas.drawCircle(inicio.getX(), inicio.getY(), raio, pincelInicio);

        canvas.drawCircle(destino.getX(), destino.getY(), raioContorno, contorno);
        canvas.drawCircle(destino.getX(), destino.getY(), raio, pincelDestino);
    }
}