package com.kandidat23;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Klasse for å lage adapter til VisTilsyn sin RecyclerView
 */
public class TilsynAdapter extends RecyclerView.Adapter<TilsynAdapter.TilsynHolder> {

    /**
     * minInflater = LayoutInflater er et xml layout som legges inn i et View
     * tilsynListe er en liste med objekter av klassen Tilsyn {@link com.kandidat23.Tilsyn}
     */
    private LayoutInflater minInflater;
    private ArrayList<Tilsyn> tilsynListe;

    /**
     * Konstruktør metode for adapterklassen
     * @param context = contexten som adapteren skal leges inn i
     * @param tilsynListen =  liste med Spisested Objekter laget i {@link Tilsyn}
     */
    public TilsynAdapter(Context context, ArrayList<Tilsyn> tilsynListen) {
        minInflater = LayoutInflater.from(context);
        this.tilsynListe = tilsynListen;
    }

    /**
     * Kalles på når RecyclerView trenger en ny ViewHolder av en ,gitt type for å representere et objekt
     * @param parent = ViewGroup hvor det nye viewet blir lagt til etter det er bundet til en adapter
     * @param i = viewType, typen view på det nye View'et
     * @return et objekt av TilsynHolder som er en holder for adapter elementer
     */
    @Override
    public TilsynAdapter.TilsynHolder onCreateViewHolder(ViewGroup parent, int i) {
        View mItemView = minInflater.inflate(R.layout.activity_tilsyn_items, parent, false);
        return new TilsynHolder(mItemView);
    }

    /**
     * Kreves av RecyclerView for å vise data på spesifikke posisjoner.
     * @param holder = oppdateres for på holde på et gitt dataset i en gitt posisjon.
     * @param pos = posisjonen til gjenstand innenfor adapteren sitt datasett
     */
    @Override
    public void onBindViewHolder(TilsynAdapter.TilsynHolder holder, int pos) {
        Tilsyn tilsyn = tilsynListe.get(pos);
        TilsynHolder th = holder;

        Date datoDate = tilsyn.getDato();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String datoStr = dateFormat.format(datoDate);
        th.dato.setText(datoStr);
        th.kravpunktnavn.setText(String.valueOf(tilsyn.kravpunktnavn));

        //Sjekker om tilsyn sin tekst er tomt. Om det er tomt
        if(tilsyn.tekst.length()> 3){
            th.tekst.setText(tilsyn.tekst);
        } else {
            th.tekst.setText("Vurdert");
        }
        int karakter = tilsyn.getKarakter();

        //Switch setning for å legge til drawable bilde utifra karakter
        switch (karakter){
            case 0 : th.karakterBilde.setImageResource(R.drawable.smil_foreground);
            case 1 : th.karakterBilde.setImageResource(R.drawable.smil_foreground); break;
            case 2 : th.karakterBilde.setImageResource(R.drawable.strekfjes_foreground); break;
            case 3 : th.karakterBilde.setImageResource(R.drawable.missfornoyd_foreground); break;
            case 4 : th.karakterBilde.setImageResource(0);
            case 5 : th.karakterBilde.setImageResource(0); break;
        }

    }

    /**
     *
     * @return antall gjenstander i ArrayListen tilsynListe
     */
    @Override
    public int getItemCount() {
        return tilsynListe.size();
    }

    /**
     * Indreklasse for å holde objekter sammen innenfor en adapter
     */
    public class TilsynHolder extends RecyclerView.ViewHolder{

        /**
         * dato er dato for tilsynet
         * kravpunktnavn = vurdert, ikke vurdert eller blank.
         * tekst er hva som sjekkes
         * karakterBilde er bildet for karakteren
         */

        public TextView dato, tekst, kravpunktnavn;
        public ImageView karakterBilde;

        /**
         * Konstruktør som henter ut de forskjellige delene som skal være innenfor holder.
         * Alle forskjellige delene blir hentet utifra activity_tilsyn_items.xml
         * @param itemView = spesifiserer hvilket View som skal brukes
         */
        public TilsynHolder(View itemView) {
            super(itemView);
            this.dato = itemView.findViewById(R.id.dato);
            this.tekst = itemView.findViewById(R.id.tekst);
            this.kravpunktnavn = itemView.findViewById(R.id.kravPunkt);
            this.karakterBilde = itemView.findViewById(R.id.tilsynsBilde);
        }
    }
}
