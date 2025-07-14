/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author nikwn
 */

import Controller.RentCar;
import Controller.ReturnCar;
import Controller.ShowUserRents;
import Controller.ViewCars;

public class Client extends User {
    
    private final Operation[] operations = new Operation[] {
        new ViewCars(), 
        new RentCar(),
        new ReturnCar(),
        new ShowUserRents()};
    
    public Client() {
        super();
    }
    
    public Operation[] getOperations() {
        return operations;
    }
}
