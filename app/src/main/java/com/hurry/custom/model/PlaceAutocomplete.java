package com.hurry.custom.model;


public class PlaceAutocomplete {
    public CharSequence placeId;
    public CharSequence description;
    public CharSequence country;

    public PlaceAutocomplete(){

    }
    public PlaceAutocomplete(CharSequence placeId, CharSequence description, CharSequence country) {
        this.placeId = placeId;
        this.description = description;
        this.country = country;
    }

    @Override
    public String toString() {
        return description.toString();
    }

}
