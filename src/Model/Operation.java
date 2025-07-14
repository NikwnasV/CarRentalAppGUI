/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Model;

import DatabaseConfig.Database;
import java.util.Scanner;

/**
 *
 * @author nikwn
 */
public interface Operation {
    
    public void operation(Database dbconfig, Scanner sc, User user);
    
}
