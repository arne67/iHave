package com.susarne.ihave2.models;

import java.util.List;

public class GetUpdatedTaxonsDto {
    //#nytfelt
    private String updatedUntil;
    private List<TaxonDto> taxonDtos;

    public List<TaxonDto> getTaxonDtos() {
        return taxonDtos;
    }

    public void setTaxonDtos(List<TaxonDto> taxonDtos) {
        this.taxonDtos = taxonDtos;
    }

    public String getUpdatedUntil() {
        return updatedUntil;
    }

    public void setUpdatedUntil(String updatedUntil) {
        this.updatedUntil = updatedUntil;
    }
}