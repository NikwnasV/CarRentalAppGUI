/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Controller.ViewCars;
import DatabaseConfig.Database;
import java.util.Scanner;

/**
 *
 * @author nikwn
 */
public class Client extends User {
    
    private final Operation[] operations = new Operation[] {new ViewCars()};
    public Client() {
        super();
    }
    
    @Override
    public void showList(Database database, Scanner sc) {
    
        while (true) {
            System.out.println("\n==== Menu ====");
            System.out.println("1. View All Cars");
            System.out.println("2. Rent Car");
            System.out.println("3. Return Car");
            System.out.println("4. Show My Rents");
            System.out.println("5. Edit My Data");
            System.out.println("0. Quit\n");
            System.out.print("Select an option: ");

            int choice;
            try {
                choice = sc.nextInt();
                System.out.println();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number from 0 to 5.");
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
