package com.kandidat23;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;


/**
 *Klasse for oppretting av adapter som legges inn i RecyclerView i activity_spise_sted_list.xml
 *
 */
public class SpisestedAdapter extends RecyclerView.Adapter<SpisestedAdapter.SpisestedHolder> {

    /**
     * minInflater = LayoutInflater er et xml layout som legges inn i et View
     * spisestedListe er en liste med objekter av klassen Spisested {@link com.kandidat23.Spisested}
     */
    private LayoutInflater minInflater;
    private ArrayList<Spisested> spisestedListe;

    /**
     * Konstruktør metode for adapterklassen
     * @param context = contexten som adapteren skal leges inn i
     * @param spisestedListen =  liste med Spisested Objekter laget i {@link Spisested}
     */
    public SpisestedAdapter(Context context, ArrayList<Spisested> spisestedListen) {
        minInflater = LayoutInflater.from(context);
        this.spisestedListe = spisestedListen;
    }

    /**
     * Kalles på når RecyclerView trenger en ny ViewHolder av en ,gitt type for å representere et objekt
     * @param parent = ViewGroup hvor det nye viewet blir lagt til etter det er bundet til en adapter
     * @param i = viewType, typen view på det nye View'et
     * @return et objekt av SpisestedHolder som er en holder for adapter elementer
     */
    @Override
    public SpisestedAdapter.SpisestedHolder onCreateViewHolder(ViewGroup parent, int i) {
        View mItemView = minInflater.inflate(R.layout.activity_adapter_items, parent, false);
        return new SpisestedHolder(mItemView);
    }

    /**
     * Kreves av RecyclerView for å vise data på spesifikke posisjoner. Oppdateres ettersom man sletter elementer fra listen.
     * @param holder = oppdateres for på holde på et gitt dataset i en gitt posisjon.
     * @param pos = posisjonen til gjenstand innenfor adapteren sitt datasett
     */
    @Override
    public void onBindViewHolder(SpisestedAdapter.SpisestedHolder holder, int pos) {

        Spisested spisested = spisestedListe.get(pos);
        SpisestedHolder sh = (SpisestedHolder) holder;

        //Henter ut forskjellige var som skal vises for bruker
        sh.navn.setText(spisested.navn);
        sh.postSted.setText(spisested.postSted);
        sh.orgNr.setText(String.valueOf(spisested.orgNr));
        sh.postNr.setText(String.valueOf(spisested.postNr));
        sh.addresse.setText(spisested.adresse);
        sh.tilsynIdAdapter = spisested.getTilsynid();
        int karakter = spisested.getKarakter();
        sh.spisestedNavn = spisested.getNavn();


        //Switch-case for å vise forskjellige bilder avhengig av karakter.
        switch (karakter){
            case 0 : sh.karakterBilde.setImageResource(R.drawable.smil_foreground); //Har ikke break; fordi den er lik til neste linje
            case 1 : sh.karakterBilde.setImageResource(R.drawable.smil_foreground); break;
            case 2 : sh.karakterBilde.setImageResource(R.drawable.strekfjes_foreground); break;
            case 3 : sh.karakterBilde.setImageResource(R.drawable.missfornoyd_foreground); break;
            case 4 : sh.karakterBilde.setImageResource(0); //Har ikke break; fordi den er lik til neste linje
            case 5 : sh.karakterBilde.setImageResource(0); break;

        }

    }

    /**
     *
     * @return antall gjenstander i ArrayListen spisestedListe
     */
    @Override
    public int getItemCount() {
        return spisestedListe.size();
    }

    /**
     * Indreklasse for å holde objekter sammen innenfor en adapter
     */
    public class SpisestedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * Forskjellige TextViews som skal vises innenfor adapteren. ImageView brukes for å kunne vise drawable bilde for karakter.
         * tilsynsAdapter og spisestedNavn brukes for å kunne sende informasjonen videre når bruker trykker inn på noe innenfor RecuclerViewet
         */
        private TextView navn, postSted, orgNr, postNr, addresse;
        public ImageView karakterBilde;
        public String tilsynIdAdapter, spisestedNavn;

        /**
         * Konstruktør som henter ut de forskjellige delene som skal være innenfor holder.
         * Alle forskjellige delene blir hentet utifra activity_adapter_items.xml
         * @param itemView = spesifiserer hvilket View som skal brukes
         */
        public SpisestedHolder(View itemView) {
            super(itemView);
            this.navn = itemView.findViewById(R.id.navn);
            this.postSted = itemView.findViewById(R.id.postSted);
            this.orgNr = itemView.findViewById(R.id.orgNr);
            this.postNr = itemView.findViewById(R.id.postNr);
            this.addresse = itemView.findViewById(R.id.addresse);
            this.karakterBilde = itemView.findViewById(R.id.karakterBilde);

            //Setter onClickListener på hvert element innenfor RecyclerViewet
            itemView.setOnClickListener(this);
        }

        /**
         * Context henter ut informasjon om applikasjonens context
         * vT sender bruker til neste aktivitet som er VisTilsyn {@link VisTilsyn}
         * Bruker context til å starte neste aktivitet siden adapterklassen ikke er koblet til en XML fil.
         * Legger med navn(navnet på bedriften som er trykket på) og id(tilsynsiden som trengs for å søke gjennom kravpunkt API)
         * Begge brukes i VisTilsyn {@link VisTilsyn}
         * @param v = Viewet som skal brukes.
         *          Henter ut Viewet som skal videresendes med v.getContext().
         */
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent vT = new Intent(context, VisTilsyn.class);
            vT.putExtra("id", this.tilsynIdAdapter);
            vT.putExtra("navn", this.spisestedNavn);
            context.startActivity(vT);
        }

    }
}
