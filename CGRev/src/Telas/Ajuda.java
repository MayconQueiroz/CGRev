/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Telas;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeCellRenderer;
import static revcg.RevCG.ErroPadrao;

/**
 * Fornece topicos de ajuda e imagens ao usuario
 * 
 * @author Maycon
 */
public class Ajuda extends javax.swing.JFrame {

  /**
   * Variaveis Locais
   */
  public Principal Pr;
  public Perfil Pe;
  public int Cod = 0;
  public DefaultListModel dl = new DefaultListModel();
  
  /**
   * Creates new form Ajuda
   */
  public Ajuda() {
    initComponents();
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    setResizable(false); //Nao deixa redimensionar a janela
    setIconImage(new ImageIcon(ClassLoader.getSystemResource("Icones/Ajuda.png")).getImage());
    lstTopicos.setRootVisible(false);
    DefaultTreeCellRenderer a = new DefaultTreeCellRenderer();
    ImageIcon leafIcon = new ImageIcon(ClassLoader.getSystemResource("Icones/blackbox.png"));
    a.setOpenIcon(leafIcon);
    a.setClosedIcon(leafIcon);
    lstTopicos.setCellRenderer(a);
  }
  
  /**
   * Construtor da ajuda que recebe as opcoes
   * @param P : Tela Principal
   * @param C : Codigo da tela que esta chamando
   */
  public Ajuda(Principal P, int C){
    this();
    ErrosIniciais();
    Pr = P;
    Pe = null;
    Cod = C;
    PoeAjuda();
  }
  
  /**
   * Construtor da ajuda que recebe as opcoes
   * @param P : Tela de perfil
   * @param C : Codigo da tela que esta chamando
   */
  public Ajuda(Perfil P, int C){
    this();
    Pe = P;
    Pr = null;
    Cod = C;
    PoeAjuda();
  }
  
  /**
   * Escreve a ajuda
   */
  public void PoeAjuda(){
    if (Cod == 0){
      dl.addElement("Ajuda não implementada");
    } else if (Cod == 1){ //Principal
      dl.addElement("");
    } else if (Cod == 2){ //Perfil
      dl.addElement("");
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane2 = new javax.swing.JScrollPane();
    txtInstrucoes = new javax.swing.JTextArea();
    btnAnterior = new javax.swing.JButton();
    btnProximo = new javax.swing.JButton();
    pnlImagem = new javax.swing.JPanel();
    jScrollPane3 = new javax.swing.JScrollPane();
    lstTopicos = new javax.swing.JTree();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosed(java.awt.event.WindowEvent evt) {
        formWindowClosed(evt);
      }
    });

    txtInstrucoes.setColumns(20);
    txtInstrucoes.setRows(5);
    jScrollPane2.setViewportView(txtInstrucoes);

    btnAnterior.setText("Anterior");

    btnProximo.setText("Próximo");

    javax.swing.GroupLayout pnlImagemLayout = new javax.swing.GroupLayout(pnlImagem);
    pnlImagem.setLayout(pnlImagemLayout);
    pnlImagemLayout.setHorizontalGroup(
      pnlImagemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 518, Short.MAX_VALUE)
    );
    pnlImagemLayout.setVerticalGroup(
      pnlImagemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 380, Short.MAX_VALUE)
    );

    javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
    lstTopicos.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
    jScrollPane3.setViewportView(lstTopicos);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnProximo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(pnlImagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane3)
          .addComponent(pnlImagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jScrollPane2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(btnAnterior)
              .addComponent(btnProximo))))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    if (Pe == null){
      Pr.setEnabled(true);
      Pr.requestFocus(); //Traz o foco para tela anterior
      Pr.PintaTudo();
    } else if (Pr == null){
      Pe.setEnabled(true);
      Pe.requestFocus(); //Traz o foco para tela anterior
      Pe.DesenhaPerfil();
    } else {
      ErroPadrao();
    }
  }//GEN-LAST:event_formWindowClosed

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
        new Ajuda().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnAnterior;
  private javax.swing.JButton btnProximo;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JTree lstTopicos;
  private javax.swing.JPanel pnlImagem;
  private javax.swing.JTextArea txtInstrucoes;
  // End of variables declaration//GEN-END:variables
}
