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
import java.net.Socket;
import java.util.HashMap;

public class InitialDashboardController implements Controller
{
  @FXML
  VBox buttonContainer;
  @FXML
  Button joinButton;
  @FXML
  private Button nameButton;
  private Parent parent;
  private Scene scene;
  private Stage stage;
  private Client user;
  private String name;
  @FXML
  private Text userName;
  private HashMap options;

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
    this.options = options;
    stage.setScene(scene);
    name = (String) options.get("name");
    userName.setText(name);
    this.stage = stage;
    nameButton.setVisible(false);
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
        HashMap options = new HashMap();
//        options.put("user", user);
        options.put("name", name);
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
    options.put("name", name);
    options.put("host", true);
    options.put("ip", getLocalAddress());
    new ChatRoomController().changeStage(stage, options);
  }

  private static String getLocalAddress() {
    String temp = "0.0.0.0";
    try {
      Socket s = new Socket("www.google.com", 80);
      temp = s.getLocalAddress().getHostAddress();
      s.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return temp;
  }
  
  @FXML
  private void handleBack() {
      new WelcomeController().launch(stage);
  }
  
  @FXML
  private void handleNameButton() {
    TextField editNameTextField = nameButtonToTextField();
    editNameTextField.setOnAction(new EventHandler<ActionEvent>()
    {
      @Override
      public void handle(ActionEvent event) {
        user.sendMessage(".bye", null);
        name = editNameTextField.getText().trim();
        user = new Client((String) options.get("ip"), Main.PORT, name, null);
      }
    });
  }

  private TextField nameButtonToTextField() {
    buttonContainer.getChildren().remove(nameButton);
    TextField editNameTextField = new TextField();
    editNameTextField.setPrefSize(200, 20);
    editNameTextField.setMaxWidth(200);
    editNameTextField.setAlignment(Pos.TOP_CENTER);
    buttonContainer.getChildren().add(0, editNameTextField);
    editNameTextField.requestFocus();
    return editNameTextField;
  }
}
