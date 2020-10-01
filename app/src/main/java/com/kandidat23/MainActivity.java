package com.kandidat23;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


/**
 * Klasse for navigering fra hovedmeny.
 * Har ikke konstruktør.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * onCreate når bruker åpner activity_main.xml
     * @param savedInstanceState = null første gang siden åpnes. Holder styr på state.
     *                           Eks. Om telefonen er snudd eller ikke.
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Sender bruker til FinnSpisested{@link com.kandidat23.FinnSpisested}
     * @param view
     * @return void
     */
    public void finnSpisested(View view) {
        Intent il = new Intent(this, FinnSpisested.class);
        startActivity(il);
    }

    /**
     * Sender bruker til SpisteStedListe{@link com.kandidat23.SpiseStedListe}
     * og setter søkemodus  til 3 (GeoSok i SpiseStedListe)
     * @param view
     * @return void
     */
    public void geoSok(View view) {
         Intent ul = new Intent(this, SpiseStedListe.class);
         SharedPreferences sokeData = getSharedPreferences("sokeData", MODE_PRIVATE);
         SharedPreferences.Editor editor = sokeData.edit();
         editor.clear();
         editor.putInt("sokeModus", 3);
         editor.apply();
         startActivity(ul);
    }

    /**
     * Sender bruker til Instillinger {@link com.kandidat23.Innstillinger}
     * @param view
     * @return void
     */
    public void visInstillinger(View view) {
        Intent inst = new Intent(this, Innstillinger.class);
        startActivity(inst);
    }
}
