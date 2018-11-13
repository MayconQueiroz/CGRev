package Objetos;

/**
 * Classe de estrutura e manipulacao de arestas
 * @author Maycon
 */
public class Aresta {
  
  /**
   * Variaveis publicas
   */
  public Ponto i; //Ponto Inicial
  public Ponto f; //Ponto Final
  public Face e; //Face Esquerda
  public Face d; //Face Direita

  /**
   * Construtor Default
   */
  public Aresta() {
    i = new Ponto();
    f = new Ponto();
  }  

  /**
   * Construtor com parametros
   * @param I Ponto inicial da aresta
   * @param F Ponto final da aresta
   */
  public Aresta(Ponto I, Ponto F) {
    i = I;
    f = F;
  }
  
  /**
   * Construtor de copia (Cria novos pontos)
   * @param old Aresta a ser copiada
   */
  public Aresta(Aresta old){
    this();
    this.f = new Ponto(old.f.x, old.f.y, old.f.z);
    this.i = new Ponto(old.i.x, old.i.y, old.i.z);
  }
  
  /**
   * Retorna aresta no formato {p1; p2}
   * @return String com o conteudo do retorno
   */
  @Override
  public String toString() {
    return "{" + i.toString() + "; " + f.toString() + "}";
  }

}
