package com.kandidat23;


import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasse som oppretter objekter til et Tilsyn
 * Brukes av VisTilsyn {@link VisTilsyn} og TilsynAdapter {@link TilsynAdapter}
 */
public class Tilsyn {
    /**
     * dato er dato for tilsynet
     * kravpunktnavn = vurdert, ikke vurdert eller blank.
     * Tekst er hva som sjekkes
     * karakter er karakteren for det som skulle sjekkes.
     */
    Date dato;
    String kravpunktnavn, tekst;
    int karakter;

    /**
     * Konstruktør for å opprette et objekt utifra data fra API'et
     * @param tilsyn = JSONObjekt som blir hentet fra API'et i VisTilsyn {@link VisTilsyn}
     * @catch ParseException = signaliserer at en error har skjedd når programmet parser
     */
    public Tilsyn(JSONObject tilsyn){
        this.kravpunktnavn = tilsyn.optString("kravpunktnavn_no");
        this.tekst = tilsyn.optString("tekst_no");
        this.karakter = tilsyn.optInt("karakter");

        //Parser data fra String til Date med SimpleDateFormat
        try{
            String datoStr = tilsyn.optString("dato");
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            this.dato = sdf.parse(datoStr);

        } catch (ParseException e) {

            e.printStackTrace();
        }
    }

    /**
     *
     * @return kravpunktet = stringen til objektet
     * @values Vurdert, ikke vurdert, blank
     */
    public String getKravpunktnavn(){
        return this.kravpunktnavn;
    }

    /**
     *
     * @return tekst, beskrivelse av hva tilsynet gikk ut på
     */
    public String getTekst(){
        return this.tekst;
    }

    /**
     *
     * @return datoen for tilsynet
     */
    public Date getDato(){
        return this.dato;
    }

    /**
     *
     * @return krakteren for tilsynet
     * @values 0, 1, 2, 3, 4, 5
     */
    public int getKarakter(){
        return this.karakter;
    }
}
