package com.kandidat23;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
 * SpiseStedListe henter og håndterer info fra API ved hjelp av Volley.
 */
public class SpiseStedListe extends AppCompatActivity
        implements Response.Listener<String>, Response.ErrorListener, AdapterView.OnItemSelectedListener {
    private static final long LOCATION_REFRESH_DISTANCE = 1000;
    /**
     * ENDPOOINT er starten på API stringen som søkes etter.
     * SpisestedAdapter er en liste med objekter fra klassen Spisested {@link Spisested} som sendes til SpisestedAdapter {@link Spisested}
     * spisestedRecyclerView er RecyclerViewet som adapteren skal fylle
     * spisestedArray er et array for å holde styr på objektene av Spisested{@link Spisested} etter API har hentet inn data
     * favAr er året som er satt som favoritt. Blir laget globalt her så det kan brukes i flere metoder.
     */

    private String ENDPOINT = "https://hotell.difi.no/api/json/mattilsynet/smilefjes/tilsyn?";
    private SpisestedAdapter spisestedAdapter;
    private RecyclerView spisestedRecyclerView;
    ArrayList<Spisested> spisestedArray;
    int favAr;
    private LocationManager locationManager;
    private String locationProvider = LocationManager.GPS_PROVIDER;
    private Location minPos;
    private static final int MY_REQUEST_LOCATION = 1;
    int permissionCheck, sokemodus;
    String postNummer= "";


    /**
     * onCreate kjøres  når aktiviteten åpnes.
     * Henter først inn RecyclerViewet fra activity_spise_sted_liste.xml
     * Sorter er en Spinner med dropdown funksjonalitet for sortering av svaret fra API.
     * @param savedInstanceState = null første gang siden åpnes. Holder styr på state.
     *                           Eks. Om telefonen er snudd eller ikke.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spise_sted_liste);
        spisestedRecyclerView = findViewById(R.id.recyclerView);

        Spinner sorter = findViewById(R.id.sortMeny);
        //Legger til forskjellige sorteringsvalg
        String[] valg = new String[]{"Alle år","2016", "2017", "2018", "2019"};

        //Legger til valg i sorter med en adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, valg );
        sorter.setAdapter(adapter);

        //Henter ut data fra favoritt år
        SharedPreferences sokeData = getSharedPreferences("sokeData", MODE_PRIVATE);
        favAr= sokeData.getInt("favAr", 0);

        //Legger til Listener på Spinner.
        sorter.setOnItemSelectedListener(this);
        //Finner ut hvordan søkemodus som skal brukes
        sokemodus = sokeData.getInt("sokeModus", 0);



        //Sjekker hvordan søk bruker gjør
        if(sokemodus == 1){
            hentSpisesteder();
        } else if(sokemodus == 2){
            if(favAr < 2014){
                hentSpisesteder();
            } else{
                sorteringDato(favAr);
            }
        } else if(sokemodus == 3){
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            //Sjekker om bruker har aktivert GPS
            if (!locationManager.isProviderEnabled(locationProvider)) {
                lagToast("Aktiver " + locationProvider + " under Posisjon i Innstillinger");
            } else {
                permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

                //Sjekker etter tillatelse fra bruker
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_LOCATION);
                } else{
                    minPos = locationManager.getLastKnownLocation(locationProvider);
                    geoSok(minPos);
                }
            }
        } else{
            lagToast("En feil oppstod prøv igjen");
        }

    }

    /**
     * Klasse som henter ut postnummer fra Kartverket sitt API.
     * Bruker også en egen onResponse metode for tolking av data.
     * Metoden lager også et adresse objekt av klassen Adresse{@link Adresse}
     * Sender så videre til hentSpisesteder() som søker etter spisesteder med samme postnummer
     * @param l = lokasjonen til brukes
     */
    private void geoSok(Location l) {

        if (l != null) {
            //Henter ut brukers pos
            String breddeGradString = Location.convert(l.getLatitude(),Location.FORMAT_DEGREES);
            String lengdeGradString = Location.convert(l.getLongitude(),Location.FORMAT_DEGREES);

            //konverterer komma(,) til punktum(.) i kordinatene
            breddeGradString = breddeGradString.replaceAll(",",".");
            lengdeGradString = lengdeGradString.replaceAll(",",".");
            String hentSted =
                    "https://ws.geonorge.no/adresser/v1/punktsok?radius=1000&lat=" + breddeGradString + "&lon="+ lengdeGradString +"&filtrer=adresser.postnummer&asciiKompatibel=true";

            if(isOnline()){
                RequestQueue queue = Volley.newRequestQueue(this);
                StringRequest geoRequest =
                        new StringRequest(Request.Method.GET, hentSted, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject responseAdresse = new JSONObject(response);
                                    JSONArray adresseArray = responseAdresse.getJSONArray("adresser");
                                    Adresse postNr = new Adresse(adresseArray.getJSONObject(0));
                                    postNummer = postNr.getPostnr();
                                    hentSpisesteder();
                                } catch (JSONException e){
                                    e.printStackTrace();

                                }
                            }
                        }, this);
                queue.add(geoRequest);
            }

        }

    }

    /**
     * Callback fra resultatet når man spør etter tillatelse.
     * @param requestCode = koden som er passert gjennom requestPremissions
     * @param permissions = Tillatelsen for spørringen
     * @param grantResults = resultatet, enten PERMISSION_GRANTED eller PERMISSION_DENIED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_REQUEST_LOCATION) {
            // Hvis bruker avviser tillatelsen vil arrayen grantResults være tom.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Tillatelse er gitt, ok å starte bruk av GPS
                permissionCheck = PackageManager.PERMISSION_GRANTED;
            } else {
                // Tillatelse er avvist fra bruker. Sender bruker tilbake til søkefelt.
                lagToast("Fikk ikke tilgang til GPS. Går tilbake.");
                Intent il = new Intent(this, MainActivity.class);
                startActivity(il);
            }
        }
    }

    /**
     * Håndtering av API spørring.
     *  hentSpisesteder er en string med spørringen, som bruker ENDPOINT og får lagt til forskjellige deler
     *  ettersom hva bruker spør etter.
     *  Henter først ut SharedPreferences for så å bruke det i spørringen.
     *
     */
    public void hentSpisesteder(){
        String hentSpisesteder = ENDPOINT;
        SharedPreferences sokeData = getSharedPreferences("sokeData", MODE_PRIVATE);
        String sokeNavn = "navn="+ sokeData.getString("navn", "");
        String postSted = "poststed=" + sokeData.getString("postSted", "");


        hentSpisesteder += sokeNavn + "&";
        hentSpisesteder += postSted;
        hentSpisesteder += "&postnr="+postNummer;
        //Om favar er ulikt 0 skal det brukes i spørringen.
        if(favAr != 0){
            hentSpisesteder += "&dato=*"+favAr;
        }

        //Om koblingen er online kjør VolleyRequest
        if (isOnline()) {
            //Legger til spørring i kø, for så å hente ut data med GET som metode.
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, hentSpisesteder, this, this);
            queue.add(stringRequest);
        }
    }

    /**
     * onResponse kjører når Volley har fått et svar fra API'et.
     * Parser først svaret fra string tyil et JSONObjekt
     * For så å legget alt inn i et JSONArray.
     * spisestedArray er for å kunne holde på de forskjellige objektene av Spisested {@link Spisested}
     * @param response = svaret fra API'et i string format.
     * @catch JSONException = Indikerer problemer med JSON API
     *                      Dette kan være ting som null som navn eller forsøk på å parse misslagde dokumenter
     */
    public void onResponse(String response) {
            try {
                JSONObject responseObjekt = new JSONObject(response);
                JSONArray responseArray = responseObjekt.getJSONArray("entries");
                spisestedArray = new ArrayList<>();
                //For-løkke for å gå igjennom svaret fra API'et og lage objekter ut av det.
                for(int i = 0; i< responseArray.length(); i++){
                    Spisested spisested = new Spisested(responseArray.getJSONObject(i));
                    spisestedArray.add(spisested);
                }
                //Metodekall på metoden oppdaterSPisestedListe
                oppdaterSpisestedListe(spisestedArray);

            }
            catch (JSONException e) {
                lagToast("Ugyldig JSON-data.");

            }

    }

    /**
     * Metoden oppretter adapter og setter adapter til Viewet.
     * Oppretter adapter av SpisestedAdapter {@link SpisestedAdapter}
     * Setter også LayoutManager som posisjonerer obejkter innenfor RecyclerViewet.
     * @param responseArray = array med objekter fra klassen Spisesteder generert fra API respons
     */
    private void oppdaterSpisestedListe(ArrayList<Spisested> responseArray) {
        spisestedAdapter = new SpisestedAdapter(this, responseArray);
            new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(spisestedRecyclerView);
        spisestedRecyclerView.setAdapter(spisestedAdapter);
        spisestedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Viser en Toast for bruker om det er noe feil med spørringen.
     * @param error = feil ved spørring
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        lagToast("Volley error");
    }

    /**
     * Klasse som sjekker om det er mulig å koble til API'et
     * @return boolean = kobling til API'et er true eller false.
     */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * ItemTouchHelper legger til swipe drag og drop til et RecyclerView
     * @params ItemTouchHelper.LEFT & ItemTouchHelper.RIGHT = dra fra
     */
    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        /**
         *
         * @param recyclerView = Valgt RecyclerView
         * @param viewHolder = holder innenfor SpisestedAdapter {@link SpisestedAdapter.SpisestedHolder} som skal beveges
         * @param viewHolder1  = ny viewholder som erstatter den gamle etter noe har blitt slettet
         * @return false
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        /**
         * Metoden gjør RecyclerLViewet swipeable
         * @param viewHolder = hva som skal være swipeable
         * @param i = hvilket items som blir swiped
         */
        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                //Oppretter ny RecyclerView som kan erstatte det gamle ettersom ting blir slettet
                final RecyclerView.ViewHolder viewHolder2 = viewHolder;

                /**
                 * onClick metode for om bruker faktisk vil slette det som blir swipet eller ikke.
                 * Case med Negativ og Positiv avhengig av hvilken knapp som blir trykket.
                 * Ettersom om bruker sletter eller ikke vil elementer bli fjernet eller lagt tilbake.
                 * @param dialog = Objekt som skal vises
                 * @param which = Hvilket objekt som skal fjernes fra listen
                 */
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            spisestedArray.remove(viewHolder2.getAdapterPosition());
                            spisestedAdapter = new SpisestedAdapter(SpiseStedListe.this, spisestedArray);
                            new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(spisestedRecyclerView);
                            spisestedRecyclerView.setAdapter(spisestedAdapter);
                            spisestedRecyclerView.setLayoutManager(new LinearLayoutManager(SpiseStedListe.this));
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            spisestedAdapter = new SpisestedAdapter(SpiseStedListe.this, spisestedArray);
                            new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(spisestedRecyclerView);
                            spisestedRecyclerView.setAdapter(spisestedAdapter);
                            spisestedRecyclerView.setLayoutManager(new LinearLayoutManager(SpiseStedListe.this));
                            break;
                    }
                }
            };


            //Oppretter en AlerDialog så bruker kan svare ja eller nei til sletting
            AlertDialog.Builder builder = new AlertDialog.Builder(SpiseStedListe.this);
            builder.setMessage("Vil du slette spisestedet?").setPositiveButton("Ja", dialogClickListener)
                    .setNegativeButton("Nei", dialogClickListener).show();
        }
    };


    /**
     *
     * @param parent = Hva som er adapterView av det som skal sjekkes for onItemSelected
     * @param view = Hvor det er
     * @param position = hvilken pos innenfor Spinner som er valgt
     * @param id = raden til det som blir selektert. brukes ikke side position kan brukes.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //Swtich case for å gjøre forskjellige sorteringer. Sendes videre til sorteringsDato
        switch (position){
            case 0: hentSpisesteder(); favAr = 0; break; //Setter favAr = 0 så man ikke sorterer
            case 1: sorteringDato(2016);     break;
            case 2: sorteringDato(2017);     break;
            case 3: sorteringDato(2018);     break;
            case 4: sorteringDato(2019);     break;
        }
    }

    /**
     * Kreves for å implementere AdapterView.OnItemSelectedListener, brukes ikke.
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Null
    }

    /**
     * Henter ut ny data fra API'et slik at det er sortert etter det bruker ønsker.
     * Switchen i onItemSelected er det eneste som bruker denne metoden.
     * @param aar = året som skal søkes etter
     * Etter kjøring vil onResponse parse svaret.
     */
    public void sorteringDato(int aar){
        String hentSpisesteder = ENDPOINT;
        SharedPreferences sokeData = getSharedPreferences("sokeData", MODE_PRIVATE);
        String sokeNavn = "navn="+ sokeData.getString("navn", "");
        String postSted = "poststed=" + sokeData.getString("postSted", "");
        //Legger til forskjellige spørringselementer.
        hentSpisesteder += sokeNavn + "&";
        hentSpisesteder += postSted + "&";
        hentSpisesteder +="dato=*"+aar;

        //Sjekker tilkobling før spørring.
        if (isOnline()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest =
                    new StringRequest(Request.Method.GET, hentSpisesteder, this, this);
            queue.add(stringRequest);
        }
    }

    /**
     * Metode for å lage Toast
     * @param s = Teksten i Toasten.
     */
    private void lagToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
