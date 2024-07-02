package com.susarne.ihave2.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Taxon {
    @PrimaryKey()
    @NonNull
    private String arterId;
    private String taxonRang;
    private String videnskabeligtNavn;
    private String danskNavn;
    private String danskSynonym;
    private String artsGruppe;
    private String beskrivelse;
    private String taxonId;
    private String parentTaxonid;


    // Constructor, getters, and setters


    public Taxon() {
    }

    public Taxon(TaxonDto other) {
        this.arterId = other.getId();
        this.taxonRang = other.getTaxonrang();
        this.videnskabeligtNavn = other.getVidenskabeligt_navn();
        this.danskNavn = other.getAccepteret_dansk_navn();
        this.danskSynonym = other.getDansk_synonym();
        this.artsGruppe = other.getArtsgruppe();
        this.beskrivelse = other.getBeskrivelse();
        this.taxonId = other.getTaxonid();
        this.parentTaxonid = other.getParenttaxonid();
    }

    public String getArterId() {
        return arterId;
    }

    public void setArterId(String arterId) {
        this.arterId = arterId;
    }

    public String getTaxonRang() {
        return taxonRang;
    }

    public void setTaxonRang(String taxonRang) {
        this.taxonRang = taxonRang;
    }

    public String getVidenskabeligtNavn() {
        return videnskabeligtNavn;
    }

    public void setVidenskabeligtNavn(String videnskabeligtNavn) {
        this.videnskabeligtNavn = videnskabeligtNavn;
    }

    public String getDanskNavn() {
        return danskNavn;
    }

    public void setDanskNavn(String danskNavn) {
        this.danskNavn = danskNavn;
    }

    public String getDanskSynonym() {
        return danskSynonym;
    }

    public void setDanskSynonym(String danskSynonym) {
        this.danskSynonym = danskSynonym;
    }

    public String getArtsGruppe() {
        return artsGruppe;
    }

    public void setArtsGruppe(String artsGruppe) {
        this.artsGruppe = artsGruppe;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getTaxonId() {
        return taxonId;
    }

    public void setTaxonId(String taxonId) {
        this.taxonId = taxonId;
    }

    public String getParentTaxonid() {
        return parentTaxonid;
    }

    public void setParentTaxonid(String parentTaxonid) {
        this.parentTaxonid = parentTaxonid;
    }
}