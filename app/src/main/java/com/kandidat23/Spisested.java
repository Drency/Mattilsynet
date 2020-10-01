package com.kandidat23;


import org.json.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasse Spisested.
 * Oppretter et objekt fra et JSONObjekt med konstruktøren Spisested.
 * Brukes for å lettere behandle info fra API'et.
 * Blir brukt i SpiseStedListe {@link com.kandidat23.SpiseStedListe} og SpisestedAdapter {@link com.kandidat23.SpisestedAdapter}
 */
public class Spisested{

    /**
     * orgNr er organisasjonsnummeret til bedriften.
     * postNr er post nummeret til bedriften.
     * navn er navnet til bedriften.
     * adresse er adressen til bedriften.
     * postSted er post stedet til bedriften.
     * tilsynid er id'en som blir brukt ved forskjellige tilsyn.
     * totalkarakter er den totale karakteren som bedriften fikk etter tilsynet.
     * dato er dato for tilsynet.
     */
    int orgNr, postNr;
    String navn, adresse, postSted, tilsynid;
    int totalkarakter;
    Date dato;


    /**
     * Konstruktøren for å opprette klasseobjekt fra et JSONObjekt
     * @param spisested = JSONObjekt fra API spørring.
     *
     * @catch ParseException = signaliserer at en error har skjedd når programmet parser
     * Dato blir hentet inn som String også blir det parset til Date via SimpleDateFormat
     * Bruker eget format ddMMyyyy for å representere dager, månder og år.
     */
    public Spisested(JSONObject spisested){
        this.orgNr = spisested.optInt("orgnummer", -1);
        this.postNr = spisested.optInt("postnr");
        this.navn = spisested.optString("navn");
        this.adresse = spisested.optString("adrlinje1");
        this.postSted = spisested.optString("poststed");
        this.totalkarakter = spisested.optInt("total_karakter");
        this.tilsynid = spisested.optString("tilsynid");

        //Parsing fra String til Date
        try{
            String datoStr = spisested.optString("dato");
            DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
            this.dato = dateFormat.parse(datoStr);

        } catch(ParseException e){
            //Printer ut stack trace for bugchecking
            e.printStackTrace();
        }
    }

    /**
     *
     * @return orgNr til objektet
     */
    public int getOrgNr(){
        return this.orgNr;
    }

    /**
     *
     * @return postNr til objektet
     */
    public int getPostNr(){
        return this.postNr;
    }

    /**
     *
     * @return navnet til objektet
     */
    public String getNavn(){
        return this.navn;
    }

    /**
     *
     * @return adressen til objektet
     */
    public String getAdresse(){
        return this.adresse;
    }

    /**
     *
     * @return post stedet til objektet
     */
    public String getPostSted(){
        return this.postSted;
    }

    /**
     *
     * @return total karakter til objektet
     */
    public int getKarakter(){
        return this.totalkarakter;
    }

    /**
     *
     * @return tilsynsid'en til objektet
     * @uses for å finne en eller flere kravpunkt til en bedrift.
     */
    public String getTilsynid(){
        return this.tilsynid;
    }

    /**
     *
     * @return datoen for et tilsyn hos en bedrift
     */
    public Date getDato() {
        return dato;
    }

}

