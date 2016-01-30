package com.example.chefj.hometrashaudit;

/**
 * Class to model a metal object.
 * @author John D. Miller
 * @version 1.0.3
 * @since 12/10/2015
 */

public class Metal extends Waste
{
    public Metal()
    {
        super();
    }

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