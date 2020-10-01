package com.kandidat23;

import org.json.JSONObject;

/**
 * Klasse for å opprette objekt fra API spørring mot Kartverket
 */
public class Adresse {
    /**
     * postnummeret fra API
     */
    String postnr;

    /**
     * Konstruktør
     * @param adresse = JSONObjekt fra API spørring
     */
    public Adresse(JSONObject adresse){
        this.postnr = adresse.optString("postnummer");
    }

    /**
     *
     * @return objektet sitt postnummer
     */
    public String getPostnr(){
        return this.postnr;
    }
}
