package com.kandidat23;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Klasse som tar input fra bruker og sender den videre slik at neste activity kan bruke info.
 * Om bruker har lagt inn favoritter og bruker favoritt søk, vil dataen i SharedPreference bli byttet ut med favData.
 */
public class FinnSpisested extends AppCompatActivity {

    /**
     * onCreate metode for activity_finn_spisested.
     * @param savedInstanceState = null første gang siden åpnes. Holder styr på state.
     *                           Eks. Om telefonen er snudd eller ikke.
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finn_spisested);
    }


    /**
     * Henter ut søkedata fra EditText felt for så å sende info videre til SpiseStedListe.java{@link com.kandidat23.SpiseStedListe}
     * Sendingen av data skjer via SharedPreferences.
     * Meetoden er en onClick fra sokKnapp i activity_finn_spisested.xml og henter info fra API'et i SpiseStedListe{@link com.kandidat23.SpiseStedListe}
     * @param view
     * @return void
     * Bruker editor.apply() siden dette sender data i bakgrunnen, istedenfor editor.commit som sender data med en gang.
     * Ved sending av store mengder data vil editor.commit gjøre at appen kjører senere.
     */
    public void sokSpisested(View view) {
        Intent il = new Intent(this, SpiseStedListe.class);

        SharedPreferences sokeData = getSharedPreferences("sokeData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sokeData.edit();
        EditText finnNavn = findViewById(R.id.innNavn);
        EditText finnPostSted = findViewById(R.id.innPostSted);
        String navn;
        String postSted;
        editor.clear();
        //Sjekker om bruker har skrevet inn noen søkeparametere
        if(finnNavn != null) {
            navn = finnNavn.getText().toString();
            editor.putString("navn", navn);
        }
        if(finnPostSted != null) {
            postSted = finnPostSted.getText().toString();
            editor.putString("postSted", postSted);
        }

        editor.putInt("sokeModus", 1);
        editor.apply();
        startActivity(il);

    }

    /**
     * Når bruker trykker på Favoritt Søk knappen, hentes det inn data fra Instillinger{@link com.kandidat23.Instillinger}
     * Så settes favoritt data inn i SharedPreferences slik at dataen kan brukes med søk mot API i SpiseStedListe{@link com.kandidat23.SpiseStedListe}
     * @param view
     * @return void
     */
    public void sokFav(View view) {
        SharedPreferences favData = getSharedPreferences("favData", MODE_PRIVATE);
        String favSted = favData.getString("favSted", "");
        int favArstall = favData.getInt("favAr",0);
        SharedPreferences sokeData = getSharedPreferences("sokeData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sokeData.edit();
        editor.clear();
        editor.putString("postSted", favSted);
        editor.putInt("favAr", favArstall);
        editor.putInt("sokeModus", 2);
        editor.apply();
        Intent il = new Intent(this, SpiseStedListe.class);
        startActivity(il);
    }


}
