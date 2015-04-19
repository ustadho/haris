/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

/**
 *
 * @author ustadho.sb
 */
import org.jdesktop.swingx.graphics.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

/**
 * Images is a simple enum that provides access to the images in icons.zip
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since Jan 28, 2008, 12:50:35 PM
 */
public enum Images {

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// images
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
//  NetworkConnected("NetworkConnected.png"),
//  NetworkDisconnected("NetworkDisconnected.png"),
//  SearchTagihan("KDICT.png"),
//  Tumpukan("KDF.png"),
//  Pulpen("KATE.png"),
//  User("KUSER.png"),
//  Quit("quit.png");
  Book("book.png"),
  Books("books.png"),
  Communicate("Communicate.png"),
  CubeClass("cubes_class.png"),
  Diagram("Diagram.png"),
  Host("Host2.png"),
  Molecule("Molecule.png"),
  New_file("New_file.png"),
  Order("Order.png"),
  Pen("Pen.png"),
  Setting("Setting.png"),
  Shopping_Full("Shopping_Full.png")
  ;
  
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// data
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
String imagefilename;

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// constructor
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
Images(String name) {
  imagefilename = name;
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// methods
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
BufferedImage getImage() {
  try {
    return ImageIO.read(ClassLoader.getSystemResourceAsStream(imagefilename));
  }
  catch (IOException e) {
    return null;
  }
}

Icon getIcon() {
  return new ImageIcon(getImage());
}

BufferedImage getImage(int width, int height) {
  return GraphicsUtilities.createThumbnail(getImage(), width, height);
}

Icon getIcon(int width, int height) {
  return new ImageIcon(getImage(width, height));
}

}//end enum Images
