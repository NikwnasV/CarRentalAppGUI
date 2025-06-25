/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author nikwn
 */
public class Admin extends User {
    
    public Admin() {
        super();
    }
    
    @Override
    public void showList() {
        System.out.println("\n1. Add New Car");
        System.out.println("2. View All Cars");
        System.out.println("3. Update Car");
        System.out.println("4. Delete Car");
        System.out.println("5. Add New Admin");
        System.out.println("6. Show Rents");
        System.out.println("7. Show Stats");
        System.out.println("0. Quit\n");
    }
    
}
