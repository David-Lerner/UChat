package uchat.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uchat.main.Main;
import uchat.model.Client;
import uchat.model.Message;
import uchat.model.Server;
import uchat.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatRoomController implements Controller
{
  private Client user;
  private String name;
  private Parent parent;
  private Scene scene;
  private Stage stage;
  private Server server;
  @FXML
  private ListView users;
  @FXML
  private Text userName;
  @FXML
  private TextField message;
  @FXML
  private ListView messageWindow;


  public ChatRoomController() {
    server = null;
    FXMLLoader fxmlLoader = new FXMLLoader(
        getClass().getResource("/uchat/view/chatRoomView.fxml")
    );
    fxmlLoader.setController(this);
    try {
      parent = fxmlLoader.load();
      scene = new Scene(parent, Main.PREF_WIDTH, Main.PREF_HEIGHT);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void changeStage(Stage stage, HashMap options) {
    stage.setScene(scene);
//    user = (Client) options.get("user");
    name = (String) options.get("name");
    userName.setText(name);
    message.requestFocus();
    users.getItems().add("\n\n");
    users.getItems().add(name);
    this.stage = stage;
    stage.show();
    if (options.containsKey("host")) {
      server = new Server(Main.PORT);
      Timer t = new Timer(50, new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent ae) {
          ArrayList<Message> messages = server.getMessages();
          for (Message m : messages) {
            messageWindow.getItems().add(m.getText());
          }
          ArrayList<User> allUsers = server.getUsers();
          for (User u : allUsers) {
            if (! users.getItems().contains(u.getName())) {
              users.getItems().add(u.getName());
            }
          }
        }
      });
      t.start();
    }
    user = new Client((String) options.get("ip"), Main.PORT, name, null);
    Timer tt = new Timer(50, new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent ae) {
        ArrayList<Message> messages = user.getMessages();
        for (Message m : messages) {
          messageWindow.getItems().add(m.getName() + ": " + m.getText());
        }
      }
    });
    tt.start();

  }

  @FXML
  private void handleMessage() {
    if (message.getText().startsWith(".list") && server != null) {
      listUsers(server);
    } else if (message.getText().startsWith(".ban") && server != null) {
      banUser(server, message.getText());
    } else {
      user.sendMessage(message.getText(), null);
    }
    message.clear();
  }

  @FXML
  private void handleExit() {
    user.sendMessage(".bye", null);
    HashMap options = new HashMap();
    options.put("name", name);
    new InitialDashboardController().changeStage(stage, options);
  }

  private void listUsers(Server server) {
    messageWindow.getItems().add(
        "There are " + server.getUsers().size() + " users in the chat room."
    );
    messageWindow.getItems().add(String.format(
        "%-7s %-17s %-27s%n", "ID", "IP Address", "Name"
    ));
    for (User u : server.getUsers()) {
      messageWindow.getItems().add(String.format(
          "%-7s %-17s %-27s%n", u.getId(), u.getAddress(), u.getName()
      ));
    }
  }

  private User findUser(Client client, Server server) {
    for (User user : server.getUsers()) {
      System.out.println(user);
      if (user.getId() == Integer.parseInt(client.getID())) return user;
    }
    return null;
  }

  private void banUser(Server server, String input) {
    try {
      int ID = Integer.parseInt(input.substring(4).trim());
      server.banUser(ID);
      user.sendMessage(".bye", null);
    } catch (Exception e) {
      System.out.println("No such user ID");
    }
  }
}
