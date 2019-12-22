package com.example.chanthaj.truememorysquare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * IL s'agit d'un jeu de mémorisation où plusieurs carrés seront affichés à l'écran
 * Certains carrés choisis aléatoirement seront affiché en bleu au début du niveau pendant 1.5 à 1 secondes,
 * après quoi, ils prendront la même couleur que les autres et le joueur devra appuyer sur ceux-ci pour passer au niveau suivant.
 * Le joueur a droit à 3 erreurs.
 * Les carrés seront représentés par des boutons de différentes couleurs à l'affichage.
 */
public class Game {

    //suite d'entiers correspondant aux ids des carrés à mémoriser
    private ArrayList<Integer> suite;
    //score calculé à chaque réponse valide, incrémenté si le joueur n'était jamais arrivé à ce niveau durant cette partie, sinon non modifié
    private int score;
    //niveau actuel
    private int level;
    //nombre d'essais restant
    private int life;
    //plus haut niveau atteint par le joueur durant cette session, si le niveau actuel n'est pas égal à celui-ci, aucun point de seront ajoutés
    private int maxLevelYet;
    //clé = niveau, value = nombres de colonnes/lignes (toujours un carré), permet de récupérer le nombre de carré à générer par
    private HashMap<Integer,Integer> numberOfButton;

    /**
     * On commence une partie au niveau 1, avec 3 vies
     */
    public Game(){
        this.score=0;
        this.level=1;
        this.life = 3;
        this.maxLevelYet=1;

        this.numberOfButton= new HashMap<>();

        //correspond au nombres de carrés par colonnes/lignes
        int numberOfSquares = 3;
        for (int levelNumber = 1; levelNumber < 50 ; levelNumber++) {
            //niveau 1 et 2 => carré 3x3, 3 et 4 => 4x4, 5 et supérieurs => 5x5
            if(levelNumber%2 == 1 && levelNumber!=1 && numberOfSquares<5)
                numberOfSquares++;
            this.numberOfButton.put(levelNumber,numberOfSquares);
        }
        this.suite= new ArrayList<>();
        createSuite();

    }

    /**
     * Crée un nouveau niveau
     * Si nextLevel = true, on passe au niveau suivant et on change maxLevelYet si le joueur n'était pas encore arrivé là
     * Si nextLevel = false, on repasse au niveau précédent et le joueur perd une vie
     * Dans tous les cas, on regénère une suite de carrés à mémoriser
     */
    public void newLevel(boolean nextLevel){
        if(nextLevel){
            this.level++;
            if(this.level > this.maxLevelYet)
                this.maxLevelYet=this.level;
        }
        else
        {
            if (this.level >1)
                this.level--;
            this.life--;
        }
        createSuite();
    }

    //Vérifie si la partie est finis
    public boolean gameOver(){
       return this.life<=0;
    }

    /**
     * Vérifie que le jouer a terminé le niveau ou non
     * userEntries correspond aux id des boutons sur lequel le jouer a appuyé
     * Ici, on vérifie seulement la taille de celle-ci car l'ordre n'a pas d'importance et le joueur perd immédiatement après avoir cliqué
     * sur un mauvais bouton
     */
    public boolean nextLevel(ArrayList<Integer> userEntries){
        return userEntries.size() == this.suite.size();
    }

    /**
     * Génére la suite d'entiers aléatoire
      */
    public void createSuite(){
        this.suite.clear();
        Random r = new Random();
        int suiteSize=this.level*2;
        for (int i = 1; i <= suiteSize ; i++) {
            //-1)+1 ----> +1 en dehors du random car doit pas etre 0, -1 car doit etre entre 1 et le nombre de carrés qu'il y aura et non le nb de carré+1
            int newEntry = r.nextInt(getNumberOfButtons()* getNumberOfButtons()-1)+1;
            //la suite ne doit pas être identique à la précédente
            while(this.suite.contains(newEntry))
                newEntry = r.nextInt(getNumberOfButtons()* getNumberOfButtons()-1)+1;
            this.suite.add(newEntry);
        }
    }

    /**
     * Verifie si le carré appuyé est bon
     * Reçois l'id du bouton appuyé en paramètre
     */
    public boolean verifCarre(int userEntry){
        return this.suite.contains(userEntry);
    }

    public void calculScore(){
        this.score = this.score + this.level;
    }


    /******** GETTERS AND SETTERS  ***********/
    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getNumberOfButtons(){
        return this.numberOfButton.get(this.level);
    }

    public int getLife() {
        return life;
    }

    public ArrayList<Integer> getSuite(){
        return this.suite;
    }

}