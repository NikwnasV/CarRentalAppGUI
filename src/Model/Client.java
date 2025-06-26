/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import DatabaseConfig.Database;
import java.util.Scanner;

/**
 *
 * @author nikwn
 */
public class Client extends User {
    
    public Client() {
        super();
    }
    
    @Override
    public void showList(Database database, Scanner sc) {
        System.out.println("\n1. View Cars");
        System.out.println("2. Rent Car");
        System.out.println("3. Return Car");
        System.out.println("4. Show My Rents");
        System.out.println("5. Edit My Data");
        System.out.println("0. Quit\n");
    }
    
}
