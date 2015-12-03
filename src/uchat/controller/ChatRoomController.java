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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatRoomController implements Controller
{
  private Client user;
  private Parent parent;
  private Scene scene;
  private Stage stage;
  @FXML
  private ListView users;
  @FXML
  private Text userName;
  @FXML
  private TextField message;
  @FXML
  private ListView messageWindow;


  public ChatRoomController() {
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
    user = (Client) options.get("user");
    userName.setText(user.name);
    message.requestFocus();
    users.getItems().add(user.name);
    this.stage = stage;
    stage.show();
    Server server = new Server(Main.PORT);
    Timer t = new Timer(50, new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent ae) {
        ArrayList<Message> output = server.getMessages();
        for (Message m : output) {
          messageWindow.getItems().add(m.getText());
//          System.out.println(m.getText());
        }
      }
    });
    t.start();
    user = new Client("0.0.0.0", Main.PORT, user.name, null);
    Timer tt = new Timer(50, new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent ae) {
        ArrayList<Message> output = user.getMessages();
        for (Message m : output) {
          messageWindow.getItems().add(m.getName() + ": " + m.getText());
//          System.out.println(m.getName() + ": " + m.getText());
        }
      }
    });
    tt.start();

  }

  @FXML
  private void handleMessage() {
    user.sendMessage(message.getText(), null);
//    System.out.println(message.getText());
//    messageWindow.getItems().add(user.name + ": " + message.getText());
    message.clear();
  }
}
