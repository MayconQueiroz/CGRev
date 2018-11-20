package Objetos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe de estrutura e manipulacao de faces
 * (A maior parte da manipulacao esta na classe de objeto ja 
 * que nao da pra fazer muita coisa com indices)
 * @author Maycon
 */
public class Face implements Serializable {

  /**
   * Variaveis Publicas
   */
  public ArrayList<Integer> fAresta; //Indices das arestas da face
  public Ponto N; //Normal normalizada da face

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
  public Face(Face f) {
    this();
    for (int a : f.fAresta) {
      fAresta.add(a);
    }
    N = f.N;
  }

  /**
   * Construtor com uma aresta (Pra ter um comeco)
   * @param a Aresta inicial
   */
  public Face(int a) {
    this();
    fAresta.add(a);
  }
}
