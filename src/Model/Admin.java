/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Controller.AddNewAccount;
import Controller.AddNewCar;
import Controller.DeleteCar;
import Controller.ShowRents;
import Controller.UpdateCar;
import Controller.ViewCars;
import DatabaseConfig.Database;
import java.util.Scanner;

/**
 *
 * @author nikwn
 */
public class Admin extends User {
    
    private Operation[] operations = new Operation[] {
        new AddNewCar(), 
        new ViewCars(), 
        new UpdateCar(), 
        new DeleteCar(), 
        new AddNewAccount(1),
        new ShowRents()};
    
    public Admin() {
        super();
    }
    
    @Override
    public void showList(Database database, Scanner sc) {
    
        while (true) {
            System.out.println("\n==== Admin Menu ====");
            System.out.println("1. Add New Car");
            System.out.println("2. View All Cars");
            System.out.println("3. Update Car");
            System.out.println("4. Delete Car");
            System.out.println("5. Add New Admin");
            System.out.println("6. Show Rents");
            System.out.println("7. Show Stats");
            System.out.println("0. Quit\n");
            System.out.print("Select an option: ");

            int choice;
            try {
                choice = sc.nextInt();
                System.out.println();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number from 0 to 7.");
                sc.nextLine(); // clear input buffer
                continue;
            }

            if (choice == 0) {
                System.out.println("Goodbye!");
                break;
            }

            if (choice < 1 || choice > operations.length) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            operations[choice - 1].operation(database, sc, this);
        }
    }
}
