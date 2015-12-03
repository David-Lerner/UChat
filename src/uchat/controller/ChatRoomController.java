package uchat.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uchat.main.CLIMain;
import uchat.main.Main;
import uchat.model.Client;

import java.io.IOException;
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
    users.getItems().add(user.name);
    this.stage = stage;
    stage.show();
    CLIMain.hostChat();
  }

  @FXML
  private void writeMessage() {
    messageWindow.getItems().add(user.name + ": " + message.getText());
    message.clear();
  }
}
