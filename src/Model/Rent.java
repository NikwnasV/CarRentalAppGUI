/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author nikwn
 */
public class Rent {
    private int ID;
    private User user;
    private Car car;
    private LocalDateTime dateTime;
    private int days;
    private double total;
    private int status;

    /*
        Status => 0 Running
               => 1 Returned
    */
             
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    
    public Rent() {
        dateTime = LocalDateTime.now();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getDateTime() {
        return formatter.format(dateTime);
    }

    public void setDateTime(String dateTimeString) {
        this.dateTime = LocalDateTime.parse(dateTimeString, formatter);
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public LocalDateTime getRawDateTime() {
        return dateTime;
    }

}
