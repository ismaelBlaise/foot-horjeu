package util;

import java.util.List;

public class Equipe {
    
    List<Joueur> joueurs;
    Joueur gardien;
    Joueur dernierDeffenseur;
    public Equipe() {
    }
    public Equipe(List<Joueur> joueurs, Joueur gardien, Joueur dernierDeffenseur) {
        this.joueurs = joueurs;
        this.gardien = gardien;
        this.dernierDeffenseur = dernierDeffenseur;
    }
    public List<Joueur> getJoueurs() {
        return joueurs;
    }
    public void setJoueurs(List<Joueur> joueurs) {
        this.joueurs = joueurs;
    }
    public Joueur getGardien() {
        return gardien;
    }
    public void setGardien(Joueur gardien) {
        this.gardien = gardien;
    }
    public Joueur getDernierDeffenseur() {
        return dernierDeffenseur;
    }
    public void setDernierDeffenseur(Joueur dernierDeffenseur) {
        this.dernierDeffenseur = dernierDeffenseur;
    }
    
    
}
