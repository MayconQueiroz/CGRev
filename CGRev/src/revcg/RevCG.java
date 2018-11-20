/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package revcg;

import Telas.*;
import java.awt.Color;
import javax.swing.JOptionPane;

/**
 *
 * @author Maycon
 */
public class RevCG {

  public static Principal P;
  public static byte VERSAO_PERFIL = 1;
  public static byte VERSAO_CENA = 32;
  public static Color Sel = Color.BLUE; //Cor de selecao
  public static short PLargura = 340;
  public static short PAltura = 255;
  public static Color CFundo = new Color(240, 240, 240);

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    P = new Principal();
    P.setVisible(true);
  }

  public static void ErroPadrao() {
    JOptionPane.showMessageDialog(P, "I'm sorry Dave, I'm afraid I can't do that", "HAL 9000 says", JOptionPane.ERROR_MESSAGE);
    System.exit(-1);
  }
}
