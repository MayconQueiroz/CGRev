package Objetos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static revcg.RevCG.*;

/**
 * Classe de estrutura e manipulacao de camera
 * @author Maycon
 */
public class Camera {
  public Ponto VRP; //Ponto da camera (VRP)
  public Ponto P; //Ponto focal P (Plano de projecao fica nesse ponto)
  public double D; //Distancia do plano de projecao
  public int Umax, Vmax, Xmin, Xmax, Ymin, Ymax; //Limites da window (mundo)
  public Ponto Y; //Direcao de Y
  public double near, far; //Distancia plano near e far
  public float ZBuffer[][]; //ZBuffer
  public BufferedImage IBuffer; //Imagem a ir pro painel
  public double TC[][] = new double[4][4]; //Matriz de transformacao para SRC
  public double TT[][] = new double[4][4]; //Matriz de transformacao para SRT
  public double TP[][] = new double[4][4]; //Matriz de transformacao perspectiva ou paralela
  public boolean plla; //True se for paralela
  public ArrayList<Objeto> obj; //Lista de objetos em coordenadas do sistema de camera
  public Graphics DP; //Graphics para desenhos

  /**
   * Construtor com praticamente todos os parametros
   * @param C Posicao do VRP
   * @param p Ponto P
   * @param V "Vetor" direcao de View Up
   * @param umax U maximo da janela
   * @param vmax V maximo da janela
   * @param xmin X minimo da cena
   * @param xmax X maximo da cena
   * @param ymin Y minimo da cena
   * @param ymax Y maximo da cena
   * @param d Distancia do plano de projecao entre VRP e P
   * @param Plla True se a projecao for paralela, perspectiva caso contrario
   */
  public Camera(Ponto C, Ponto p, Ponto V, int umax, int vmax, int xmin, int xmax, int ymin, int ymax, double d, boolean Plla) {
    if (C == null || p == null || V == null){
      ErroPadrao();
    }
    VRP = C;
    P = p;
    Y = V;
    Umax = umax;
    Vmax = vmax;
    Xmin = xmin;
    Xmax = xmax;
    Ymin = ymin;
    Ymax = ymax;
    near = 1;
    far = 50;
    obj = new ArrayList<>();
    D = d;
    plla = Plla;
    IBuffer = new BufferedImage(Umax, Vmax, BufferedImage.TYPE_INT_ARGB);
    DP = IBuffer.getGraphics();
  }

  /**
   * Construtor padrao sem parametros
   */
  public Camera() {
    VRP = new Ponto();
    P = new Ponto();
    Y = new Ponto();
    obj = new ArrayList<>();
  }
  
  /**
   * Atualiza os objetos da camera, com as coordenadas em SRC
   * @param An Lista de objetos principal para copia
   */
  public void AtualizaCamera(ArrayList<Objeto> An){
    obj.clear();
    MatrizTransformacaoPSRC();
    for (Objeto u : An){ //Copia todos os objetos para a camera
      obj.add(new Objeto(u));
    }
    double COT[][] = new double[4][1]; //Coordenadas do objeto temporarias
    for (Objeto u : obj){
      for (Ponto pi : u.arrPonto){
        COT[0][0] = pi.x;
        COT[1][0] = pi.y;
        COT[2][0] = pi.z;
        COT[3][0] = pi.w;
        COT = MultMatrizes(TC, COT);
        COT = ChecaW(COT);
        pi.x = COT[0][0];
        pi.y = COT[1][0];
        pi.z = COT[2][0];
        pi.w = COT[3][0];
      }
    }
  }
  
  /**
   * Carrega a visualizacao para a tela
   * @param Op Operacao de visualizacao
   * 0 - Wireframe
   * 1 - Wireframe com ocultacao de linhas
   * 2 - Sombreamento constante
   * 3 - Sombreamento Gouraud
   * @param Obsel Indice do objeto selecionado
   */
  public void AtualizaVisao(byte Op, int Obsel){
    Objeto o;
    DP.setColor(CFundo);
    DP.fillRect(0, 0, Umax, Vmax);
    DP.setColor(Color.BLACK);
    if (Op == 0){
      if (plla){
        CalculaZbuffer(Op, Obsel);
      } else {
        
      }
    } else if (Op == 1){
      if (plla){
        
      } else {
        
      }
    } else if (Op == 2){
      if (plla){
        
      } else {
        
      }
    } else if (Op == 3){
      if (plla){
        
      } else {
        
      }
    } else {
      ErroPadrao();
    }
  }
  
  /**
   * Onde a magica acontece para o ZBuffer
   * @param Op Operacao de visualizacao
   * 0 - Wireframe
   * 1 - Wireframe com ocultacao de linhas
   * 2 - Sombreamento constante
   * 3 - Sombreamento Gouraud
   * @param Obsel Indice do objeto selecionado
   */
  public void CalculaZbuffer(byte Op, int Obsel){
    Objeto o;
    double Escala = (double) (Xmax - Xmin) / Umax;
    if (Op == 0){
      for (int i = 0; i < obj.size(); i++){
        o = obj.get(i);
        DP.setColor(i == Obsel ? Sel : Color.BLACK);
        for (Aresta w : o.arrAresta){
          DP.drawLine((int) ((o.arrPonto.get(w.i).x * Escala) + (Umax/2)), (int) ((o.arrPonto.get(w.i).y * Escala) + (Vmax/2)), (int) ((o.arrPonto.get(w.f).x * Escala) + (Umax/2)), (int) ((o.arrPonto.get(w.f).y * Escala) + (Vmax/2)));
        }
      }
    } else if (Op == 1){
      
    } else if (Op == 2){
      
    } else if (Op == 3){
      
    } else {
      ErroPadrao();
    }
  }
  
  /**
   * Calcula a matriz de transformacao da camera
   */
  public void MatrizTransformacaoPSRC(){
    Ponto N = new Ponto(VRP.x-P.x, VRP.y-P.y, VRP.z-P.z);
    N.Normaliza(); //Calcula e normaliza N
    double PE = (N.x * Y.x) + (N.y * Y.y) + (N.z * Y.z); //ViewUp vai ser paralelo com alguem, inverter processamento de U
    //Fazer N com viewUp para U e usar U para gerar V (To tentando outra coisa
    Ponto V = new Ponto(Y.x-(PE*N.x), Y.y-(PE*N.y), Y.z-(PE*N.z));
    V.Normaliza(); //Calcula e normaliza V
    Ponto U = V.ProdutoVetorial(N);
    U.Normaliza(); //Calcula e normaliza U
    TC[0][0] = U.x;
    TC[0][1] = U.y;
    TC[0][2] = U.z;
    TC[0][3] = -(VRP.ProdutoEscalar(U));
    TC[1][0] = V.x;
    TC[1][1] = V.y;
    TC[1][2] = V.z;
    TC[1][3] = -(VRP.ProdutoEscalar(V));
    TC[2][0] = N.x;
    TC[2][1] = N.y;
    TC[2][2] = N.z;
    TC[2][3] = -(VRP.ProdutoEscalar(N));
    TC[3][0] = 0;
    TC[3][1] = 0;
    TC[3][2] = 0;
    TC[3][3] = 1;
  }
  
  /**
   * Cria a matriz de transformacao de SRC para STR
   */
  public void MatrizTransformacaoPSRT(){
    TT[0][0] = Umax/(Xmax - Xmin);
    TT[0][1] = 0;
    TT[0][2] = (-Xmin * TT[0][0]);
    TT[1][0] = 0;
    TT[1][1] = Vmax/(Ymax - Ymin);
    TT[1][2] = (Ymin * TT[1][1]) + Vmax;
    TT[2][0] = 0;
    TT[2][1] = 0;
    TT[2][2] = 1;
  }
  
  /**
   * Cria a matriz de transformacao perspectiva
   */
  public void MatrizTransformacaoPers(){
    TP[0][0] = 1;
    TP[0][1] = 0;
    TP[0][2] = 0;
    TP[0][3] = 0;
    TP[1][0] = 0;
    TP[1][1] = 1;
    TP[1][2] = 0;
    TP[1][3] = 0;
    TP[2][0] = 0;
    TP[2][1] = 0;
    TP[2][2] = 1;
    TP[2][3] = 0;
    TP[3][0] = 0;
    TP[3][1] = 0;
    TP[3][2] = -(1/P.calculaDistancia(VRP));
    TP[3][3] = 0;
  }
  
  /**
   * Cria a matriz de transformacao paralela
   */
  public void MatrizTransformacaoPara(){
    TP[0][0] = 1;
    TP[0][1] = 0;
    TP[0][2] = 0;
    TP[0][3] = 0;
    TP[1][0] = 0;
    TP[1][1] = 1;
    TP[1][2] = 0;
    TP[1][3] = 0;
    TP[2][0] = 0;
    TP[2][1] = 0;
    TP[2][2] = 0;
    TP[2][3] = 0;
    TP[3][0] = 0;
    TP[3][1] = 0;
    TP[3][2] = 0;
    TP[3][3] = 1;
  }
  
  /**
   * Multiplica duas matrizes de dimensao 4x4 e 4x?
   * @param M1 Primeira matriz
   * @param M2 Segunda matriz
   * @return Matriz resultado
   */
  public double[][] MultMatrizes(double[][] M1, double[][] M2){
    int M2L = M2.length;
    int M1C = M1[0].length;
    int M2C = M2[0].length;
    double R[][] = new double[M2L][M2C];
    
    for (int i = 0; i < M1C; i++) {
      for (int j = 0; j < M2C; j++) {
        R[i][j] = (M1[i][0] * M2[0][j]) + (M1[i][1] * M2[1][j]) + (M1[i][2] * M2[2][j]) + (M1[i][3] * M2[3][j]);
      }
    }
    
    return R;
  }
  
  /**
   * Verifica se o parametro W dos pontos eh 1, se nao for, divide todos por ele pra retornar 1
   * @param M Matriz 4x1 com os pontos x, y, z e w
   * @return Mesma matriz mas verificada
   */
  public double[][] ChecaW(double[][] M){
    if (M[3][0] == 1){
      return M;
    }
    M[0][0] = M[0][0]/M[3][0];
    M[1][0] = M[1][0]/M[3][0];
    M[2][0] = M[2][0]/M[3][0];
    M[3][0] = M[3][0]/M[3][0];
    return M;
  }
}
