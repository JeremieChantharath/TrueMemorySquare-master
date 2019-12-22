package com.example.chanthaj.truememorysquare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chanthaj.truememorysquare.model.ScoreDAO;

import java.util.ArrayList;

/**
 * TODO:
 * -mode de jeu : séquence à mémoriser
 * -menuActivity avec séléction mode, de sorte à ce que si fin de jeu, on revient au menu et non fermeture de l'app
 * -highscore avec une bdd !!!!
 */

public class FullscreenActivity extends AppCompatActivity {

    //Les entrées du joueur
    private ArrayList<Integer> userEntries;
    //Moteur du jeu
    private Game game;

    private ScoreDAO scoreDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        //Crée l'instance de la partie
        this.game = new Game();
        this.userEntries = new ArrayList<>();

        printInfos();

        scoreDAO = new ScoreDAO(this);
        scoreDAO.open();

        generateWithDelay(1500);
    }


    //Affiche les infos de la partie : le score, le niveau actuel et les vies restantes
    public void printInfos(){

        Context context = getApplicationContext();

        TextView score = (TextView)findViewById(R.id.score);
        score.setText(context.getString(R.string.score) + this.game.getScore());

        TextView level = (TextView)findViewById(R.id.level);
        level.setText(context.getString(R.string.level) + this.game.getLevel());

        TextView life = (TextView)findViewById(R.id.life);
        life.setText(context.getString(R.string.life) + this.game.getLife());
    }

    /**
     * Génère les boutons à mémoriser puis ceux qui recevront les réponses du joueur
     * La placement des boutons est identique
     * Reçois en paramètre le délai entre l'affichage des boutons à mémoriser et leur disparition, ie la possibilité au joueur de répondre
     * Au début le délai est de 1.5 secondes, après le niveau 7 il est de 1 seconde
     */
    public void generateWithDelay(int delay){

        generateButtonsToMemorize(this.game.getNumberOfButtons());

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                generateButtons(game.getNumberOfButtons());
            }
        }, delay);

    }

    /**
     * Génère les boutons du jeu
     * Reçois en paramètres le nombre des boutons à générer par colonnes/lignes
     */
    public void generateButtons(final int numButtons) {

        //Correspond à l'id du bouton, sera incrémenté à chaque fin de boucle
        int buttonId = 1;
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativelayout);
        //S'il y avait un niveau avant, supprime tous les boutons précédents
        rl.removeAllViews();

        //Sert à placer les boutons sur une ligne
        for (int i = 1; i <= numButtons; i++) {
            //Sert à les placer sur une colonne
            for (int j = 1; j <= numButtons; j++) {

                Button btn = new Button(this);
                btn.setId(buttonId);

                //Couleur de base (gris)
                btn.setBackgroundColor(Color.rgb(70, 80, 90));

                //Créer l'objet pour les paramètres du bouton
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                //Donne les dimensions au bouton
                setButtonsParams(p);

                //Verifie si le bouton est le premier de la colonne, s'il ne l'est pas on place ce bouton à droite du bouton précédent
                if (!isInFirstColomn(buttonId, numButtons))
                {
                    p.leftMargin = 50;
                    p.addRule(RelativeLayout.RIGHT_OF, buttonId - 1);
                }

                //Vérifie si le bouton est sur la première ligne, s'il ne l'est pas, place le bouton en dessous du bouton censé être juste au dessus de lui
                if(i>1)
                    p.addRule(RelativeLayout.BELOW, buttonId - numButtons);

                //Donne les paramètres au bouton
                btn.setLayoutParams(p);

                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        // une fois que le joueur a cliqué sur le bouton, celui devient vert si c'était juste et n'a plus de raison d'être clickable, sinon, on retourne au niveau d'avant
                        view.setClickable(false);

                        //En cas de bonne réponse
                        if(game.verifCarre(view.getId())){
                            //Ajoute à la liste des réponses du joueur
                            userEntries.add(view.getId());
                            //colorie le bouton en vert
                            view.setBackgroundColor(Color.rgb(70, 200, 70));

                            //Si le jouer a trouvé toutes les bonnes réponses
                            if(game.nextLevel(userEntries))
                            {
                                //on désactive tous les boutons jusqu'à ce que le niveau suivant soit généré
                                allButtonsDisable(numButtons*numButtons);
                                userEntries.clear();
                                //Niveau suivant
                                game.newLevel(true);
                                //Applique le temps où les boutons à mémoriser reste affichés
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(game.getLevel()>=7)
                                            generateWithDelay(1000);
                                        generateWithDelay(1500);
                                    }
                                }, 1000);
                            Toast.makeText(getApplicationContext(),"Bien joué !",Toast.LENGTH_SHORT).show();
                            }
                            game.calculScore();
                        }
                        //En cas de mauvaise réponse
                        else{
                            allButtonsDisable(numButtons*numButtons);
                            //Colorie le bouton en rouge, pour bien montrer au joueur qu'il s'est trompé
                            view.setBackgroundColor(Color.rgb(200, 0, 0));
                            userEntries.clear();
                            //Niveau précédent
                            game.newLevel(false);
                            //Vérifie que la partie continue, ie que le joueur a encore des vies
                            if(game.gameOver())
                            {
                                scoreDAO.addScore(game.getScore());

                                Intent intent = new Intent(getApplicationContext(), GameOver.class);
                                intent.putExtra("NEW_SCORE", game.getScore());
                                startActivity(intent);
                                finish();
                            }
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(game.getLevel()>=7)
                                        generateWithDelay(1000);
                                    generateWithDelay(1500);
                                }
                            }, 1000);
                            Toast.makeText(getApplicationContext(),"Dommage..",Toast.LENGTH_SHORT).show();
                        }
                        //Actualise les infos de la partie
                        printInfos();
                    }
                });
                rl.addView(btn);
                //Augmente la variable id de 1 (le premier bouton aura 0 pour id, le 2e aura 1, etc)
                buttonId++;
            }
        }
    }

    /**
     * Génère les boutons à mémoriser
     * Aucun ne seront clickable
     * Reçois en paramètres le nombre des boutons à générer par colonnes/lignes
     */
    public void generateButtonsToMemorize(int numButtons) {

        int buttonId = 1;

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativelayout);
        rl.removeAllViews();

        for (int i = 1; i <= numButtons; i++) {
            for (int j = 1; j <= numButtons; j++) {
                Button btn = new Button(this);
                btn.setId(buttonId);
                //Si fait partit de la suite bleu, sinon comme les autres
                if(this.game.getSuite().contains(buttonId))
                    btn.setBackgroundColor(Color.rgb(60, 180, 250));
                else
                    btn.setBackgroundColor(Color.rgb(70, 80, 90));

                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                //On ne peut pas commencer à jouer avant le début du niveau
                btn.setClickable(false);
                setButtonsParams(p);
                if (!isInFirstColomn(buttonId, numButtons))
                {
                    p.leftMargin = 50;
                    p.addRule(RelativeLayout.RIGHT_OF, buttonId - 1);
                }

                if(i>1)
                    p.addRule(RelativeLayout.BELOW, buttonId - numButtons);
                btn.setLayoutParams(p);
                rl.addView(btn);
                buttonId++;
            }
        }
    }

    //Désactive tous les boutons
    public void allButtonsDisable(int max){
        for (int i = 1; i < max; i++) {
            Button btn = (Button)findViewById(i);
            btn.setClickable(false);
        }
    }

    public void setButtonsParams(RelativeLayout.LayoutParams p) {
        p.topMargin = 50;
        p.width = 150;
        p.height = 150;
    }

    public boolean isInFirstColomn(int buttonId, int numberOfButtons) {
        return buttonId == 1 || buttonId % numberOfButtons == 1;
    }


}


