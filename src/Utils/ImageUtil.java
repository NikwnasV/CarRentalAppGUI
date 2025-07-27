/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.awt.Component;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author nikwn
 */
public class ImageUtil {

    /**
     * Opens a file chooser and returns the selected image file.
     */
    public static File chooseImage(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Car Image");
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Saves the image as PNG to the target path: src/resources/{carID}.png
     */
    public static String saveImage(File sourceFile, int carID) throws IOException {
        BufferedImage img = ImageIO.read(sourceFile);
        File dir = new File("src/resources/");
        if (!dir.exists()) dir.mkdirs();

        File target = new File(dir, carID + ".png");
        ImageIO.write(img, "png", target);
        return carID + ".png";
    }
}
