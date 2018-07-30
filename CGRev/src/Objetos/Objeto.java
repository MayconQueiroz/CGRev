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
  
  /**
   * Constroi arestas com base nos pontos ja existentes (De leitura por exemplo)
   * @param fechado Se o primeiro ponto eh igual ao ultimo
   */
  public void ConstroiArestas(boolean fechado) {
    Ponto Au;
    arrAresta.clear();
    Au = arrPonto.get(0);
    for (Ponto p : arrPonto.subList(1, arrPonto.size())) {
      arrAresta.add(new Aresta(Au, p));
      Au = p;
    }
    if (fechado) { //Fechada
      arrAresta.add(new Aresta(arrPonto.get(arrPonto.size() - 1), arrPonto.get(0)));
    }
  }
  
}
