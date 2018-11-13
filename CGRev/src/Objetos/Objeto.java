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
  
  /**
   * Constroi arestas e ja cria faces para estas arestas
   * @param fechado Se o primeiro ponto e igualao ultimo (nao duplica)
   */
  public void ConstroiArestasMaisFaces(boolean fechado){
    Ponto Au;
    arrAresta.clear();
    arrFace.clear();
    Au = arrPonto.get(0);
    for (Ponto p : arrPonto.subList(1, arrPonto.size())) {
      arrAresta.add(new Aresta(Au, p));
      arrFace.add(new Face(arrAresta.get(arrAresta.size()-1))); //Cria nova face com a aresta recem criada
      arrAresta.get(arrAresta.size()-1).d = arrFace.get(arrFace.size()-1); //Acrescenta essa face a face "direita" da aresta
      Au = p;
    }
    if (fechado) { //Fechada
      arrAresta.add(new Aresta(arrPonto.get(arrPonto.size() - 1), arrPonto.get(0)));
      arrFace.add(new Face(arrAresta.get(arrAresta.size()-1))); //Cria nova face com a aresta recem criada
      arrAresta.get(arrAresta.size()-1).d = arrFace.get(arrFace.size()-1); //Acrescenta essa face a face "direita" da aresta
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
  
}
