package com.kandidat23;


import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import static java.lang.Integer.parseInt;

/**
 * Instillinger er en klasse som holder styr på bruker sine favoritter.
 * Klassen har ikke konstruktør.
 */
public class Innstillinger extends AppCompatActivity {
    /**
     * favSted for lagring av favoritt stedet saom bruker har satt
     * favAr for lagring av favoritt året som bruker hgar satt
     */
    TextView favSted, favAr;

    /**
     * onCreate til activity_instillinger.xml.
     * Åpner og sjekker om bruker har skrevet inn noe favoritter.
     * Favoritt sted og år blir lagt inn i EditText slik at bruker kan se hva som er lagt inn
     * og endre på disse om ønskelig.
     * @param savedInstanceState = null første gang siden åpnes. Holder styr på state.
     *                            Eks. Om telefonen er snudd eller ikke.
     * @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instillinger);
        SharedPreferences favData = getApplicationContext().getSharedPreferences("favData", MODE_PRIVATE);
        favSted = findViewById(R.id.favSted);
        favAr = findViewById(R.id.favArstall);

        String favStedTxt = favData.getString("favStedfavSted", "");
        int favArTxt = favData.getInt("favAr", 0);
        favSted.setText(favStedTxt);
        if(favArTxt == 0)
            favAr.setText("");
        else
            favAr.setText(String.valueOf(favArTxt));

    }

    /**
     * onClick funksjon til setFav i Instillinger.xml.
     * Henter ut data som bruker har skrevet inn i de forskjellige EditText boksene.
     * Dataen fra bruker blir lagt inn i SharedPreference som blir brukt i FinnSpisesteder{@link com.kandidat23.FinnSpisested}
     * @param v
     * @return void
     * Når en ny favoritt blir lagt til fjernes de gamle favorittene.
     * Bruker editor.apply() siden det skjer i bakgrunnen og reduserer lag om store menger data blir sendt.
     * Kan også bruke editor.commit(), med dette sender data med en gang. Kan gå utover ytelse ved store datamengder.
     */
    public void setFavoritt(View v){

        SharedPreferences favData = getSharedPreferences("favData", MODE_PRIVATE);
        SharedPreferences.Editor editor = favData.edit();
        String sted = favSted.getText().toString();


        editor.clear(); //Sletter data fra SharedPreference

        //Sjekker om bruker har skrevet inn noe.
        if(!sted.equals("")){
            editor.putString("favSted", sted);
        } else if(!favAr.getText().toString().equals("")){
            int ar = parseInt(favAr.getText().toString());
            editor.putInt("favAr", ar);
        }


        editor.apply();

    }
}
