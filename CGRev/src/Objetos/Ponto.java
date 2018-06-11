package Objetos;

/**
 * Classe de estrutura e manipulacao de vertices
 * @author Maycon
 */
public class Ponto {
  
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
   * Retorna o ponto no formato (x; y; z)
   * @return String com o conteudo do retorno
   */
  @Override
  public String toString() {
    return "(" + x + "; " + y + "; " + z + "; " + w + ")";
  }
}
