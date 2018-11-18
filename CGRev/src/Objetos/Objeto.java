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
    this();
    for (Ponto p : Oab.arrPonto){
      arrPonto.add(new Ponto(p));
    }
    for (Aresta a : Oab.arrAresta){
      arrAresta.add(new Aresta(a));
    }
    for (Face f : Oab.arrFace){
      arrFace.add(new Face(f));
    }
    BG = Oab.BG;
    Fechado = Oab.Fechado;
    C = new Ponto(Oab.C);
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
   * Pode cair, as bibliotecas estao pra ser mudadas!!!
   * @param r Vermelho
   * @param g Verde
   * @param b Azul
   */
  public void VaiCor(short r, short g, short b) {
    BG = Color.rgb(r, g, b);
  }
  
  /**
   * Calcula o centro de uma face (Coordenadas x, y e z)
   * @param f Face a ser computada
   * @return Ponto com as coordenadas (do mundo) do centro da face
   */
  public Ponto CentroideFace(Face f){
    Ponto Ce = new Ponto();
    double tmx, tmy, tmz, tMx, tMy, tMz;
    tmx = arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).x;
    tmy = arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).y;
    tmz = arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).z;
    tMx = arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).x;
    tMy = arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).y;
    tMz = arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).z;
    for (int i : f.fAresta){
      tmx = tmx > arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).x ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).x : tmx;
      tmx = tmx > arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).x ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).x : tmx;
      tmy = tmy > arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).y ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).y : tmy;
      tmy = tmy > arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).y ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).y : tmy;
      tmz = tmz > arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).z ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).z : tmz;
      tmz = tmz > arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).z ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).z : tmz;
      tMx = tMx < arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).x ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).x : tMx;
      tMx = tMx < arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).x ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).x : tMx;
      tMy = tMy < arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).y ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).y : tMy;
      tMy = tMy < arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).y ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).y : tMy;
      tMz = tMz < arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).z ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).i).z : tMz;
      tMz = tMz < arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).z ? arrPonto.get(arrAresta.get(f.fAresta.get(0)).f).z : tMz;
    }
    Ce.x = (tMx + tmx) / 2;
    Ce.y = (tMy + tmy) / 2;
    Ce.z = (tMz + tmz) / 2;
    return Ce;
  }
  
  /**
   * Retira o centro de todo o objeto (sem alterar o mesmo)
   */
  public void MenosCentro(){
    for (Ponto t : arrPonto){
      t.x -= C.x;
      t.y -= C.y;
      t.z -= C.z;
    }
  }
  
  /**
   * Soma o centro de todo o objeto (sem alterar o mesmo)
   */
  public void MaisCentro(){
    for (Ponto t : arrPonto){
      t.x += C.x;
      t.y += C.y;
      t.z += C.z;
    }
  }
  
}
