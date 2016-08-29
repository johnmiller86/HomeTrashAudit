package com.johnmillercoding.hometrashaudit.waste;

/**
 * Class to model a plastic object.
 * @author John D. Miller
 * @version 1.0.3
 * @since 12/10/2015
 */

public class Plastic extends Waste
{

    // Casting Constructor
    public Plastic(Waste w)
    {
        super();
        this.setAmount(w.getAmount());
        this.setDate(w.getDate());
        this.setWasteCategory(w.getWasteCategory());
        this.setWasteMaterial(w.getWasteMaterial());
    }
}
