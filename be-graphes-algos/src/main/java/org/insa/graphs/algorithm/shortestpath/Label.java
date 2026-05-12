package org.insa.graphs.algorithm.shortestpath;

public class Label {

    private int sommetCourant;
    private boolean marque;
    private int coutRealise;
    private int pere;

    public Label(int sommetCourant, boolean marque, int coutRealise, int pere){ 
        this.sommetCourant = sommetCourant;
        this.marque = marque;
        this.coutRealise = coutRealise;
        this.pere = pere;
    }

    public int getSommetCourant(){
        return this.sommetCourant;
    }

    public boolean getMarque(){
        return this.marque;
    }

    public int getCoutRealise(){
        return this.coutRealise;
    }
    
    public int getpere(){
        return this.pere;
    }

    public int getCost(){  //Pour le moent il retourne juste le cout réel maiss va être modfier plus tard
        return this.coutRealise;
    }

    // associé à chauqe noeud son label
     
    



    

    //attention à la tradition des majuscule pour les classes et les minuscules pour les variables et les méthodes
    
}
