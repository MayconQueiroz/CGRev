package Objetos;

import java.util.ArrayList;
import javafx.scene.paint.Color;

/**
 * Classe de estrutura e manipulacao de objetos (em geral)
 * @author Maycon
 */
public class Objeto {
  
  /**
   * Variaveis publicas
   */
  public ArrayList<Aresta> arrAresta; //Arestas no objeto
  public ArrayList<Ponto> arrPonto; //Pontos do objeto
  public Color AC = Color.BLACK; //Arestas (Cor)
  public boolean Fechado;
  public Ponto C; //Centro do Objeto

  public Objeto() {
    arrAresta = new ArrayList<>();
    arrPonto = new ArrayList<>();
    Fechado = false;
    C = new Ponto();
  }
  
}
