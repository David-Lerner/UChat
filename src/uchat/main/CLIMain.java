package uchat.main;

import uchat.model.Client;
import uchat.model.Message;
import uchat.model.Server;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Command line version of UChat
 *
 * @author David
 */
public class CLIMain
{
  static final int DELAY = 200;
  static final int PORT = 12345;
  static final String DEFAULT_IMAGE = "C:/Users/David/Documents/NetBeansProjects/UChat/src/uchat/images/defaultUser.png";
  static final String DEFAULT_NAME = "Anonymous";
  static String name;
  static BufferedImage pic;

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    name = DEFAULT_NAME;
    pic = null;
    try {
      pic = ImageIO.read(new File(DEFAULT_IMAGE));
    } catch (IOException e) {
      System.out.println("Failed to load image");
    }

    Welcome();
  }

  private static void Welcome() {
    boolean exit = false;
    while (! exit) {
      System.out.println("Welcome to UChat, " + name + ", a simple chat program");
      System.out.println("Get started by entering one of the following commands:");
      System.out.println("1: Host new chatroom and join it");
      System.out.println("2: Join chatroom");
      System.out.println("3: Change user name");
      System.out.println("4: View user image");
      System.out.println("5: Change user image");
      System.out.println("0: Exit");
      Scanner in = new Scanner(System.in);
      int input = in.nextInt();
      switch (input) {
        case 0:
          exit = true;
          break;
        case 1:
          hostChat();
          break;
        case 2:
          joinChat();
          break;
        case 3:
          changeName();
          break;
        case 4:
          viewImage();
          break;
        case 5:
          changeImage();
          break;
        default:
          break;
      }
    }

  }

  public static void hostChat() {
    Server server = new Server(PORT);
    Timer t = new Timer(DELAY, new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent ae) {
        ArrayList<Message> output = server.getMessages();
        for (Message m : output) {
          System.out.println(m.getText());
        }
      }
    });
    t.start();
    startChat("0.0.0.0", PORT);
  }

  private static void joinChat() {
    System.out.println("Enter ip for chatroom (leave blank if on same device)");
    Scanner in = new Scanner(System.in);
    String ip = in.nextLine();
    if (ip.equals("")) ip = "0.0.0.0";
    startChat(ip, PORT);
  }

  private static void changeName() {
    System.out.println("Current name: " + name);
    System.out.println("Enter your new name");
    Scanner in = new Scanner(System.in);
    name = in.nextLine();
    if (name.equals(""))
      name = DEFAULT_NAME;
  }

  private static void viewImage() {
    System.out.println("Image is in new popup window");
    JFrame f = new JFrame("User Image");
    ImageIcon image = new ImageIcon(pic);
    JLabel lbl = new JLabel(image);
    f.getContentPane().add(lbl);
    f.setSize(image.getIconWidth(), image.getIconHeight());
    f.pack();
    f.setVisible(true);

  }

  private static void changeImage() {
    BufferedImage temp;
    try {
      JFileChooser chooser = new JFileChooser();
      if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        temp = ImageIO.read(new File(chooser.getSelectedFile().getPath()));
        if (temp == null)
          throw new Exception();
        pic = temp;
        viewImage();
      } else
        throw new Exception();
    } catch (Exception e) {
      System.out.println("Error loading file");
    }
  }

  private static void startChat(String ip, int port) {
    Client client = new Client(ip, PORT, name, pic);
    Timer t = new Timer(DELAY, new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent ae) {
        ArrayList<Message> output = client.getMessages();
        for (Message m : output) {
          System.out.println(m.getName() + ": " + m.getText());
        }
      }
    });
    t.start();
    System.out.println("Welcome to UChat. Type any text and press enter to send a message");
    System.out.println("To send an image, end your message with a *");
    while (true) {
      Scanner in = new Scanner(System.in);
      BufferedImage temp = null;
      String input = in.nextLine();
      if (input.length() > 0) {
        if (input.charAt(input.length() - 1) == '*') {
          try {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
              temp = ImageIO.read(new File(chooser.getSelectedFile().getPath()));
              if (temp == null)
                throw new Exception();
            } else
              throw new Exception();
          } catch (Exception e) {
            System.out.println("Error loading file");
            temp = null;
          }
        }
        client.sendMessage(input, temp);
      }
      if (input.equals(".bye"))
        break;
    }
  }
}
