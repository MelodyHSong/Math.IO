/*
☆
☆ Author: ☆ MelodyHSong ☆
☆ Language: Java
☆ File Name: MainActivity.java
☆ Date: 2025-11-19
☆
*/

//Este es el archivo MainActivity.java principal para la aplicación MathIO.
//Contiene la lógica del juego, manejo de UI, temporizador, sistema de vida, puntuación y sonido.
//Incluye mejoras para categorías, dificultades, sistema de vida y gestión de estado.

package com.alexis_benejan.mathio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.Group;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Vistas de la UI
    Button startButton;
    TextView resultTextView;
    TextView pointsTextView;
    TextView sumTextView;
    TextView timerTextView;
    TextView highScoreTextView;
    TextView healthTextView;
    Button playAgainButton;

    // Objeto para agrupar las vistas de los elementos del juego
    Group gameGroup;

    // ArrayList para los botones de respuesta
    ArrayList<Button> answerButtons = new ArrayList<>();

    // Variables de estado del juego (Partes I y II)
    ArrayList<Integer> answers = new ArrayList<>();
    int locationOfCorrectAnswer;
    int score = 0;
    int numberOfQuestions = 0;
    CountDownTimer countDownTimer;
    private long millisLeft = 60100; // 60 segundos

    // Variables de Vida y Racha (Health System)
    private int health = 5; // Vida inicial y máxima
    private int correctStreak = 0; // Contador para la racha de respuestas correctas

    // Parte III: Constantes para SharedPreferences
    public static final String PREFS_NAME = "MathQuizPrefs";
    public static final String HIGH_SCORE_KEY = "highScore";

    // Parte III: Variables para SoundPool
    private SoundPool soundPool;
    private int soundCorrect;
    private int soundWrong;
    private int soundNewRecord;

    // Parte 1.0.0a: Variables de Categoría y Dificultad
    private String currentCategory;
    private String currentDifficulty;
    private int difficultyFactor;

    //---------------------------------------------------------
    // MÉTODOS DE CICLO DE VIDA Y SETUP
    //---------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Parte 1.0.0a: Recibir Intent y configurar dificultad
        Intent intent = getIntent();
        if (intent != null) {
            currentCategory = intent.getStringExtra(MenuActivity.EXTRA_CATEGORY);
            currentDifficulty = intent.getStringExtra(MenuActivity.EXTRA_DIFFICULTY);

            if ("EASY".equals(currentDifficulty)) {
                difficultyFactor = 1;
            } else if ("HARD".equals(currentDifficulty)) {
                difficultyFactor = 2;
            } else if ("GENIUS".equals(currentDifficulty)) {
                difficultyFactor = 3;
            } else {
                difficultyFactor = 1;
            }
        } else {
            currentCategory = "ADDITION";
            difficultyFactor = 1;
        }

        // Inicialización de las vistas
        startButton = findViewById(R.id.startButton);
        sumTextView = findViewById(R.id.sumTextView);
        resultTextView = findViewById(R.id.resultTextView);
        pointsTextView = findViewById(R.id.pointsTextView);
        timerTextView = findViewById(R.id.timerTextView);
        playAgainButton = findViewById(R.id.playAgainButton);
        gameGroup = findViewById(R.id.gameGroup);
        highScoreTextView = findViewById(R.id.highScoreTextView);
        healthTextView = findViewById(R.id.healthTextView); // Inicializa HealthTextView

        // Inicialización de botones de respuesta
        answerButtons.add(findViewById(R.id.button0));
        answerButtons.add(findViewById(R.id.button1));
        answerButtons.add(findViewById(R.id.button2));
        answerButtons.add(findViewById(R.id.button3));

        // Parte III: CONFIGURACIÓN DE SONIDO
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        soundCorrect = soundPool.load(this, R.raw.correct, 1);
        soundWrong = soundPool.load(this, R.raw.error, 1);
        soundNewRecord = soundPool.load(this, R.raw.new_record_win, 1);

        // Iniciar la secuencia de juego
        startButton.setVisibility(View.INVISIBLE);
        gameGroup.setVisibility(View.VISIBLE);
        playAgain(null);
    }

    /*
    ☆
    ☆ Metodo: onPause
    ☆ Descripcion: Cancela el temporizador cuando la actividad pierde el foco (Parte II).
    ☆
    */
    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /*
    ☆
    ☆ Metodo: onResume
    ☆ Descripcion: Reanuda el temporizador si el juego estaba en curso (Parte II).
    ☆
    */
    @Override
    protected void onResume() {
        super.onResume();
        if (gameGroup.getVisibility() == View.VISIBLE && millisLeft > 0) {
            resumeTimer(millisLeft);
        }
    }

    /*
    ☆
    ☆ Metodo: onSaveInstanceState
    ☆ Descripcion: Guarda las variables importantes del estado del juego (Parte II).
    ☆
    */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score", score);
        outState.putInt("numberOfQuestions", numberOfQuestions);
        outState.putInt("locationOfCorrectAnswer", locationOfCorrectAnswer);
        outState.putString("questionText", sumTextView.getText().toString());
        outState.putString("resultText", resultTextView.getText().toString());
        outState.putIntegerArrayList("answers", answers);
        outState.putLong("millisLeft", millisLeft);
        outState.putInt("health", health); // Guarda el valor de la vida
        outState.putInt("correctStreak", correctStreak); // Guarda la racha
        outState.putInt("gameGroupVisibility", gameGroup.getVisibility());
        outState.putInt("startButtonVisibility", startButton.getVisibility());
        outState.putInt("playAgainButtonVisibility", playAgainButton.getVisibility());
    }

    /*
    ☆
    ☆ Metodo: onRestoreInstanceState
    ☆ Descripcion: Restaura el estado del juego cuando la Activity se vuelve a crear (Parte II).
    ☆
    */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        gameGroup.setVisibility(savedInstanceState.getInt("gameGroupVisibility"));
        startButton.setVisibility(savedInstanceState.getInt("startButtonVisibility"));
        playAgainButton.setVisibility(savedInstanceState.getInt("playAgainButtonVisibility"));

        if (gameGroup.getVisibility() == View.VISIBLE) {
            score = savedInstanceState.getInt("score");
            numberOfQuestions = savedInstanceState.getInt("numberOfQuestions");
            locationOfCorrectAnswer = savedInstanceState.getInt("locationOfCorrectAnswer");
            health = savedInstanceState.getInt("health"); // Restaura la vida
            correctStreak = savedInstanceState.getInt("correctStreak"); // Restaura la racha

            sumTextView.setText(savedInstanceState.getString("questionText"));
            resultTextView.setText(savedInstanceState.getString("resultText"));

            pointsTextView.setText(String.format(Locale.getDefault(), "%d/%d", score, numberOfQuestions));
            updateHealthDisplay(); // Actualiza la UI de la vida

            answers = savedInstanceState.getIntegerArrayList("answers");

            if (answers != null && !answers.isEmpty()) {
                for (int i = 0; i < answers.size(); i++) {
                    answerButtons.get(i).setText(String.valueOf(answers.get(i)));
                }
            }

            millisLeft = savedInstanceState.getLong("millisLeft");

            if (millisLeft <= 0) {
                timerTextView.setText("0s");
                setAnswerButtonsEnabled(false);
            }
        }
    }

    /*
    ☆
    ☆ Metodo: onDestroy
    ☆ Descripcion: Libera los recursos de SoundPool (Parte III).
    ☆
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    //---------------------------------------------------------
    // MÉTODOS DEL JUEGO
    //---------------------------------------------------------

    /*
    ☆
    ☆ Metodo: start
    ☆ Descripcion: Inicia la secuencia del juego (Mantener por compatibilidad).
    ☆
    */
    public void start(View view) {
        startButton.setVisibility(View.INVISIBLE);
        gameGroup.setVisibility(View.VISIBLE);
        playAgain(null);
    }

    /*
    ☆
    ☆ Metodo: updateHealthDisplay
    ☆ Descripcion: Actualiza el TextView de la vida con corazones (♥).
    ☆
    */
    private void updateHealthDisplay() {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < health; i++) {
            hearts.append("♥ ");
        }
        for (int i = health; i < 5; i++) {
            hearts.append("♡ "); // Corazones vacíos si la vida no es máxima
        }
        healthTextView.setText("Life: " + hearts.toString().trim());
    }

    /*
    ☆
    ☆ Metodo: playAgain
    ☆ Descripcion: Reinicia el estado del juego (Puntuación, tiempo y UI).
    ☆
    */
    public void playAgain(View view) {
        score = 0;
        numberOfQuestions = 0;
        millisLeft = 60100; // 60 segundos

        health = 5; // Vida máxima
        correctStreak = 0; // Racha reiniciada

        timerTextView.setText("60s");
        pointsTextView.setText("0/0");
        resultTextView.setText("");

        updateHealthDisplay(); // Actualiza la UI de la vida

        playAgainButton.setVisibility(View.INVISIBLE);
        setAnswerButtonsEnabled(true);

        loadAndDisplayHighScore();

        generateQuestion();
        resumeTimer(millisLeft);
    }

    /*
    ☆
    ☆ Metodo: loadAndDisplayHighScore
    ☆ Descripcion: Carga y muestra el high score guardado en SharedPreferences (Parte III).
    ☆
    */
    private void loadAndDisplayHighScore() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int highScore = prefs.getInt(HIGH_SCORE_KEY, 0);
        highScoreTextView.setText(String.format(Locale.getDefault(), "Record: %d", highScore));
    }

    /*
    ☆
    ☆ Metodo: resumeTimer
    ☆ Descripcion: Inicia o reanuda el temporizador desde el tiempo restante (millis) (Parte II).
    ☆
    */
    public void resumeTimer (Long millis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                timerTextView.setText(String.format(Locale.getDefault(), "%ds", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                millisLeft = 0;
                timerTextView.setText("0s");
                playAgainButton.setVisibility(View.VISIBLE);

                // LÓGICA FINAL DE RÉCORD
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                int highScore = prefs.getInt(HIGH_SCORE_KEY, 0);

                if (score > highScore) {
                    resultTextView.setText(String.format(Locale.getDefault(), "¡New High Score!: %d", score));

                    soundPool.play(soundNewRecord, 1, 1, 0, 0, 1);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(HIGH_SCORE_KEY, score);
                    editor.apply();

                    loadAndDisplayHighScore();

                } else {
                    resultTextView.setText(String.format(Locale.getDefault(), "TIME OUT! Your score: %d/%d", score, numberOfQuestions));
                }

                setAnswerButtonsEnabled(false);
            }
        }.start();
    }

    /*
    ☆
    ☆ Metodo: generateQuestion
    ☆ Descripcion: Genera una pregunta basada en la categoría y dificultad (Parte 1.0.0a).
    ☆
    */
    public void generateQuestion() {
        Random rand = new Random();
        int a = 0;
        int b = 0;
        int correctAnswer = 0;
        String questionSymbol = "";

        // Ajustar límites para sumas/restas/multiplicaciones/divisiones
        int maxNumber = 10;
        if (difficultyFactor == 2) maxNumber = 50;
        if (difficultyFactor == 3) maxNumber = 200;

        String finalCategory = currentCategory;

        if ("RANDOM".equals(currentCategory)) {
            // Random solo incluye operaciones básicas
            String[] operations = {"ADDITION", "SUBTRACTION", "MULTIPLICATION", "DIVISION"};
            finalCategory = operations[rand.nextInt(operations.length)];
        }

        switch (finalCategory) {
            case "ADDITION":
                a = rand.nextInt(maxNumber) + 1;
                b = rand.nextInt(maxNumber) + 1;
                correctAnswer = a + b;
                questionSymbol = "+";
                break;

            case "SUBTRACTION":
                int x = rand.nextInt(maxNumber) + maxNumber/2;
                int y = rand.nextInt(maxNumber) + 1;
                a = Math.max(x, y);
                b = Math.min(x, y);
                correctAnswer = a - b;
                questionSymbol = "-";
                break;

            case "MULTIPLICATION":
                maxNumber = (difficultyFactor == 3) ? 20 : maxNumber / 2;
                if(maxNumber < 5) maxNumber = 5;
                a = rand.nextInt(maxNumber) + 1;
                b = rand.nextInt(maxNumber) + 1;
                correctAnswer = a * b;
                questionSymbol = "x";
                break;

            case "DIVISION":
                int divisorMax = (difficultyFactor == 3) ? 15 : 10;
                b = rand.nextInt(divisorMax) + 2;
                int quotient = rand.nextInt(maxNumber / 2) + 1;
                a = b * quotient;
                correctAnswer = quotient;
                questionSymbol = "÷";
                break;

            case "FACTORIALS":
                // Lógica de varianza mejorada para Factoriales
                int minN;
                int maxN;

                if (difficultyFactor == 1) { // Easy
                    minN = 2;
                    maxN = 4;
                } else if (difficultyFactor == 2) { // Hard
                    minN = 3;
                    maxN = 5;
                } else { // Genius (difficultyFactor == 3)
                    minN = 4;
                    maxN = 6;
                }

                // Selecciona un número aleatorio (n) dentro del rango
                int n = rand.nextInt(maxN - minN + 1) + minN;

                a = n;
                b = 0;
                correctAnswer = calculateFactorial(n);
                sumTextView.setText(String.format(Locale.getDefault(), "%d!", a));
                break;

            default:
                a = rand.nextInt(maxNumber) + 1;
                b = rand.nextInt(maxNumber) + 1;
                correctAnswer = a + b;
                questionSymbol = "+";
                break;
        }

        if (!"FACTORIALS".equals(finalCategory)) {
            sumTextView.setText(String.format(Locale.getDefault(), "%d %s %d", a, questionSymbol, b));
        }

        locationOfCorrectAnswer = rand.nextInt(4);
        answers.clear();
        int answerRange = (difficultyFactor == 3) ? 50 : 20;

        for (int i = 0; i < 4; i++) {
            if (i == locationOfCorrectAnswer) {
                answers.add(correctAnswer);
            } else {
                int incorrectAnswer = correctAnswer + rand.nextInt(answerRange) * (rand.nextBoolean() ? 1 : -1);
                while (incorrectAnswer <= 0 || incorrectAnswer == correctAnswer) {
                    int deviation = rand.nextInt(answerRange) + 1;
                    incorrectAnswer = correctAnswer + (rand.nextBoolean() ? deviation : -deviation);
                    if (incorrectAnswer == correctAnswer) {
                        incorrectAnswer += (rand.nextBoolean() ? 1 : -1);
                    }
                }
                answers.add(incorrectAnswer);
            }
        }

        for (int i = 0; i < answerButtons.size(); i++) {
            answerButtons.get(i).setText(String.valueOf(answers.get(i)));
        }
    }

    /*
    ☆
    ☆ Metodo: calculateFactorial
    ☆ Descripcion: Calcula el factorial de un número.
    ☆
    */
    private int calculateFactorial(int n) {
        if (n < 0) return 0;
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /*
    ☆
    ☆ Metodo: chooseAnswer
    ☆ Descripcion: Procesa la respuesta del usuario, actualiza la puntuación, salud y racha.
    ☆
    */
    public void chooseAnswer(View view) {

        if (view.getTag().toString().equals(String.valueOf(locationOfCorrectAnswer))) {
            // Respuesta Correcta
            score++;
            correctStreak++; // Incrementa la racha de respuestas correctas
            resultTextView.setText("Correct!");
            soundPool.play(soundCorrect, 1, 1, 0, 0, 1);

            // Lógica de Recompensa (Cada 5 respuestas correctas)
            if (correctStreak % 5 == 0) {
                if (health < 5) {
                    health++; // Gana 1 vida (si no está a tope)
                    resultTextView.setText("Correct! +1 Health! (" + health + ")");
                } else {
                    score += 5; // Gana 5 puntos extra (si la vida está a tope)
                    resultTextView.setText("Correct! +5 Bonus Points!");
                }
            }

        } else {
            // Respuesta Incorrecta
            health--; // Pierde 1 vida
            correctStreak = 0; // Reinicia la racha
            resultTextView.setText("Wrong! Health: " + health);
            soundPool.play(soundWrong, 1, 1, 0, 0, 1);
        }

        numberOfQuestions++;
        pointsTextView.setText(String.format(Locale.getDefault(), "%d/%d", score, numberOfQuestions));
        updateHealthDisplay(); // Actualiza la UI de la vida

        // Lógica de Derrota por 5 fallos (Health <= 0)
        if (health <= 0) {
            // Detener el juego
            if (countDownTimer != null) {
                countDownTimer.cancel();
                millisLeft = 0;
            }
            timerTextView.setText("0s");
            playAgainButton.setVisibility(View.VISIBLE);
            setAnswerButtonsEnabled(false);

            // Lógica de fin de juego (guardar récord)
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int highScore = prefs.getInt(HIGH_SCORE_KEY, 0);

            if (score > highScore) {
                resultTextView.setText(String.format(Locale.getDefault(), "¡New High Score!: %d (DEFEAT)", score));
                soundPool.play(soundNewRecord, 1, 1, 0, 0, 1);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(HIGH_SCORE_KEY, score);
                editor.apply();
                loadAndDisplayHighScore();
            } else {
                resultTextView.setText(String.format(Locale.getDefault(), "DEFEAT! Your score: %d/%d", score, numberOfQuestions));
            }

        } else {
            generateQuestion();
        }
    }

    /*
    ☆
    ☆ Metodo: setAnswerButtonsEnabled
    ☆ Descripcion: Método auxiliar para habilitar/deshabilitar los botones de respuesta.
    ☆
    */
    private void setAnswerButtonsEnabled(boolean isEnabled) {
        for (Button button : answerButtons) {
            button.setEnabled(isEnabled);
        }
    }

    /*
    ☆
    ☆ Metodo: backToMenu
    ☆ Descripcion: Navega de regreso a MenuActivity y finaliza la actividad actual.
    ☆
    */
    public void backToMenu(View view) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}