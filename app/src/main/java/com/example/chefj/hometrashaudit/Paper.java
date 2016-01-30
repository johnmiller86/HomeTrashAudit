package com.example.chefj.hometrashaudit;

/**
 * Class to model a paper object.
 * @author John D. Miller
 * @version 1.0.3
 * @since 12/10/2015
 */

public class Paper extends Waste
{
    public Paper()
    {
        super();
    }

    // Casting Constructor
    public Paper(Waste w)
    {
        super();
        this.setAmount(w.getAmount());
        this.setDate(w.getDate());
        this.setWasteCategory(w.getWasteCategory());
        this.setWasteMaterial(w.getWasteMaterial());
    }
}