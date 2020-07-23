package com.ve.vehiclealertcontrolapp.display_crimes;

//a model class
public class Crime {
    public String registration_number;
    public String name;
    public String contact_no;
    public String details;
    public String imageuri;
    class location
    {
        public double latitude;
        public double longitude;
        location(double latitude,double longitude)
        {
            this.latitude=latitude;
            this.longitude=longitude;
        }
        public location()
        {

        }
    }
    public location lc;
    Crime()
    {

    }

}
