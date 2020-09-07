package com.example.demo;

import android.graphics.Color;

import java.util.ArrayList;

public class Accelrometer {

    public double calculateIRI(ArrayList<Double> arr)
    {
        double sum=0;
       for(double i:arr)
       {
            sum+=i;
       }
    return sum;
    }

    /*public Color getColor(double iri)
    {
       Color lineColor;
        if(iri>0 && iri<=2)
        {
            lineColor.green();
        }
        return Color.(lineColor);
    }*/
}
