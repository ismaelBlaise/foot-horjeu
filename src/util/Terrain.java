package util;

import org.opencv.core.Point;

public class Terrain {
    
    private final double largeur;  // Largeur du terrain
    private final double longueur; // Longueur du terrain
    private final Point butGauche; // Position du but gauche
    private final Point butDroit;  // Position du but droit
    private final Point centre;    // Point central du terrain
    private final Point ligneCentre; // Ligne du centre

    // Constructeur pour initialiser les dimensions et les points clés du terrain
    public Terrain(double largeur, double longueur) {
        this.largeur = largeur;
        this.longueur = longueur;
        
        // Calcul des points de chaque élément du terrain
        this.butGauche = new Point(0, longueur / 2);  // But à gauche du terrain
        this.butDroit = new Point(largeur, longueur / 2); // But à droite du terrain
        this.centre = new Point(largeur / 2, longueur / 2); // Centre du terrain
        this.ligneCentre = new Point(largeur / 2, 0); // Ligne du centre (verticale)
    }

    // Getter pour la largeur
    public double getLargeur() {
        return largeur;
    }

    // Getter pour la longueur
    public double getLongueur() {
        return longueur;
    }

    // Getter pour le but gauche
    public Point getButGauche() {
        return butGauche;
    }

    // Getter pour le but droit
    public Point getButDroit() {
        return butDroit;
    }

    // Getter pour le centre
    public Point getCentre() {
        return centre;
    }

    // Getter pour la ligne centrale
    public Point getLigneCentre() {
        return ligneCentre;
    }

    // Affichage des composants du terrain
    @Override
    public String toString() {
        return "Terrain: [Largeur=" + largeur + ", Longueur=" + longueur + 
               ", ButGauche=" + butGauche + ", ButDroit=" + butDroit + 
               ", Centre=" + centre + ", LigneCentre=" + ligneCentre + "]";
    }
}
