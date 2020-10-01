package com.kandidat23;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * VisTilsyn klassen henter ut info fra API'et Kravpunkter
 * Det bruker klassen  Tilsyn {@link Tilsyn} til å lage objekter
 */
public class VisTilsyn extends AppCompatActivity
        implements Response.Listener<String>, Response.ErrorListener {
    /**
     * id = tilsynsid'en til et tilsyn som man skal søke etter
     * ENDPOINT = url'en til API'et
     * tilsynsRecyclerView = recyclerView'et som adapterklassen {@link TilsynAdapter} skal bruke.
     */
    public String id, ENDPOINT = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/kravpunkter?", navn;
    private RecyclerView tilsynRecyclerView;

    /**
     * onCreate kjøres  når aktiviteten åpnes.
     * Henter inn RecyclerViewet fra activity_vis_tilsyn.xml
     * Henter ut id fra Intent.putExtra(), og navn fra Intent.putExtra().
     * @param savedInstanceState = null første gang siden åpnes. Holder styr på state.
     *                           Eks. Om telefonen er snudd eller ikke.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_tilsyn);

        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        navn = intent.getExtras().getString("navn");

        //Setter navnet til bedriften som er overskrift i activity_vis_tilsyn.xml
        TextView bedriftNavn = findViewById(R.id.bedriftNavn);
        bedriftNavn.setText(navn);

        //Henter RecyclerView fra activity_vis_tilsyn.xml
        tilsynRecyclerView = findViewById(R.id.tilsynRecycler);

        //Kjører volley
        hentKravpunkt();
    }

    /**
     * API spørring.
     * hentKrav er en string som består av ENDPOINT og id some r tilsynsid'en
     */
    public void hentKravpunkt(){
        String hentKrav = ENDPOINT + "tilsynid="+id;

        if (isOnline()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, hentKrav, this, this);
            queue.add(stringRequest);
        }
    }

    /**
     * Får inn API respons som prases til JSONObjekt så legges inn i JSONArray
     * For så å gjøres om til objekter av Tilsyn {@link Tilsyn}
     * @param response = string med resultater fra API'et
     * @catch JSONException = Indikerer problemer med JSON API
     */
    @Override
    public void onResponse(String response) {
        try{
            JSONObject responseTilsyn = new JSONObject(response);
            JSONArray tilsynArray = responseTilsyn.getJSONArray("entries");
            ArrayList<Tilsyn> tilsynObjektArray = new ArrayList<>();
            for(int i = 0; i< tilsynArray.length(); i++){
                Tilsyn tilsyn = new Tilsyn(tilsynArray.getJSONObject(i));
                tilsynObjektArray.add(tilsyn);
            }
            //Sender info til metode som lager adapter
            toAdapter(tilsynObjektArray);

        }catch(JSONException e){
            Toast.makeText(this, "Ugyldig JSON-data.", Toast.LENGTH_LONG).show();
        }


    }

    /**
     * Metode som lager adapter og setter adapter til view'et
     * Oppretter adapter fra TilsynAdapter klassen {@link TilsynAdapter}
     * @param tilsynObjektArray = array med objekter fra klassen Tilsyn generert fra API respons
     */
    private void toAdapter(ArrayList<Tilsyn> tilsynObjektArray) {
        TilsynAdapter tilsynAdapter= new TilsynAdapter(this, tilsynObjektArray);
        tilsynRecyclerView.setAdapter(tilsynAdapter);
        tilsynRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     *  Viser en Toast for bruker om det er noe feil med spørringen.
     * @param error == feilmelding ved spørring
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Volley feilet!", Toast.LENGTH_LONG).show();
    }

    /**
     * Klasse som sjekker om det er mulig å koble til API'et
     * @return boolean = kobling til API'et (true eller false).
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
