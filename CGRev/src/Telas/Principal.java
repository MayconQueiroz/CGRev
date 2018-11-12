package Telas;

import Objetos.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import static revcg.RevCG.*;

/**
 * Tela principal do programa
 * 
 * @author Maycon
 */
public class Principal extends javax.swing.JFrame {

  /**
   * Variáveis globais
   */
  public ArrayList<Objeto> Obj; //objetos em cena
  public Graphics DT; //Topo
  public Graphics DF; //Frente
  public Graphics DL; //Lado
  public Graphics DP; //Pers
  public final int mx = 170; //Meio em x
  public final int my = 127; //Meio em y
  public int ObSel = -1;
  Color Sel = Color.BLUE;
  public Camera VL, VF, VT, VP;
  
  /**
   * Variaveis Locais Globais
   */
  byte cabecalho;
  byte Per = 0; //Operacao
  JFileChooser fc = new JFileChooser();
  double EL=1, ET=1, EP=1, EF=1;
  
  Objeto o;
  
  /**
   * Creates new form Principal
   */
  public Principal() {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"); 
    } catch (Exception ex) { 
      ex.printStackTrace(); 
      EI = 4;
    }   
    initComponents();
    ErrosIniciais();
    //Seta janela para o meio da tela, independente da resolucao.
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation(dim.width/2-this.getSize().width/2, 0);
    setResizable(false); //Nao deixa redimensionar a janela
    DT = pnlTopoI.getGraphics();
    //pnlTopoI.setBackground(Color.LIGHT_GRAY);
    DF = pnlFrenteI.getGraphics();
    //pnlFrenteI.setBackground(Color.LIGHT_GRAY);
    DL = pnlLadoI.getGraphics();
    //pnlLadoI.setBackground(Color.LIGHT_GRAY);
    DP = pnlPerspectivaI.getGraphics();
    //pnlPerspectivaI.setBackground(Color.LIGHT_GRAY);
    Obj = new ArrayList<>();
    LimpaPaineis();
    setIconImage(new ImageIcon(ClassLoader.getSystemResource("Icones/Principal.png")).getImage());
    VL = new Camera(new Ponto(-20,   0,   0), new Ponto(0, 0, 0), new Ponto(0, 1, 0), -170, 170, -127, 128, 20.0);
    VF = new Camera(new Ponto(  0,   0, -20), new Ponto(0, 0, 0), new Ponto(0, 1, 0), -170, 170, -127, 128, 20.0);
    VT = new Camera(new Ponto(  0, -20,   0), new Ponto(0, 0, 0), new Ponto(0, 1, 0), -170, 170, -127, 128, 20.0);
    VP = new Camera(new Ponto(-20, -20, -20), new Ponto(0, 0, 0), new Ponto(0, 1, 0), -170, 170, -127, 128, 20.0);
  }
  
  /**
   * Limpa tudo, tudo mesmo
   */
  public void LimpaTudo(){
    LimpaPaineis();
    Obj.clear();
  }
  
  /**
   * Limpa os paineis
   */
  public void LimpaPaineis(){
    DL.clearRect(0, 0, 340, 255);
    DF.clearRect(0, 0, 340, 255);
    DT.clearRect(0, 0, 340, 255);
    DP.clearRect(0, 0, 340, 255);
  }
  
  /**
   * Pinta todos paineis
   */
  public void PintaTudo(){
    PintaLado();
    PintaTopo();
    PintaFrente();
    PintaPerspectiva();
  }
  
  /**
   * Simplificacao adotada:
   * escala(coordenada-centro)+centro
   * Baseada na distributiva de:
   * (Escala*coordenada)+((1-Escala)*Centro)
   */
  
  /**
   * Pinta o painel Lado
   */
  public void PintaLado(){
    DL.clearRect(0, 0, 340, 255);
    for(int i = 0; i < Obj.size(); i++){
      o = Obj.get(i);
      DL.setColor(i == ObSel ? Sel : Color.BLACK);
      for (Aresta a : o.arrAresta){
        DL.drawLine((int)(EL*((a.i.z)-o.C.z)+o.C.z)+mx, (int)(EL*((a.i.y)-o.C.y)+o.C.y), (int)(EL*((a.f.z)-o.C.z)+o.C.z)+mx, (int)(EL*((a.f.y)-o.C.y)+o.C.y));
      }
    }
  }
  
  /**
   * Pinta o painel Topo
   */
  public void PintaTopo(){
    DT.clearRect(0, 0, 340, 255);
    for(int i = 0; i < Obj.size(); i++){
      DT.setColor(i == ObSel ? Sel : Color.BLACK);
      o = Obj.get(i);
      for (Aresta a : o.arrAresta){
        DT.drawLine((int)(ET*((a.i.x)-o.C.x)+o.C.x)+mx, (int)(ET*((a.i.z)-o.C.z)+o.C.z)+my, (int)(ET*((a.f.x)-o.C.x)+o.C.x)+mx, (int)(ET*((a.f.z)-o.C.z)+o.C.z)+my);
      }
    }
  }
  
  /**
   * Pinta o painel Frente
   */
  public void PintaFrente(){
    DF.clearRect(0, 0, 340, 255);
    for(int i = 0; i < Obj.size(); i++){
      DF.setColor(i == ObSel ? Sel : Color.BLACK);
      o = Obj.get(i);
      for (Aresta a : o.arrAresta){
        DF.drawLine((int)(EF*((a.i.x)-o.C.x)+o.C.x)+mx, (int)(EF*((a.i.y)-o.C.y)+o.C.y), (int)(EF*((a.f.x)-o.C.x)+o.C.x)+mx, (int)(EF*((a.f.y)-o.C.y)+o.C.y));
      }
    }
  }
  
  /**
   * Pinta o painel de perspectiva
   */
  public void PintaPerspectiva(){
    DP.clearRect(0, 0, 340, 255);
  }
  
  //////////////////////////////////////////////////////////////////////////////

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jMenuItem2 = new javax.swing.JMenuItem();
    pnlTopo = new javax.swing.JPanel();
    pnlTopoI = new javax.swing.JPanel();
    btnAmpliarTopo = new javax.swing.JButton();
    tY = new javax.swing.JTextField();
    tX = new javax.swing.JTextField();
    pnlMenus = new javax.swing.JTabbedPane();
    pnlObjetos = new javax.swing.JPanel();
    btnAdicionar = new javax.swing.JButton();
    pnlFerramentas = new javax.swing.JPanel();
    btnMover = new javax.swing.JToggleButton();
    btnRedesenhar = new javax.swing.JButton();
    btnRotacionar = new javax.swing.JToggleButton();
    btnRedimensionar = new javax.swing.JToggleButton();
    btnDesselecionar = new javax.swing.JButton();
    pnlAmbiente = new javax.swing.JPanel();
    pnlFrente = new javax.swing.JPanel();
    pnlFrenteI = new javax.swing.JPanel();
    btnAmpliarFrente = new javax.swing.JButton();
    fX = new javax.swing.JTextField();
    fY = new javax.swing.JTextField();
    pnlLado = new javax.swing.JPanel();
    pnlLadoI = new javax.swing.JPanel();
    btnAmpliarLado = new javax.swing.JButton();
    lY = new javax.swing.JTextField();
    lX = new javax.swing.JTextField();
    pnlPerspectiva = new javax.swing.JPanel();
    pnlPerspectivaI = new javax.swing.JPanel();
    btnAmpliarPerspectiva = new javax.swing.JButton();
    pY = new javax.swing.JTextField();
    pX = new javax.swing.JTextField();
    menuBar = new javax.swing.JMenuBar();
    menuArquivo = new javax.swing.JMenu();
    itemNovo = new javax.swing.JMenuItem();
    itemAbrir = new javax.swing.JMenuItem();
    itemSalvar = new javax.swing.JMenuItem();
    itemSalvarComo = new javax.swing.JMenuItem();
    menuEditar = new javax.swing.JMenu();
    menuAjuda = new javax.swing.JMenu();
    itemAjuda = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JPopupMenu.Separator();
    itemSobre = new javax.swing.JMenuItem();

    jMenuItem2.setText("jMenuItem2");

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("CGRev");

    pnlTopo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Topo"));
    pnlTopo.setPreferredSize(new java.awt.Dimension(350, 276));

    pnlTopoI.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        pnlTopoIMouseMoved(evt);
      }
    });

    javax.swing.GroupLayout pnlTopoILayout = new javax.swing.GroupLayout(pnlTopoI);
    pnlTopoI.setLayout(pnlTopoILayout);
    pnlTopoILayout.setHorizontalGroup(
      pnlTopoILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 340, Short.MAX_VALUE)
    );
    pnlTopoILayout.setVerticalGroup(
      pnlTopoILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 255, Short.MAX_VALUE)
    );

    btnAmpliarTopo.setText("+");

    javax.swing.GroupLayout pnlTopoLayout = new javax.swing.GroupLayout(pnlTopo);
    pnlTopo.setLayout(pnlTopoLayout);
    pnlTopoLayout.setHorizontalGroup(
      pnlTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(pnlTopoI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopoLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(tX, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(tY, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnAmpliarTopo)
        .addContainerGap())
    );
    pnlTopoLayout.setVerticalGroup(
      pnlTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopoLayout.createSequentialGroup()
        .addGroup(pnlTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(pnlTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(tX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(tY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(btnAmpliarTopo))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(pnlTopoI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pnlObjetos.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        pnlObjetosMouseMoved(evt);
      }
    });

    btnAdicionar.setText("Adicionar");
    btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAdicionarActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout pnlObjetosLayout = new javax.swing.GroupLayout(pnlObjetos);
    pnlObjetos.setLayout(pnlObjetosLayout);
    pnlObjetosLayout.setHorizontalGroup(
      pnlObjetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnlObjetosLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(btnAdicionar, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        .addContainerGap())
    );
    pnlObjetosLayout.setVerticalGroup(
      pnlObjetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnlObjetosLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(btnAdicionar)
        .addContainerGap(538, Short.MAX_VALUE))
    );

    pnlMenus.addTab("Objetos", pnlObjetos);

    btnMover.setText("Mover");
    btnMover.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        btnMoverMouseClicked(evt);
      }
    });
    btnMover.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnMoverActionPerformed(evt);
      }
    });

    btnRedesenhar.setText("Redesenhar");
    btnRedesenhar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRedesenharActionPerformed(evt);
      }
    });

    btnRotacionar.setText("Rotacionar");
    btnRotacionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRotacionarActionPerformed(evt);
      }
    });

    btnRedimensionar.setText("Redimensionar");
    btnRedimensionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRedimensionarActionPerformed(evt);
      }
    });

    btnDesselecionar.setText("Desselecionar");
    btnDesselecionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnDesselecionarActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout pnlFerramentasLayout = new javax.swing.GroupLayout(pnlFerramentas);
    pnlFerramentas.setLayout(pnlFerramentasLayout);
    pnlFerramentasLayout.setHorizontalGroup(
      pnlFerramentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnlFerramentasLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(pnlFerramentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(btnMover, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
          .addComponent(btnRedesenhar, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
          .addComponent(btnRotacionar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnRedimensionar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnDesselecionar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    pnlFerramentasLayout.setVerticalGroup(
      pnlFerramentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnlFerramentasLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(btnMover)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnRotacionar)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnRedimensionar)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnDesselecionar)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 417, Short.MAX_VALUE)
        .addComponent(btnRedesenhar)
        .addContainerGap())
    );

    pnlMenus.addTab("Ferramentas", null, pnlFerramentas, "");
    pnlFerramentas.getAccessibleContext().setAccessibleName("");
    pnlFerramentas.getAccessibleContext().setAccessibleDescription("");

    javax.swing.GroupLayout pnlAmbienteLayout = new javax.swing.GroupLayout(pnlAmbiente);
    pnlAmbiente.setLayout(pnlAmbienteLayout);
    pnlAmbienteLayout.setHorizontalGroup(
      pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 174, Short.MAX_VALUE)
    );
    pnlAmbienteLayout.setVerticalGroup(
      pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 572, Short.MAX_VALUE)
    );

    pnlMenus.addTab("Ambiente", pnlAmbiente);

    pnlFrente.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Frente"));
    pnlFrente.setPreferredSize(new java.awt.Dimension(350, 276));

    pnlFrenteI.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        pnlFrenteIMouseMoved(evt);
      }
    });
    pnlFrenteI.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        pnlFrenteIMouseClicked(evt);
      }
    });

    javax.swing.GroupLayout pnlFrenteILayout = new javax.swing.GroupLayout(pnlFrenteI);
    pnlFrenteI.setLayout(pnlFrenteILayout);
    pnlFrenteILayout.setHorizontalGroup(
      pnlFrenteILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 340, Short.MAX_VALUE)
    );
    pnlFrenteILayout.setVerticalGroup(
      pnlFrenteILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );

    btnAmpliarFrente.setText("+");

    javax.swing.GroupLayout pnlFrenteLayout = new javax.swing.GroupLayout(pnlFrente);
    pnlFrente.setLayout(pnlFrenteLayout);
    pnlFrenteLayout.setHorizontalGroup(
      pnlFrenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(pnlFrenteI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(pnlFrenteLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(fX, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(fY, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnAmpliarFrente)
        .addContainerGap())
    );
    pnlFrenteLayout.setVerticalGroup(
      pnlFrenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFrenteLayout.createSequentialGroup()
        .addGroup(pnlFrenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btnAmpliarFrente)
          .addComponent(fX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(fY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(pnlFrenteI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pnlLado.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Lado"));
    pnlLado.setPreferredSize(new java.awt.Dimension(350, 276));

    pnlLadoI.setPreferredSize(new java.awt.Dimension(340, 255));
    pnlLadoI.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        pnlLadoIMouseMoved(evt);
      }
    });
    pnlLadoI.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        pnlLadoIMouseReleased(evt);
      }
    });

    javax.swing.GroupLayout pnlLadoILayout = new javax.swing.GroupLayout(pnlLadoI);
    pnlLadoI.setLayout(pnlLadoILayout);
    pnlLadoILayout.setHorizontalGroup(
      pnlLadoILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    pnlLadoILayout.setVerticalGroup(
      pnlLadoILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 255, Short.MAX_VALUE)
    );

    btnAmpliarLado.setText("+");

    javax.swing.GroupLayout pnlLadoLayout = new javax.swing.GroupLayout(pnlLado);
    pnlLado.setLayout(pnlLadoLayout);
    pnlLadoLayout.setHorizontalGroup(
      pnlLadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLadoLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lX, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(lY, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 221, Short.MAX_VALUE)
        .addComponent(btnAmpliarLado)
        .addContainerGap())
      .addComponent(pnlLadoI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    pnlLadoLayout.setVerticalGroup(
      pnlLadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLadoLayout.createSequentialGroup()
        .addGroup(pnlLadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(pnlLadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(lX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(btnAmpliarLado))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(pnlLadoI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pnlPerspectiva.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Perspectiva"));
    pnlPerspectiva.setPreferredSize(new java.awt.Dimension(350, 276));

    pnlPerspectivaI.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        pnlPerspectivaIMouseMoved(evt);
      }
    });

    javax.swing.GroupLayout pnlPerspectivaILayout = new javax.swing.GroupLayout(pnlPerspectivaI);
    pnlPerspectivaI.setLayout(pnlPerspectivaILayout);
    pnlPerspectivaILayout.setHorizontalGroup(
      pnlPerspectivaILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 340, Short.MAX_VALUE)
    );
    pnlPerspectivaILayout.setVerticalGroup(
      pnlPerspectivaILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );

    btnAmpliarPerspectiva.setText("+");

    javax.swing.GroupLayout pnlPerspectivaLayout = new javax.swing.GroupLayout(pnlPerspectiva);
    pnlPerspectiva.setLayout(pnlPerspectivaLayout);
    pnlPerspectivaLayout.setHorizontalGroup(
      pnlPerspectivaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(pnlPerspectivaI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPerspectivaLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(pX, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(pY, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnAmpliarPerspectiva)
        .addContainerGap())
    );
    pnlPerspectivaLayout.setVerticalGroup(
      pnlPerspectivaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlPerspectivaLayout.createSequentialGroup()
        .addGroup(pnlPerspectivaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(pnlPerspectivaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(pX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(pY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(btnAmpliarPerspectiva))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(pnlPerspectivaI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    menuArquivo.setText("Arquivo");

    itemNovo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
    itemNovo.setText("Novo");
    itemNovo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemNovoActionPerformed(evt);
      }
    });
    menuArquivo.add(itemNovo);

    itemAbrir.setText("Abrir");
    itemAbrir.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemAbrirActionPerformed(evt);
      }
    });
    menuArquivo.add(itemAbrir);

    itemSalvar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
    itemSalvar.setText("Salvar");
    itemSalvar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemSalvarActionPerformed(evt);
      }
    });
    menuArquivo.add(itemSalvar);

    itemSalvarComo.setText("Salvar Como");
    menuArquivo.add(itemSalvarComo);

    menuBar.add(menuArquivo);

    menuEditar.setText("Editar");
    menuBar.add(menuEditar);

    menuAjuda.setText("Ajuda");

    itemAjuda.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
    itemAjuda.setText("Tópicos de ajuda");
    itemAjuda.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemAjudaActionPerformed(evt);
      }
    });
    menuAjuda.add(itemAjuda);
    menuAjuda.add(jSeparator1);

    itemSobre.setText("Sobre");
    itemSobre.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemSobreActionPerformed(evt);
      }
    });
    menuAjuda.add(itemSobre);

    menuBar.add(menuAjuda);

    setJMenuBar(menuBar);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(pnlLado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(pnlTopo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(pnlPerspectiva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(pnlFrente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(pnlMenus)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(pnlLado, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
              .addComponent(pnlFrente, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(pnlTopo, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
              .addComponent(pnlPerspectiva, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)))
          .addComponent(pnlMenus))
        .addContainerGap())
    );

    pnlMenus.getAccessibleContext().setAccessibleName("pnlTabs");

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void itemSobreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSobreActionPerformed
    this.setEnabled(false);
    new Sobre(this).setVisible(true);
  }//GEN-LAST:event_itemSobreActionPerformed

  private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
    this.setEnabled(false);
    new Perfil(this).setVisible(true);
  }//GEN-LAST:event_btnAdicionarActionPerformed

  private void pnlObjetosMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlObjetosMouseMoved
    PintaTudo();
  }//GEN-LAST:event_pnlObjetosMouseMoved

  private void itemNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemNovoActionPerformed
    if(!Obj.isEmpty()){
      Object[] options = {"Sim", "Não", "Cancelar"};
      int n = JOptionPane.showOptionDialog(this, "Há objetos não salvos, deseja salvá-los?",
      "Cena existente", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
      options, options[2]);
      if (n == 0){
        itemSalvarActionPerformed(null);
        LimpaTudo();
      } else if (n == 1){
        LimpaTudo();
      }
    } else {
      LimpaTudo();
    }
  }//GEN-LAST:event_itemNovoActionPerformed

  private void itemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAbrirActionPerformed
    fc = new JFileChooser();
    byte r;
    double xi, yi, zi, xf, yf, zf;
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
          r = entr.readByte();
          if (r == 0){ //Objeto
            Obj.add(new Objeto());
          } else if (r == 1){ //Ponto
            Obj.get(Obj.size()-1).arrPonto.add(new Ponto(entr.readDouble(), entr.readDouble(), entr.readDouble()));
          } else if (r == 2){ //Aresta
            Obj.get(Obj.size()-1).arrAresta.add(new Aresta(new Ponto(entr.readDouble(), entr.readDouble(), entr.readDouble()), new Ponto(entr.readDouble(), entr.readDouble(), entr.readDouble())));
          } else {
            JOptionPane.showMessageDialog(this, "Identificador invalido", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
      } catch (FileNotFoundException ex) {
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Nao foi possivel ler o arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      } catch (IOException ex) {
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Erro generico de leitura", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
    LimpaPaineis();
    PintaTudo();
    LimpaPaineis();
    PintaTudo();
  }//GEN-LAST:event_itemAbrirActionPerformed

  private void btnMoverMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMoverMouseClicked
    if(btnMover.isSelected()){
      //System.out.println("Prendi");
    } else {
      //System.out.println("Soltei");
    }
  }//GEN-LAST:event_btnMoverMouseClicked

  private void pnlFrenteIMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlFrenteIMouseClicked
    if(btnMover.isSelected()){
      //System.out.println("Policia");
    } else {
      //System.out.println("Juiz");
    }
  }//GEN-LAST:event_pnlFrenteIMouseClicked

  private void itemSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSalvarActionPerformed
    fc = new JFileChooser();
    int returnVal = fc.showSaveDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      String s = file.toString() + ".acr"; //Arquivo CGRev (Perfil, cena)
      //System.out.println("Saida = " + s);
      cabecalho = (byte) (VERSAO_CENA << 4);
      try {
        try (DataOutputStream said = new DataOutputStream(new FileOutputStream(s))) {
          said.writeByte(cabecalho);
          for (Objeto o : Obj) {
            said.writeByte(0);
            for (Ponto p : o.arrPonto) {
              said.writeByte(1);
              said.writeDouble(p.x);
              said.writeDouble(p.y);
              said.writeDouble(p.z);
            }
            for (Aresta a : o.arrAresta) {
              said.writeByte(2);
              said.writeDouble(a.i.x);
              said.writeDouble(a.i.y);
              said.writeDouble(a.i.z);
              said.writeDouble(a.f.x);
              said.writeDouble(a.f.y);
              said.writeDouble(a.f.z);
            }
          }
        }
      } catch (FileNotFoundException ex) {
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }//GEN-LAST:event_itemSalvarActionPerformed

  private void btnRedesenharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedesenharActionPerformed
    LimpaPaineis();
    PintaTudo();
  }//GEN-LAST:event_btnRedesenharActionPerformed

  private void btnMoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoverActionPerformed
    if (btnMover.isSelected()){
      Per = (byte) ((Per&0b11111101)|0b00000001);
      btnRedimensionar.setSelected(false);
      btnRotacionar.setSelected(false);
    } else {
      Per = (byte) (Per&0b11111100);
      ObSel = -1;
    }
  }//GEN-LAST:event_btnMoverActionPerformed

  private void btnRotacionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRotacionarActionPerformed
    if (btnRotacionar.isSelected()) {
      Per = (byte) ((Per&0b11111110)|0b00000010);
      btnRedimensionar.setSelected(false);
      btnMover.setSelected(false);
    } else {
      Per = (byte) (Per&0b11111100);
      ObSel = -1;
    }
  }//GEN-LAST:event_btnRotacionarActionPerformed

  private void btnRedimensionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedimensionarActionPerformed
    if (btnRedimensionar.isSelected()){
      Per = (byte) ((Per&0b11111111)|0b00000011);
      btnMover.setSelected(false);
      btnRotacionar.setSelected(false);
    } else {
      Per = (byte) (Per&0b11111100);
      ObSel = -1;
    }
  }//GEN-LAST:event_btnRedimensionarActionPerformed

  private void btnDesselecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesselecionarActionPerformed
    Per = (byte) (Per&0b11111100);
    btnMover.setSelected(false);
    btnRotacionar.setSelected(false);
    btnRedimensionar.setSelected(false);
    ObSel = -1;
  }//GEN-LAST:event_btnDesselecionarActionPerformed

  private void itemAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAjudaActionPerformed
    this.setEnabled(false);
    new Ajuda(this, 1).setVisible(true);
  }//GEN-LAST:event_itemAjudaActionPerformed

  private void pnlLadoIMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLadoIMouseReleased
    if (ObSel > -1){
      if ((Per&1) == 1){ //Mover
        //
      } else if ((Per&2) == 2){ //Redimensionar
        //
      } else if ((Per&3) == 3){ //Rotacionar
        //
      }
    } else { //Selecionar
      //
    }
  }//GEN-LAST:event_pnlLadoIMouseReleased

  private void pnlLadoIMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLadoIMouseMoved
    int Aux = (evt.getY() * -1) + 255;
    lX.setText("" + evt.getX());
    lY.setText("" + Aux);
  }//GEN-LAST:event_pnlLadoIMouseMoved

  private void pnlFrenteIMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlFrenteIMouseMoved
    int Aux = (evt.getY() * -1) + 255;
    fX.setText("" + evt.getX());
    fY.setText("" + Aux);
  }//GEN-LAST:event_pnlFrenteIMouseMoved

  private void pnlTopoIMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopoIMouseMoved
    int Aux = (evt.getY() * -1) + 255;
    tX.setText("" + evt.getX());
    tY.setText("" + Aux);
  }//GEN-LAST:event_pnlTopoIMouseMoved

  private void pnlPerspectivaIMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlPerspectivaIMouseMoved
    int Aux = (evt.getY() * -1) + 255;
    pX.setText("" + evt.getX());
    pY.setText("" + Aux);
  }//GEN-LAST:event_pnlPerspectivaIMouseMoved

  public void ErrosIniciais(){
    if (EI == -1){
      JOptionPane.showMessageDialog(this, "Algo esta impedindo a execucao deste programa, consulte o log de saida para mais informacoes", "Erro", JOptionPane.ERROR_MESSAGE);
    } else if (EI == 0){
      return; //Ready to go
    } else if (EI == 1){
      JOptionPane.showMessageDialog(this, "Classe faltante, consulte o log de saida para mais informacoes", "Erro", JOptionPane.ERROR_MESSAGE);
    } else if (EI == 2){
      JOptionPane.showMessageDialog(this, "Erro de instanciacao, consulte o log de saida para mais informacoes", "Erro", JOptionPane.ERROR_MESSAGE);
    } else if (EI == 3){
      JOptionPane.showMessageDialog(this, "Acesso Ilegal, consulte o log de saida para mais informacoes", "Erro", JOptionPane.ERROR_MESSAGE);
    } else if (EI == 4){
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
    /*try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Windows".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }*/
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new Principal().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnAdicionar;
  private javax.swing.JButton btnAmpliarFrente;
  private javax.swing.JButton btnAmpliarLado;
  private javax.swing.JButton btnAmpliarPerspectiva;
  private javax.swing.JButton btnAmpliarTopo;
  private javax.swing.JButton btnDesselecionar;
  private javax.swing.JToggleButton btnMover;
  private javax.swing.JButton btnRedesenhar;
  private javax.swing.JToggleButton btnRedimensionar;
  private javax.swing.JToggleButton btnRotacionar;
  private javax.swing.JTextField fX;
  private javax.swing.JTextField fY;
  private javax.swing.JMenuItem itemAbrir;
  private javax.swing.JMenuItem itemAjuda;
  private javax.swing.JMenuItem itemNovo;
  private javax.swing.JMenuItem itemSalvar;
  private javax.swing.JMenuItem itemSalvarComo;
  private javax.swing.JMenuItem itemSobre;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JPopupMenu.Separator jSeparator1;
  private javax.swing.JTextField lX;
  private javax.swing.JTextField lY;
  private javax.swing.JMenu menuAjuda;
  private javax.swing.JMenu menuArquivo;
  private javax.swing.JMenuBar menuBar;
  private javax.swing.JMenu menuEditar;
  private javax.swing.JTextField pX;
  private javax.swing.JTextField pY;
  private javax.swing.JPanel pnlAmbiente;
  private javax.swing.JPanel pnlFerramentas;
  private javax.swing.JPanel pnlFrente;
  private javax.swing.JPanel pnlFrenteI;
  private javax.swing.JPanel pnlLado;
  private javax.swing.JPanel pnlLadoI;
  private javax.swing.JTabbedPane pnlMenus;
  private javax.swing.JPanel pnlObjetos;
  private javax.swing.JPanel pnlPerspectiva;
  private javax.swing.JPanel pnlPerspectivaI;
  private javax.swing.JPanel pnlTopo;
  private javax.swing.JPanel pnlTopoI;
  private javax.swing.JTextField tX;
  private javax.swing.JTextField tY;
  // End of variables declaration//GEN-END:variables
}
