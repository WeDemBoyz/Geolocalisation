package com.manhattanproject.geolocalisation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vince_000 on 25/02/2015.
 */
public class Utilisateur {
    int identifiant,duree;
    boolean partagePos;
    String pseudo;
    String mdp;
    String statut;
    LatLng position;

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }


    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String prenom) {
        this.pseudo = prenom;
    }

    public String getMdp() {
        return mdp;
    }


    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public int getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(int identifiant) {
        this.identifiant = identifiant;
    }

    public void setDuree(int duree){ this.duree = duree;}

    public int getDuree(){ return this.duree;}

    public boolean getPartagePos(){ return this.partagePos;}

    public void setPartagePos(boolean b){ this.partagePos = b;}

    public Utilisateur() {
        this.identifiant = -1;
        this.pseudo = "";
        this.mdp = "";
        this.statut = "";
        this.position = null;
        this.duree = -1;
        this.partagePos = false;
    }

    public Utilisateur( int identifiant, String pseudo, String mdp, String statut, LatLng position) {

        this.identifiant = identifiant;
        this.pseudo = pseudo;
        this.statut = statut;
        this.mdp = mdp;
        this.position = position;
    }

    public Utilisateur(Utilisateur u) {

        this.identifiant = u.identifiant;
        this.pseudo = u.pseudo;
        this.statut = u.statut;
        this.mdp = u.mdp;
        this.position = u.position;
    }

    public String toString(){
        return new String("Utilisateur "+this.identifiant+" :\n"+this.pseudo+"\nMot de passe:"+this.mdp+"\n"+"\n"+"statut : "+this.statut+"\n"+this.position);
    }

    public void save(Context c){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("identifiant", this.identifiant);
        editor.putString("pseudo", this.pseudo);
        editor.putString("mdp", this.mdp);
        editor.putString("statut",this.statut);
        editor.putFloat("latitude", (float)this.position.latitude);
        editor.putFloat("longitude",(float)this.position.longitude);
        editor.putInt("duree",this.getDuree());
        editor.putBoolean("partagePos",this.getPartagePos());
        editor.commit();
    }

    public void recup(Context c){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
        this.setIdentifiant(preferences.getInt("identifiant",-1));
        this.setPseudo(preferences.getString("pseudo", ""));
        this.setMdp(preferences.getString("mdp", ""));
        this.setStatut(preferences.getString("statut", ""));
        this.setPosition(new LatLng(preferences.getFloat("latitude",0.f),preferences.getFloat("longitude",0.f)));
        this.setDuree(preferences.getInt("duree",-1));
        this.setPartagePos(preferences.getBoolean("partagePos",false));
    }
}
