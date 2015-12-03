package uchat.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uchat.main.CLIMain;
import uchat.main.Main;
import uchat.model.Client;

import java.io.IOException;
import java.util.HashMap;

public class InitialDashboardController implements Controller
{
  @FXML
  VBox buttonContainer;
  @FXML
  Button joinButton;
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
    TextField ipTextField = joinButtonToTextField();
    ipTextField.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent event) {
        String ip = "";
        if (ipTextField.getText().trim().equals("")) {
          ip = CLIMain.getLocalAddress();
        } else {
          ip = ipTextField.getText().trim();
        }
        ipTextField.clear();
        user = new Client(ip, Main.PORT, user.name, null);
        HashMap options = new HashMap();
        options.put("user", user);
        options.put("ip", ip);
        new ChatRoomController().changeStage(stage, options);
      }
    });

  }

  private TextField joinButtonToTextField() {
    buttonContainer.getChildren().remove(joinButton);
    TextField ipTextField = new TextField();
    ipTextField.setPrefSize(200, 35);
    ipTextField.setMaxWidth(200);
    ipTextField.setAlignment(Pos.CENTER);
    buttonContainer.getChildren().add(ipTextField);
    ipTextField.requestFocus();
    return ipTextField;
  }

  @FXML
  private void handleHost() {
    HashMap options = new HashMap();
    options.put("user", user);
    options.put("host", true);
    options.put("ip", "0.0.0.0");
    new ChatRoomController().changeStage(stage, options);
  }
}
