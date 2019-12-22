package com.example.chanthaj.truememorysquare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chanthaj.truememorysquare.model.ScoreDAO;

import java.util.List;

public class GameOver extends AppCompatActivity {

    private ScoreDAO scoreDAO;
    private List<String> allTheScores;
    private final String lastScores = "5";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Button button;

    private int newScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.game_over);

        super.onCreate(savedInstanceState);

        displayNewScore();
        setButtonListener();
        displayScores();
    }

    private void displayNewScore() {
        newScore = getIntent().getIntExtra("NEW_SCORE",0);

        TextView newScoreView = (TextView) findViewById(R.id.textViewYourScore);
        newScoreView.setText(Integer.toString(newScore));
    }

    private void displayScores() {
        scoreDAO = new ScoreDAO(this);
        scoreDAO.open();

        allTheScores = scoreDAO.getScores(lastScores);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(allTheScores);
        recyclerView.setAdapter(mAdapter);
    }

    private void setButtonListener(){
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
                startActivity(intent);
                finish();
            }});
    }
}
