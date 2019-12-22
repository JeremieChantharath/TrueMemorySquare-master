package com.example.chanthaj.truememorysquare.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ScoreDAO {

    // Champs de la base de données
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_SCORE };


    public ScoreDAO(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Score addScore(int score) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_SCORE, score);
        long insertId = database.insert(MySQLiteHelper.TABLE_SCORE, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SCORE,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Score newComment = cursorToScore(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteScore(Score comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_SCORE, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    /**
     * On récupère les scores sous forme de string pour les afficher
     */
    public List<String> getScores(String topScores) {
        List<String> scores = new ArrayList<>();

        Cursor cursor = database.query(true,MySQLiteHelper.TABLE_SCORE, allColumns ,null,null,null,null,MySQLiteHelper.COLUMN_SCORE + " DESC" ,topScores);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            String score = cursorToScore(cursor).getScoreString();

            Log.i("Curseur Comment",score);

            scores.add(score);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();
        return scores;
    }

    private Score cursorToScore(Cursor cursor) {
        Score score = new Score();
        score.setId(cursor.getLong(0));
        score.setScore(cursor.getInt(1));
        return score;
    }
}