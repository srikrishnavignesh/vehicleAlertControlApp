package com.ve.vehiclealertcontrolapp.traffic_personnel;
/* this is a model class*/
public class TrafficPersonnel {
    public String fname;
    public String lname;
    public String gen;
    public String date;
    public String grade;
    public TrafficPersonnel()
    {

    }
    public TrafficPersonnel(String fname,String lname,String gen,String date,String grade)
    {
        this.fname=fname;
        this.lname=lname;
        this.gen=gen;
        this.date=date;
        this.grade=grade;
    }
}
