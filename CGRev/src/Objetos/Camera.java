package Objetos;

import static revcg.RevCG.*;

/**
 * Classe de estrutura e manipulacao de camera
 * @author Maycon
 */
public class Camera {
  public Ponto VRP; //Ponto da camera (VRP)
  public Ponto P; //Ponto focal P
  public int Umax, Vmax, Xmin, Xmax, Ymin, Ymax; //Limites da window (mundo)
  public Ponto Y; //Direcao de Y
  public double D; //Distancia do plano de projecao
  public double near, far; //Distancia plano near e far
  public float ZBuffer[][]; //ZBuffer
  public double TC[][] = new double[4][4]; //Matriz de transformacao para SRC
  public double TT[][] = new double[3][3]; //Matriz de transformacao para SRT
  public boolean plla; //True se for paralela

  /**
   * Construtor com praticamente todos os parametros
   * @param C Posicao do VRP
   * @param P Ponto P
   * @param V "Vetor" direcao de View Up
   * @param umax U maximo da janela
   * @param vmax V maximo da janela
   * @param xmin X minimo da cena
   * @param xmax X maximo da cena
   * @param ymin Y minimo da cena
   * @param ymax Y maximo da cena
   * @param d Distancia do plano de projecao (rever, pode ser o P sem problemas)
   */
  public Camera(Ponto C, Ponto P, Ponto V, int umax, int vmax, int xmin, int xmax, int ymin, int ymax, double d) {
    if (C == null || P == null || V == null){
      ErroPadrao();
    }
    this.VRP = C;
    this.P = P;
    this.Y = V;
    Umax = umax;
    Vmax = vmax;
    Xmin = xmin;
    Xmax = xmax;
    Ymin = ymin;
    Ymax = ymax;
    near = 1;
    far = 50;
    D = d;
  }

  /**
   * Construtor padrao sem parametros
   */
  public Camera() {
    VRP = new Ponto();
    P = new Ponto();
    Y = new Ponto();
  }
  
  /**
   * Calcula a matriz de transformacao da camera
   */
  public void MatrizTransformacaoPSRC(){
    Ponto N = new Ponto(VRP.x-P.x, VRP.y-P.y, VRP.z-P.z);
    N.Normaliza(); //Calcula e normaliza N
    double PE = (N.x * Y.x) + (N.y * Y.y) + (N.z * Y.z); //ViewUp vai ser paralelo com alguem, inverter processamento de U
    //Fazer N com viewUp para U e usar U para gerar V
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
    TC[3][3] = 1; //Constroi a     
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
}
