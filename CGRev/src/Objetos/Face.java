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
  public ArrayList<Integer> fAresta; //Indices das arestas da face

  /**
   * Construtor padrao, setar as arestas manualmente
   */
  public Face() {
    fAresta = new ArrayList();
  }
  
  /**
   * Construtor de copia
   * @param f Face para copia
   */
  public Face(Face f){
    this();
    for (int a : f.fAresta){
      fAresta.add(a);
    }
  }
  
  /**
   * Construtor com uma aresta (Pra ter um comeco)
   * @param a Aresta inicial
   */
  public Face(int a){
    this();
    fAresta.add(a);
  }
}
