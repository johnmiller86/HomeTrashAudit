/**
 * Class to model a waste object.
 * @author John D. Miller
 * @version 1.0.3
 * @since 12/07/2015
 */
package com.johnmillercoding.hometrashaudit.waste;

public class Waste
{
    // Instance Variables
    private float amount;
    private String wasteType, wasteCategory, date, percentage;

    // Constructor accepting no arguments
    public Waste()
    {
        date = "";
        wasteType = "";
        wasteCategory = "";
        amount = 0;
        percentage = "";
    }


    // Arguments constructor
    public Waste(String date, String wasteType, String wasteCategory, float amount)
    {
        this.date = date;
        this.wasteType = wasteType;
        this.wasteCategory = wasteCategory;
        this.amount = amount;
        percentage = "";
    }

    /**
     * Gets the amount of the waste.
     * @return the weight.
     */
    public float getAmount()
    {
        return amount;
    }

    /**
     * Sets the amount of the waste.
     * @param amount the amount.
     */
    public void setAmount(float amount)
    {
        this.amount = amount;
    }

    /**
     * Gets the type of the waste.
     * @return the type.
     */
    public String getWasteMaterial()
    {
        return wasteType;
    }

    /**
     * Sets the waste type.
     * @param wasteType the type to be set.
     */
    public void setWasteMaterial(String wasteType)
    {
        this.wasteType = wasteType;
    }

    /**
     * Gets the waste category.
     * @return the category.
     */
    public String getWasteCategory()
    {
        return wasteCategory;
    }

    /**
     * Sets the waste category.
     * @param wasteCategory the category to be set.
     */
    public void setWasteCategory(String wasteCategory)
    {
        this.wasteCategory = wasteCategory;
    }

    /**
     * Gets the waste Date.
     * @return the Date.
     */
    public String getDate()
    {
        return date;
    }

    /**
     * Sets the waste Date.
     * @param date the date to set.
     */
    public void setDate(String date)
    {
        this.date = date;
    }

    /**
     * Gets the waste percentage.
     * @return the percentage.
     */
    public String getPercentage()
    {
        return percentage;
    }

    /**
     * Sets the waste percentage.
     * @param percentage the percentage to be set.
     */
    public void setPercentage(String percentage)
    {
        this.percentage = percentage;
    }

    @Override
    public String toString()
    {
        return this.getWasteMaterial()+ "    " + this.getWasteCategory()+ "    " + this.getAmount() + "    " + this.getPercentage();
    }
}
