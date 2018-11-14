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
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
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
  public ArrayList<Objeto> Obj; //Objetos em cena
  public Graphics DT; //Topo
  public Graphics DF; //Frente
  public Graphics DL; //Lado
  public Graphics DP; //Pers
  public final int mx = 170; //Meio em x
  public final int my = 127; //Meio em y
  public int ObSel = -1; //Objeto selecionado (posicao no array)
  Color Sel = Color.BLUE; //Cor de selecao
  public Camera VL, VF, VT, VP; //4 Cameras

  /**
   * Variaveis Locais Globais
   */
  byte cabecalho; //Byte de cabecalho do arquivo (Documentar melhor uso dos bits)
  byte Per = 0; //Operacao (Translacao, rotacao ou escala)
  JFileChooser fc = new JFileChooser(); //Instancia do filechooser para salvar e abrir
  double EL = 1, ET = 1, EP = 1, EF = 1;
  double ClicX, ClicY; //Posicao onde o painel foi clicado
  double ReleX, ReleY; //Posicao onde o painel foi "solto"
  String fileName; //Nome do arquivo aberto/recem salvo

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
    this.setLocation(dim.width / 2 - this.getSize().width / 2, 0);
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
    btnCor.setEnabled(false);
    setIconImage(new ImageIcon(ClassLoader.getSystemResource("Icones/Principal.png")).getImage());
    VL = new Camera(new Ponto(-20, 0, 0), new Ponto(0, 0, 0), new Ponto(0, 1, 0), 340, 255, -170, 170, -127, 128, 20.0);
    VF = new Camera(new Ponto(0, 0, -20), new Ponto(0, 0, 0), new Ponto(0, 1, 0), 340, 255, -170, 170, -127, 128, 20.0);
    VT = new Camera(new Ponto(0, -20, 0), new Ponto(0, 0, 0), new Ponto(0, 1, 0), 340, 255, -170, 170, -127, 128, 20.0);
    VP = new Camera(new Ponto(-20, -20, -20), new Ponto(0, 0, 0), new Ponto(0, 1, 0), 340, 255, -170, 170, -127, 128, 20.0);
  }

  /**
   * Limpa tudo, tudo mesmo
   */
  public void LimpaTudo() {
    LimpaPaineis();
    Obj.clear();
  }

  /**
   * Limpa os paineis
   */
  public void LimpaPaineis() {
    DL.clearRect(0, 0, 340, 255);
    DF.clearRect(0, 0, 340, 255);
    DT.clearRect(0, 0, 340, 255);
    DP.clearRect(0, 0, 340, 255);
  }

  /**
   * Pinta todos paineis
   */
  public void PintaTudo() {
    oldPintaLado();
    oldPintaTopo();
    oldPintaFrente();
    PintaPerspectiva();
  }

  /**
   * Simplificacao adotada: escala(coordenada-centro)+centro Baseada na
   * distributiva de: (Escala*coordenada)+((1-Escala)*Centro)
   */
  /**
   * Pinta o painel Lado
   */
  public void oldPintaLado() {
    DL.clearRect(0, 0, 340, 255);
    for (int i = 0; i < Obj.size(); i++) {
      o = Obj.get(i);
      DL.setColor(i == ObSel ? Sel : Color.BLACK);
      for (Aresta a : o.arrAresta) {
        DL.drawLine((int) (EL * ((o.arrPonto.get(a.i).z) - o.C.z) + o.C.z) + mx, (int) (EL * ((o.arrPonto.get(a.i).y) - o.C.y) + o.C.y), (int) (EL * ((o.arrPonto.get(a.f).z) - o.C.z) + o.C.z) + mx, (int) (EL * ((o.arrPonto.get(a.f).y) - o.C.y) + o.C.y));
      }
    }
  }

  /**
   * Pinta o painel Topo
   */
  public void oldPintaTopo() {
    DT.clearRect(0, 0, 340, 255);
    for (int i = 0; i < Obj.size(); i++) {
      DT.setColor(i == ObSel ? Sel : Color.BLACK);
      o = Obj.get(i);
      for (Aresta a : o.arrAresta) {
        DT.drawLine((int) (ET * ((o.arrPonto.get(a.i).x) - o.C.x) + o.C.x) + mx, (int) (ET * ((o.arrPonto.get(a.i).z) - o.C.z) + o.C.z) + my, (int) (ET * ((o.arrPonto.get(a.f).x) - o.C.x) + o.C.x) + mx, (int) (ET * ((o.arrPonto.get(a.f).z) - o.C.z) + o.C.z) + my);
      }
    }
  }

  /**
   * Pinta o painel Frente
   */
  public void oldPintaFrente() {
    DF.clearRect(0, 0, 340, 255);
    for (int i = 0; i < Obj.size(); i++) {
      DF.setColor(i == ObSel ? Sel : Color.BLACK);
      o = Obj.get(i);
      for (Aresta a : o.arrAresta) {
        DF.drawLine((int) (EF * ((o.arrPonto.get(a.i).x) - o.C.x) + o.C.x) + mx, (int) (EF * ((o.arrPonto.get(a.i).y) - o.C.y) + o.C.y), (int) (EF * ((o.arrPonto.get(a.f).x) - o.C.x) + o.C.x) + mx, (int) (EF * ((o.arrPonto.get(a.f).y) - o.C.y) + o.C.y));
      }
    }
  }

  /**
   * Pinta o painel de perspectiva
   */
  public void PintaPerspectiva() {
    DP.clearRect(0, 0, 340, 255);
  }

  /**
   * Seleciona algum dos objetos dependendo da posicao que a tela foi clicada
   */
  public void SelecionaAlguem() {
    //FAZER
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
    btnSelecionar = new javax.swing.JToggleButton();
    btnDesselecionar = new javax.swing.JButton();
    btnApagar = new javax.swing.JButton();
    btnMover = new javax.swing.JToggleButton();
    btnRotacionar = new javax.swing.JToggleButton();
    btnRedimensionar = new javax.swing.JToggleButton();
    pnlPropriedades = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    btnCor = new javax.swing.JButton();
    pnlCor = new javax.swing.JPanel();
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

    btnAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/mais.png"))); // NOI18N
    btnAdicionar.setText("Adicionar");
    btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAdicionarActionPerformed(evt);
      }
    });

    btnSelecionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/curs.png"))); // NOI18N
    btnSelecionar.setText("Selecionar");
    btnSelecionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSelecionarActionPerformed(evt);
      }
    });

    btnDesselecionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/cursB.png"))); // NOI18N
    btnDesselecionar.setText("Desselecionar");
    btnDesselecionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnDesselecionarActionPerformed(evt);
      }
    });

    btnApagar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/apag.png"))); // NOI18N
    btnApagar.setText("Apagar");
    btnApagar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnApagarActionPerformed(evt);
      }
    });

    btnMover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/Move.png"))); // NOI18N
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

    btnRotacionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/rota.png"))); // NOI18N
    btnRotacionar.setText("Rotacionar");
    btnRotacionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRotacionarActionPerformed(evt);
      }
    });

    btnRedimensionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/redm.png"))); // NOI18N
    btnRedimensionar.setText("Redimensionar");
    btnRedimensionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRedimensionarActionPerformed(evt);
      }
    });

    pnlPropriedades.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Propriedades"));

    jLabel1.setText("Cor");

    btnCor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/corsel.png"))); // NOI18N
    btnCor.setText("Alterar cor");
    btnCor.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCorActionPerformed(evt);
      }
    });

    pnlCor.setPreferredSize(new java.awt.Dimension(20, 12));

    javax.swing.GroupLayout pnlCorLayout = new javax.swing.GroupLayout(pnlCor);
    pnlCor.setLayout(pnlCorLayout);
    pnlCorLayout.setHorizontalGroup(
      pnlCorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 20, Short.MAX_VALUE)
    );
    pnlCorLayout.setVerticalGroup(
      pnlCorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 12, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout pnlPropriedadesLayout = new javax.swing.GroupLayout(pnlPropriedades);
    pnlPropriedades.setLayout(pnlPropriedadesLayout);
    pnlPropriedadesLayout.setHorizontalGroup(
      pnlPropriedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnlPropriedadesLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(pnlPropriedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(btnCor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(pnlPropriedadesLayout.createSequentialGroup()
            .addGap(21, 21, 21)
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(pnlCor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    pnlPropriedadesLayout.setVerticalGroup(
      pnlPropriedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnlPropriedadesLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(pnlPropriedadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel1)
          .addComponent(pnlCor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnCor)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout pnlObjetosLayout = new javax.swing.GroupLayout(pnlObjetos);
    pnlObjetos.setLayout(pnlObjetosLayout);
    pnlObjetosLayout.setHorizontalGroup(
      pnlObjetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnlObjetosLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(pnlObjetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(btnAdicionar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnSelecionar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnDesselecionar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnApagar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnMover, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnRotacionar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnRedimensionar, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
          .addComponent(pnlPropriedades, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    pnlObjetosLayout.setVerticalGroup(
      pnlObjetosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnlObjetosLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(btnAdicionar)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnSelecionar)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnDesselecionar)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnApagar)
        .addGap(18, 18, 18)
        .addComponent(btnMover)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnRotacionar)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnRedimensionar)
        .addGap(18, 18, 18)
        .addComponent(pnlPropriedades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(264, Short.MAX_VALUE))
    );

    pnlMenus.addTab("Objetos", pnlObjetos);

    javax.swing.GroupLayout pnlAmbienteLayout = new javax.swing.GroupLayout(pnlAmbiente);
    pnlAmbiente.setLayout(pnlAmbienteLayout);
    pnlAmbienteLayout.setHorizontalGroup(
      pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 175, Short.MAX_VALUE)
    );
    pnlAmbienteLayout.setVerticalGroup(
      pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 588, Short.MAX_VALUE)
    );

    pnlMenus.addTab("Ambiente", pnlAmbiente);

    pnlFrente.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Frente"));
    pnlFrente.setPreferredSize(new java.awt.Dimension(350, 276));

    pnlFrenteI.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        pnlFrenteIMouseMoved(evt);
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
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 221, Short.MAX_VALUE)
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
      public void mousePressed(java.awt.event.MouseEvent evt) {
        pnlLadoIMousePressed(evt);
      }
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
    itemNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/novo.png"))); // NOI18N
    itemNovo.setText("Novo");
    itemNovo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemNovoActionPerformed(evt);
      }
    });
    menuArquivo.add(itemNovo);

    itemAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
    itemAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/abre.png"))); // NOI18N
    itemAbrir.setText("Abrir");
    itemAbrir.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemAbrirActionPerformed(evt);
      }
    });
    menuArquivo.add(itemAbrir);

    itemSalvar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
    itemSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/salva.png"))); // NOI18N
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
    itemAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/interr.png"))); // NOI18N
    itemAjuda.setText("Tópicos de ajuda");
    itemAjuda.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        itemAjudaActionPerformed(evt);
      }
    });
    menuAjuda.add(itemAjuda);
    menuAjuda.add(jSeparator1);

    itemSobre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/_sobreic.png"))); // NOI18N
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
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
          .addComponent(pnlMenus, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
    if (!Obj.isEmpty()) {
      Object[] options = {"Sim", "Não", "Cancelar"};
      int n = JOptionPane.showOptionDialog(this, "Há objetos não salvos, deseja salvá-los?",
              "Cena existente", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
              options, options[2]);
      if (n == 0) { //Se o usuario quiser salvar
        itemSalvarActionPerformed(null);
        LimpaTudo();
      } else if (n == 1) { //Se nao quiser
        LimpaTudo();
      } //Qualquer outra coisa cancela
    } else { //Mesmo que Obj esteja vazio, pode ter algum lixo em algum outro lugar
      LimpaTudo();
    }
  }//GEN-LAST:event_itemNovoActionPerformed

  private void itemAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAbrirActionPerformed
    fc = new JFileChooser();
    double x, y, z;
    short s, e, l, ri;
    int returnVal = fc.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) { //FAZER Adaptar para novo modo de leitura e gravacao
      File file = fc.getSelectedFile();
      fileName = file.toString();
      if (!file.canRead()) {
        JOptionPane.showMessageDialog(this, "Nao foi possivel ler o arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        fileName = "";
        return;
      }
      try {
        DataInputStream entr = new DataInputStream(new FileInputStream(fileName));
        cabecalho = entr.readByte();
        LimpaTudo();
        if ((cabecalho & 8) == 0) { //Tentando ler arquivo que nao e de cena
          JOptionPane.showMessageDialog(this, "O arquivo nao e um arquivo de cena, verifique", "Erro", JOptionPane.ERROR_MESSAGE);
          fileName = "";
          return;
        }
        if ((cabecalho & 16) == 0) { //So sei ler a versao 1 do arquivo
          JOptionPane.showMessageDialog(this, "Versao de arquivo de cena nao suportado por esta versao, espera-se a versao: 1", "Erro", JOptionPane.ERROR_MESSAGE);
          fileName = "";
          return;
        }
        while (entr.available() > 0) { //Le
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
            r = entr.readByte();
            ri = entr.readShort();
            if (r == 0) {
              tmp.arrFace.add(new Face(ri));
            } else {
              tmp.arrFace.get(tmp.arrFace.size() - 1).fAresta.add((int) ri);
            }
          }
        }
      } catch (FileNotFoundException ex) {
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Nao foi possivel ler o arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        LimpaTudo();
        return;
      } catch (IOException ex) {
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Erro generico de leitura", "Erro", JOptionPane.ERROR_MESSAGE);
        LimpaTudo();
        return;
      }
    }
    //Duas vezes porque so uma as vezes dava problema (eu tambem nao gosto)
    LimpaPaineis();
    PintaTudo();
    LimpaPaineis();
    PintaTudo();
  }//GEN-LAST:event_itemAbrirActionPerformed

  private void btnMoverMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMoverMouseClicked
    if (btnMover.isSelected()) {
      //System.out.println("Policia");
    } else {
      //System.out.println("Juiz");
    }
  }//GEN-LAST:event_btnMoverMouseClicked

  private void itemSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSalvarActionPerformed
    int returnVal, Amn = 0;
    if (fileName.isEmpty()) {
      fc = new JFileChooser();
      returnVal = fc.showSaveDialog(this); //FAZER salvar o nome do arquivo se lido ou ja salvo para sobrescrever sem perguntar
      if (returnVal == JFileChooser.APPROVE_OPTION) { //FAZER Adaptar para novo modo de leitura e gravacao
        File file = fc.getSelectedFile();
        fileName = file.toString() + ".acr"; //Arquivo CGRev (Perfil, cena)
        Amn = 1;
      }
    } else if (fileName.contains(".acr")) {
      Amn = 1;
    }
    if (Amn == 1) {
      cabecalho = (byte) VERSAO_CENA;
      cabecalho += 8; //Marcador de "Cena"
      try {
        try (DataOutputStream said = new DataOutputStream(new FileOutputStream(fileName))) {
          said.writeByte(cabecalho);
          for (Objeto ob : Obj) {
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
          }
        }
      } catch (FileNotFoundException ex) {
        JOptionPane.showMessageDialog(this, "Erro de arquivo nao encontrado: " + ex.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Erro generico de escrita: " + ex.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
      }
    }
  }//GEN-LAST:event_itemSalvarActionPerformed

  private void btnMoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoverActionPerformed
    if (btnMover.isSelected()) {
      Per = 1;
      btnRedimensionar.setSelected(false);
      btnRotacionar.setSelected(false);
    } else {
      Per = 0;
      ObSel = -1;
    }
  }//GEN-LAST:event_btnMoverActionPerformed

  private void btnRotacionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRotacionarActionPerformed
    if (btnRotacionar.isSelected()) {
      Per = 2;
      btnRedimensionar.setSelected(false);
      btnMover.setSelected(false);
    } else {
      Per = 0;
      ObSel = -1;
    }
  }//GEN-LAST:event_btnRotacionarActionPerformed

  private void btnRedimensionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedimensionarActionPerformed
    if (btnRedimensionar.isSelected()) {
      Per = 3;
      btnMover.setSelected(false);
      btnRotacionar.setSelected(false);
    } else {
      Per = 0;
      ObSel = -1;
    }
  }//GEN-LAST:event_btnRedimensionarActionPerformed

  private void btnDesselecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesselecionarActionPerformed
    Per = 0;
    btnMover.setSelected(false);
    btnRotacionar.setSelected(false);
    btnRedimensionar.setSelected(false);
    ObSel = -1;
    btnCor.setEnabled(false);
  }//GEN-LAST:event_btnDesselecionarActionPerformed

  private void itemAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAjudaActionPerformed
    this.setEnabled(false);
    new Ajuda(this, 1).setVisible(true);
  }//GEN-LAST:event_itemAjudaActionPerformed

  private void pnlLadoIMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLadoIMouseReleased
    if (ObSel > -1) { //Se ja tiver algum objeto selecionado
      if (Per == 1) { //Mover
        //
      } else if (Per == 2) { //Redimensionar
        //
      } else if (Per == 3) { //Rotacionar
        //
      } else if (Per == 4) { //Selecionar
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

  private void pnlLadoIMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLadoIMousePressed

  }//GEN-LAST:event_pnlLadoIMousePressed

  private void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarActionPerformed
    if (btnSelecionar.isSelected()) {
      Per = 4;
    } else {
      Per = 0;
    }
  }//GEN-LAST:event_btnSelecionarActionPerformed

  private void btnApagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApagarActionPerformed
    if (ObSel > -1) { //Alguem selecionado
      Obj.remove(ObSel); //Remove sem perguntar
    }
  }//GEN-LAST:event_btnApagarActionPerformed

  private void btnCorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCorActionPerformed
    this.setEnabled(false);
    new SeletorCor(this).setVisible(true);
  }//GEN-LAST:event_btnCorActionPerformed

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
  private javax.swing.JButton btnApagar;
  public  javax.swing.JButton btnCor;
  private javax.swing.JButton btnDesselecionar;
  private javax.swing.JToggleButton btnMover;
  private javax.swing.JToggleButton btnRedimensionar;
  private javax.swing.JToggleButton btnRotacionar;
  private javax.swing.JToggleButton btnSelecionar;
  private javax.swing.JTextField fX;
  private javax.swing.JTextField fY;
  private javax.swing.JMenuItem itemAbrir;
  private javax.swing.JMenuItem itemAjuda;
  private javax.swing.JMenuItem itemNovo;
  private javax.swing.JMenuItem itemSalvar;
  private javax.swing.JMenuItem itemSalvarComo;
  private javax.swing.JMenuItem itemSobre;
  private javax.swing.JLabel jLabel1;
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
  private javax.swing.JPanel pnlCor;
  private javax.swing.JPanel pnlFrente;
  private javax.swing.JPanel pnlFrenteI;
  private javax.swing.JPanel pnlLado;
  private javax.swing.JPanel pnlLadoI;
  private javax.swing.JTabbedPane pnlMenus;
  private javax.swing.JPanel pnlObjetos;
  private javax.swing.JPanel pnlPerspectiva;
  private javax.swing.JPanel pnlPerspectivaI;
  private javax.swing.JPanel pnlPropriedades;
  private javax.swing.JPanel pnlTopo;
  private javax.swing.JPanel pnlTopoI;
  private javax.swing.JTextField tX;
  private javax.swing.JTextField tY;
  // End of variables declaration//GEN-END:variables
}
