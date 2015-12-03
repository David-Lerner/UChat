package uchat.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uchat.main.Main;
import uchat.model.Client;

import java.io.IOException;
import java.util.HashMap;

public class InitialDashboardController implements Controller
{
  private Parent parent;
  private Scene scene;
  private Stage stage;
  private Client user;
  @FXML
  private Text userName;

  public InitialDashboardController() {
    FXMLLoader fxmlLoader = new FXMLLoader(
        getClass().getResource("/uchat/view/initialDashboardView.fxml")
    );
    fxmlLoader.setController(this);
    try {
      parent = fxmlLoader.load();
      scene = new Scene(parent, Main.PREF_WIDTH, Main.PREF_HEIGHT);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void changeStage(Stage stage, HashMap options) {
    stage.setScene(scene);
    user = (Client) options.get("user");
    userName.setText(user.name);
    this.stage = stage;
    stage.show();
  }

  @FXML
  private void handleJoin() {

  }

  @FXML
  private void handleHost() {
    //start server
    //connect self to server
    //change scenes
    HashMap options = new HashMap();
    options.put("user", user);
    new ChatRoomController().changeStage(stage, options);
  }
}
