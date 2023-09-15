/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Daniel Moreno
 */

import java.lang.Comparable;

public class Government implements Comparable<Government>{
    private String govName;
    
    //constructor
    public Government (String name) {
        govName = name;
    }
    
    public String getName(){
        return govName;
    }
    
    //toString that overrides default version
    @Override
    public String toString () {
        return String.format("%s",govName);
    }
    
    @Override
    public int compareTo (Government otherGov) {
        return this.getName().compareTo(otherGov.getName());
    }
}
