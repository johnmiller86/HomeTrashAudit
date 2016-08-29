package com.johnmillercoding.hometrashaudit.waste;

/**
 * Class to model a metal object.
 * @author John D. Miller
 * @version 1.0.3
 * @since 12/10/2015
 */

public class Metal extends Waste
{

    // Casting Constructor
    public Metal(Waste w)
    {
        super();
        this.setAmount(w.getAmount());
        this.setDate(w.getDate());
        this.setWasteCategory(w.getWasteCategory());
        this.setWasteMaterial(w.getWasteMaterial());
    }
}