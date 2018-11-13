package Objetos;

import static revcg.RevCG.*;

/**
 * Classe de estrutura e manipulacao de camera
 * @author Maycon
 */
public class Camera {
  public Ponto VRP; //Ponto da camera (VRP)
  public Ponto P; //Ponto focal P
  public int Xmin, Xmax, Ymin, Ymax; //Limites da window
  public Ponto Y; //Direcao de Y
  public double D; //Distancia do plano de projecao
  public double near, far; //Distancia plano near e far
  public float ZBuffer[][]; //ZBuffer
  public double Tr[][] = new double[4][4]; //Matriz de transformacao
  public boolean plla; //True se for paralela

  /**
   * Construtor com praticamente todos os parametros
   * @param C Posicao do VRP
   * @param P Ponto P
   * @param V "Vetor" direcao de View Up
   * @param xmin X minimo da cena
   * @param xmax X maximo da cena
   * @param ymin Y minimo da cena
   * @param ymax Y maximo da cena
   * @param d Distancia do plano de projecao (rever, pode ser o P sem problemas)
   */
  public Camera(Ponto C, Ponto P, Ponto V, int xmin, int xmax,int ymin, int ymax, double d) {
    if (C == null || P == null || V == null){
      ErroPadrao();
    }
    this.VRP = C;
    this.P = P;
    this.Y = V;
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
  public void MatrizTransformacao(){
    Ponto N = new Ponto(VRP.x-P.x, VRP.y-P.y, VRP.z-P.z);
    N.Normaliza(); //Calcula e normaliza N
    double PE = (N.x * Y.x) + (N.y * Y.y) + (N.z * Y.z);
    Ponto V = new Ponto(Y.x-(PE*N.x), Y.y-(PE*N.y), Y.z-(PE*N.z));
    V.Normaliza(); //Calcula e normaliza V
    Ponto U = V.ProdutoVetorial(N);
    U.Normaliza(); //Calcula e normaliza U
    Tr[0][0] = U.x;
    Tr[0][1] = U.y;
    Tr[0][2] = U.z;
    Tr[0][3] = -(VRP.ProdutoEscalar(U));
    Tr[1][0] = V.x;
    Tr[1][1] = V.y;
    Tr[1][2] = V.z;
    Tr[1][3] = -(VRP.ProdutoEscalar(V));
    Tr[2][0] = N.x;
    Tr[2][1] = N.y;
    Tr[2][2] = N.z;
    Tr[2][3] = -(VRP.ProdutoEscalar(N));
    Tr[3][0] = 0;
    Tr[3][1] = 0;
    Tr[3][2] = 0;
    Tr[3][3] = 1; //Constroi a matriz
  }
}
