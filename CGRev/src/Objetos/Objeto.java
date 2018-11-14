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
  public ArrayList<Face> arrFace; //Faces do objeto
  public Color AC = Color.BLACK; //Arestas (Cor)
  public Color BG = Color.grayRgb(240); //Cor de fundo (Cor do objeto)
  public boolean Fechado; //Se o objeto e fechado ou aberto (dupla face ou unica face)
  public Ponto C; //Centro do Objeto

  /**
   * Construtor padrao do objeto
   */
  public Objeto() {
    arrAresta = new ArrayList<>();
    arrPonto = new ArrayList<>();
    arrFace = new ArrayList<>();
    Fechado = false;
    C = new Ponto();
  }
  
  /**
   * Construtor de copia
   * @param Oab Objeto para copia (ou organizacao dos associados do bradesco)
   */
  public Objeto(Objeto Oab){
    
  }
  
  /**
   * Constroi arestas com base nos pontos ja existentes (De leitura por exemplo)
   * @param fechado Se o primeiro ponto eh igual ao ultimo
   */
  public void ConstroiArestas(boolean fechado) {
    arrAresta.clear();
    for (int i = 1; i < arrPonto.size(); i++){
      arrAresta.add(new Aresta(i-1, i));
    }
    if (fechado) { //Fechada (Aresta entre ultimo ponto e primeiro)
      arrAresta.add(new Aresta(arrPonto.size() - 1, 0));
    }
  }
  
  /**
   * Constroi arestas e ja cria faces para estas arestas
   * @param fechado Se o primeiro ponto e igualao ultimo (nao duplica)
   */
  public void ConstroiArestasMaisFaces(boolean fechado){
    arrAresta.clear();
    arrFace.clear();
    for (int i = 1; i < arrPonto.size(); i++){
      arrAresta.add(new Aresta(i-1, i));
      arrFace.add(new Face(arrAresta.size()-1)); //Cria nova face com a aresta recem criada
      arrAresta.get(arrAresta.size()-1).d = arrFace.size()-1; //Acrescenta essa face a face "direita" da aresta
    }
    if (fechado) { //Fechada
      arrAresta.add(new Aresta(arrPonto.size() - 1, 0));
      arrFace.add(new Face(arrAresta.size()-1)); //Cria nova face com a aresta recem criada
      arrAresta.get(arrAresta.size()-1).d = arrFace.size()-1; //Acrescenta essa face a face "direita" da aresta
    }
  }
  
  /**
   * Calcula o centro do objeto (varre os pontos e calcula as medias)
   */
  public void CalculaCentro(){
    double mx, nx, my, ny, mz, nz;
    if (this == null){ //Se objeto ainda nao estiver preenchido so retorna
      return;
    }
    mx = arrPonto.get(0).x;
    nx = arrPonto.get(0).x;
    my = arrPonto.get(0).y;
    ny = arrPonto.get(0).y;
    mz = arrPonto.get(0).z;
    nz = arrPonto.get(0).z;
    for (Ponto p : arrPonto){ //Para todos os pontos do objeto
      if (p.x > mx){
        mx = p.x;
      }
      if (p.x < nx){
        nx = p.x;
      }
      if (p.y > my){
        my = p.y;
      }
      if (p.y < ny){
        ny = p.y;
      }
      if (p.z > mz){
        mz = p.z;
      }
      if (p.z < nz){
        nz = p.z;
      }
    }
    C.x = (mx + nx) / 2; //Grava direto na variavel C
    C.y = (my + ny) / 2;
    C.z = (mz + nz) / 2;
  }

  /**
   * Define a cor do objeto (Eu sei que eh publico, 
   * eh que na tela principal ja tem uma importacao 
   * pra uma classe de cor e tava dando intereferencia)
   * @param r Vermelho
   * @param g Verde
   * @param b Azul
   */
  public void VaiCor(short r, short g, short b) {
    BG = Color.rgb(r, g, b);
  }
  
}
