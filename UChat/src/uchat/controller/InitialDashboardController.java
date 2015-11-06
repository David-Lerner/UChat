package uchat.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uchat.main.Main;

import java.io.IOException;
import java.util.HashMap;

public class InitialDashboardController implements Controller
{
  private Parent parent;
  private Scene scene;
  private Stage stage;
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
    userName.setText((String) options.get("name"));
    stage.show();
  }
}
