package Objetos;

import static revcg.RevCG.*;

/**
 * Classe de estrutura e manipulacao de camera
 * @author Maycon
 */
public class Camera {
  public Ponto C; //Ponto da camera (VRP)
  public Ponto P; //Ponto focal P

  public Camera(Ponto C, Ponto P) {
    if (C == null || P == null){
      ErroPadrao();
    }
    this.C = C;
    this.P = P;
  }

  public Camera() {
    C = new Ponto();
    P = new Ponto();
  }
}
