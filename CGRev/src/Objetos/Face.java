package Objetos;

import java.util.ArrayList;

/**
 * Classe de estrutura e manipulacao de faces
 *
 * @author Maycon
 */
public class Face {

  /**
   * Variaveis Publicas
   */
  public ArrayList<Aresta> fAresta; //Arestas da face

  /**
   * Construtor padrao, setar as arestas manualmente
   */
  public Face() {
    fAresta = new ArrayList();
  }
  
  /**
   * Construtor com uma aresta (Pra ter um comeco)
   * @param a Aresta inicial
   */
  public Face(Aresta a){
    this();
    fAresta.add(a);
  }
  
  /**
   * Calcula o centro de uma face (Coordenadas x, y e z)
   * @return Ponto com as coordenadas (do mundo) do centro da face
   */
  public Ponto Centroide(){
    Ponto Ce = new Ponto();
    //FAZER Busca de pontos maiores e menores para tirar a media
    return Ce;
  }

}
