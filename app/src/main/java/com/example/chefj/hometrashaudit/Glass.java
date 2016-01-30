package com.example.chefj.hometrashaudit;

/**
 * Class to model a glass object.
 * @author John D. Miller
 * @version 1.0.3
 * @since 12/10/2015
 */

public class Glass extends Waste
{
    public Glass()
    {
        super();
    }

    // Casting Constructor
    public Glass(Waste w)
    {
        super();
        this.setAmount(w.getAmount());
        this.setDate(w.getDate());
        this.setWasteCategory(w.getWasteCategory());
        this.setWasteMaterial(w.getWasteMaterial());
    }
}
