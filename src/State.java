
import java.util.Iterator;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Daniel Moreno
 */

public class State extends Government{
    private OrderedAddOnce<City> cityList = new OrderedAddOnce <>();
    
    public State (String name) {
        super(name);
    }
    
    public int addCity (City newCity, int zip) {
        City tempCity = cityList.addOnce(newCity);
        return tempCity.addZipcode(zip);
    }
    
    public OrderedAddOnce<City> getCityList () {
        return cityList;
    }
    
    public int getNumOfZipcodes(){
        Iterator<City> iter = cityList.iterator();
        int numOfZipcodes = 0;
        while (iter.hasNext()) {
            numOfZipcodes += iter.next().getNumOfZipcodes();
        }
        return numOfZipcodes;
    }
    
    public int getNumOfCities(){
        return cityList.getLength();
    }
}
