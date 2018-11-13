package Telas;

//ToFuture: Int max value: 2147483647

import Objetos.*;
import java.util.ArrayList;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;
import static revcg.RevCG.*;

/**
 * Janela onde se pode gerar perfis que serao rotacionados
 *
 * @author Maycon
 */
public class Perfil extends javax.swing.JFrame {

  /**
   * Variaveis publicas
   */
  public Principal P;
  public ArrayList<Ponto> arrPonto;
  public Graphics D; //de Desenho
  public ArrayList<Aresta> arrAresta;
  public int iniY = 1023;
  public JFileChooser fc = new JFileChooser();
  public byte cabecalho;
  public Objeto obj;
  public boolean issaved;
  public boolean fechado;

  /**
   * Variaveis globais internas
   */
  byte Bcount = 0;
  ArrayList<Ponto> BarrPonto;
  ArrayList<Ponto> BarrPontoC;
  byte Bsp = -1;
  Ponto Slc;
  int Bseg = 5;
  double Bau = 0;

  /**
   * Creates new form Perfil
   * Privado so pra evitar a cagada
   */
  private Perfil() {
    initComponents();
    ErrosIniciais();
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    setResizable(false); //Nao deixa redimensionar a janela
    //Instancia
    arrPonto = new ArrayList();
    BarrPonto = new ArrayList();
    BarrPontoC = new ArrayList();
    arrAresta = new ArrayList();
    Slc = new Ponto();
    //pnlRevI.setBackground(Color.LIGHT_GRAY);
    D = pnlRevI.getGraphics();
    issaved = true;
    fechado = false;
    lstEixos.setSelectedIndex(1);
    txtBaixo.setEnabled(false);
    txtLado.setEnabled(false);
    setIconImage(new ImageIcon(ClassLoader.getSystemResource("Icones/Perfil.png")).getImage());
  }

  /**
   * Novo perfil - Construtor usado
   * @param P
   */
  public Perfil(Principal P) {
    this();
    this.P = P;
  }

  /**
   * Calcula as arestas de bezier
   */
  public void CalculaBezier() {
    Bau = (double) 1 / Bseg;
    BarrPonto.clear();
    BarrPonto.add(BarrPontoC.get(0));
    double Bau2 = Bau;
    for (int i = 1; i < Bseg; i++) {
      DeCasteljau(Bau2);
      Bau2 += Bau;
      BarrPonto.add(new Ponto(Slc));
    }
    BarrPonto.add(new Ponto(BarrPontoC.get(3)));
    DesenhaPerfil();
  }

  /**
   * Desenha as linhas do perfil escolhido
   */
  public void DesenhaPerfil() {
    D.clearRect(0, 0, 400, 300);
    for (Aresta a : arrAresta) {
      D.drawLine((int) a.i.x, (int) a.i.y, (int) a.f.x, (int) a.f.y);
    }
    if (isB) { //Se estiver no meio de uma curva de Bezier
      D.setColor(Color.BLUE);
      if (Bcount == 3) {
        Ponto k = new Ponto();
        D.drawLine((int) BarrPontoC.get(0).x, (int) BarrPontoC.get(0).y, (int) BarrPontoC.get(1).x, (int) BarrPontoC.get(1).y);
        D.drawLine((int) BarrPontoC.get(3).x, (int) BarrPontoC.get(3).y, (int) BarrPontoC.get(2).x, (int) BarrPontoC.get(2).y);
        D.setColor(Color.BLACK);
        k = BarrPonto.get(0);
        for (int j = 1; j < BarrPonto.size(); j++) {
          D.drawLine((int) k.x, (int) k.y, (int) BarrPonto.get(j).x, (int) BarrPonto.get(j).y);
          k = BarrPonto.get(j);
        }
        D.setColor(Color.BLUE);
      }
      for (Ponto v : BarrPontoC) {
        //System.out.println("Ponto em = " + v.toString());
        D.drawRect((int) v.x - 3, (int) v.y - 3, 6, 6);
      }
      D.setColor(Color.BLACK);
    }
  }

  /**
   * Reinica Tudo
   */
  public void LimpaTudo() {
    arrPonto.clear();
    arrAresta.clear();
    D.clearRect(0, 0, 400, 300);
    lblInfo.setText("");
    iniY = 1023;
  }

  /**
   * Constroi arestas com base nos pontos ja existentes (De leitura por exemplo)
   */
  public void ConstroiArestas() {
    Ponto Au = null;
    arrAresta.clear();
    Au = arrPonto.get(0);
    for (Ponto p : arrPonto.subList(1, arrPonto.size())) {
      arrAresta.add(new Aresta(Au, p));
      Au = p;
    }
    if ((cabecalho & 8) != 0) { //Fechada
      arrAresta.add(new Aresta(arrPonto.get(arrPonto.size() - 1), arrPonto.get(0)));
    }
    ChecaQuantidade();
  }

  /**
   * Retorna um ponto da curva de bezier
   *
   * @param t Fator de ponderacao
   */
  public void DeCasteljau(double t) {
    double z1, d3, u2, ut, z12, u23;
    ut = 1 - t;
    for (Ponto v : BarrPontoC) {
      //System.out.println("t = " + t + "PC1 = " + v.toString());
      D.drawRect((int) v.x - 3, (int) v.y - 3, 6, 6);
    }
    z1 = ((ut * BarrPontoC.get(0).x) + (t * BarrPontoC.get(1).x));
    u2 = ((ut * BarrPontoC.get(1).x) + (t * BarrPontoC.get(2).x));
    d3 = ((ut * BarrPontoC.get(2).x) + (t * BarrPontoC.get(3).x));
    z12 = ((ut * z1) + (t * u2));
    u23 = ((ut * u2) + (t * d3));
    Slc.x = (ut * z12) + (t * u23);
    z1 = ((ut * BarrPontoC.get(0).y) + (t * BarrPontoC.get(1).y));
    u2 = ((ut * BarrPontoC.get(1).y) + (t * BarrPontoC.get(2).y));
    d3 = ((ut * BarrPontoC.get(2).y) + (t * BarrPontoC.get(3).y));
    z12 = ((ut * z1) + (t * u2));
    u23 = ((ut * u2) + (t * d3));
    Slc.y = (ut * z12) + (t * u23);
    for (Ponto v : BarrPontoC) {
      //System.out.println("PC2 = " + v.toString());
      D.drawRect((int) v.x - 3, (int) v.y - 3, 6, 6);
    }
    //System.out.println("SLC = " + Slc.toString());
  }
  
  /**
   * Onde a magica acontece (Para y)
   *
   * @param Num Numero de segmentos
   * @param Ang Angulo maximo de rotacao
   */
  public void CriaObjetoY(int Num, double Ang) {
    double teta = (Ang * (Math.PI / 180)) / Num; //Calcula o angulo de distancia para cada passo da revolucao
    double ccos = Math.cos(teta); //Cosseno do angulo
    double csin = Math.sin(teta); //Seno do angulo
    /*System.out.println("Teta2 = " + teta);
    System.out.println("ccos = " + ccos);
    System.out.println("csin = " + csin);*/
    double xold = arrPonto.get(0).y, zold = arrPonto.get(0).y;
    Ponto plinha = new Ponto();
    int[] BPosi = new int[arrPonto.size()];
    obj = new Objeto();
    int y = 0;
    for (Ponto p : arrPonto) { //Interacao inicial com os pontos do perfil
      BPosi[y] = y;
      y++;
      obj.arrPonto.add(new Ponto(p)); //Copia todos os pontos iniciais para o objeto
      if (p.y < xold) {
        xold = p.y; //xold carrega y minimo
      }
      if (p.y > zold) {
        zold = p.y; //zold carrega y maximo
      }
    }
    //Constroi as arestas iniciais (primeiro perfil)
    obj.ConstroiArestasMaisFaces(fechado);
    int[] FPosi = new int[obj.arrFace.size()]; //Fposi carrega os indices das faces que estao sendo criadas
    int UPP = -1, PPP = -1;
    for (int i = 0; i < FPosi.length; i++){
      FPosi[i] = i;
    }
    if (arrPonto.get(0).x != 0){
      PPP = 0; //Se o primeiro ponto e do eixo
    }
    if (arrPonto.get(arrPonto.size()-1).x != 0){
      UPP = 0; //Se o ultimo ponto e do eixo
    }
    //FAZER Definir se objeto e fechado ou nao (dupla face ou unica face)
    obj.Fechado = false; //Assim esta sempre aberto
    
    ///////////////////////////////////////////////////////////////
    if (Ang == 360.0){ //Rotacao completa
      for (int i = 0; i < Num-1; i++){ //Todas as revolucoes menos uma (ja que junta no fim)
        int ij = 0; //Indice para vetor de indices de face sendo construida
        for (y = 0; y < arrPonto.size(); y++){
          plinha = arrPonto.get(y); //plinha recebe cada um dos pontos do perfil
          if (plinha.x != 0 || plinha.z != 0) { //Ponto fora do eixo
            xold = plinha.x;
            plinha.x = (plinha.x * ccos) + (plinha.z * csin); //Rotaciona
            plinha.z = (xold * (-csin)) + (plinha.z * ccos);
            obj.arrPonto.add(new Ponto(plinha)); //copia plinha para o objeto
            obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[y]), obj.arrPonto.get(obj.arrPonto.size()-1))); //Aresta entre o ponto rotacionado e o seu irmão anterior
            BPosi[y] = obj.arrPonto.size()-1; //Agora o irmao anterior passa a ser ele
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1)); //Adiciona aresta na face
            obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]); //Adiciona a aresta esta face como a esquerda
            if ((FPosi.length - 1) > (ij + 1)){ //Se nao for a ultima face liga com a proxima tambem
              obj.arrFace.get(FPosi[ij+1]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij+1]);
            }
            if (y == 0){
              PPP = obj.arrAresta.size()-1; //Pode estar errado!
            } else if (y > 0){ //Qualquer outro ponto que nao o primeiro (liga com o irmao de cima)
              if (arrPonto.get(y - 1).x != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(obj.arrPonto.size() - 2), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else { //Ponto anterior no eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y - 1), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          } else { //Ponto no eixo
            if (y > 0){ //Se nao for o primeiro ponto (ja que so liga com o irmao de cima)
              if (arrPonto.get(y - 1).x != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                ErroPadrao();
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++; //Para a proxima face
            }
          }
        }
        if (fechado){ //Se o objeto for fechado (Vertice final == inicial)
          obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[BPosi.length-1]), obj.arrPonto.get(BPosi[0])));
          obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
          if (PPP != -1){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(PPP));
            obj.arrAresta.get(PPP).e = obj.arrFace.get(FPosi[ij]);
          }
          if (UPP == 0){ //Tenho minhas duvidas sobre esse trecho
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-3));
            obj.arrAresta.get(obj.arrAresta.size()-3).e = obj.arrFace.get(FPosi[ij]);
          }
          
        }
      }
      //Apos a "ultima" rotacao
      int ij = 0;
      for (y = 0; y < arrPonto.size(); y++){ //Praticamente tudo igual, mas liga os pontos nos iniciais ao inves de rotacionar mais uma vez
        if (arrPonto.get(y).x != 0 || arrPonto.get(y).z != 0) { //Se nao for do eixo
          obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[y]), obj.arrPonto.get(y))); //Ultimos vertices com os primeiros
          if (y > 0){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
            if ((FPosi.length - 1) > (ij + 1)){
              obj.arrFace.get(FPosi[ij+1]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij+1]);
            }
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(ij));
            obj.arrAresta.get(ij).e = obj.arrFace.get(FPosi[ij]);
            ij++;
          } else { //y = 0
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij]);
          }
        } else { //Ponto no eixo
          if (y > 0){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(ij));
            obj.arrAresta.get(ij).e = obj.arrFace.get(FPosi[ij]);
            ij++;
          }
        }
      }
    } else { //Rotacao nao completa
      for (int i = 0; i < Num; i++){ //Todas as revolucoes (vai ate o angulo)
        int ij = 0;
        for (y = 0; y < arrPonto.size(); y++){
          plinha = arrPonto.get(y); //plinha recebe cada um dos pontos do perfil
          if (plinha.x != 0 || plinha.z != 0) { //Ponto fora do eixo
            xold = plinha.x;
            plinha.x = (plinha.x * ccos) + (plinha.z * csin); //Rotaciona
            plinha.z = (xold * (-csin)) + (plinha.z * ccos);
            obj.arrPonto.add(new Ponto(plinha)); //copia plinha
            obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[y]), obj.arrPonto.get(obj.arrPonto.size()-1)));
            BPosi[y] = obj.arrPonto.size()-1;
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
            if ((FPosi.length - 1) > (ij + 1)){
              obj.arrFace.get(FPosi[ij+1]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij+1]);
            }
            if (y == 0){
              PPP = obj.arrAresta.size()-1;
            } else if (y > 0){
              if (arrPonto.get(y - 1).x != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(obj.arrPonto.size() - 2), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y - 1), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          } else { //Ponto no eixo
            if (y > 0){
              if (arrPonto.get(y - 1).x != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                ErroPadrao();
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          }
        }
        if (fechado){
          obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[BPosi.length-1]), obj.arrPonto.get(BPosi[0])));
          obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
          if (PPP != -1){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(PPP));
            obj.arrAresta.get(PPP).e = obj.arrFace.get(FPosi[ij]);
          }
          if (UPP == 0){ //Duvidas sobre esse trecho
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-3));
            obj.arrAresta.get(obj.arrAresta.size()-3).e = obj.arrFace.get(FPosi[ij]);
          }
        }
      }
    }
    obj.CalculaCentro(); //Calcula centrodo objeto para operacoes posteriores
  }
  
  /**
   * Onde a magica acontece (Para x)
   *
   * @param Num Numero de segmentos
   * @param Ang Angulo maximo de rotacao
   */
  public void CriaObjetoX(int Num, double Ang) {
    double teta = (Ang * (Math.PI / 180)) / Num; //Calcula o angulo de distancia para cada passo da revolucao
    double ccos = Math.cos(teta); //Cosseno do angulo
    double csin = Math.sin(teta); //Seno do angulo
    /*System.out.println("Teta2 = " + teta);
    System.out.println("ccos = " + ccos);
    System.out.println("csin = " + csin);*/
    
    //Como as coordenadas estao para x e y, e necessario converte-las
    for (Ponto p : arrPonto) {
      p.z = p.x;
      p.x = p.y;
      p.y = p.z;
      p.z = 0.0;
    }
    
    double xold = arrPonto.get(0).x, zold = arrPonto.get(0).x;
    Ponto plinha = new Ponto();
    int[] BPosi = new int[arrPonto.size()];
    obj = new Objeto();
    int y = 0;
    for (Ponto p : arrPonto) {
      BPosi[y] = y;
      y++;
      obj.arrPonto.add(new Ponto(p)); //Copia todos os pontos iniciais para o objeto
      if (p.x < xold) {
        xold = p.x; //xold carrega y minimo
      }
      if (p.x > zold) {
        zold = p.x; //zold carrega y maximo
      }
    }
    //Constroi as arestas iniciais (primeiro perfil)
    obj.ConstroiArestasMaisFaces(fechado);
    int[] FPosi = new int[obj.arrFace.size()];
    int UPP = -1, PPP = -1;
    for (int i = 0; i < FPosi.length; i++){
      FPosi[i] = i;
    }
    if (arrPonto.get(0).x != 0){
      PPP = 0;
    }
    if (arrPonto.get(arrPonto.size()-1).x != 0){
      UPP = 0;
    }
    //FAZER Definir se objeto e fechado ou nao (dupla face ou unica face)
    obj.Fechado = false;
    
    ///////////////////////////////////////////////////////////////
    if (Ang == 360.0){ //Rotacao completa
      for (int i = 0; i < Num-1; i++){ //Todas as revolucoes menos uma (ja que junta no fim)
        int ij = 0;
        for (y = 0; y < arrPonto.size(); y++){
          plinha = arrPonto.get(y); //plinha recebe cada um dos pontos do perfil
          if (plinha.y != 0 || plinha.z != 0) { //Ponto fora do eixo
            xold = plinha.y;
            plinha.y = (plinha.y * ccos) + (plinha.z * (-csin));
            plinha.z = (xold * csin) + (plinha.z * ccos);
            obj.arrPonto.add(new Ponto(plinha)); //copia plinha
            obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[y]), obj.arrPonto.get(obj.arrPonto.size()-1)));
            BPosi[y] = obj.arrPonto.size()-1;
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
            if ((FPosi.length - 1) > (ij + 1)){
              obj.arrFace.get(FPosi[ij+1]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij+1]);
            }
            if (y == 0){
              PPP = obj.arrAresta.size()-1;
            } else if (y > 0){
              if (arrPonto.get(y - 1).y != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(obj.arrPonto.size() - 2), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y - 1), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          } else { //Ponto no eixo
            if (y > 0){
              if (arrPonto.get(y - 1).y != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                ErroPadrao();
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          }
        }
        if (fechado){
          obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[BPosi.length-1]), obj.arrPonto.get(BPosi[0])));
          obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
          if (PPP != -1){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(PPP));
            obj.arrAresta.get(PPP).e = obj.arrFace.get(FPosi[ij]);
          }
          if (UPP == 0){ //Duvidas sobre esse trecho
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-3));
            obj.arrAresta.get(obj.arrAresta.size()-3).e = obj.arrFace.get(FPosi[ij]);
          }
        }
      }
      //Apos a ultima rotacao
      int ij = 0;
      for (y = 0; y < arrPonto.size(); y++){
        if (arrPonto.get(y).y != 0 || arrPonto.get(y).z != 0) { //Se nao for do eixo
          obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[y]), obj.arrPonto.get(y)));
          if (y > 0){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
            if ((FPosi.length - 1) > (ij + 1)){
              obj.arrFace.get(FPosi[ij+1]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij+1]);
            }
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(ij));
            obj.arrAresta.get(ij).e = obj.arrFace.get(FPosi[ij]);
            ij++;
          } else {
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij]);
          }
        } else { //Ponto no eixo
          if (y > 0){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(ij));
            obj.arrAresta.get(ij).e = obj.arrFace.get(FPosi[ij]);
            ij++;
          }
        }
      }
    } else { //Rotacao nao completa
      for (int i = 0; i < Num; i++){ //Todas as revolucoes
        int ij = 0;
        for (y = 0; y < arrPonto.size(); y++){
          plinha = arrPonto.get(y); //plinha recebe cada um dos pontos do perfil
          if (plinha.y != 0 || plinha.z != 0) { //Ponto fora do eixo
            xold = plinha.y;
            plinha.y = (plinha.y * ccos) + (plinha.z * (-csin));
            plinha.z = (xold * csin) + (plinha.z * ccos);
            obj.arrPonto.add(new Ponto(plinha)); //copia plinha
            obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[y]), obj.arrPonto.get(obj.arrPonto.size()-1)));
            BPosi[y] = obj.arrPonto.size()-1;
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
            if ((FPosi.length - 1) > (ij + 1)){
              obj.arrFace.get(FPosi[ij+1]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij+1]);
            }
            if (y == 0){
              PPP = obj.arrAresta.size()-1;
            } else if (y > 0){
              if (arrPonto.get(y - 1).y != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(obj.arrPonto.size() - 2), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y - 1), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          } else { //Ponto no eixo
            if (y > 0){
              if (arrPonto.get(y - 1).y != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                ErroPadrao();
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          }
        }
        if (fechado){
          obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[BPosi.length-1]), obj.arrPonto.get(BPosi[0])));
          obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
          if (PPP != -1){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(PPP));
            obj.arrAresta.get(PPP).e = obj.arrFace.get(FPosi[ij]);
          }
          if (UPP == 0){ //Duvidas sobre esse trecho
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-3));
            obj.arrAresta.get(obj.arrAresta.size()-3).e = obj.arrFace.get(FPosi[ij]);
          }
        }
      }
    }
    obj.CalculaCentro();
  }
  
  /**
   * Onde a magica acontece (Para z)
   *
   * @param Num Numero de segmentos
   * @param Ang Angulo maximo de rotacao
   */
  public void CriaObjetoZ(int Num, double Ang) {
    double teta = (Ang * (Math.PI / 180)) / Num; //Calcula o angulo de distancia para cada passo da revolucao
    double ccos = Math.cos(teta); //Cosseno do angulo
    double csin = Math.sin(teta); //Seno do angulo
    /*System.out.println("Teta2 = " + teta);
    System.out.println("ccos = " + ccos);
    System.out.println("csin = " + csin);*/
    
    //Como as coordenadas estao para x e y, e necessario converte-las
    for (Ponto p : arrPonto) {
      p.z = p.y;
      p.y = 0.0;
    }
    
    double xold = arrPonto.get(0).z, zold = arrPonto.get(0).z;
    Ponto plinha = new Ponto();
    int[] BPosi = new int[arrPonto.size()];
    obj = new Objeto();
    int y = 0;
    for (Ponto p : arrPonto) {
      BPosi[y] = y;
      y++;
      obj.arrPonto.add(new Ponto(p)); //Copia todos os pontos iniciais para o objeto
      if (p.z < xold) {
        xold = p.z; //xold carrega y minimo
      }
      if (p.z > zold) {
        zold = p.z; //zold carrega y maximo
      }
    }
    //Constroi as arestas iniciais (primeiro perfil)
    obj.ConstroiArestasMaisFaces(fechado);
    int[] FPosi = new int[obj.arrFace.size()];
    int UPP = -1, PPP = -1;
    for (int i = 0; i < FPosi.length; i++){
      FPosi[i] = i;
    }
    if (arrPonto.get(0).x != 0){
      PPP = 0;
    }
    if (arrPonto.get(arrPonto.size()-1).x != 0){
      UPP = 0;
    }
    //FAZER Definir se objeto e fechado ou nao (dupla face ou unica face)
    obj.Fechado = false;
    
    ///////////////////////////////////////////////////////////////
    if (Ang == 360.0){ //Rotacao completa
      for (int i = 0; i < Num-1; i++){ //Todas as revolucoes menos uma (ja que junta no fim)
        int ij = 0;
        for (y = 0; y < arrPonto.size(); y++){
          plinha = arrPonto.get(y); //plinha recebe cada um dos pontos do perfil
          if (plinha.x != 0 || plinha.y != 0) { //Ponto fora do eixo
            xold = plinha.x;
            plinha.x = (plinha.x * ccos) + (plinha.y * (-csin));
            plinha.y = (xold * csin) + (plinha.y * ccos);
            obj.arrPonto.add(new Ponto(plinha)); //copia plinha
            obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[y]), obj.arrPonto.get(obj.arrPonto.size()-1)));
            BPosi[y] = obj.arrPonto.size()-1;
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
            if ((FPosi.length - 1) > (ij + 1)){
              obj.arrFace.get(FPosi[ij+1]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij+1]);
            }
            if (y == 0){
              PPP = obj.arrAresta.size()-1;
            } else if (y > 0){
              if (arrPonto.get(y - 1).x != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(obj.arrPonto.size() - 2), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y - 1), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          } else { //Ponto no eixo
            if (y > 0){
              if (arrPonto.get(y - 1).x != 0 || arrPonto.get(y - 1).y != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                ErroPadrao();
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          }
        }
        if (fechado){
          obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[BPosi.length-1]), obj.arrPonto.get(BPosi[0])));
          obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
          if (PPP != -1){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(PPP));
            obj.arrAresta.get(PPP).e = obj.arrFace.get(FPosi[ij]);
          }
          if (UPP == 0){ //Duvidas sobre esse trecho
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-3));
            obj.arrAresta.get(obj.arrAresta.size()-3).e = obj.arrFace.get(FPosi[ij]);
          }
        }
      }
      //Apos a ultima rotacao
      int ij = 0;
      for (y = 0; y < arrPonto.size(); y++){
        if (arrPonto.get(y).x != 0 || arrPonto.get(y).y != 0) { //Se nao for do eixo
          obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[y]), obj.arrPonto.get(y)));
          if (y > 0){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
            if ((FPosi.length - 1) > (ij + 1)){
              obj.arrFace.get(FPosi[ij+1]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij+1]);
            }
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(ij));
            obj.arrAresta.get(ij).e = obj.arrFace.get(FPosi[ij]);
            ij++;
          } else {
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij]);
          }
        } else { //Ponto no eixo
          if (y > 0){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(ij));
            obj.arrAresta.get(ij).e = obj.arrFace.get(FPosi[ij]);
            ij++;
          }
        }
      }
    } else { //Rotacao nao completa
      for (int i = 0; i < Num; i++){ //Todas as revolucoes
        int ij = 0;
        for (y = 0; y < arrPonto.size(); y++){
          plinha = arrPonto.get(y); //plinha recebe cada um dos pontos do perfil
          if (plinha.x != 0 || plinha.y != 0) { //Ponto fora do eixo
            xold = plinha.x;
            plinha.x = (plinha.x * ccos) + (plinha.y * (-csin));
            plinha.y = (xold * csin) + (plinha.y * ccos);
            obj.arrPonto.add(new Ponto(plinha)); //copia plinha
            obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[y]), obj.arrPonto.get(obj.arrPonto.size()-1)));
            BPosi[y] = obj.arrPonto.size()-1;
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
            obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
            if ((FPosi.length - 1) > (ij + 1)){
              obj.arrFace.get(FPosi[ij+1]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(FPosi[ij+1]);
            }
            if (y == 0){
              PPP = obj.arrAresta.size()-1;
            } else if (y > 0){
              if (arrPonto.get(y - 1).x != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(obj.arrPonto.size() - 2), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y - 1), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          } else { //Ponto no eixo
            if (y > 0){
              if (arrPonto.get(y - 1).x != 0 || arrPonto.get(y - 1).y != 0){ //Ponto anterior tambem fora do eixo
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(y), obj.arrPonto.get(obj.arrPonto.size() - 1)));
              } else {
                ErroPadrao();
              }
              obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
              obj.arrFace.add(new Face(obj.arrAresta.get(obj.arrAresta.size()-1)));
              obj.arrAresta.get(obj.arrAresta.size()-1).d = obj.arrFace.get(obj.arrFace.size()-1);
              obj.arrAresta.get(obj.arrAresta.size()-1).e = obj.arrFace.get(FPosi[ij]);
              FPosi[ij] = obj.arrFace.size()-1;
              ij++;
            }
          }
        }
        if (fechado){
          obj.arrAresta.add(new Aresta(obj.arrPonto.get(BPosi[BPosi.length-1]), obj.arrPonto.get(BPosi[0])));
          obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-1));
          if (PPP != -1){
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(PPP));
            obj.arrAresta.get(PPP).e = obj.arrFace.get(FPosi[ij]);
          }
          if (UPP == 0){ //Duvidas sobre esse trecho
            obj.arrFace.get(FPosi[ij]).fAresta.add(obj.arrAresta.get(obj.arrAresta.size()-3));
            obj.arrAresta.get(obj.arrAresta.size()-3).e = obj.arrFace.get(FPosi[ij]);
          }
        }
      }
    }
    obj.CalculaCentro();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jSeparator1 = new javax.swing.JSeparator();
    jMenuItem6 = new javax.swing.JMenuItem();
    jMenuItem7 = new javax.swing.JMenuItem();
    jTabbedPane3 = new javax.swing.JTabbedPane();
    pnlRev = new javax.swing.JPanel();
    pnlRevI = new javax.swing.JPanel();
    lblInfo = new javax.swing.JLabel();
    txtBaixo = new javax.swing.JTextField();
    lblEixoBaixo = new javax.swing.JLabel();
    lblEixoLado = new javax.swing.JLabel();
    txtLado = new javax.swing.JTextField();
    eixoBaixo = new javax.swing.JLabel();
    eixoLado = new javax.swing.JLabel();
    jTabbedPane1 = new javax.swing.JTabbedPane();
    jPanel2 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    ctrSegmentos = new javax.swing.JSpinner();
    btnRotacionar = new javax.swing.JButton();
    jLabel2 = new javax.swing.JLabel();
    lstEixos = new javax.swing.JComboBox<>();
    jLabel3 = new javax.swing.JLabel();
    ctrAngulo = new javax.swing.JSpinner();
    ckbFechado = new javax.swing.JCheckBox();
    jPanel1 = new javax.swing.JPanel();
    btnFecha = new javax.swing.JButton();
    btnIniciaEixo = new javax.swing.JButton();
    btnFechaEixo = new javax.swing.JButton();
    btnDesfazer = new javax.swing.JButton();
    btnModelos = new javax.swing.JButton();
    jPanel3 = new javax.swing.JPanel();
    btnBezier = new javax.swing.JButton();
    segBezier = new javax.swing.JSpinner();
    jLabel4 = new javax.swing.JLabel();
    jMenuBar1 = new javax.swing.JMenuBar();
    jMenu1 = new javax.swing.JMenu();
    itemNovo = new javax.swing.JMenuItem();
    itemAbrir = new javax.swing.JMenuItem();
    itemSalvar = new javax.swing.JMenuItem();
    jMenu2 = new javax.swing.JMenu();
    itemAjuda = new javax.swing.JMenuItem();
    jMenuItem5 = new javax.swing.JMenuItem();

    jMenuItem6.setText("jMenuItem6");

    jMenuItem7.setText("jMenuItem7");

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Desenhar Perfil");
    addWindowFocusListener(new java.awt.event.WindowFocusListener() {
      public void windowGainedFocus(java.awt.event.WindowEvent evt) {
        formWindowGainedFocus(evt);
      }
      public void windowLostFocus(java.awt.event.WindowEvent evt) {
      }
    });
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosed(java.awt.event.WindowEvent evt) {
        formWindowClosed(evt);
      }
    });

    pnlRev.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Perfil a ser revolucionado"));

    pnlRevI.setMaximumSize(new java.awt.Dimension(400, 300));
    pnlRevI.setPreferredSize(new java.awt.Dimension(400, 300));
    pnlRevI.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        pnlRevIMouseMoved(evt);
      }
    });
    pnlRevI.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseExited(java.awt.event.MouseEvent evt) {
        pnlRevIMouseExited(evt);
      }
      public void mousePressed(java.awt.event.MouseEvent evt) {
        pnlRevIMousePressed(evt);
      }
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        pnlRevIMouseReleased(evt);
      }
    });

    javax.swing.GroupLayout pnlRevILayout = new javax.swing.GroupLayout(pnlRevI);
    pnlRevI.setLayout(pnlRevILayout);
    pnlRevILayout.setHorizontalGroup(
      pnlRevILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 400, Short.MAX_VALUE)
    );
    pnlRevILayout.setVerticalGroup(
      pnlRevILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 300, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout pnlRevLayout = new javax.swing.GroupLayout(pnlRev);
    pnlRev.setLayout(pnlRevLayout);
    pnlRevLayout.setHorizontalGroup(
      pnlRevLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(pnlRevI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    pnlRevLayout.setVerticalGroup(
      pnlRevLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(pnlRevI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    lblInfo.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

    txtBaixo.setToolTipText("Posição do cursor no painel");

    lblEixoBaixo.setText("x");

    lblEixoLado.setText("y");

    txtLado.setToolTipText("Posição do cursor no painel");

    eixoBaixo.setText("x");

    eixoLado.setText("y");

    jLabel1.setText("Segmentos Revolução");

    ctrSegmentos.setModel(new javax.swing.SpinnerNumberModel(10, 3, 100, 1));
    ctrSegmentos.setToolTipText("Segmentos para revolução");

    btnRotacionar.setText("Rotacionar");
    btnRotacionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRotacionarActionPerformed(evt);
      }
    });

    jLabel2.setText("Eixo de rotação");

    lstEixos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "x", "y", "z" }));
    lstEixos.setToolTipText("Eixo no qual a rotação será realizada");
    lstEixos.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        lstEixosItemStateChanged(evt);
      }
    });

    jLabel3.setText("Ângulo");

    ctrAngulo.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(360.0f), Float.valueOf(0.01f), Float.valueOf(360.0f), Float.valueOf(1.0f)));
    ctrAngulo.setToolTipText("Ângulo de rotação em graus");

    ckbFechado.setText("Fechado");
    ckbFechado.setToolTipText("Define se o objeto será fechado ou aberto (ângulo < 360)");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(ctrSegmentos, javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
            .addGap(0, 9, Short.MAX_VALUE)
            .addComponent(btnRotacionar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(lstEixos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(ckbFechado)
              .addComponent(jLabel1)
              .addComponent(jLabel2)
              .addComponent(jLabel3))
            .addGap(0, 0, Short.MAX_VALUE))
          .addComponent(ctrAngulo))
        .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(ctrSegmentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(lstEixos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel3)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(ctrAngulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(ckbFechado)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
        .addComponent(btnRotacionar)
        .addContainerGap())
    );

    jTabbedPane1.addTab("Rotacionar", jPanel2);

    btnFecha.setText("Fechar Forma");
    btnFecha.setToolTipText("Põe uma aresta ligando o último ponto ao ponto inicial");
    btnFecha.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnFechaActionPerformed(evt);
      }
    });

    btnIniciaEixo.setText("Seleciona Eixo");
    btnIniciaEixo.setToolTipText("Permite a seleção de um ponto com altura qualquer junto ao eixo vertical");
    btnIniciaEixo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnIniciaEixoActionPerformed(evt);
      }
    });

    btnFechaEixo.setText("Fecha eixo");
    btnFechaEixo.setToolTipText("Liga o último ponto ao eixo na mesma altura");
    btnFechaEixo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnFechaEixoActionPerformed(evt);
      }
    });

    btnDesfazer.setText("Apaga");
    btnDesfazer.setToolTipText("Apaga o último ponto criado");
    btnDesfazer.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnDesfazerActionPerformed(evt);
      }
    });

    btnModelos.setText("Carregar Modelos");
    btnModelos.setToolTipText("Carrega modelos pré-definidos");
    btnModelos.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnModelosActionPerformed(evt);
      }
    });

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Bezier"));

    btnBezier.setText("Iniciar/Parar");
    btnBezier.setToolTipText("Inicia/Finaliza a construção de uma curva de Bezier");
    btnBezier.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnBezierActionPerformed(evt);
      }
    });

    segBezier.setModel(new javax.swing.SpinnerNumberModel(5, 1, 50, 1));
    segBezier.setToolTipText("Segmentos a seremcalculados para a curva");
    segBezier.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        segBezierStateChanged(evt);
      }
    });

    jLabel4.setText("Segmentos");

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(btnBezier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(segBezier, javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel4)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addComponent(btnBezier)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jLabel4)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(segBezier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(12, 12, 12))
    );

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(btnFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnIniciaEixo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnFechaEixo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnDesfazer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnModelos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(btnFecha)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnIniciaEixo)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnFechaEixo)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnDesfazer)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnModelos)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(18, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab("Perfil", jPanel1);

    jMenu1.setText("Arquivo");

    itemNovo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
    itemNovo.setText("Novo");
    itemNovo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemNovoActionPerformed(evt);
      }
    });
    jMenu1.add(itemNovo);

    itemAbrir.setText("Abrir");
    itemAbrir.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemAbrirActionPerformed(evt);
      }
    });
    jMenu1.add(itemAbrir);

    itemSalvar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
    itemSalvar.setText("Salvar");
    itemSalvar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemSalvarActionPerformed(evt);
      }
    });
    jMenu1.add(itemSalvar);

    jMenuBar1.add(jMenu1);

    jMenu2.setText("Ajuda");

    itemAjuda.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
    itemAjuda.setText("Tópicos de Ajuda");
    itemAjuda.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemAjudaActionPerformed(evt);
      }
    });
    jMenu2.add(itemAjuda);

    jMenuItem5.setText("Opção não selecionável");
    jMenu2.add(jMenuItem5);

    jMenuBar1.add(jMenu2);

    setJMenuBar(jMenuBar1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(eixoLado)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(pnlRev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(eixoBaixo)
                .addGap(0, 0, Short.MAX_VALUE))
              .addComponent(jTabbedPane1))
            .addContainerGap())
          .addGroup(layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(lblEixoBaixo, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtBaixo, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(lblEixoLado, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtLado, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
            .addComponent(pnlRev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
              .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(eixoBaixo)))
          .addComponent(eixoLado))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(txtLado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblEixoLado)
            .addComponent(txtBaixo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblEixoBaixo))
          .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void btnFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechaActionPerformed
    if (arrPonto.size() < 3) {
      JOptionPane.showMessageDialog(this, "Devem existir pelo o menos duas arestas para fechar o objeto", "Erro", JOptionPane.ERROR_MESSAGE);
      iniY = 2047;
    } else {
      if (arrPonto.get(arrPonto.size() - 1).x == 0 && arrPonto.get(0).x == 0) {
        JOptionPane.showMessageDialog(this, "Aresta ilegal. Desfazendo...", "Erro", JOptionPane.ERROR_MESSAGE);
        //arrAresta.add(new Aresta(arrPonto.get(arrPonto.size() - 1), arrPonto.get(0)));
        //btnDesfazerActionPerformed(null);
      } else {
        arrAresta.add(new Aresta(arrPonto.get(arrPonto.size() - 1), arrPonto.get(0)));
        DesenhaPerfil();
        fechado = true;
      }
    }
  }//GEN-LAST:event_btnFechaActionPerformed

  private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    P.setEnabled(true);
    P.requestFocus(); //Traz o foco para tela anterior
    P.PintaTudo();
  }//GEN-LAST:event_formWindowClosed

  private void btnIniciaEixoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciaEixoActionPerformed
    lblInfo.setText("Escolha a altura que o ponto deve ter no eixo usando o painel");
    iniY = 0;
  }//GEN-LAST:event_btnIniciaEixoActionPerformed

  private void btnFechaEixoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechaEixoActionPerformed
    if (arrPonto.size() < 2) {
      JOptionPane.showMessageDialog(this, "Devem existir pelo o menos uma aresta para fechar o objeto", "Erro", JOptionPane.ERROR_MESSAGE);
      iniY = 2047;
    } else {
      arrPonto.add(new Ponto(0.0, arrPonto.get(arrPonto.size() - 1).y, 0.0));
      arrAresta.add(new Aresta(arrPonto.get(arrPonto.size() - 1), arrPonto.get(arrPonto.size() - 2)));
      DesenhaPerfil();
    }
  }//GEN-LAST:event_btnFechaEixoActionPerformed

  private void btnDesfazerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesfazerActionPerformed
    if (isB) {
      return;
    }
    if (arrAresta.size() < 1) {
      if (arrPonto.size() > 0) {
        arrPonto.clear();
      }
      iniY = 2047;
      return;
    }
    Aresta a = arrAresta.get(arrAresta.size() - 1);
    D.setColor(pnlRevI.getBackground());
    D.drawLine((int) a.i.x, (int) a.i.y, (int) a.f.x, (int) a.f.y);
    if (!fechado) {
      arrPonto.remove(arrPonto.size() - 1);
    }
    arrAresta.remove(arrAresta.size() - 1);
    D.setColor(Color.BLACK);
    iniY = 2047;
    fechado = false;
  }//GEN-LAST:event_btnDesfazerActionPerformed

  private void pnlRevIMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlRevIMouseMoved
    DesenhaPerfil();
    int Aux = (evt.getY() * -1) + 300;
    txtBaixo.setText("" + evt.getX());
    txtLado.setText("" + Aux);
  }//GEN-LAST:event_pnlRevIMouseMoved

  private void btnRotacionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRotacionarActionPerformed
    if (arrAresta.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Não há arestas a rotacionar", "Erro", JOptionPane.ERROR_MESSAGE);
      return;
    }
    String input = ctrSegmentos.getValue().toString();
    if (input.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Quantidade de sementos não informada", "Erro", JOptionPane.ERROR_MESSAGE);
      return;
    } else {
      int Num = 6;
      try {
        Num = Integer.parseInt(input);
      } catch (NumberFormatException | NullPointerException e) {
        JOptionPane.showMessageDialog(this, "Valor de segmentos informado não é inteiro - definido como 6", "Não pode ser...", JOptionPane.WARNING_MESSAGE);
        return;
      }
      if (Num < 3) {
        JOptionPane.showMessageDialog(this, "Valor deve ser maior que 3 - definido como 3", "Erro", JOptionPane.ERROR_MESSAGE);
        Num = 3;
      } else if (Num > 100) {
        JOptionPane.showMessageDialog(this, "Valor deve ser menor que 100 - definido como 100", "Erro", JOptionPane.ERROR_MESSAGE);
        Num = 100;
      }
      input = ctrAngulo.getValue().toString();
      double Ang;
      if (input.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ângulo não informado", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      } else {
        try {
          Ang = Double.parseDouble(input);
        } catch (NumberFormatException | NullPointerException e) {
          JOptionPane.showMessageDialog(this, "Valor de ângulo informado não é válido - definido como 360", "Não pode ser...", JOptionPane.WARNING_MESSAGE);
          Ang = 360.0;
        }
        if (Ang < 0.01) {
          JOptionPane.showMessageDialog(this, "Valor deve ser maior que 0.01 - definido como 0.01", "Erro", JOptionPane.ERROR_MESSAGE);
          Ang = 0.01;
        } else if (Ang > 360.0) {
          JOptionPane.showMessageDialog(this, "Valor deve ser menor que 360 - definido como o complemento", "Erro", JOptionPane.ERROR_MESSAGE);
          Ang = Ang % 360;
        }
      }
      cabecalho = 0;
      if (Ang != 360.0) { //Revolucao nao completa
        cabecalho = 1;
        if (ckbFechado.isSelected()) { //Se for fechado
          cabecalho += 2;
        }
      }
      //Ang = Ang * (Math.PI / 180); //Para pi rad

      //obj.CalculaCentro();
      if ("x".equals(lstEixos.getSelectedItem().toString())) { //Rotacao em x
        CriaObjetoX(Num, Ang); //Faz rotacoes
      } else if ("y".equals(lstEixos.getSelectedItem().toString())) {
        CriaObjetoY(Num, Ang); //Faz rotacoes
      } else if ("z".equals(lstEixos.getSelectedItem().toString())) {
        CriaObjetoZ(Num, Ang); //Faz rotacoes
      } else {
        ErroPadrao();
      }
    }
    obj.CalculaCentro();
    P.Obj.add(obj);
    P.ObSel = P.Obj.size() - 1;
    this.dispose();
    P.setEnabled(true);
    P.requestFocus(); //Traz o foco para tela anterior
    P.PintaTudo();
  }//GEN-LAST:event_btnRotacionarActionPerformed

  private void itemAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAjudaActionPerformed
    this.setEnabled(false);
    new Ajuda(this, 2).setVisible(true);
  }//GEN-LAST:event_itemAjudaActionPerformed

  private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
    DesenhaPerfil();
  }//GEN-LAST:event_formWindowGainedFocus

  private void pnlRevIMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlRevIMouseReleased
    if (!fechado) {
      if (isB) {
        if (Bcount < 3) {
          //System.out.println("Pontos " + Bcount);
          Ponto p = new Ponto();
          p.x = evt.getX() & iniY; //*0.25 0~100
          iniY = 1023;
          p.y = evt.getY(); //*0.25 0~75
          BarrPontoC.add(p);
          Bcount++;
          if (Bcount == 3) {
            CalculaBezier();
          }
        } else { //Ja tem todos os pontos
          //System.out.println("Pontos " + Bcount);
          Ponto p = new Ponto();
          p.x = evt.getX(); //*0.25 0~100
          p.y = evt.getY(); //*0.25 0~75
          if (Bsp >= 0 && p.x > 0 && p.x < 400 && p.y > 0 && p.y < 300) { //Mover algum ponto
            BarrPontoC.get(Bsp).x = p.x;
            BarrPontoC.get(Bsp).y = p.y;
            CalculaBezier();
          }
          Slc = new Ponto();
        }
      } else {
        Ponto p = new Ponto();
        p.x = evt.getX() & iniY; //*0.25 0~100
        iniY = 1023;
        p.y = evt.getY(); //*0.25 0~75
        //System.out.println("X = " + p.x + " - Y = " + p.y);
        arrPonto.add(p);
        if (arrPonto.size() > 1) {
          if (arrPonto.get(arrPonto.size() - 1).x == 0 && arrPonto.get(arrPonto.size() - 2).x == 0) {
            JOptionPane.showMessageDialog(this, "Aresta ilegal. Desfazendo...", "Erro", JOptionPane.ERROR_MESSAGE);
            arrAresta.add(new Aresta(arrPonto.get(arrPonto.size() - 1), arrPonto.get(arrPonto.size() - 2)));
            btnDesfazerActionPerformed(null);
          } else {
            arrAresta.add(new Aresta(arrPonto.get(arrPonto.size() - 2), arrPonto.get(arrPonto.size() - 1)));
            DesenhaPerfil();
          }
        }
        lblInfo.setText("");
        issaved = false;
      }
    }
    ChecaQuantidade();
  }//GEN-LAST:event_pnlRevIMouseReleased

  private void lstEixosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lstEixosItemStateChanged
    if (lstEixos.getSelectedItem() == "x") {
      eixoLado.setText("x");
      eixoBaixo.setText("y");
      lblEixoBaixo.setText("y");
      lblEixoLado.setText("x");
    } else if (lstEixos.getSelectedItem() == "y") {
      eixoLado.setText("y");
      eixoBaixo.setText("x");
      lblEixoBaixo.setText("x");
      lblEixoLado.setText("y");
    } else if (lstEixos.getSelectedItem() == "z") {
      eixoLado.setText("z");
      eixoBaixo.setText("x");
      lblEixoBaixo.setText("x");
      lblEixoLado.setText("z");
    } else {
      ErroPadrao();
    }
    //System.out.println("I'm being called twice");
  }//GEN-LAST:event_lstEixosItemStateChanged

  private void pnlRevIMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlRevIMouseExited
    txtBaixo.setText("Fora");
    txtLado.setText("Fora");
  }//GEN-LAST:event_pnlRevIMouseExited

    private void btnModelosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModelosActionPerformed
      this.setEnabled(false);
      new Modelos(this).setVisible(true);
    }//GEN-LAST:event_btnModelosActionPerformed

  private void itemSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSalvarActionPerformed
    fc = new JFileChooser();
    int returnVal = fc.showSaveDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      String s = file.toString() + ".acr"; //Arquivo CGRev (Perfil, cena)
      //System.out.println("Saida = " + s);
      cabecalho = (byte) (VERSAO_PERFIL << 4);
      //System.out.println("i = " + arrAresta.get(arrAresta.size()-1).i.toString() + "f = " + arrAresta.get(arrAresta.size()-1).f.toString());
      if (arrAresta.get(0).i.equals(arrAresta.get(arrAresta.size() - 1).f)) { //Fechado
        cabecalho += 8;
      }
      //System.out.println("Versao = " + cabecalho);
      try {
        try (DataOutputStream said = new DataOutputStream(new FileOutputStream(s))) {
          said.writeByte(cabecalho);
          for (Ponto p : arrPonto) {
            said.writeDouble(p.x);
            said.writeDouble(p.y);
          }
        }
      } catch (FileNotFoundException ex) {
        JOptionPane.showMessageDialog(this, "Nao foi possivel encontrar o arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Nao foi possivel escrever o arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      }
      issaved = true;
    }
  }//GEN-LAST:event_itemSalvarActionPerformed

  private void itemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAbrirActionPerformed
    //Metodo e compativelcom versoes 0 e 1
    fc = new JFileChooser();
    int returnVal = fc.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      String s = file.toString();
      if (!file.canRead()) {
        JOptionPane.showMessageDialog(this, "Nao foi possivel ler o arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      }
      try {
        DataInputStream entr = new DataInputStream(new FileInputStream(s));
        cabecalho = entr.readByte();
        LimpaTudo();
        while (entr.available() > 0) { //Le
          arrPonto.add(new Ponto(entr.readDouble(), entr.readDouble(), 0.0));
        }
        ConstroiArestas();
        DesenhaPerfil();
      } catch (FileNotFoundException ex) {
        JOptionPane.showMessageDialog(this, "Nao foi possivel ler o arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Erro generico de leitura", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      }
      issaved = true;
    }
    DesenhaPerfil();
    iniY = 2047;
  }//GEN-LAST:event_itemAbrirActionPerformed

  private void itemNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemNovoActionPerformed
    if (!issaved) {
      int result = JOptionPane.showConfirmDialog(this, "Há arestas não salvas, deseja salvá-las?");
      if (result == JOptionPane.NO_OPTION) {
        LimpaTudo();
      } else if (result == JOptionPane.YES_OPTION) {
        itemSalvarActionPerformed(evt);
        LimpaTudo();
      }
    } else {
      LimpaTudo();
      issaved = true;
    }
  }//GEN-LAST:event_itemNovoActionPerformed

  /**
   * Variaveis so de Bezier
   */
  boolean isB = false;

  private void btnBezierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBezierActionPerformed
    if (isB) {
      if (Bcount != 3) {
        return;
      } else {
        isB = false;
        BarrPonto.remove(0);
        arrPonto.addAll(BarrPonto);
        Bcount = 0;
        BarrPonto.clear();
        BarrPontoC.clear();
        ConstroiArestas();
      }
    } else {
      Bcount = 0;
      if (arrPonto.isEmpty()) { //No elements
        JOptionPane.showMessageDialog(this, "Deve haver pelo o menos um ponto já na área de desenho para se iniciar uma curva de bezier", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      } else {
        isB = true;
        BarrPonto.add(arrPonto.get(arrPonto.size() - 1));
        BarrPontoC.add(arrPonto.get(arrPonto.size() - 1));
      }
    }
  }//GEN-LAST:event_btnBezierActionPerformed

  private void segBezierStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_segBezierStateChanged
    if (isB) {
      String input = segBezier.getValue().toString();
      Bseg = 5;
      if (input.isEmpty()) {
        segBezier.setValue(5);
      } else {
        try {
          Bseg = Integer.parseInt(input);
          CalculaBezier();
        } catch (NumberFormatException | NullPointerException e) {
          segBezier.setValue(5);
        }
      }
      DesenhaPerfil();
    }
  }//GEN-LAST:event_segBezierStateChanged

  private void pnlRevIMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlRevIMousePressed
    if (isB) {
      if (Bcount == 3) {
        Ponto p = new Ponto();
        p.x = evt.getX(); //*0.25 0~100
        iniY = 1023;
        p.y = evt.getY(); //*0.25 0~75
        double dist = Math.sqrt((p.x - BarrPontoC.get(0).x) * (p.x - BarrPontoC.get(0).x) + (p.y - BarrPontoC.get(0).y) * (p.y - BarrPontoC.get(0).y));
        double d2;
        Bsp = 0;
        for (int i = 1; i < BarrPontoC.size(); i++) {
          d2 = Math.sqrt((p.x - BarrPontoC.get(i).x) * (p.x - BarrPontoC.get(i).x) + (p.y - BarrPontoC.get(i).y) * (p.y - BarrPontoC.get(i).y));
          if (d2 < dist) {
            dist = d2;
            Bsp = (byte) i;
          }
        }
        if (dist > 10) { //No raio de ninguem
          Bsp = -1;
        }
        //System.out.println("Bsp = " + Bsp);
      }
    }
  }//GEN-LAST:event_pnlRevIMousePressed

  /**
   * Verifica a quantidade de pontos ja adicionados ao painel
   *  - Nao pode ser superior a 250 ja que eu uso um byte pra identificar quantos tem no arquivo
   */
  public void ChecaQuantidade(){
    if (arrPonto.size() > 250){
      JOptionPane.showMessageDialog(this, "Eu acho que voce ja adicionou pontos demais. Tá procurando algum bug!? Eu vou limpar tudo pra voce", "Erro", JOptionPane.WARNING_MESSAGE);
      LimpaTudo();
    }
  }
  
  public void ErrosIniciais() {
    if (EI == -1) {
      JOptionPane.showMessageDialog(this, "Algo esta impedindo a execucao deste programa, consulte o log de saida para mais informacoes", "Erro", JOptionPane.ERROR_MESSAGE);
    } else if (EI == 0) {
      return; //Ready to go
    } else if (EI == 1) {
      JOptionPane.showMessageDialog(this, "Classe faltante, consulte o log de saida para mais informacoes", "Erro", JOptionPane.ERROR_MESSAGE);
    } else if (EI == 2) {
      JOptionPane.showMessageDialog(this, "Erro de instanciacao, consulte o log de saida para mais informacoes", "Erro", JOptionPane.ERROR_MESSAGE);
    } else if (EI == 3) {
      JOptionPane.showMessageDialog(this, "Acesso Ilegal, consulte o log de saida para mais informacoes", "Erro", JOptionPane.ERROR_MESSAGE);
    } else if (EI == 4) {
      JOptionPane.showMessageDialog(this, "Aparencia do programa com problemas (Apenas windows), consulte o log de saida para mais informacoes", "Erro", JOptionPane.ERROR_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(this, "Algo esta impedindo a execucao deste programa, consulte o log de saida para mais informacoes", "Erro", JOptionPane.ERROR_MESSAGE);
    }
    System.exit(-1);
  }

  public static byte EI = 0;

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Windows look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */

    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Windows".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;

        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(Perfil.class
              .getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EI = 1;

    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(Perfil.class
              .getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EI = 2;

    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(Perfil.class
              .getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EI = 3;

    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(Perfil.class
              .getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EI = 4;
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new Perfil().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnBezier;
  private javax.swing.JButton btnDesfazer;
  private javax.swing.JButton btnFecha;
  private javax.swing.JButton btnFechaEixo;
  private javax.swing.JButton btnIniciaEixo;
  private javax.swing.JButton btnModelos;
  private javax.swing.JButton btnRotacionar;
  private javax.swing.JCheckBox ckbFechado;
  public javax.swing.JSpinner ctrAngulo;
  private javax.swing.JSpinner ctrSegmentos;
  private javax.swing.JLabel eixoBaixo;
  private javax.swing.JLabel eixoLado;
  private javax.swing.JMenuItem itemAbrir;
  private javax.swing.JMenuItem itemAjuda;
  private javax.swing.JMenuItem itemNovo;
  private javax.swing.JMenuItem itemSalvar;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JMenu jMenu1;
  private javax.swing.JMenu jMenu2;
  private javax.swing.JMenuBar jMenuBar1;
  private javax.swing.JMenuItem jMenuItem5;
  private javax.swing.JMenuItem jMenuItem6;
  private javax.swing.JMenuItem jMenuItem7;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JTabbedPane jTabbedPane3;
  private javax.swing.JLabel lblEixoBaixo;
  private javax.swing.JLabel lblEixoLado;
  private javax.swing.JLabel lblInfo;
  private javax.swing.JComboBox<String> lstEixos;
  private javax.swing.JPanel pnlRev;
  private javax.swing.JPanel pnlRevI;
  private javax.swing.JSpinner segBezier;
  private javax.swing.JTextField txtBaixo;
  private javax.swing.JTextField txtLado;
  // End of variables declaration//GEN-END:variables
}
