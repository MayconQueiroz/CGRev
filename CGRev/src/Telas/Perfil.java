package Telas;

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
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
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
  public ArrayList<Ponto> arrPontoNil;
  public Graphics D; //de Desenho
  public ArrayList<Aresta> arrAresta;
  public int iniY = 1023;
  public JFileChooser fc = new JFileChooser();
  public byte cabecalho;
  public Objeto obj;
  public boolean issaved;
  public boolean fechado;

  /**
   * Creates new form Perfil
   */
  private Perfil() {
    initComponents();
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    //this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    setResizable(false); //Nao deixa redimensionar a janela
    arrPonto = new ArrayList();
    arrAresta = new ArrayList();
    arrPontoNil = new ArrayList();
    //pnlRevI.setBackground(Color.LIGHT_GRAY);
    D = pnlRevI.getGraphics();
    issaved = true;
    fechado = false;
    lstEixos.setSelectedIndex(1);
    txtBaixo.setEnabled(false);
    txtLado.setEnabled(false);
  }

  public Perfil(Principal P) {
    this();
    this.P = P;
  }

  /**
   * Desenha as linhas do perfil escolhido
   */
  public void DesenhaPerfil() {
    for (Aresta a : arrAresta) {
      D.drawLine((int) a.i.x, (int) a.i.y, (int) a.f.x, (int) a.f.y);
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
    if ((cabecalho & 8) != 0){ //Fechada
      arrAresta.add(new Aresta(arrPonto.get(arrPonto.size()-1), arrPonto.get(0)));
    }
  }

  /**
   * Imprime os pontos (Debug mode)
   */
  public void PrintPontos() {
    int i = 0;
    for (Ponto p : arrPonto) {
      System.out.println("P" + i + " = " + p.toString());
      i++;
    }
    i = 0;
    for (Ponto p : arrPontoNil) {
      System.out.println("PN" + i + " = " + p.toString());
      i++;
    }
  }

  /**
   * Imprime as arestas do objeto (Debug mode)
   */
  public void PrintListaArestas() {
    int i = 0;
    for (Aresta a : obj.arrAresta) {
      System.out.println("A" + i + " = " + a.toString());
      i++;
    }
  }

  /**
   * Imprime as arestas (Debug mode)
   */
  public void PrintListaArestasIn() {
    int i = 0;
    for (Aresta a : arrAresta) {
      System.out.println("-IN-A" + i + " = " + a.toString());
      i++;
    }
  }

  /**
   * Onde a magica acontece
   *
   * @param Num Numero de segmentos
   * @param op Operacao (0 e 1 fechados, 2 abertos)
   */
  public void CriaObjeto(int Num, int op, double Ang) {
    double teta = Ang / Num;
    double ccos = Math.cos(teta);
    double csin = Math.sin(teta);
    double xold, zold;
    int l;
    Ponto plinha = new Ponto();
    Aresta arAux;
    //System.out.println("001");
    obj = new Objeto();
    //PrintPontos();
    if (op == 0) { //Inicia e encerra no eixo - fechado
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
            plinha.x = (plinha.x * ccos) + (plinha.z * csin);
            plinha.z = (xold * (-csin)) + (plinha.z * ccos);
            arrPontoNil.add(new Ponto(plinha));
            arrPonto.get(y).x = plinha.x;
            arrPonto.get(y).z = plinha.z;
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
        //System.out.println("Saí para construir arestas, já volto");
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
        //System.out.println("Saí para construir arestas, já volto");
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
      //System.out.println("Aberto OP2");
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
        //System.out.println("Saí para construir arestas, já volto");
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
    pnlRev = new javax.swing.JPanel();
    pnlRevI = new javax.swing.JPanel();
    btnRotacionar = new javax.swing.JButton();
    btnFecha = new javax.swing.JButton();
    btnIniciaEixo = new javax.swing.JButton();
    btnFechaEixo = new javax.swing.JButton();
    lblInfo = new javax.swing.JLabel();
    btnDesfazer = new javax.swing.JButton();
    ctrSegmentos = new javax.swing.JSpinner();
    jLabel1 = new javax.swing.JLabel();
    txtBaixo = new javax.swing.JTextField();
    lblEixoBaixo = new javax.swing.JLabel();
    lblEixoLado = new javax.swing.JLabel();
    txtLado = new javax.swing.JTextField();
    lstEixos = new javax.swing.JComboBox<>();
    jLabel2 = new javax.swing.JLabel();
    eixoBaixo = new javax.swing.JLabel();
    eixoLado = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    ctrAngulo = new javax.swing.JSpinner();
    jMenuBar1 = new javax.swing.JMenuBar();
    jMenu1 = new javax.swing.JMenu();
    itemNovo = new javax.swing.JMenuItem();
    itemAbrir = new javax.swing.JMenuItem();
    itemSalvar = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JPopupMenu.Separator();
    itemCarregarModelos = new javax.swing.JMenuItem();
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
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        pnlRevIMouseClicked(evt);
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
      .addComponent(pnlRevI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    btnRotacionar.setText("Rotacionar");
    btnRotacionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRotacionarActionPerformed(evt);
      }
    });

    btnFecha.setText("Fechar Forma");
    btnFecha.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnFechaActionPerformed(evt);
      }
    });

    btnIniciaEixo.setText("Seleciona Eixo");
    btnIniciaEixo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnIniciaEixoActionPerformed(evt);
      }
    });

    btnFechaEixo.setText("Fecha eixo");
    btnFechaEixo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnFechaEixoActionPerformed(evt);
      }
    });

    lblInfo.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

    btnDesfazer.setText("Desfazer");
    btnDesfazer.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnDesfazerActionPerformed(evt);
      }
    });

    ctrSegmentos.setModel(new javax.swing.SpinnerNumberModel(10, 3, 100, 1));

    jLabel1.setText("Segmentos Revolução");

    lblEixoBaixo.setText("x");

    lblEixoLado.setText("y");

    lstEixos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "x", "y", "z" }));
    lstEixos.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        lstEixosItemStateChanged(evt);
      }
    });

    jLabel2.setText("Eixo de rotação");

    eixoBaixo.setText("x");

    eixoLado.setText("y");

    jLabel3.setText("Ângulo");

    ctrAngulo.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(360.0f), Float.valueOf(0.01f), Float.valueOf(360.0f), Float.valueOf(1.0f)));

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
    jMenu1.add(jSeparator2);

    itemCarregarModelos.setText("Carregar modelos");
    itemCarregarModelos.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemCarregarModelosActionPerformed(evt);
      }
    });
    jMenu1.add(itemCarregarModelos);

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
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addComponent(btnFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(btnIniciaEixo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(btnFechaEixo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(btnDesfazer, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(ctrSegmentos, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .addComponent(eixoBaixo)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                  .addComponent(jLabel2)
                  .addComponent(btnRotacionar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(jLabel3))
                .addComponent(ctrAngulo))
              .addComponent(lstEixos, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)))
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
            .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
            .addComponent(pnlRev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
              .addComponent(btnFecha)
              .addGap(12, 12, 12)
              .addComponent(btnIniciaEixo)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
              .addComponent(btnFechaEixo)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
              .addComponent(btnDesfazer)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(jLabel1)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(ctrSegmentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
              .addComponent(btnRotacionar)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
              .addComponent(jLabel2)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(lstEixos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(jLabel3)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(ctrAngulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(eixoBaixo)))
          .addComponent(eixoLado))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(txtBaixo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblEixoBaixo))
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(txtLado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblEixoLado))
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
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Nao foi possivel ler o arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      } catch (IOException ex) {
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Erro generico de leitura", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      }
      issaved = true;
    }
    DesenhaPerfil();
    iniY = 2047;
  }//GEN-LAST:event_itemAbrirActionPerformed

  private void itemSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSalvarActionPerformed
    fc = new JFileChooser();
    int returnVal = fc.showSaveDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      String s = file.toString() + ".acr"; //Arquivo CGRev (Perfil, cena)
      //System.out.println("Saida = " + s);
      cabecalho = (byte) (VERSAO_PERFIL << 4);
      //System.out.println("i = " + arrAresta.get(arrAresta.size()-1).i.toString() + "f = " + arrAresta.get(arrAresta.size()-1).f.toString());
      if (arrAresta.get(0).i.equals(arrAresta.get(arrAresta.size()-1).f)){ //Fechado
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
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
      }
      issaved = true;
    }
  }//GEN-LAST:event_itemSalvarActionPerformed

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
    arrPonto.remove(arrPonto.size() - 1);
    arrAresta.remove(arrAresta.size() - 1);
    D.setColor(Color.BLACK);
    iniY = 2047;
  }//GEN-LAST:event_btnDesfazerActionPerformed

  private void pnlRevIMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlRevIMouseMoved
    DesenhaPerfil();
    txtBaixo.setText("" + evt.getX());
    txtLado.setText("" + evt.getY());
  }//GEN-LAST:event_pnlRevIMouseMoved

  private void pnlRevIMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlRevIMouseClicked
    ///Funcao Isolada, transferida para mouseReleased para garantir funcionalidade
    /*if (!fechado) {
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
    }*/
  }//GEN-LAST:event_pnlRevIMouseClicked

  private void itemCarregarModelosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCarregarModelosActionPerformed
    this.setEnabled(false);
    new Modelos(this).setVisible(true);
  }//GEN-LAST:event_itemCarregarModelosActionPerformed

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

  private void btnRotacionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRotacionarActionPerformed
    if (arrAresta.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Não há arestas a rotacionar", "Erro", JOptionPane.ERROR_MESSAGE);
      return;
    }
    String input = ctrSegmentos.getValue().toString();
    if (input.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Quantidade de sementos não informada", "Erro", JOptionPane.ERROR_MESSAGE);
    } else {
      int Num = 6;
      try {
        Num = Integer.parseInt(input);
      } catch (NumberFormatException | NullPointerException e) {
        JOptionPane.showMessageDialog(this, "Valor de segmentos informado não é inteiro - definido como 6", "Não pode ser...", JOptionPane.WARNING_MESSAGE);
      }
      if (Num < 3) {
        JOptionPane.showMessageDialog(this, "Valor deve ser maior que 3 - definido como 3", "Erro", JOptionPane.ERROR_MESSAGE);
        Num = 3;
      } else if (Num > 100) {
        JOptionPane.showMessageDialog(this, "Valor deve ser menor que 100 - definido como 100", "Erro", JOptionPane.ERROR_MESSAGE);
        Num = 100;
      }
      int op;
      //System.out.println("Inicial = " + arrAresta.get(0).toString());
      //System.out.println("Final   = " + arrAresta.get(arrAresta.size() - 1));
      if (arrAresta.get(0).i.x == 0 && arrAresta.get(arrAresta.size() - 1).f.x == 0) { // 0 nao roda
        op = 0;
      } else if (arrAresta.get(0).i.x == arrAresta.get(arrAresta.size() - 1).f.x) { //Inicial igual ao final
        op = 1;
      } else {
        op = 2;
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
          JOptionPane.showMessageDialog(this, "Valor deve ser menor que 360 - definido como 360", "Erro", JOptionPane.ERROR_MESSAGE);
          Ang = 360.0;
        }
      }
      Ang = Ang * (Math.PI / 180); //Para pi rad
      
      CriaObjeto(Num, op, Ang);
    }
    P.Obj.add(obj);
    this.dispose();
    P.setEnabled(true);
    P.requestFocus(); //Traz o foco para tela anterior
    P.PintaTudo();
  }//GEN-LAST:event_btnRotacionarActionPerformed

  private void itemAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAjudaActionPerformed
    
  }//GEN-LAST:event_itemAjudaActionPerformed

  private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
    DesenhaPerfil();
  }//GEN-LAST:event_formWindowGainedFocus

  private void pnlRevIMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlRevIMouseReleased
    if (!fechado) {
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
  }//GEN-LAST:event_pnlRevIMouseReleased

  private void lstEixosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_lstEixosItemStateChanged
    if (lstEixos.getSelectedItem() == "x"){
      eixoLado.setText("x");
      eixoBaixo.setText("y");
    } else if(lstEixos.getSelectedItem() == "y"){
      eixoLado.setText("y");
      eixoBaixo.setText("x");
    } else if (lstEixos.getSelectedItem() == "z"){
      eixoLado.setText("z");
      eixoBaixo.setText("x");
    } else {
      ErroPadrao();
    }
    //System.out.println("I'm being called twice");
  }//GEN-LAST:event_lstEixosItemStateChanged
  
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
      java.util.logging.Logger.getLogger(Perfil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(Perfil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(Perfil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(Perfil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
  private javax.swing.JButton btnDesfazer;
  private javax.swing.JButton btnFecha;
  private javax.swing.JButton btnFechaEixo;
  private javax.swing.JButton btnIniciaEixo;
  private javax.swing.JButton btnRotacionar;
  private javax.swing.JSpinner ctrAngulo;
  private javax.swing.JSpinner ctrSegmentos;
  private javax.swing.JLabel eixoBaixo;
  private javax.swing.JLabel eixoLado;
  private javax.swing.JMenuItem itemAbrir;
  private javax.swing.JMenuItem itemAjuda;
  private javax.swing.JMenuItem itemCarregarModelos;
  private javax.swing.JMenuItem itemNovo;
  private javax.swing.JMenuItem itemSalvar;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JMenu jMenu1;
  private javax.swing.JMenu jMenu2;
  private javax.swing.JMenuBar jMenuBar1;
  private javax.swing.JMenuItem jMenuItem5;
  private javax.swing.JMenuItem jMenuItem6;
  private javax.swing.JMenuItem jMenuItem7;
  private javax.swing.JSeparator jSeparator1;
  private javax.swing.JPopupMenu.Separator jSeparator2;
  private javax.swing.JLabel lblEixoBaixo;
  private javax.swing.JLabel lblEixoLado;
  private javax.swing.JLabel lblInfo;
  private javax.swing.JComboBox<String> lstEixos;
  private javax.swing.JPanel pnlRev;
  private javax.swing.JPanel pnlRevI;
  private javax.swing.JTextField txtBaixo;
  private javax.swing.JTextField txtLado;
  // End of variables declaration//GEN-END:variables
}
