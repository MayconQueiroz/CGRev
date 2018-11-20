package revcg;

import Objetos.Objeto;

/**
 * Metodos isolados por darem problema ou terem sido substituidos por metodos mais eficientes
 * @author Maycon
 */
public class MetodosIsolados {
  
  /**
   * Novo metodo de rotacao (nao concluido, requeria muitas variaveis)
   */
  /**
   * Onde a magica acontece (Para y)
   *
   * @param Num Numero de segmentos
   * @param Ang Angulo maximo de rotacao
   */
  /*public void CriaObjetoY(int Num, double Ang) {
    double teta = Ang / Num;
    double ccos = Math.cos(teta);
    double csin = Math.sin(teta);
    double xold = arrPonto.get(0).y, zold = arrPonto.get(0).y;
    Ponto plinha = new Ponto();
    BarrPonto.clear();
    obj = new Objeto();
    for (Ponto p : arrPonto) {
      obj.arrPonto.add(new Ponto(p));
      if (p.y < xold) {
        xold = p.y;
      }
      if (p.y > zold) {
        zold = p.y;
      }
    }
    obj.ConstroiArestas(fechado);
    //FAZER Construir os parametros C depois da revolucao do objeto
    obj.C.x = 0;
    obj.C.y = (xold + zold) / 2;
    obj.C.z = 0;
    obj.Fechado = false;
    ///////////////////////////////////////////////////////////////
    for (Ponto u : obj.arrPonto){ //Copia todos os pontos para BarrPonto
      BarrPonto.add(new Ponto(u));
    }
    if (Ang == 360){ //Rotacao completa
      for (int i = 0; i < Num-1; i++){ //Todas as revolucoes menos uma (ja que junta no fim)
        for (int y = 0; y < arrPonto.size(); y++){
          plinha = arrPonto.get(y);
          if (plinha.x != 0 || plinha.z != 0) { //Ponto fora do eixo
            plinha.x = (plinha.x * ccos) + (plinha.z * csin); //Rotacionando ponto
            plinha.z = (xold * (-csin)) + (plinha.z * ccos);
            BarrPontoC.set(y, BarrPonto.get(y));
            BarrPonto.set(y, new Ponto(plinha));
            obj.arrPonto.add(BarrPonto.get(y)); //copia plinha
            //arrPonto.get(y).x = plinha.x;
            //arrPonto.get(y).z = plinha.z; //Salvando novo ponto
            if (y > 0){
              if (arrPonto.get(y - 1).x != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
               obj.arrAresta.add(new Aresta(obj.arrPonto.get(obj.arrPonto.size() - 1), obj.arrPonto.get(obj.arrPonto.size() - 2)));
              } else {
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(obj.arrPonto.size() - 1), obj.arrPonto.get(y - 1)));
              }
            }
          } else { //Ponto no eixo
            if (y > 0){
              if (arrPonto.get(y - 1).x != 0 || arrPonto.get(y - 1).z != 0){ //Ponto anterior tambem fora do eixo
               obj.arrAresta.add(new Aresta(obj.arrPonto.get(obj.arrPonto.size() - 1), obj.arrPonto.get(obj.arrPonto.size() - 2)));
              } else {
                obj.arrAresta.add(new Aresta(obj.arrPonto.get(obj.arrPonto.size() - 1), obj.arrPonto.get(y - 1)));
              }
            }
          }
          
        }
      }
    } else { //Rotacao nao completa
      
    }
  }*/
  
  /**
   * Substituido pelo novo
   */
  /**
   * Antigo metodo de rotacao.
   * Readaptado ja que novo nao conseguiu ser concluido
   * Varios System.out.println comentados (debug efect)
   * @param Num Quantidade de rotacoes
   * @param op Operacao
   */
  /*public void CriaObjeto(int Num, int op) {
    double teta = 6.283185 / Num;
    System.out.println("Teta2 = " + teta);
    double ccos = Math.cos(teta);
    double csin = Math.sin(teta);
    System.out.println("ccos = " + ccos);
    System.out.println("csin = " + csin);
    double xold, zold;
    Ponto plinha = new Ponto();
    ArrayList<Ponto> arrPontoNil = new ArrayList<>();
    Aresta arAux;
    //System.out.println("Dentro do codigo CriaObjeto");
    obj = new Objeto();
    //PrintPontos();
    if (op == 0) { //Inicia e encerra no eixo - "fechado"
      //System.out.println("Fechado OP0");
      //System.out.println("002 - Num = " + Num);
      obj.Fechado = true;
      for (Aresta a : arrAresta) {
        obj.arrAresta.add(new Aresta(a));
        //System.out.println("ADD = " + a.toString());
      }
      for (Ponto p : arrPonto) {
        arrPontoNil.add(new Ponto(p));
      }
      //PrintListaArestas();PrintPontos();
      for (int i = 0; i < Num - 1; i++) { //Para todos os passos da revlucao -1
        //System.out.println("()()()()()()()()()()()()()()()()()");
        for (int y = 1; y < arrPonto.size(); y++) {
          plinha = arrPonto.get(y);
          //System.out.println("Plinha = " + plinha.toString());
          //System.out.println("+.+.+.+.+");
          //PrintListaArestas();
          //PrintListaArestasIn();
          if (plinha.x != 0 || plinha.z != 0) { //So rotaciona se o ponto for fora do eixo, senao vai usar o mesmo
            //System.out.println("Previously on x = " + p.x + " - z = " + p.z);
            //System.out.println("ccos = " + ccos + " - csin = " + csin);
            xold = plinha.x;
            zold = plinha.z;
            System.out.println("Bef arrPonto.get(y)0 = x = " + arrPonto.get(y).x + " z = " + arrPonto.get(y).z);
            plinha.x = (plinha.x * ccos) + (plinha.z * csin);
            plinha.z = (xold * (-csin)) + (plinha.z * ccos);
            System.out.println("    arrPonto.get(y)0 = x = " + arrPonto.get(y).x + " z = " + arrPonto.get(y).z);
            arrPontoNil.add(new Ponto(plinha));
            //arrPonto.get(y).x = plinha.x;
            //arrPonto.get(y).z = plinha.z;
            if (arrPonto.get(y - 1).x == 0) { //Se o ponto anterior for do eixo
              obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(y - 1)));
              //obj.arrAresta.add(new Aresta(arrAresta.get(y-1)));
            } else {
              obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(arrPontoNil.size() - 2)));
              //obj.arrAresta.add(new Aresta(arrAresta.get(y-1)));
            }
            obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), new Ponto(xold, arrPonto.get(y).y, zold)));
            //System.out.println("x = " + p.x + " - z = " + p.z);
            //System.out.println("....");
            //PrintPontos();
          } else {
            arAux = new Aresta(arrPontoNil.get(y), arrPontoNil.get(arrPontoNil.size() - 1));
            obj.arrAresta.add(new Aresta(arAux));
          }
        }
        //System.out.println("Sai para construir arestas, ja volto");
        //ConstroiArestas(); //Constroi arestas baseadas nos pontos
        //PrintListaArestasIn();
        //PrintListaArestas();
      }
      for (int u = 0; u < arrPonto.size(); u++) {
        if (arrPonto.get(u).x != 0 || arrPonto.get(u).z != 0) {
          obj.arrAresta.add(new Aresta(arrPonto.get(u), arrPontoNil.get(u)));
        }
      }
      //PrintListaArestas();
    } else if (op == 1) { //Inicia e encerra no mesmo ponto - fechado
      //System.out.println("Fechado OP1");
      ////////////////////////////////////////////////////////////////////////
      ////////////////////////////////////////////////////////////////////////
      ////////////////////////////////////////////////////////////////////////
      obj.Fechado = true;
      for (Aresta a : arrAresta) {
        obj.arrAresta.add(new Aresta(a));
        //System.out.println("ADD = " + a.toString());
      }
      for (Ponto p : arrPonto) {
        arrPontoNil.add(new Ponto(p));
      }
      //PrintListaArestas();PrintPontos();
      //System.out.println("Num = " + Num);
      for (int i = 0; i < Num - 1; i++) { //Para todos os passos da revlucao -1
        //System.out.println("()()()()()()()()()()()()()()()()()");
        for (int y = 0; y < arrPonto.size(); y++) {
          plinha = arrPonto.get(y);
          //System.out.println("Plinha = " + plinha.toString());
          //System.out.println("+.+.+.+.+ y = " + y);
          //PrintListaArestas();
          //PrintListaArestasIn();
          if (plinha.x != 0 || plinha.z != 0) { //So rotaciona se o ponto for fora do eixo, senao vai usar o mesmo
            //System.out.println("Previously on x = " + p.x + " - z = " + p.z);
            //System.out.println("ccos = " + ccos + " - csin = " + csin);
            xold = plinha.x;
            zold = plinha.z;
            plinha.x = (plinha.x * ccos) + (plinha.z * csin);
            plinha.z = (xold * (-csin)) + (plinha.z * ccos);
            arrPontoNil.add(new Ponto(plinha));
            arrPonto.get(y).x = plinha.x;
            arrPonto.get(y).z = plinha.z;
            if (y != 0) { //Para nao calcular pro primeiro
              if (arrPonto.get(y - 1).x == 0) { //Se o ponto anterior for do eixo
                obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(y - 1)));
                //obj.arrAresta.add(new Aresta(arrAresta.get(y-1)));
              } else {
                obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(arrPontoNil.size() - 2)));
                //obj.arrAresta.add(new Aresta(arrAresta.get(y-1)));
              }
            }
            obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), new Ponto(xold, arrPonto.get(y).y, zold)));
            //System.out.println("x = " + p.x + " - z = " + p.z);
            //System.out.println("....");
            //PrintPontos();
          } else {
            arAux = new Aresta(arrPontoNil.get(y), arrPontoNil.get(arrPontoNil.size() - 1));
            obj.arrAresta.add(new Aresta(arAux));
          }
        }
        obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), new Ponto(arrPonto.get(0))));
        //System.out.println("SaÃ­ para construir arestas, jÃ¡ volto");
        //ConstroiArestas(); //Constroi arestas baseadas nos pontos
        //PrintListaArestasIn();
        //--PrintListaArestas();
      }
      for (int u = 0; u < arrPonto.size(); u++) {
        if (arrPonto.get(u).x != 0 || arrPonto.get(u).z != 0) {
          obj.arrAresta.add(new Aresta(arrPonto.get(u), arrPontoNil.get(u)));
        }
      }
      //PrintListaArestas();
    } else if (op == 2) { //Encerra em ponto diferente que inicia - aberto
      System.out.println("Aberto OP2");
      ////////////////////////////////////////////////////////////////////////
      ////////////////////////////////////////////////////////////////////////
      ////////////////////////////////////////////////////////////////////////
      obj.Fechado = false;
      for (Aresta a : arrAresta) {
        obj.arrAresta.add(new Aresta(a));
        //System.out.println("ADD = " + a.toString());
      }
      for (Ponto p : arrPonto) {
        arrPontoNil.add(new Ponto(p));
      }
      //PrintListaArestas();//PrintPontos();
      for (int i = 0; i < Num - 1; i++) { //Para todos os passos da revlucao -1
        //System.out.println("()()()()()()()()()()()()()()()()()");
        for (int y = 0; y < arrPonto.size(); y++) {
          plinha = arrPonto.get(y);
          //System.out.println("Plinha = " + plinha.toString());
          //System.out.println("+.+.+.+.+");
          //PrintListaArestas();
          //System.out.println("=*=*=*=*=");
          //PrintListaArestasIn();
          //System.out.println("Previously on x = " + p.x + " - z = " + p.z);
          //System.out.println("ccos = " + ccos + " - csin = " + csin);
          xold = plinha.x;
          zold = plinha.z;
          plinha.x = (plinha.x * ccos) + (plinha.z * csin);
          plinha.z = (xold * (-csin)) + (plinha.z * ccos);
          arrPontoNil.add(new Ponto(plinha));
          arrPonto.get(y).x = plinha.x;
          arrPonto.get(y).z = plinha.z;
          if (y != 0) { //Para nao calcular pro primeiro
            if (arrPonto.get(y - 1).x == 0) { //Se o ponto anterior for do eixo
              obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(y - 1)));
              //obj.arrAresta.add(new Aresta(arrAresta.get(y-1)));
            } else {
              obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(arrPontoNil.size() - 2)));
              //obj.arrAresta.add(new Aresta(arrAresta.get(y-1)));
            }
          }
          //System.out.println("P98 = " + new Ponto(xold, arrPonto.get(y).y, zold).toString());
          //System.out.println("Novo= " + plinha);
          obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), new Ponto(xold, arrPonto.get(y).y, zold)));
          //System.out.println("x = " + p.x + " - z = " + p.z);
          //System.out.println("....");
          //PrintPontos();
          //PrintListaArestas();
        }
        //System.out.println("SaÃ­ para construir arestas, jÃ¡ volto");
        //ConstroiArestas(); //Constroi arestas baseadas nos pontos
        //PrintListaArestasIn();
        //PrintListaArestas();
      }
      for (int u = 0; u < arrPonto.size(); u++) {
        if (arrPonto.get(u).x != 0 || arrPonto.get(u).z != 0) {
          obj.arrAresta.add(new Aresta(arrPonto.get(u), arrPontoNil.get(u)));
        }
      }
      //PrintListaArestas();
    } else {
      ErroPadrao();
    }
    obj.arrPonto = arrPontoNil;
  }
  */
  
  /**
   * Substituido pelo novo metodo
   */
  /**
   * Antigo metodo de rotacao.
   * Readaptado ja que novo nao conseguiu ser concluido
   * Nao efetua rotacao completa
   * Varios System.out.println comentados (debug efect)
   * @param Num Quantidade de rotacoes
   * @param op Operacao
   * @param Ang Angulo maximo de rotacao (nesse caso, menor que 360)
   */
  /*public void CriaObjetoN(int Num, int op, double Ang) {
    double teta = Ang / Num;
    System.out.println("Teta = " + teta);
    double ccos = Math.cos(teta);
    double csin = Math.sin(teta);
    double xold, zold;
    Ponto plinha = new Ponto();
    ArrayList<Ponto> arrPontoNil = new ArrayList<>();
    Aresta arAux;
    obj = new Objeto();
    //PrintPontos();
    if (op == 0) { //Inicia e encerra no eixo - "fechado"
      obj.Fechado = true;
      for (Aresta a : arrAresta) {
        obj.arrAresta.add(new Aresta(a));
      }
      for (Ponto p : arrPonto) {
        arrPontoNil.add(new Ponto(p));
      }
      for (int i = 0; i < Num; i++) { //Para todos os passos da revlucao
        for (int y = 1; y < arrPonto.size(); y++) {
          plinha = arrPonto.get(y);
          if (plinha.x != 0 || plinha.z != 0) { //So rotaciona se o ponto for fora do eixo, senao vai usar o mesmo
            xold = plinha.x;
            zold = plinha.z;
            plinha.x = (plinha.x * ccos) + (plinha.z * csin);
            plinha.z = (xold * (-csin)) + (plinha.z * ccos);
            System.out.println("arrPonto.get(y)0 = x = " + arrPonto.get(y).x + " y = " + arrPonto.get(y).y);
            arrPontoNil.add(new Ponto(plinha));
            if (arrPonto.get(y - 1).x == 0) { //Se o ponto anterior for do eixo
              obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(y - 1)));
            } else {
              obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(arrPontoNil.size() - 2)));
            }
            obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), new Ponto(xold, arrPonto.get(y).y, zold)));
          } else {
            arAux = new Aresta(arrPontoNil.get(y), arrPontoNil.get(arrPontoNil.size() - 1));
            obj.arrAresta.add(new Aresta(arAux));
            System.out.println("Else");
          }
        }
      }
      //Apos todas as revolucoes
      //for (int u = 0; u < arrPonto.size(); u++) {
      //  if (arrPonto.get(u).x != 0 || arrPonto.get(u).z != 0) {
      //    obj.arrAresta.add(new Aresta(arrPonto.get(u), arrPontoNil.get(u)));
      //  }
      //}
    } else if (op == 1) { //Inicia e encerra no mesmo ponto - fechado
      obj.Fechado = true;
      for (Aresta a : arrAresta) {
        obj.arrAresta.add(new Aresta(a));
      }
      for (Ponto p : arrPonto) {
        arrPontoNil.add(new Ponto(p));
      }
      for (int i = 0; i < Num; i++) { //Para todos os passos da revlucao
        for (int y = 0; y < arrPonto.size(); y++) {
          plinha = arrPonto.get(y);
          if (plinha.x != 0 || plinha.z != 0) { //So rotaciona se o ponto for fora do eixo, senao vai usar o mesmo
            xold = plinha.x;
            zold = plinha.z;
            plinha.x = (plinha.x * ccos) + (plinha.z * csin);
            plinha.z = (xold * (-csin)) + (plinha.z * ccos);
            System.out.println("arrPonto.get(y)1 = x = " + arrPonto.get(y).x + " y = " + arrPonto.get(y).y);
            arrPontoNil.add(new Ponto(plinha));
            arrPonto.get(y).x = plinha.x;
            arrPonto.get(y).z = plinha.z;
            if (y != 0) { //Para nao calcular pro primeiro
              if (arrPonto.get(y - 1).x == 0) { //Se o ponto anterior for do eixo
                obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(y - 1)));
              } else {
                obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(arrPontoNil.size() - 2)));
              }
            }
            obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), new Ponto(xold, arrPonto.get(y).y, zold)));
          } else {
            arAux = new Aresta(arrPontoNil.get(y), arrPontoNil.get(arrPontoNil.size() - 1));
            obj.arrAresta.add(new Aresta(arAux));
            System.out.println("Else");
          }
        }
        obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), new Ponto(arrPonto.get(0))));
      }
      //for (int u = 0; u < arrPonto.size(); u++) {
      //  if (arrPonto.get(u).x != 0 || arrPonto.get(u).z != 0) {
      //    obj.arrAresta.add(new Aresta(arrPonto.get(u), arrPontoNil.get(u)));
      //  }
      //}
    } else if (op == 2) { //Encerra em ponto diferente que inicia - aberto
      obj.Fechado = false;
      for (Aresta a : arrAresta) {
        obj.arrAresta.add(new Aresta(a));
      }
      for (Ponto p : arrPonto) {
        arrPontoNil.add(new Ponto(p));
      }
      for (int i = 0; i < Num; i++) { //Para todos os passos da revlucao
        for (int y = 0; y < arrPonto.size(); y++) {
          plinha = arrPonto.get(y);
          xold = plinha.x;
          zold = plinha.z;
          plinha.x = (plinha.x * ccos) + (plinha.z * csin);
          plinha.z = (xold * (-csin)) + (plinha.z * ccos);
          System.out.println("arrPonto.get(y)2 = x = " + arrPonto.get(y).x + " y = " + arrPonto.get(y).y);
          arrPontoNil.add(new Ponto(plinha));
          arrPonto.get(y).x = plinha.x;
          arrPonto.get(y).z = plinha.z;
          if (y != 0) { //Para nao calcular pro primeiro
            if (arrPonto.get(y - 1).x == 0) { //Se o ponto anterior for do eixo
              obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(y - 1)));
            } else {
              obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), arrPontoNil.get(arrPontoNil.size() - 2)));
            }
          }
          obj.arrAresta.add(new Aresta(arrPontoNil.get(arrPontoNil.size() - 1), new Ponto(xold, arrPonto.get(y).y, zold)));
        }
      }
      //for (int u = 0; u < arrPonto.size(); u++) {
      //  if (arrPonto.get(u).x != 0 || arrPonto.get(u).z != 0) {
      //    obj.arrAresta.add(new Aresta(arrPonto.get(u), arrPontoNil.get(u)));
      //  }
      //}
    } else {
      ErroPadrao();
    }
    obj.arrPonto = arrPontoNil;
  }
  */
  
  
  ///////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////OLDPINTA////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Simplificacao adotada: escala(coordenada-centro)+centro Baseada na
   * distributiva de: (Escala*coordenada)+((1-Escala)*Centro)
   */
  /**
   * Pinta o painel Lado
   */
  /*public void oldPintaLado() {
    DL.clearRect(0, 0, 340, 255);
    for (int i = 0; i < Obj.size(); i++) {
      o = Obj.get(i);
      DL.setColor(i == ObSel ? Sel : Color.BLACK);
      for (Aresta a : o.arrAresta) {
        DL.drawLine((int) (EL * ((o.arrPonto.get(a.i).z) - o.C.z) + o.C.z) + mx, (int) (EL * ((o.arrPonto.get(a.i).y) - o.C.y) + o.C.y), (int) (EL * ((o.arrPonto.get(a.f).z) - o.C.z) + o.C.z) + mx, (int) (EL * ((o.arrPonto.get(a.f).y) - o.C.y) + o.C.y));
      }
    }
  }*/
  
  /**
   * Pinta o painel Topo
   */
  /*public void oldPintaTopo() {
    DT.clearRect(0, 0, 340, 255);
    for (int i = 0; i < Obj.size(); i++) {
      DT.setColor(i == ObSel ? Sel : Color.BLACK);
      o = Obj.get(i);
      for (Aresta a : o.arrAresta) {
        DT.drawLine((int) (ET * ((o.arrPonto.get(a.i).x) - o.C.x) + o.C.x) + mx, (int) (ET * ((o.arrPonto.get(a.i).z) - o.C.z) + o.C.z) + my, (int) (ET * ((o.arrPonto.get(a.f).x) - o.C.x) + o.C.x) + mx, (int) (ET * ((o.arrPonto.get(a.f).z) - o.C.z) + o.C.z) + my);
      }
    }
  }*/

  /**
   * Pinta o painel Frente
   */
  /*public void oldPintaFrente() {
    DF.clearRect(0, 0, 340, 255);
    for (int i = 0; i < Obj.size(); i++) {
      DF.setColor(i == ObSel ? Sel : Color.BLACK);
      o = Obj.get(i);
      for (Aresta a : o.arrAresta) {
        DF.drawLine((int) (EF * ((o.arrPonto.get(a.i).x) - o.C.x) + o.C.x) + mx, (int) (EF * ((o.arrPonto.get(a.i).y) - o.C.y) + o.C.y), (int) (EF * ((o.arrPonto.get(a.f).x) - o.C.x) + o.C.x) + mx, (int) (EF * ((o.arrPonto.get(a.f).y) - o.C.y) + o.C.y));
      }
    }
  }*/
  
  //Antigos for's que salvavam a cena no formato versao 1
  
  //Esse salva
  /*for (Objeto ob : Obj) {
            said.writeShort((short) (ob.BG.getRed() * 255)); //RGB
            said.writeShort((short) (ob.BG.getGreen() * 255));
            said.writeShort((short) (ob.BG.getBlue() * 255));
            if (ob.Fechado) {
              said.writeByte(1);
            } else {
              said.writeByte(0);
            }
            said.writeShort(ob.arrPonto.size());
            for (Ponto p : ob.arrPonto) {
              said.writeDouble(p.x);
              said.writeDouble(p.y);
              said.writeDouble(p.z);
            }
            said.writeShort(ob.arrAresta.size());
            for (Aresta a : ob.arrAresta) {
              said.writeShort(a.i);
              said.writeShort(a.f);
              said.writeShort(a.e);
              said.writeShort(a.d);
            }
            said.writeShort(ob.arrFace.size());
            for (Face f : ob.arrFace) {
              said.writeByte(0);
              said.writeShort(f.fAresta.get(0));
              for (int ia = 1; ia < f.fAresta.size(); ia++) {
                said.writeByte(1);
                said.writeShort(f.fAresta.get(ia));
              }
            }
          }*/
  
  //Esse le
  /*while (entr.available() > 0) { //Le
          Objeto tmp = new Objeto();
          short r = entr.readShort();
          short g = entr.readShort();
          short b = entr.readShort();
          tmp.VaiCor(r, g, b);
          byte F = entr.readByte();
          tmp.Fechado = (F == 1);
          short QP = entr.readShort();
          for (int i = 0; i < QP; i++) {
            x = entr.readDouble();
            y = entr.readDouble();
            z = entr.readDouble();
            tmp.arrPonto.add(new Ponto(x, y, z));
          }
          QP = entr.readShort();
          for (int i = 0; i < QP; i++) {
            s = entr.readShort();
            e = entr.readShort();
            l = entr.readShort();
            ri = entr.readShort();
            tmp.arrAresta.add(new Aresta(s, e, l, ri));
          }
          QP = entr.readShort();
          for (int i = 0; i < QP; i++) {
            byte ui = entr.readByte();
            ri = entr.readShort();
            if (ui == 0) {
              tmp.arrFace.add(new Face(ri));
            } else {
              tmp.arrFace.get(tmp.arrFace.size() - 1).fAresta.add((int) ri);
            }
          }
        }*/
  
}
