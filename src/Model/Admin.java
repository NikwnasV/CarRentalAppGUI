/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Controller.AddNewAccount;
import Controller.AddNewCar;
import Controller.DeleteCar;
import Controller.UpdateCar;
import Controller.ViewCars;
import DatabaseConfig.Database;
import java.util.Scanner;

/**
 *
 * @author nikwn
 */
public class Admin extends User {
    
    private Operation[] operations = new Operation[] {new AddNewCar(), new ViewCars(), new UpdateCar(), new DeleteCar(), new AddNewAccount(2)};
    public Admin() {
        super();
    }
    
    @Override
    public void showList(Database database, Scanner sc) {
        System.out.println("\n1. Add New Car");
        System.out.println("2. View All Cars");
        System.out.println("3. Update Car");
        System.out.println("4. Delete Car");
        System.out.println("5. Add New Admin");
        System.out.println("6. Show Rents");
        System.out.println("7. Show Stats");
        System.out.println("0. Quit\n");
        
        int i = sc.nextInt();
        operations[i-1].operation(database, sc, this);
    }
    
}
