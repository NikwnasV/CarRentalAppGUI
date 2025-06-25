/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import DatabaseConfig.UserDAO;

/**
 *
 * @author nikwn
 */
public class CarRentalApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UserDAO userDAO = UserDAO.getInstance();
        userDAO.showList();
    }
    
}
