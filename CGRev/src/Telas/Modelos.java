package Telas;

import Objetos.Aresta;
import Objetos.Ponto;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import static java.lang.Math.round;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Fornece modelos pre-configurados de perfis para escolha do usuario
 * 
 * @author Maycon
 */
public class Modelos extends javax.swing.JFrame {

  /**
   * Variaveis publicas
   */
  public Perfil P;
  public ArrayList<Ponto> arPonto;
  public Graphics De; //de Desenho
  public ArrayList<Aresta> arAresta;
  public DefaultListModel dl = new DefaultListModel();
  public Ponto[] BP;
  /**
   * Calcula Bezier para os pontos das figuras que usam aproximacao curva
   */
  
  //Ponto especial para Bezier
  Ponto Slc = new Ponto();
  
  /**
   * Calcula as arestas de bezier
   * 
   * @param Seg Quantidade de segmentos da curva
   */
  public void CalculaBezier(int Bseg) {
    double Bau = (double) 1 / Bseg;
    double Bau2 = Bau;
    for (int i = 1; i < Bseg; i++) {
      DeCasteljau(Bau2);
      Bau2 += Bau;
      arPonto.add(new Ponto(Slc));
    }
    arPonto.add(new Ponto(BP[3]));
    System.out.println("Bseg = " + Bseg);
    for (Ponto t : arPonto){
      System.out.println("P=" + t.toString());
    }
    //DesenhaPerfil();
  }
  
  /**
   * Retorna um ponto da curva de bezier
   *
   * @param t Fator de ponderacao
   */
  public void DeCasteljau(double t) {
    double z1, d3, u2, ut, z12, u23;
    ut = 1 - t;
    z1 = ((ut * BP[0].x) + (t * BP[1].x));
    u2 = ((ut * BP[1].x) + (t * BP[2].x));
    d3 = ((ut * BP[2].x) + (t * BP[3].x));
    z12 = ((ut * z1) + (t * u2));
    u23 = ((ut * u2) + (t * d3));
    Slc.x = (ut * z12) + (t * u23);
    z1 = ((ut * BP[0].y) + (t * BP[1].y));
    u2 = ((ut * BP[1].y) + (t * BP[2].y));
    d3 = ((ut * BP[2].y) + (t * BP[3].y));
    z12 = ((ut * z1) + (t * u2));
    u23 = ((ut * u2) + (t * d3));
    Slc.y = (ut * z12) + (t * u23);
  }
  
  
  /**
   * Creates new form Modelos
   */
  public Modelos() {
    initComponents();
    ErrosIniciais();
    dl.addElement("Prisma regular/Cilindro");
    dl.addElement("Pirâmide/Cone");
    dl.addElement("Copo Falso");
    dl.addElement("Copo Real");
    dl.addElement("Vaso");
    dl.addElement("Esfera");
    dl.addElement("Toróide");
    dl.addElement("Pião");
    dl.addElement("Abajur");
    dl.addElement("Taça");
    dl.addElement("Cálice");
    dl.addElement("Dois Pontos");
    lstModelos.setModel(dl);
    ctrSegmentos.setEnabled(false);
    ctrDistancia.setEnabled(false);
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    setResizable(false); //Nao deixa redimensionar a janela
    arPonto = new ArrayList();
    arAresta = new ArrayList();
    //pnlTempIn.setBackground(Color.LIGHT_GRAY);
    De = pnlTempIn.getGraphics();
    setIconImage(new ImageIcon(ClassLoader.getSystemResource("Icones/Modelos.png")).getImage());
    BP = new Ponto[4];
    BP[0] = new Ponto();
    BP[1] = new Ponto();
    BP[2] = new Ponto();
    BP[3] = new Ponto();
  }

  public Modelos(Perfil P) {
    this();
    this.P = P;
  }

  /**
   * Desenha as linhas do perfil escolhido
   */
  public void DesenhaPerfil() {
    De.clearRect(0, 0, 193, 258);
    for (Aresta a : arAresta) {
      De.drawLine((int) a.i.x, (int) a.i.y, (int) a.f.x, (int) a.f.y);
    }
  }

  /**
   * Reinica Tudo
   */
  public void LimpaTudo() {
    arPonto.clear();
    arAresta.clear();
    De.clearRect(0, 0, 193, 258);
  }

  /**
   * Constroi arestas com base nos pontos ja existentes (De leitura por exemplo)
   */
  public void ConstroiArestas() {
    Ponto Au = null;
    for (Ponto p : arPonto) {
      if (p == arPonto.get(0)) {
        Au = p;
        continue;
      }
      arAresta.add(new Aresta(Au, p));
      Au = p;
    }
  }

  /**
   * Constroi arestas de objetos fechados fora do eixo, assim não repete o
   * ultimo ponto
   */
  public void ConstroiArestasFechado() {
    Ponto Au = null;
    for (Ponto p : arPonto) {
      if (p == arPonto.get(0)) {
        Au = p;
        continue;
      }
      arAresta.add(new Aresta(Au, p));
      Au = p;
    }
    arAresta.add(new Aresta(arPonto.get(arPonto.size() - 1), arPonto.get(0)));
  }

  /**
   * Porque me deu dor de cabeca
   */
  public void DesenhaSelecionado() {
    LimpaTudo();
    if (lstModelos.getSelectedIndex() == 0) { //Cilindro
      ctrSegmentos.setEnabled(false);
      ctrDistancia.setEnabled(false);
      arPonto.add(new Ponto(0.0, 20.0));
      arPonto.add(new Ponto(70.0, 20.0));
      arPonto.add(new Ponto(70.0, 160.0));
      arPonto.add(new Ponto(0.0, 160.0));
      System.out.println("Cilindro");
      ConstroiArestas();
    } else if (lstModelos.getSelectedIndex() == 1) { //Piramide
      ctrSegmentos.setEnabled(false);
      ctrDistancia.setEnabled(false);
      arPonto.add(new Ponto(0.0, 20.0));
      arPonto.add(new Ponto(70.0, 160.0));
      arPonto.add(new Ponto(0.0, 160.0));
      ConstroiArestas();
    } else if (lstModelos.getSelectedIndex() == 2) { //Copo Falso
      ctrSegmentos.setEnabled(false);
      ctrDistancia.setEnabled(false);
      arPonto.add(new Ponto(0.0, 160.0));
      arPonto.add(new Ponto(70.0, 160.0));
      arPonto.add(new Ponto(80.0, 20.0));
      ConstroiArestas();
    } else if (lstModelos.getSelectedIndex() == 3) { //Copo Real
      ctrSegmentos.setEnabled(false);
      ctrDistancia.setEnabled(false);
      arPonto.add(new Ponto(0.0, 160.0));
      arPonto.add(new Ponto(70.0, 160.0));
      arPonto.add(new Ponto(80.0, 20.0));
      arPonto.add(new Ponto(77.0, 20.0));
      arPonto.add(new Ponto(67.0, 157.0));
      arPonto.add(new Ponto(0.0, 157.0));
      ConstroiArestas();
    } else if (lstModelos.getSelectedIndex() == 4) { //Vaso
      ctrSegmentos.setEnabled(true);
      ctrDistancia.setEnabled(false);
      arPonto.add(new Ponto(0.0, 160.0));
      arPonto.add(new Ponto(40.0, 160.0));
      /*BP[0] = new Ponto(40.0, 160.0);
      BP[1] = new Ponto(120.0, 100.0);
      BP[2] = new Ponto(-20.0, 80.0);
      BP[3] = new Ponto(39.0, 20.0);
      String input = (String) ctrSegmentos.getValue().toString();
      int Num = 6;
      if (input.isEmpty()){
        ctrSegmentos.setValue("6");
      } else {
        try {
          Num = Integer.parseInt(input);
          System.out.println("input = " + input);
          System.out.println("Num = " + Num);
        } catch (NumberFormatException | NullPointerException e) {
          JOptionPane.showMessageDialog(this, "Valor informado não é inteiro", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        if (Num < 2) {
          //JOptionPane.showMessageDialog(this, "Valor informado inválido (Deve ser maior que 1)", "Erro" , JOptionPane.ERROR_MESSAGE);
          ctrSegmentos.setValue(2);
          Num = 2;
        } else if (Num > 100) {
          //JOptionPane.showMessageDialog(this, "Mais que 100 pontos provoca repetição", "Erro", JOptionPane.ERROR_MESSAGE);
          ctrSegmentos.setValue(100);
          //Num = 100;
        }
      }
      CalculaBezier(Num);
      System.out.println("Fim Bezier");*/
      arPonto.add(new Ponto(50.0, 155.0));
      arPonto.add(new Ponto(60.0, 140.0));
      arPonto.add(new Ponto(60.0, 115.0));
      arPonto.add(new Ponto(50.0, 95.0));
      arPonto.add(new Ponto(15.0, 65.0));
      arPonto.add(new Ponto(15.0, 45.0));
      arPonto.add(new Ponto(23.0, 33.0));
      arPonto.add(new Ponto(39.0, 20.0));
      ConstroiArestas();
    } else if (lstModelos.getSelectedIndex() == 5) { //Esfera
      ctrSegmentos.setEnabled(true);
      ctrDistancia.setEnabled(false);
      String input = (String) ctrSegmentos.getValue().toString();
      if (input.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nenhum valor informado", "Erro", JOptionPane.ERROR_MESSAGE);
        arPonto.add(new Ponto(0.0, 138.0));
        arPonto.add(new Ponto(40.0, 118.0));
        arPonto.add(new Ponto(40.0, 78.0));
        arPonto.add(new Ponto(0.0, 58.0));
        ctrDistancia.setValue(60);
      } else {
        int Num = 6;
        try {
          Num = Integer.parseInt(input);
        } catch (NumberFormatException | NullPointerException e) {
          JOptionPane.showMessageDialog(this, "Valor informado não é inteiro", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        if (Num < 2) {
          //JOptionPane.showMessageDialog(this, "Valor informado inválido (Deve ser maior que 1)", "Erro" , JOptionPane.ERROR_MESSAGE);
          ctrSegmentos.setValue(2);
          Num = 2;
        } else if (Num > 100) {
          //JOptionPane.showMessageDialog(this, "Mais que 100 pontos provoca repetição", "Erro", JOptionPane.ERROR_MESSAGE);
          ctrSegmentos.setValue(100);
          Num = 100;
        }
        double teta = -1.571;
        double passo = Math.PI / (Num);
        for (int i = 0; i < Num + 1; i++) {
          arPonto.add(new Ponto(Math.cos(teta) * 40, Math.sin(teta) * 40 + 98));
          //System.out.println("x = " + round(Math.cos(teta) * 40) + ", y = " + round(Math.sin(teta) * 40 + 98));
          teta += passo;
        }
        ConstroiArestas();
      }
    } else if (lstModelos.getSelectedIndex() == 6) { //Toroide
      ctrSegmentos.setEnabled(true);
      ctrDistancia.setEnabled(true);
      String input = (String) ctrSegmentos.getValue().toString();
      String input2 = (String) ctrDistancia.getValue().toString();
      if (input.isEmpty() || input2.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nenhum valor informado", "Erro", JOptionPane.ERROR_MESSAGE);
        arPonto.add(new Ponto(128.0, 60.0));
        arPonto.add(new Ponto(98.0, 90.0));
        arPonto.add(new Ponto(68.0, 60.0));
        arPonto.add(new Ponto(98.0, 30.0));
        arPonto.add(new Ponto(128.0, 60.0));
      } else {
        int Num = 6, Dis = 60;
        try {
          Num = Integer.parseInt(input);
        } catch (NumberFormatException | NullPointerException e) {
          JOptionPane.showMessageDialog(this, "Valor de segmentos informado não é inteiro", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        try {
          Dis = Integer.parseInt(input2);
        } catch (NumberFormatException | NullPointerException e) {
          JOptionPane.showMessageDialog(this, "Valor de distancia informada não é inteiro", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        if (Dis < 31 || Dis > 229) {
          //JOptionPane.showMessageDialog(this, "Distancia do eixo fora dos limites = " + Dis, "Erro" , JOptionPane.ERROR_MESSAGE);
          ctrDistancia.setValue(60);
          Dis = 60;
        }
        if (Num < 3) {
          //JOptionPane.showMessageDialog(this, "Valor informado inválido (Deve ser maior que 2)", "Erro" , JOptionPane.ERROR_MESSAGE);
          ctrSegmentos.setValue(3);
          Num = 3;
        } else if (Num > 100) {
          //JOptionPane.showMessageDialog(this, "Mais que 100 pontos provoca repetição", "Erro", JOptionPane.ERROR_MESSAGE);
          ctrSegmentos.setValue(100);
          Num = 100;
        }
        double teta = -1.571;
        double passo = (2*Math.PI) / Num;
        for (int i = 0; i < Num; i++) {
          arPonto.add(new Ponto((Math.cos(teta) * 30 + Dis), Math.sin(teta) * 30 + 98));
          //System.out.println("x = " + round(Math.cos(teta)*30+Dis) + ", y = " + round(Math.sin(teta)*30+98));
          //System.out.println("teta = " + teta);
          teta += passo;
        }
        ConstroiArestasFechado();
      }
    } else if (lstModelos.getSelectedIndex() == 7) { //Piao
      ctrSegmentos.setEnabled(false);
      ctrDistancia.setEnabled(false);
      arPonto.add(new Ponto(0.0, 160.0));
      arPonto.add(new Ponto(3.0, 157.0));
      arPonto.add(new Ponto(3.0, 152.0));
      arPonto.add(new Ponto(55.0, 55.0));
      arPonto.add(new Ponto(40.0, 35.0));
      arPonto.add(new Ponto(15.0, 30.0));
      arPonto.add(new Ponto(15.0, 15.0));
      arPonto.add(new Ponto(0.0, 15.0));
      ConstroiArestas();
    } else if (lstModelos.getSelectedIndex() == 8) { //Abajur
      ctrSegmentos.setEnabled(false);
      ctrDistancia.setEnabled(false);
      arPonto.add(new Ponto(0.0, 160.0));
      arPonto.add(new Ponto(25.0, 160.0));
      arPonto.add(new Ponto(45.0, 125.0));
      arPonto.add(new Ponto(25.0, 92.0));
      arPonto.add(new Ponto(3.0, 92.0));
      arPonto.add(new Ponto(3.0, 53.0)); //Fim caule
      arPonto.add(new Ponto(30.0, 53.0));
      arPonto.add(new Ponto(45.0, 80.0));
      arPonto.add(new Ponto(70.0, 100.0));
      arPonto.add(new Ponto(75.0, 100.0));
      arPonto.add(new Ponto(49.0, 80.0));
      arPonto.add(new Ponto(33.0, 53.0));
      arPonto.add(new Ponto(25.0, 22.0));
      arPonto.add(new Ponto(22.0, 22.0));
      arPonto.add(new Ponto(28.0, 50.0));
      arPonto.add(new Ponto(3.0, 50.0));
      arPonto.add(new Ponto(6.0, 43.0));
      arPonto.add(new Ponto(3.0, 38.0));
      arPonto.add(new Ponto(0.0, 38.0));
      ConstroiArestas();
    } else if (lstModelos.getSelectedIndex() == 9) { //Taca
      ctrSegmentos.setEnabled(false);
      ctrDistancia.setEnabled(false);
      arPonto.add(new Ponto(0.0, 145.0));
      arPonto.add(new Ponto(40.0, 160.0));
      arPonto.add(new Ponto(5.0, 140.0));
      arPonto.add(new Ponto(5.0, 80.0));
      arPonto.add(new Ponto(35.0, 60.0));
      arPonto.add(new Ponto(50.0, 15.0));
      arPonto.add(new Ponto(31.0, 56.0));
      arPonto.add(new Ponto(0.0, 73.0));
      ConstroiArestas();
    } else if (lstModelos.getSelectedIndex() == 10) { //Calice
      ctrSegmentos.setEnabled(false);
      ctrDistancia.setEnabled(false);
      arPonto.add(new Ponto(0.0, 180.0));
      arPonto.add(new Ponto(40.0, 180.0));
      arPonto.add(new Ponto(40.0, 177.0));
      arPonto.add(new Ponto(30.0, 171.0));
      arPonto.add(new Ponto(30.0, 168.0));
      arPonto.add(new Ponto(15.0, 162.0));
      arPonto.add(new Ponto(15.0, 158.0));
      arPonto.add(new Ponto(18.0, 156.0));
      arPonto.add(new Ponto(14.0, 156.0));
      arPonto.add(new Ponto(3.0, 115.0));
      arPonto.add(new Ponto(6.0, 115.0));
      arPonto.add(new Ponto(6.0, 112.0));
      arPonto.add(new Ponto(3.0, 112.0));
      arPonto.add(new Ponto(3.0, 109.0));
      arPonto.add(new Ponto(5.0, 105.0));
      arPonto.add(new Ponto(13.0, 102.0));
      arPonto.add(new Ponto(13.0, 98.0));
      arPonto.add(new Ponto(5.0, 95.0));
      arPonto.add(new Ponto(3.0, 91.0));
      arPonto.add(new Ponto(3.0, 88.0));
      arPonto.add(new Ponto(30.0, 75.0));
      arPonto.add(new Ponto(33.0, 65.0)); //Detalhes
      arPonto.add(new Ponto(31.0, 63.0));
      arPonto.add(new Ponto(34.0, 62.0));
      arPonto.add(new Ponto(32.0, 60.0));
      arPonto.add(new Ponto(35.0, 59.0));
      arPonto.add(new Ponto(33.0, 57.0));
      arPonto.add(new Ponto(36.0, 56.0));
      arPonto.add(new Ponto(34.0, 54.0));
      arPonto.add(new Ponto(37.0, 53.0));
      arPonto.add(new Ponto(40.0, 40.0)); //////
      arPonto.add(new Ponto(35.0, 40.0));
      arPonto.add(new Ponto(42.0, 20.0));
      arPonto.add(new Ponto(28.0, 50.0));
      arPonto.add(new Ponto(21.0, 72.0));
      arPonto.add(new Ponto(0.0, 82.0));
      ConstroiArestas();
    } else if (lstModelos.getSelectedIndex() == 11) { //Dois pontos
      ctrSegmentos.setEnabled(false);
      ctrDistancia.setEnabled(false);
      arPonto.add(new Ponto(30.0, 30.0));
      arPonto.add(new Ponto(140.0, 100.0));
      ConstroiArestas();
    } else {
      LimpaTudo();
    }
    ctrSegmentos.setValue(arAresta.size());
    DesenhaPerfil();
    //pnlTemp.repaint();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jTextField1 = new javax.swing.JTextField();
    jScrollPane1 = new javax.swing.JScrollPane();
    lstModelos = new javax.swing.JList<>();
    pnlTemp = new javax.swing.JPanel();
    pnlTempIn = new javax.swing.JPanel();
    btnSelecionar = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    ctrSegmentos = new javax.swing.JSpinner();
    ctrDistancia = new javax.swing.JSpinner();
    jLabel2 = new javax.swing.JLabel();

    jTextField1.setText("jTextField1");

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Modelos pré-configurados");
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosed(java.awt.event.WindowEvent evt) {
        formWindowClosed(evt);
      }
    });

    lstModelos.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        lstModelosValueChanged(evt);
      }
    });
    jScrollPane1.setViewportView(lstModelos);

    pnlTemp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    pnlTemp.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        pnlTempMouseEntered(evt);
      }
    });

    javax.swing.GroupLayout pnlTempInLayout = new javax.swing.GroupLayout(pnlTempIn);
    pnlTempIn.setLayout(pnlTempInLayout);
    pnlTempInLayout.setHorizontalGroup(
      pnlTempInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 258, Short.MAX_VALUE)
    );
    pnlTempInLayout.setVerticalGroup(
      pnlTempInLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 193, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout pnlTempLayout = new javax.swing.GroupLayout(pnlTemp);
    pnlTemp.setLayout(pnlTempLayout);
    pnlTempLayout.setHorizontalGroup(
      pnlTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(pnlTempIn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    pnlTempLayout.setVerticalGroup(
      pnlTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(pnlTempIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    btnSelecionar.setText("Selecionar");
    btnSelecionar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnSelecionarActionPerformed(evt);
      }
    });

    jLabel1.setText("Segmentos");

    ctrSegmentos.setModel(new javax.swing.SpinnerNumberModel(3, 2, 100, 1));
    ctrSegmentos.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        ctrSegmentosStateChanged(evt);
      }
    });

    ctrDistancia.setModel(new javax.swing.SpinnerNumberModel(60, 31, 229, 1));
    ctrDistancia.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        ctrDistanciaStateChanged(evt);
      }
    });

    jLabel2.setText("Distância");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(pnlTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel1)
              .addComponent(jLabel2))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(ctrDistancia)
              .addComponent(ctrSegmentos, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
          .addComponent(btnSelecionar, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(pnlTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(jLabel1)
              .addComponent(ctrSegmentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(ctrDistancia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2)
          .addComponent(btnSelecionar))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarActionPerformed
    for (Ponto p : arPonto) {
      p.x = round(p.x * 1.538461538461538461); //Proporcao entre as telas
      p.y = round(p.y * 1.538461538461538461);
    }
    P.LimpaTudo();
    P.arrPonto = arPonto;
    P.arrAresta = arAresta;
    P.fechado = (lstModelos.getSelectedIndex() == 6);
    this.dispose();
    P.setEnabled(true);
    P.requestFocus(); //Traz o foco para tela anterior
  }//GEN-LAST:event_btnSelecionarActionPerformed

  private void lstModelosValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstModelosValueChanged
    ctrSegmentos.setValue(3);
    DesenhaSelecionado();
  }//GEN-LAST:event_lstModelosValueChanged

  private void pnlTempMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTempMouseEntered
    DesenhaPerfil();
  }//GEN-LAST:event_pnlTempMouseEntered

  private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    P.setEnabled(true);
    P.requestFocus(); //Traz o foco para tela anterior
  }//GEN-LAST:event_formWindowClosed

  private void ctrSegmentosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ctrSegmentosStateChanged
    DesenhaSelecionado();
  }//GEN-LAST:event_ctrSegmentosStateChanged

  private void ctrDistanciaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ctrDistanciaStateChanged
    DesenhaSelecionado();
  }//GEN-LAST:event_ctrDistanciaStateChanged

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
    
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Windows".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(Perfil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EI = 1;
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(Perfil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EI = 2;
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(Perfil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EI = 3;
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(Perfil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EI = 4;
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new Modelos().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnSelecionar;
  private javax.swing.JSpinner ctrDistancia;
  private javax.swing.JSpinner ctrSegmentos;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTextField jTextField1;
  private javax.swing.JList<String> lstModelos;
  private javax.swing.JPanel pnlTemp;
  private javax.swing.JPanel pnlTempIn;
  // End of variables declaration//GEN-END:variables
}
