/*
☆
☆ Author: ☆ MelodyHSong ☆
☆ Language: Java
☆ File Name: AboutActivity.java
☆ Date: 2025-11-19
☆
*/

//Este archivo es parte de la aplicacion Math.IO
//Contiene la logica para la actividad "Acerca de" que permite a los usuarios
//acceder a enlaces externos como GitHub y Ko-fi.

package com.alexis_benejan.mathio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    // Constantes para las URLs
    private static final String GITHUB_URL = "https://github.com/MelodyHSong/Math.IO";
    private static final String KOFI_URL = "https://ko-fi.com/melodyhsong";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    /*
    ☆
    ☆ Metodo: goToKofi
    ☆ Descripcion: Lanza un Intent para abrir la URL de Ko-fi en un navegador web.
    ☆
    */
    public void goToKofi(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(KOFI_URL));
        startActivity(browserIntent);
    }

    /*
    ☆
    ☆ Metodo: goToGithub
    ☆ Descripcion: Lanza un Intent para abrir el repositorio de GitHub en un navegador web.
    ☆
    */
    public void goToGithub(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL));
        startActivity(browserIntent);
    }
}