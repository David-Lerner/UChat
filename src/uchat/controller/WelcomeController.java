package uchat.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import uchat.main.Main;
import uchat.model.Client;

import java.io.IOException;
import java.util.HashMap;

public class WelcomeController
{
  private Parent parent;
  private Scene scene;
  private Stage stage;
  @FXML
  private TextField userName;

  public WelcomeController() {
    FXMLLoader fxmlLoader = new FXMLLoader(
        getClass().getResource("/uchat/view/welcomeView.fxml")
    );
    fxmlLoader.setController(this);
    try {
      parent = fxmlLoader.load();
      scene = new Scene(parent, Main.PREF_WIDTH, Main.PREF_HEIGHT);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void launch(Stage stage) {
    stage.setScene(scene);
    this.stage = stage;
    stage.show();
  }

  @FXML
  private void handleLogIn() {
    HashMap options = new HashMap();
    String name = userName.getText().trim();
    Client user = new Client("0.0.0.0", Main.PORT, name, null);
    options.put("user", user);
    new InitialDashboardController().changeStage(stage, options);
  }
}
