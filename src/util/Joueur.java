package util;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class Joueur extends Entite{
    String couleur;
    String statut;
    boolean gardien=false;
    double partie;

    public Joueur(Point position, String couleur, Rect dimension) {
        super(position, dimension);
        this.couleur = couleur;
        this.statut="";
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public void setStatut(String statut){
        this.statut=statut;
    }

    public String getStatut(){
        return this.statut;
    }



    public double getPartie() {
        return partie;
    }

    public void setPartie(double partie) {
        this.partie = partie;
    }

    public boolean isGardien() {
        return gardien;
    }

    public void setGardien(boolean gardien) {
        this.gardien = gardien;
    }

    // public double getBordGauche() {
    //     return this.position.x - (this.dimension.width / 2.0);
    // }
    
    // public double getBordDroit() {
    //     return this.position.x + (this.dimension.width / 2.0);
    // }

    public double getBordGauche() {
        return this.position.x ;
    }
    
    public double getBordDroit() {
        return this.position.x + (this.dimension.width/2);
    }
    
}
