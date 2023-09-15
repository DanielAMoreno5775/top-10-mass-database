/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

public class City extends Government{
    //define the data fields
    private OrderedAddOnce<Integer> zipcodes;
    private double latitude;
    private double longitude;
    private int timezone;          //relative to GMT
    private boolean yesDaylight;   //true if following daylight savings time
    private String standardTimeAbbreviation;
    
    //static constants for Fort Worth
    private static double fortWorthLatitude = 32.75736;
    private static double fortWorthLongitude = -97.3332;
    
    //constructor definition
    public City (String cName, double lat, double lon, int zone, boolean daylight) {
        super(cName);   //calls the Government constructor
        zipcodes = new OrderedAddOnce <>();
        latitude = lat;
        longitude = lon;
        timezone = zone;
        yesDaylight = daylight;
        
        //convert timezone and daylight savings time information into an abbreviated string
        if (yesDaylight) {
            if (timezone == -4) {
                standardTimeAbbreviation = "ADT";
            } else if (timezone == -5) {
                standardTimeAbbreviation = "EDT";
            } else if (timezone == -6) {
                standardTimeAbbreviation = "CDT";
            } else if (timezone == -7) {
                standardTimeAbbreviation = "MDT";
            } else if (timezone == -8) {
                standardTimeAbbreviation = "PDT";
            } else if (timezone == -9) {
                standardTimeAbbreviation = "AKDT";
            } else if (timezone == -10) {
                standardTimeAbbreviation = "HDT";
            }
        }
        else {
            if (timezone == -4) {
                standardTimeAbbreviation = "AST";
            } else if (timezone == -5) {
                standardTimeAbbreviation = "EST";
            } else if (timezone == -6) {
                standardTimeAbbreviation = "CST";
            } else if (timezone == -7) {
                standardTimeAbbreviation = "MST";
            } else if (timezone == -8) {
                standardTimeAbbreviation = "PST";
            } else if (timezone == -9) {
                standardTimeAbbreviation = "AKST";
            } else if (timezone == -10) {
                standardTimeAbbreviation = "HST";
            }
        }
    }
    
    //mutator method to add a new zip code to the city
    public Integer addZipcode(Integer zip){
        return zipcodes.addOnce(zip);
    }
    
    public int getNumOfZipcodes(){
        return zipcodes.getLength();
    }
    
    public int distanceToFortWorth(){
        //algorithm copied from the instructions
        double theta = fortWorthLongitude - this.longitude;
        double dist = Math.sin(Math.toRadians(fortWorthLatitude)) * Math.sin(Math.toRadians(this.latitude)) + (Math.cos(Math.toRadians(fortWorthLatitude)) * Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(theta)));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        
        return (int)(Math.ceil(dist));
    }
}