package uchat.main;
//Commit Test (github test 1)

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import uchat.controller.WelcomeController;
import uchat.model.Client;

public class Main extends Application
{
  public static final int PREF_WIDTH = 550;
  public static final int PREF_HEIGHT = 350;
  public static final int MIN_WIDTH = 300;
  public static final int MIN_HEIGHT = 350;
  public static final double OPACITY = 0.95;
  public static final int PORT = 12345;
  Client user;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setOpacity(OPACITY);
    primaryStage.setMinWidth(MIN_WIDTH);
    primaryStage.setMinHeight(MIN_HEIGHT);
    new WelcomeController().launch(primaryStage);
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
});
  }
}
