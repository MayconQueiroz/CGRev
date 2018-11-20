package Telas;

import Objetos.*;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
  public ArrayList<Objeto> Obj; //Objetos em cena
  public Graphics DT; //Topo
  public Graphics DF; //Frente
  public Graphics DL; //Lado
  public Graphics DP; //Pers
  public final int mx = 170; //Meio em x
  public final int my = 127; //Meio em y
  public int ObSel = -1; //Objeto selecionado (posicao no array)
  public Camera VL, VF, VT, VP; //4 Cameras
  public boolean btnCorEnabled; //Boolean para habilitar (ou desabilitar) o botao de selecao de cor
  public Ponto Clique; //Ponto de clique
  public Ponto Luz; //Ponto de fonte luminosa

  /**
   * Variaveis Locais Globais
   */
  byte cabecalho; //Byte de cabecalho do arquivo (Documentar melhor uso dos bits)
  byte Per = 0; //Operacao (Translacao, rotacao ou escala)
  byte Visual = 0; //Modo de visualizacao
  JFileChooser fc = new JFileChooser(); //Instancia do filechooser para salvar e abrir
  double EL = 1, ET = 1, EP = 1, EF = 1;
  double A1, A2, A3; //Auxiliares gerais
  String fileName = new String(); //Nome do arquivo aberto/recem salvo
  boolean salvo = false; //Indica se a cena ja esta salva

  Objeto o; //Objeto auxiliar (Declaracao para evitar mau uso de memoria)

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
    if (dim.width < 1024 || dim.height < 768){
      JOptionPane.showMessageDialog(this, "A resolucao da sua tela e inferior ao minimo recomendado para execucao deste programa (e isso e so um aviso)", "Aviso", JOptionPane.WARNING_MESSAGE);
    }
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
    Luz = new Ponto();
    setIconImage(new ImageIcon(ClassLoader.getSystemResource("Icones/Principal.png")).getImage());
    VL = new Camera(new Ponto(-20, 0, 0), new Ponto(0, 0, 0), new Ponto(0, 1, 0), 340, 255, -170, 170, -127, 128, 0.5, true);
    VF = new Camera(new Ponto(0, 0, 20), new Ponto(0, 0, 0), new Ponto(0, 1, 0), 340, 255, -170, 170, -127, 128, 0.5, true);
    VT = new Camera(new Ponto(0, -20, 0), new Ponto(0, 0, 0), new Ponto(0, 0, 1), 340, 255, -170, 170, -127, 128, 0.5, true);
    VP = new Camera(new Ponto(-100, -100, -100), new Ponto(0, 0, 0), new Ponto(0, 1, 0), 340, 255, -170, 170, -127, 128, 0.5, false);
    Clique = new Ponto();
    btnWireFrame.setSelected(true);
    vrpX.setText("" + VP.VRP.x);
    vrpY.setText("" + VP.VRP.y);
    vrpZ.setText("" + VP.VRP.z);
    ppX.setText("" + VP.P.x);
    ppY.setText("" + VP.P.y);
    ppZ.setText("" + VP.P.z);
    luzX.setText("" + Luz.x);
    luzY.setText("" + Luz.y);
    luzZ.setText("" + Luz.z);
  }

  /**
   * Atualiza todas as cameras e os objetos
   */
  public void AtualizaTudo() {
    for (Objeto u : Obj) { //Atualiza Centroides (vai que)
      u.CalculaCentro();
    }
    VL.AtualizaCamera(Obj);
    VT.AtualizaCamera(Obj);
    VF.AtualizaCamera(Obj);
    VP.AtualizaCamera(Obj);
  }

  /**
   * Limpa tudo, tudo mesmo
   */
  public void LimpaTudo() {
    LimpaPaineis();
    Obj.clear();
    VL.AtualizaCamera(Obj);
    VT.AtualizaCamera(Obj);
    VF.AtualizaCamera(Obj);
    VP.AtualizaCamera(Obj);
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
    PintaLado();
    PintaTopo();
    PintaFrente();
    PintaPerspectiva();
  }

  /**
   * Pinta a vista lateral
   */
  public void PintaLado() {
    VL.AtualizaVisao((byte) 0, ObSel);
    DL.drawImage(VL.IBuffer, 0, 0, null);
  }

  /**
   * Pinta a vista lateral
   */
  public void PintaTopo() {
    VT.AtualizaVisao((byte) 0, ObSel);
    DT.drawImage(VT.IBuffer, 0, 0, null);
  }

  /**
   * Pinta a vista lateral
   */
  public void PintaFrente() {
    VF.AtualizaVisao((byte) 0, ObSel);
    DF.drawImage(VF.IBuffer, 0, 0, null);
  }

  /**
   * Pinta o painel de perspectiva
   */
  public void PintaPerspectiva() {
    VP.AtualizaVisao((byte) 0, ObSel);
    DP.drawImage(VP.IBuffer, 0, 0, null);
  }

  /**
   * Seleciona algum dos objetos dependendo da posicao que a tela foi clicada
   * @param xl Coordenada x
   * @param yl Coordenada y
   * @param zl Coordenada z
   * @param Who Quem esta chamando (qual vista) 0 - Vista Frontal - y e x 
   * 1 - Vista Lateral - y e z; 2 - Vista Topo - x e z
   */
  public void SelecionaAlguem(double xl, double yl, double zl, byte Who) {
    int Prox = ObSel == 0 ? 1 : 0; //Indice do objeto "selecionado ate o momento"
    if (Obj.size() == 1){
      ObSel = 0;
      return;
    }
    Ponto Cl = new Ponto(xl, yl, zl); //Ponto para calculo da distancia
    if (Who == 0) {
      Cl.x = (Cl.x * EL) - (VL.Umax / 2);
      Cl.y = (Cl.y * EL) - (VL.Vmax / 2);
      A1 = Cl.calculaDistancia(VL.obj.get(Prox).C);
      for (int i = 0; i < VL.obj.size(); i++) {
        A2 = Cl.calculaDistancia(VL.obj.get(i).C);
        if ((A2 < A1) && (i != ObSel)) {
          A1 = A2;
          Prox = i;
        }
      }
      ObSel = Prox;
    } else if (Who == 1) {
      Cl.z = (Cl.z * EL) - (VL.Umax / 2);
      Cl.y = (Cl.y * EL) - (VL.Vmax / 2);
      A1 = Cl.calculaDistancia(VL.obj.get(Prox).C);
      for (int i = 0; i < VL.obj.size(); i++) {
        A2 = Cl.calculaDistancia(VL.obj.get(i).C);
        if ((A2 < A1) && (i != ObSel)) {
          A1 = A2;
          Prox = i;
        }
      }
      ObSel = Prox;
    } else if (Who == 2) {
      Cl.x = (Cl.x * EL) - (VL.Umax / 2);
      Cl.z = (Cl.z * EL) - (VL.Vmax / 2);
      A1 = Cl.calculaDistancia(VL.obj.get(Prox).C);
      for (int i = 0; i < VL.obj.size(); i++) {
        A2 = Cl.calculaDistancia(VL.obj.get(i).C);
        if (A2 < A1) {
          A1 = A2;
          Prox = i;
        }
      }
      ObSel = Prox;
    } else {
      ErroPadrao();
    }
    /*if (Per == 4) {
      Per = 0;
    }
    btnSelecionar.setSelected(false);*/
  }

  public void TiraOp() {
    Per = 0;
    btnSelecionar.setSelected(false);
    btnMover.setSelected(false);
    btnRotacionar.setSelected(false);
    btnRedimensionar.setSelected(false);
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
    escTopo = new javax.swing.JSpinner();
    jLabel4 = new javax.swing.JLabel();
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
    btnRedesenhar = new javax.swing.JButton();
    btnWireFrame = new javax.swing.JToggleButton();
    btnOcultacaoDeLinhas = new javax.swing.JToggleButton();
    btnConstante = new javax.swing.JToggleButton();
    btnGouraud = new javax.swing.JToggleButton();
    jLabel2 = new javax.swing.JLabel();
    vrpX = new javax.swing.JTextField();
    jLabel7 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    vrpY = new javax.swing.JTextField();
    vrpZ = new javax.swing.JTextField();
    jLabel9 = new javax.swing.JLabel();
    ppZ = new javax.swing.JTextField();
    jLabel10 = new javax.swing.JLabel();
    jLabel11 = new javax.swing.JLabel();
    ppY = new javax.swing.JTextField();
    ppX = new javax.swing.JTextField();
    jLabel12 = new javax.swing.JLabel();
    jLabel13 = new javax.swing.JLabel();
    jLabel14 = new javax.swing.JLabel();
    luzZ = new javax.swing.JTextField();
    jLabel15 = new javax.swing.JLabel();
    luzY = new javax.swing.JTextField();
    luzX = new javax.swing.JTextField();
    jLabel16 = new javax.swing.JLabel();
    jLabel17 = new javax.swing.JLabel();
    btnAplicar = new javax.swing.JButton();
    pnlFrente = new javax.swing.JPanel();
    pnlFrenteI = new javax.swing.JPanel();
    btnAmpliarFrente = new javax.swing.JButton();
    fX = new javax.swing.JTextField();
    fY = new javax.swing.JTextField();
    jLabel3 = new javax.swing.JLabel();
    escFrente = new javax.swing.JSpinner();
    pnlLado = new javax.swing.JPanel();
    pnlLadoI = new javax.swing.JPanel();
    btnAmpliarLado = new javax.swing.JButton();
    lY = new javax.swing.JTextField();
    lX = new javax.swing.JTextField();
    jLabel6 = new javax.swing.JLabel();
    escLado = new javax.swing.JSpinner();
    pnlPerspectiva = new javax.swing.JPanel();
    pnlPerspectivaI = new javax.swing.JPanel();
    btnAmpliarPerspectiva = new javax.swing.JButton();
    pY = new javax.swing.JTextField();
    pX = new javax.swing.JTextField();
    escPerspectiva = new javax.swing.JSpinner();
    jLabel5 = new javax.swing.JLabel();
    menuBar = new javax.swing.JMenuBar();
    menuArquivo = new javax.swing.JMenu();
    itemNovo = new javax.swing.JMenuItem();
    itemAbrir = new javax.swing.JMenuItem();
    itemSalvar = new javax.swing.JMenuItem();
    itemSalvarComo = new javax.swing.JMenuItem();
    menuEditar = new javax.swing.JMenu();
    ckbProporcionalidade = new javax.swing.JCheckBoxMenuItem();
    menuAjuda = new javax.swing.JMenu();
    itemAjuda = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JPopupMenu.Separator();
    itemSobre = new javax.swing.JMenuItem();

    jMenuItem2.setText("jMenuItem2");

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("CGRev");
    addWindowFocusListener(new java.awt.event.WindowFocusListener() {
      public void windowGainedFocus(java.awt.event.WindowEvent evt) {
        formWindowGainedFocus(evt);
      }
      public void windowLostFocus(java.awt.event.WindowEvent evt) {
      }
    });

    pnlTopo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Topo"));
    pnlTopo.setPreferredSize(new java.awt.Dimension(350, 276));

    pnlTopoI.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent evt) {
        pnlTopoIMouseDragged(evt);
      }
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        pnlTopoIMouseMoved(evt);
      }
    });
    pnlTopoI.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        pnlTopoIMouseWheelMoved(evt);
      }
    });
    pnlTopoI.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        pnlTopoIMousePressed(evt);
      }
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        pnlTopoIMouseReleased(evt);
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

    escTopo.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.001d, null, 0.1d));
    escTopo.setToolTipText("");
    escTopo.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        escTopoStateChanged(evt);
      }
    });

    jLabel4.setText("Escala");

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
        .addComponent(jLabel4)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(escTopo, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
          .addGroup(pnlTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(btnAmpliarTopo)
            .addComponent(escTopo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel4)))
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
          .addComponent(btnRedimensionar, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
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

    btnRedesenhar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icones/edit.png"))); // NOI18N
    btnRedesenhar.setText("Redesenhar");
    btnRedesenhar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnRedesenharActionPerformed(evt);
      }
    });

    btnWireFrame.setText("Wireframe");
    btnWireFrame.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnWireFrameActionPerformed(evt);
      }
    });

    btnOcultacaoDeLinhas.setText("Ocultação de Linhas");
    btnOcultacaoDeLinhas.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnOcultacaoDeLinhasActionPerformed(evt);
      }
    });

    btnConstante.setText("S. Constante");
    btnConstante.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnConstanteActionPerformed(evt);
      }
    });

    btnGouraud.setText("S. Gouraud");
    btnGouraud.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnGouraudActionPerformed(evt);
      }
    });

    jLabel2.setText("VRP");

    jLabel7.setText("x");

    jLabel8.setText("y");

    jLabel9.setText("z");

    jLabel10.setText("z");

    jLabel11.setText("y");

    jLabel12.setText("x");

    jLabel13.setText("P");

    jLabel14.setText("Fonte Luminosa");

    jLabel15.setText("z");

    jLabel16.setText("x");

    jLabel17.setText("y");

    btnAplicar.setText("Aplicar");
    btnAplicar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAplicarActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout pnlAmbienteLayout = new javax.swing.GroupLayout(pnlAmbiente);
    pnlAmbiente.setLayout(pnlAmbienteLayout);
    pnlAmbienteLayout.setHorizontalGroup(
      pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnlAmbienteLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(btnRedesenhar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnWireFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnOcultacaoDeLinhas, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
          .addComponent(btnConstante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(btnGouraud, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(pnlAmbienteLayout.createSequentialGroup()
            .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(pnlAmbienteLayout.createSequentialGroup()
                .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(pnlAmbienteLayout.createSequentialGroup()
                    .addComponent(jLabel7)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(vrpX, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(pnlAmbienteLayout.createSequentialGroup()
                    .addComponent(jLabel8)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(vrpY, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(pnlAmbienteLayout.createSequentialGroup()
                    .addComponent(jLabel9)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(vrpZ, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jLabel13)
                  .addGroup(pnlAmbienteLayout.createSequentialGroup()
                    .addComponent(jLabel12)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(ppX, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(pnlAmbienteLayout.createSequentialGroup()
                    .addComponent(jLabel11)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(ppY, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(pnlAmbienteLayout.createSequentialGroup()
                    .addComponent(jLabel10)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(ppZ, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
              .addComponent(jLabel14)
              .addGroup(pnlAmbienteLayout.createSequentialGroup()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(luzX, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(pnlAmbienteLayout.createSequentialGroup()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(luzY, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(pnlAmbienteLayout.createSequentialGroup()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(luzZ, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGap(0, 0, Short.MAX_VALUE))
          .addComponent(btnAplicar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    pnlAmbienteLayout.setVerticalGroup(
      pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(pnlAmbienteLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(btnRedesenhar)
        .addGap(18, 18, 18)
        .addComponent(btnWireFrame)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnOcultacaoDeLinhas)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnConstante)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnGouraud)
        .addGap(18, 18, 18)
        .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(jLabel13))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(pnlAmbienteLayout.createSequentialGroup()
            .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(vrpX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel7))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel8)
              .addComponent(vrpY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel9)
              .addComponent(vrpZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(pnlAmbienteLayout.createSequentialGroup()
            .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(ppX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel12))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel11)
              .addComponent(ppY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel10)
              .addComponent(ppZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jLabel14)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(luzX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel16))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel17)
          .addComponent(luzY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(pnlAmbienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel15)
          .addComponent(luzZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(btnAplicar)
        .addContainerGap(179, Short.MAX_VALUE))
    );

    pnlMenus.addTab("Ambiente", pnlAmbiente);

    pnlFrente.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Frente"));
    pnlFrente.setPreferredSize(new java.awt.Dimension(350, 276));

    pnlFrenteI.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent evt) {
        pnlFrenteIMouseDragged(evt);
      }
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        pnlFrenteIMouseMoved(evt);
      }
    });
    pnlFrenteI.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        pnlFrenteIMouseWheelMoved(evt);
      }
    });
    pnlFrenteI.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        pnlFrenteIMousePressed(evt);
      }
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        pnlFrenteIMouseReleased(evt);
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

    jLabel3.setText("Escala");

    escFrente.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.001d, null, 0.1d));
    escFrente.setToolTipText("");
    escFrente.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        escFrenteStateChanged(evt);
      }
    });

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
        .addComponent(jLabel3)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(escFrente, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnAmpliarFrente)
        .addContainerGap())
    );
    pnlFrenteLayout.setVerticalGroup(
      pnlFrenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFrenteLayout.createSequentialGroup()
        .addGroup(pnlFrenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btnAmpliarFrente)
          .addComponent(fX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(fY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3)
          .addComponent(escFrente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(pnlFrenteI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pnlLado.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Lado"));
    pnlLado.setPreferredSize(new java.awt.Dimension(350, 276));

    pnlLadoI.setPreferredSize(new java.awt.Dimension(340, 255));
    pnlLadoI.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent evt) {
        pnlLadoIMouseDragged(evt);
      }
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        pnlLadoIMouseMoved(evt);
      }
    });
    pnlLadoI.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
      public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
        pnlLadoIMouseWheelMoved(evt);
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

    jLabel6.setText("Escala");

    escLado.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.001d, null, 0.1d));
    escLado.setToolTipText("");
    escLado.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        escLadoStateChanged(evt);
      }
    });

    javax.swing.GroupLayout pnlLadoLayout = new javax.swing.GroupLayout(pnlLado);
    pnlLado.setLayout(pnlLadoLayout);
    pnlLadoLayout.setHorizontalGroup(
      pnlLadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLadoLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lX, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(lY, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
        .addComponent(jLabel6)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(escLado, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
          .addGroup(pnlLadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(jLabel6)
            .addComponent(escLado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    escPerspectiva.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.001d, null, 0.1d));
    escPerspectiva.setToolTipText("");

    jLabel5.setText("Escala");

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
        .addComponent(jLabel5)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(escPerspectiva, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
          .addGroup(pnlPerspectivaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(btnAmpliarPerspectiva)
            .addComponent(escPerspectiva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel5)))
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

    ckbProporcionalidade.setSelected(true);
    ckbProporcionalidade.setText("Manter Proporcionalidade");
    ckbProporcionalidade.setToolTipText("Mantém (ou não) proporcionalidade de escala entre as vistas");
    menuEditar.add(ckbProporcionalidade);

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
    if (!Obj.isEmpty() && !salvo) { //Se nao estiver vazio nem salvo
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
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      fileName = file.toString();
      if (!file.canRead()) {
        JOptionPane.showMessageDialog(this, "Nao foi possivel ler o arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        fileName = "";
        return;
      }
      try {
        ObjectInputStream entr = new ObjectInputStream(new FileInputStream(fileName));
        cabecalho = entr.readByte();
        LimpaTudo();
        if ((cabecalho & 8) == 0) { //Tentando ler arquivo que nao e de cena
          JOptionPane.showMessageDialog(this, "O arquivo nao e um arquivo de cena, verifique", "Erro", JOptionPane.ERROR_MESSAGE);
          fileName = "";
          return;
        }
        if ((cabecalho & 32) == 0) { //So sei ler a versao 1 do arquivo
          JOptionPane.showMessageDialog(this, "Versao de arquivo de cena nao suportado por esta versao, espera-se a versao: 1", "Erro", JOptionPane.ERROR_MESSAGE);
          fileName = "";
          return;
        }
        boolean c = true;
        Objeto pr1 = new Objeto();
        while (c){
          try{
            pr1 = (Objeto) entr.readObject();
          }catch(IOException ex){
            c = false;
          }
          if (c){ //Le objetos ate nao querer mais
            Obj.add(pr1);
          }
        }
      } catch (FileNotFoundException ex) {
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Nao foi possivel ler o arquivo", "Erro", JOptionPane.ERROR_MESSAGE);
        LimpaTudo();
        return;
      } catch (IOException ex) {
        Logger.getLogger(Perfil.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Erro generico de leitura" + ex.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
        LimpaTudo();
        return;
      } catch (ClassNotFoundException ex) {
        Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this, "Erro generico de leitura" + ex.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
        LimpaTudo();
      }
    }
    //Duas vezes porque so uma as vezes dava problema (eu tambem nao gosto)
    LimpaPaineis();
    AtualizaTudo();
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
      returnVal = fc.showSaveDialog(this); //VERIFICAR salvar o nome do arquivo se lido ou ja salvo para sobrescrever sem perguntar
      if (returnVal == JFileChooser.APPROVE_OPTION) {
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
        try (ObjectOutputStream said = new ObjectOutputStream(new FileOutputStream(fileName))) {
          said.writeByte(cabecalho);
          for (Objeto ob : Obj) {
            said.writeObject(ob);
          }
        }
      } catch (FileNotFoundException ex) {
        JOptionPane.showMessageDialog(this, "Erro de arquivo nao encontrado: " + ex.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Erro generico de escrita: " + ex.toString(), "Erro", JOptionPane.ERROR_MESSAGE);
      }
    }
    salvo = true;
  }//GEN-LAST:event_itemSalvarActionPerformed

  private void btnMoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoverActionPerformed
    if (btnMover.isSelected()) {
      Per = 1;
      btnRedimensionar.setSelected(false);
      btnRotacionar.setSelected(false);
      btnSelecionar.setSelected(false);
    } else {
      Per = 0;
      ObSel = -1;
    }
  }//GEN-LAST:event_btnMoverActionPerformed

  private void btnRotacionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRotacionarActionPerformed
    if (btnRotacionar.isSelected()) {
      Per = 3;
      btnRedimensionar.setSelected(false);
      btnMover.setSelected(false);
      btnSelecionar.setSelected(false);
    } else {
      Per = 0;
      ObSel = -1;
    }
  }//GEN-LAST:event_btnRotacionarActionPerformed

  private void btnRedimensionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedimensionarActionPerformed
    if (btnRedimensionar.isSelected()) {
      Per = 2;
      btnMover.setSelected(false);
      btnRotacionar.setSelected(false);
      btnSelecionar.setSelected(false);
    } else {
      Per = 0;
      ObSel = -1;
    }
  }//GEN-LAST:event_btnRedimensionarActionPerformed

  private void btnDesselecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesselecionarActionPerformed
    //Per = 0;
    /*btnMover.setSelected(false);
    btnRotacionar.setSelected(false);
    btnRedimensionar.setSelected(false);
    btnSelecionar.setSelected(false);*/
    ObSel = -1;
    btnCor.setEnabled(false);
  }//GEN-LAST:event_btnDesselecionarActionPerformed

  private void itemAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAjudaActionPerformed
    this.setEnabled(false);
    new Ajuda(this, 1).setVisible(true);
  }//GEN-LAST:event_itemAjudaActionPerformed

  private void pnlLadoIMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLadoIMouseReleased
    if (evt.isPopupTrigger()){
      btnDesselecionarActionPerformed(null);
    } else if (ObSel > -1) { //Se ja tiver algum objeto selecionado
      if (Per == 4) { //Selecionar (Troca a selecao)
        SelecionaAlguem(0, evt.getY(), evt.getX(), (byte) 1);
      }
    } else if (Per != 0) { //Selecionar
      SelecionaAlguem(0, evt.getY(), evt.getX(), (byte) 1);
    }
    PintaTudo();
  }//GEN-LAST:event_pnlLadoIMouseReleased

  private void pnlLadoIMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLadoIMouseMoved
    int Aux = (evt.getY() * -1) + 255;
    lX.setText("" + evt.getX());
    lY.setText("" + Aux);
    PintaTudo();
  }//GEN-LAST:event_pnlLadoIMouseMoved

  private void pnlFrenteIMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlFrenteIMouseMoved
    int Aux = (evt.getY() * -1) + 255;
    fX.setText("" + evt.getX());
    fY.setText("" + Aux);
    PintaTudo();
  }//GEN-LAST:event_pnlFrenteIMouseMoved

  private void pnlTopoIMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopoIMouseMoved
    int Aux = (evt.getY() * -1) + 255;
    tX.setText("" + evt.getX());
    tY.setText("" + Aux);
    PintaTudo();
  }//GEN-LAST:event_pnlTopoIMouseMoved

  private void pnlPerspectivaIMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlPerspectivaIMouseMoved
    int Aux = (evt.getY() * -1) + 255;
    pX.setText("" + evt.getX());
    pY.setText("" + Aux);
    PintaTudo();
  }//GEN-LAST:event_pnlPerspectivaIMouseMoved

  private void pnlLadoIMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLadoIMousePressed
    Clique.z = (evt.getX() / EL) - (VL.Umax / 2);
    Clique.y = (evt.getY() / EL) - (VL.Vmax / 2);
    Clique.x = 0;
  }//GEN-LAST:event_pnlLadoIMousePressed

  private void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarActionPerformed
    if (btnSelecionar.isSelected()) {
      Per = 4;
      btnMover.setSelected(false);
      btnRedimensionar.setSelected(false);
      btnRotacionar.setSelected(false);
    } else {
      Per = 0;
    }
  }//GEN-LAST:event_btnSelecionarActionPerformed

  private void btnApagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApagarActionPerformed
    if (ObSel > -1) { //Alguem selecionado
      Obj.remove(ObSel); //Remove sem perguntar
      ObSel = -1;
      AtualizaTudo();
    }
  }//GEN-LAST:event_btnApagarActionPerformed

  private void btnCorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCorActionPerformed
    this.setEnabled(false);
    new SeletorCor(this).setVisible(true);
  }//GEN-LAST:event_btnCorActionPerformed

  private void btnRedesenharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedesenharActionPerformed
    AtualizaTudo();
    PintaTudo();
  }//GEN-LAST:event_btnRedesenharActionPerformed

  private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
    btnCor.setEnabled(btnCorEnabled);
    VL.AtualizaCamera(Obj);
  }//GEN-LAST:event_formWindowGainedFocus

  private void btnWireFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWireFrameActionPerformed
    //Wireframe
    if (btnWireFrame.isSelected()) {
      btnOcultacaoDeLinhas.setSelected(false);
      btnConstante.setSelected(false);
      btnGouraud.setSelected(false);
      Visual = 0;
    }
  }//GEN-LAST:event_btnWireFrameActionPerformed

  private void btnOcultacaoDeLinhasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOcultacaoDeLinhasActionPerformed
    //Ocultacao de linhas (e faces)
    if (btnOcultacaoDeLinhas.isSelected()) {
      btnWireFrame.setSelected(false);
      btnConstante.setSelected(false);
      btnGouraud.setSelected(false);
      Visual = 1;
    }
  }//GEN-LAST:event_btnOcultacaoDeLinhasActionPerformed

  private void btnConstanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConstanteActionPerformed
    //Sombreamento constante
    if (btnConstante.isSelected()) {
      btnOcultacaoDeLinhas.setSelected(false);
      btnWireFrame.setSelected(false);
      btnGouraud.setSelected(false);
      Visual = 2;
    }
  }//GEN-LAST:event_btnConstanteActionPerformed

  private void btnGouraudActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGouraudActionPerformed
    //Sombreamento Gouraud
    if (btnGouraud.isSelected()) {
      btnOcultacaoDeLinhas.setSelected(false);
      btnConstante.setSelected(false);
      btnWireFrame.setSelected(false);
      Visual = 3;
    }
  }//GEN-LAST:event_btnGouraudActionPerformed

  private void escLadoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_escLadoStateChanged
    String input = escLado.getValue().toString();
    EL = 1;
    if (input.isEmpty()) {
      escLado.setValue(1);
    } else {
      try {
        EL = Double.parseDouble(input);
      } catch (NumberFormatException | NullPointerException e) {
        escLado.setValue(1);
      }
    }
    VL.Xmin = -(int) ((PLargura / 2) * EL);
    VL.Xmax = (int) ((PLargura / 2) * EL);
    VL.Ymin = -(int) ((PAltura / 2) * EL);
    VL.Ymax = (int) ((PAltura / 2) * EL);
    if (ckbProporcionalidade.isSelected()) {
      ET = EL;
      EP = EL;
      EF = EL;
      escPerspectiva.setValue(EL);
      VP.Xmin = -(int) ((PLargura / 2) * EL);
      VP.Xmax = (int) ((PLargura / 2) * EL);
      VP.Ymin = -(int) ((PAltura / 2) * EL);
      VP.Ymax = (int) ((PAltura / 2) * EL);
      escTopo.setValue(EL);
      VT.Xmin = -(int) ((PLargura / 2) * EL);
      VT.Xmax = (int) ((PLargura / 2) * EL);
      VT.Ymin = -(int) ((PAltura / 2) * EL);
      VT.Ymax = (int) ((PAltura / 2) * EL);
      escFrente.setValue(EL);
      VF.Xmin = -(int) ((PLargura / 2) * EL);
      VF.Xmax = (int) ((PLargura / 2) * EL);
      VF.Ymin = -(int) ((PAltura / 2) * EL);
      VF.Ymax = (int) ((PAltura / 2) * EL);
      PintaTudo();
    } else {
      PintaLado();
    }
  }//GEN-LAST:event_escLadoStateChanged

  private void escFrenteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_escFrenteStateChanged
    String input = escFrente.getValue().toString();
    EF = 1;
    if (input.isEmpty()) {
      escFrente.setValue(1);
    } else {
      try {
        EF = Double.parseDouble(input);
      } catch (NumberFormatException | NullPointerException e) {
        escFrente.setValue(1);
      }
    }
    VF.Xmin = -(int) ((PLargura / 2) * EF);
    VF.Xmax = (int) ((PLargura / 2) * EF);
    VF.Ymin = -(int) ((PAltura / 2) * EF);
    VF.Ymax = (int) ((PAltura / 2) * EF);
    if (ckbProporcionalidade.isSelected()) {
      ET = EF;
      EP = EF;
      EL = EF;
      escPerspectiva.setValue(EL);
      VP.Xmin = -(int) ((PLargura / 2) * EL);
      VP.Xmax = (int) ((PLargura / 2) * EL);
      VP.Ymin = -(int) ((PAltura / 2) * EL);
      VP.Ymax = (int) ((PAltura / 2) * EL);
      escTopo.setValue(EL);
      VT.Xmin = -(int) ((PLargura / 2) * EL);
      VT.Xmax = (int) ((PLargura / 2) * EL);
      VT.Ymin = -(int) ((PAltura / 2) * EL);
      VT.Ymax = (int) ((PAltura / 2) * EL);
      escLado.setValue(EL);
      VL.Xmin = -(int) ((PLargura / 2) * EL);
      VL.Xmax = (int) ((PLargura / 2) * EL);
      VL.Ymin = -(int) ((PAltura / 2) * EL);
      VL.Ymax = (int) ((PAltura / 2) * EL);
      PintaTudo();
    } else {
      PintaFrente();
    }
  }//GEN-LAST:event_escFrenteStateChanged

  private void escTopoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_escTopoStateChanged
    String input = escTopo.getValue().toString();
    ET = 1;
    if (input.isEmpty()) {
      escTopo.setValue(1);
    } else {
      try {
        ET = Double.parseDouble(input);
      } catch (NumberFormatException | NullPointerException e) {
        escTopo.setValue(1);
      }
    }
    VT.Xmin = -(int) ((PLargura / 2) * ET);
    VT.Xmax = (int) ((PLargura / 2) * ET);
    VT.Ymin = -(int) ((PAltura / 2) * ET);
    VT.Ymax = (int) ((PAltura / 2) * ET);
    if (ckbProporcionalidade.isSelected()) {
      EF = ET;
      EP = ET;
      EL = ET;
      escPerspectiva.setValue(EL);
      VP.Xmin = -(int) ((PLargura / 2) * EL);
      VP.Xmax = (int) ((PLargura / 2) * EL);
      VP.Ymin = -(int) ((PAltura / 2) * EL);
      VP.Ymax = (int) ((PAltura / 2) * EL);
      escFrente.setValue(EL);
      VF.Xmin = -(int) ((PLargura / 2) * EL);
      VF.Xmax = (int) ((PLargura / 2) * EL);
      VF.Ymin = -(int) ((PAltura / 2) * EL);
      VF.Ymax = (int) ((PAltura / 2) * EL);
      escLado.setValue(EL);
      VL.Xmin = -(int) ((PLargura / 2) * EL);
      VL.Xmax = (int) ((PLargura / 2) * EL);
      VL.Ymin = -(int) ((PAltura / 2) * EL);
      VL.Ymax = (int) ((PAltura / 2) * EL);
      PintaTudo();
    } else {
      PintaTopo();
    }
  }//GEN-LAST:event_escTopoStateChanged

  private void pnlLadoIMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLadoIMouseDragged
    if (ObSel > -1) { //Se ja tiver algum objeto selecionado
      Ponto ax = new Ponto(0, (evt.getY() / EL) - (VL.Vmax / 2), (evt.getX() / EL) - (VL.Umax / 2));
      Ponto axx = new Ponto(ax);
      if (Per == 1) { //Mover
        ax.Diferenca(Clique);
        Obj.get(ObSel).C.Soma(ax);
        for (Ponto ink : Obj.get(ObSel).arrPonto) {
          ink.Soma(ax);
        }
        AtualizaTudo();
        PintaTudo();
        Clique = axx;
      } else if (Per == 3) { //Rotacionar
        Obj.get(ObSel).MenosCentro();
        if (Clique.z < ax.z) {
          A1 = -0.1;
        } else {
          A1 = 0.1;
        }
        for (Ponto ink : Obj.get(ObSel).arrPonto) { //Multiplica pontos (decomposto)
          A2 = ink.y;
          ink.y = ink.y * Math.cos(A1) - ink.z * Math.sin(A1);
          ink.z = A2 * Math.sin(A1) + ink.z * Math.cos(A1);
          /*ink.x = (ink.x * Math.cos(A1)) - (ink.y * Math.sin(A1));
          ink.y = (A2 * Math.sin(A1)) + (ink.y * Math.cos(A1));*/
        }
        Obj.get(ObSel).MaisCentro();
        AtualizaTudo();
        PintaTudo();
        Clique = axx;
      }
    }
    AtualizaTudo();
    PintaTudo();
  }//GEN-LAST:event_pnlLadoIMouseDragged

  private void pnlLadoIMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_pnlLadoIMouseWheelMoved
    if (ObSel > -1 && Per == 2) { //Se ja tiver algum objeto selecionado e for redimensionar
      A1 = 1 + (evt.getWheelRotation() * 0.1);
      Obj.get(ObSel).MenosCentro();
      for (Ponto ink : Obj.get(ObSel).arrPonto) { //Multiplica pontos (decomposto)
        ink.x = ink.x * A1;
        ink.y = ink.y * A1;
        ink.z = ink.z * A1;
      }
      Obj.get(ObSel).MaisCentro();
      AtualizaTudo();
      //TiraOp();
    }
    PintaTudo();
  }//GEN-LAST:event_pnlLadoIMouseWheelMoved

  private void pnlFrenteIMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlFrenteIMousePressed
    Clique.x = (evt.getX() / EF) - (VF.Umax / 2);
    Clique.y = (evt.getY() / EF) - (VF.Vmax / 2);
    Clique.z = 0;
  }//GEN-LAST:event_pnlFrenteIMousePressed

  private void pnlFrenteIMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlFrenteIMouseReleased
    if (evt.isPopupTrigger()){
      btnDesselecionarActionPerformed(null);
    } else if (ObSel > -1) { //Se ja tiver algum objeto selecionado
      if (Per == 4) { //Selecionar (Troca a selecao)
        SelecionaAlguem(evt.getX(), evt.getY(), 0, (byte) 0);
      }
    } else if (Per != 0) { //Selecionar
      SelecionaAlguem(evt.getX(), evt.getY(), 0, (byte) 0);
    }
    PintaTudo();
  }//GEN-LAST:event_pnlFrenteIMouseReleased

  private void pnlFrenteIMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlFrenteIMouseDragged
    if (ObSel > -1) { //Se ja tiver algum objeto selecionado
      Ponto ax = new Ponto((evt.getX() / EF) - (VF.Umax / 2), (evt.getY() / EF) - (VF.Vmax / 2), 0);
      Ponto axx = new Ponto(ax);
      if (Per == 1) { //Mover
        ax.Diferenca(Clique);
        Obj.get(ObSel).C.Soma(ax);
        for (Ponto ink : Obj.get(ObSel).arrPonto) {
          ink.Soma(ax);
        }
        AtualizaTudo();
        PintaTudo();
        Clique = axx;
      } else if (Per == 3) { //Rotacionar
        Obj.get(ObSel).MenosCentro();
        if (Clique.x < ax.x) {
          A1 = -0.1;
        } else {
          A1 = 0.1;
        }
        for (Ponto ink : Obj.get(ObSel).arrPonto) { //Multiplica pontos (decomposto)
          A2 = ink.x;
          ink.x = (ink.x * Math.cos(A1)) - (ink.y * Math.sin(A1));
          ink.y = (A2 * Math.sin(A1)) + (ink.y * Math.cos(A1));
        }
        Obj.get(ObSel).MaisCentro();
        AtualizaTudo();
        PintaTudo();
        Clique = axx;
      }
    }
  }//GEN-LAST:event_pnlFrenteIMouseDragged

  private void pnlFrenteIMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_pnlFrenteIMouseWheelMoved
    if (ObSel > -1 && Per == 2) { //Se ja tiver algum objeto selecionado e for redimensionar
      A1 = 1 + (evt.getWheelRotation() * 0.1);
      Obj.get(ObSel).MenosCentro();
      for (Ponto ink : Obj.get(ObSel).arrPonto) { //Multiplica pontos (decomposto)
        ink.x = ink.x * A1;
        ink.y = ink.y * A1;
        ink.z = ink.z * A1;
      }
      Obj.get(ObSel).MaisCentro();
      AtualizaTudo();
      //TiraOp();
    }
    PintaTudo();
  }//GEN-LAST:event_pnlFrenteIMouseWheelMoved

  private void pnlTopoIMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopoIMousePressed
    Clique.x = (evt.getX() / ET) - (VT.Umax / 2);
    Clique.y = 0;
    Clique.z = (evt.getY() / ET) - (VT.Vmax / 2);
  }//GEN-LAST:event_pnlTopoIMousePressed

  private void pnlTopoIMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopoIMouseReleased
    if (evt.isPopupTrigger()){
      btnDesselecionarActionPerformed(null);
    } else if (ObSel > -1) { //Se ja tiver algum objeto selecionado
      if (Per == 4) { //Selecionar (Troca a selecao)
        SelecionaAlguem(evt.getX(), 0, evt.getY(), (byte) 2);
      }
    } else if (Per != 0) { //Selecionar
      SelecionaAlguem(evt.getX(), 0, evt.getY(), (byte) 2);
    }
    PintaTudo();
  }//GEN-LAST:event_pnlTopoIMouseReleased

  private void pnlTopoIMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTopoIMouseDragged
    if (ObSel > -1) { //Se ja tiver algum objeto selecionado
      Ponto ax = new Ponto((evt.getX() / ET) - (VT.Umax / 2), 0, (evt.getY() / ET) - (VT.Vmax / 2));
      Ponto axx = new Ponto(ax);
      if (Per == 1) { //Mover
        ax.Diferenca(Clique);
        Obj.get(ObSel).C.Soma(ax);
        for (Ponto ink : Obj.get(ObSel).arrPonto) {
          ink.Soma(ax);
        }
        AtualizaTudo();
        PintaTudo();
        Clique = axx;
      } else if (Per == 3) { //Rotacionar
        Obj.get(ObSel).MenosCentro();
        if (Clique.x < ax.x) {
          A1 = -0.1;
        } else {
          A1 = 0.1;
        }
        for (Ponto ink : Obj.get(ObSel).arrPonto) { //Multiplica pontos (decomposto)
          A2 = ink.x;
          ink.x = (ink.x * Math.cos(A1)) + (ink.z * Math.sin(A1));
          ink.z = (ink.z * Math.cos(A1)) - (A2 * Math.sin(A1));
        }
        Obj.get(ObSel).MaisCentro();
        AtualizaTudo();
        PintaTudo();
        Clique = axx;
      }
    }
  }//GEN-LAST:event_pnlTopoIMouseDragged

  private void pnlTopoIMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_pnlTopoIMouseWheelMoved
    if (ObSel > -1 && Per == 2) { //Se ja tiver algum objeto selecionado e for redimensionar
      A1 = 1 + (evt.getWheelRotation() * 0.1);
      Obj.get(ObSel).MenosCentro();
      for (Ponto ink : Obj.get(ObSel).arrPonto) { //Multiplica pontos (decomposto)
        ink.x = ink.x * A1;
        ink.y = ink.y * A1;
        ink.z = ink.z * A1;
      }
      Obj.get(ObSel).MaisCentro();
      AtualizaTudo();
      //TiraOp();
    }
    PintaTudo();
  }//GEN-LAST:event_pnlTopoIMouseWheelMoved

  private void btnAplicarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAplicarActionPerformed
    VP.VRP.x = Double.parseDouble(vrpX.getText()); //FAZER Verificacoes
    VP.VRP.y = Double.parseDouble(vrpY.getText());
    VP.VRP.z = Double.parseDouble(vrpZ.getText());
    VP.P.x = Double.parseDouble(ppX.getText());
    VP.P.y = Double.parseDouble(ppY.getText());
    VP.P.z = Double.parseDouble(ppZ.getText());
    Luz.x = Double.parseDouble(luzX.getText());
    Luz.y = Double.parseDouble(luzY.getText());
    Luz.z = Double.parseDouble(luzZ.getText());
    AtualizaTudo();
    PintaTudo();
  }//GEN-LAST:event_btnAplicarActionPerformed

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
  private javax.swing.JButton btnAplicar;
  private javax.swing.JToggleButton btnConstante;
  private javax.swing.JButton btnCor;
  private javax.swing.JButton btnDesselecionar;
  private javax.swing.JToggleButton btnGouraud;
  private javax.swing.JToggleButton btnMover;
  private javax.swing.JToggleButton btnOcultacaoDeLinhas;
  private javax.swing.JButton btnRedesenhar;
  private javax.swing.JToggleButton btnRedimensionar;
  private javax.swing.JToggleButton btnRotacionar;
  private javax.swing.JToggleButton btnSelecionar;
  private javax.swing.JToggleButton btnWireFrame;
  private javax.swing.JCheckBoxMenuItem ckbProporcionalidade;
  private javax.swing.JSpinner escFrente;
  private javax.swing.JSpinner escLado;
  private javax.swing.JSpinner escPerspectiva;
  private javax.swing.JSpinner escTopo;
  private javax.swing.JTextField fX;
  private javax.swing.JTextField fY;
  private javax.swing.JMenuItem itemAbrir;
  private javax.swing.JMenuItem itemAjuda;
  private javax.swing.JMenuItem itemNovo;
  private javax.swing.JMenuItem itemSalvar;
  private javax.swing.JMenuItem itemSalvarComo;
  private javax.swing.JMenuItem itemSobre;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel11;
  private javax.swing.JLabel jLabel12;
  private javax.swing.JLabel jLabel13;
  private javax.swing.JLabel jLabel14;
  private javax.swing.JLabel jLabel15;
  private javax.swing.JLabel jLabel16;
  private javax.swing.JLabel jLabel17;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JPopupMenu.Separator jSeparator1;
  private javax.swing.JTextField lX;
  private javax.swing.JTextField lY;
  private javax.swing.JTextField luzX;
  private javax.swing.JTextField luzY;
  private javax.swing.JTextField luzZ;
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
  private javax.swing.JTextField ppX;
  private javax.swing.JTextField ppY;
  private javax.swing.JTextField ppZ;
  private javax.swing.JTextField tX;
  private javax.swing.JTextField tY;
  private javax.swing.JTextField vrpX;
  private javax.swing.JTextField vrpY;
  private javax.swing.JTextField vrpZ;
  // End of variables declaration//GEN-END:variables
}
