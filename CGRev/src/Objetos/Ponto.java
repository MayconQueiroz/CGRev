package Objetos;

import java.io.Serializable;

/**
 * Classe de estrutura e manipulacao de vertices
 * @author Maycon
 */
public class Ponto implements Serializable {

  /**
   * Variaveis publicas
   */
  public double x;
  public double y;
  public double z;
  public double w; //Fator 1

  /**
   * Construtor Default
   */
  public Ponto() {
    x = 0;
    y = 0;
    z = 0;
    w = 1;
  }

  /**
   * Construtor de Copia
   * @param p Ponto a ser clonado
   */
  public Ponto(Ponto p) {
    this();
    x = p.x;
    y = p.y;
    z = p.z;
    w = p.w;
  }

  /**
   * Construtor "normalmente" usado (w e automaticamente 1)
   * @param X Coordenada x
   * @param Y Coordenada y
   * @param Z Coordenada z
   */
  public Ponto(double X, double Y, double Z) {
    x = X;
    y = Y;
    z = Z;
    w = 1;
  }

  /**
   * Construtor de pontos para perfil (que nao tem z)
   * @param X coordenada x
   * @param Y coordenada y
   */
  public Ponto(double X, double Y) {
    this(X, Y, 0.0);
  }

  /**
   * Calcula a distancia entre dois pontos, o que chamou e o passado
   * @param p Ponto para calculo de distancia
   * @return Distancia em "unidades"
   */
  public double calculaDistancia(Ponto p) {
    double difx = p.x - this.x;
    double dify = p.y - this.y;
    double difz = p.z - this.z;
    return Math.sqrt((difx * difx) + (dify * dify) + (difz * difz));
  }

  /**
   * Calcula a distancia entre dois pontos, o que chamou e o passado (mas
   * retorna valores negativos)
   * @param p Ponto para calculo de diferenca
   * @return Diferenca em "unidades"
   */
  public double calculaDiferenca(Ponto p) {
    double difx = p.x - this.x;
    double dify = p.y - this.y;
    double difz = p.z - this.z;
    return (difx + dify + difz);
  }

  /**
   * Normaliza o ponto (ou vetor)
   */
  public void Normaliza() {
    double N; //Norma
    N = Math.sqrt((x * x) + (y * y) + (z * z));
    x = x / N;
    y = y / N;
    z = z / N;
  }

  /**
   * Realiza o produto vetorial entre dois pontos
   * @param p Segundo ponto na multiplicacao
   * @return Ponto final com o resultado
   */
  public Ponto ProdutoVetorial(Ponto p) {
    Ponto T = new Ponto();
    T.x = (y * p.z) - (z * p.y);
    T.y = (z * p.x) - (x * p.z);
    T.z = (x * p.y) - (y * p.x);
    return T;
  }

  /**
   * Retorna o produto escalar de dois pontos
   * @param p Segundo Ponto na multiplicacao
   * @return Resultado da multiplicacao
   */
  public double ProdutoEscalar(Ponto p) {
    return (x * p.x) + (y * p.y) + (z * p.z);
  }

  /**
   * Calcula a diferenca entre dois pontos
   * @param p Ponto a subtrair o primeiro
   */
  public void Diferenca(Ponto p) {
    x = x - p.x;
    y = y - p.y;
    z = z - p.z;
  }

  /**
   * Calcula a soma de dois pontos
   * @param p Ponto a ser somado
   */
  public void Soma(Ponto p) {
    x = x + p.x;
    y = y + p.y;
    z = z + p.z;
  }

  /**
   * Retorna o ponto no formato (x; y; z)
   * @return String com o conteudo do retorno
   */
  @Override
  public String toString() {
    return "(" + x + "; " + y + "; " + z + "; " + w + ")";
  }
}
