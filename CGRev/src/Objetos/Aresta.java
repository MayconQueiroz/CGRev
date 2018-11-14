package Objetos;

/**
 * Classe de estrutura e manipulacao de arestas
 * @author Maycon
 */
public class Aresta {
  
  /**
   * Variaveis publicas
   */
  public int i; //Indice do ponto Inicial
  public int f; //Indice do ponto Final
  public int e; //Indice da face Esquerda
  public int d; //Indice da face Direita

  /**
   * Construtor Default
   */
  public Aresta() {
    i = 0;
    f = 0;
  }  

  /**
   * Construtor com parametros
   * @param I Ponto inicial da aresta
   * @param F Ponto final da aresta
   */
  public Aresta(int I, int F) {
    i = I;
    f = F;
  }
  
  /**
   * Construtor de aresta com todos os parametros (para leitor)
   * @param I Ponto inicial
   * @param F Ponto final
   * @param E Face da esquerda
   * @param D Face da direita
   */
  public Aresta(int I, int F, int E, int D) {
    i = I;
    f = F;
    e = E;
    d = D;
  }
  
  /**
   * Construtor de copia (Cria novos pontos)
   * @param old Aresta a ser copiada
   */
  public Aresta(Aresta old){
    f = old.f;
    i = old.i;
  }
  
  /**
   * Retorna aresta no formato {indice1; indice}
   * @return String com o conteudo do retorno
   */
  @Override
  public String toString() {
    return "{" + i + "; " + f + "}";
  }

}
