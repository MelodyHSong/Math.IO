/*
☆
☆ Author: ☆ MelodyHSong ☆
☆ Language: Java
☆ File Name: MenuActivity.java
☆ Date: 2025-11-19
☆
*/

//Este archivo es parte de la aplicacion Math.IO
//Contiene la logica para la actividad de Menu donde los usuarios
//pueden seleccionar la categoria y dificultad antes de iniciar el juego.

package com.alexis_benejan.mathio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private String selectedCategory = null;
    private String selectedDifficulty = null;
    private Button startGameButton;
    private Button currentCategoryButton = null;
    private Button currentDifficultyButton = null;

    // Keys para pasar datos al Intent
    public static final String EXTRA_CATEGORY = "com.yourdomain.mathquiz.CATEGORY";
    public static final String EXTRA_DIFFICULTY = "com.yourdomain.mathquiz.DIFFICULTY";

    //---------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        startGameButton = findViewById(R.id.startGameButton);
    }

    /*
    ☆
    ☆ Metodo: selectCategory
    ☆ Descripcion: Captura la categoría seleccionada y actualiza el estado visual.
    ☆
    */
    public void selectCategory(View view) {
        Button button = (Button) view;
        selectedCategory = button.getTag().toString();

        if (currentCategoryButton != null) {
            currentCategoryButton.setAlpha(0.5f);
        }
        button.setAlpha(1.0f);
        currentCategoryButton = button;

        checkStartButtonState();
    }

    /*
    ☆
    ☆ Metodo: selectDifficulty
    ☆ Descripcion: Captura el nivel de dificultad seleccionado y actualiza el estado visual.
    ☆
    */
    public void selectDifficulty(View view) {
        Button button = (Button) view;
        selectedDifficulty = button.getTag().toString();

        if (currentDifficultyButton != null) {
            currentDifficultyButton.setAlpha(0.5f);
        }
        button.setAlpha(1.0f);
        currentDifficultyButton = button;

        checkStartButtonState();
    }

    /*
    ☆
    ☆ Metodo: checkStartButtonState
    ☆ Descripcion: Habilita el botón de inicio si Categoría y Dificultad están seleccionadas.
    ☆
    */
    private void checkStartButtonState() {
        if (selectedCategory != null && selectedDifficulty != null) {
            startGameButton.setEnabled(true);
        } else {
            startGameButton.setEnabled(false);
        }
    }

    /*
    ☆
    ☆ Metodo: startGame
    ☆ Descripcion: Inicia MainActivity enviando la Categoría y Dificultad seleccionadas.
    ☆
    */
    public void startGame(View view) {
        if (selectedCategory != null && selectedDifficulty != null) {
            Intent intent = new Intent(this, MainActivity.class);

            intent.putExtra(EXTRA_CATEGORY, selectedCategory);
            intent.putExtra(EXTRA_DIFFICULTY, selectedDifficulty);

            startActivity(intent);
        }
    }

    /*
    ☆
    ☆ Metodo: openAbout
    ☆ Descripcion: Lanza la actividad AboutActivity.
    ☆
    */
    public void openAbout(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}