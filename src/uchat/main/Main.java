package uchat.main;
//Commit Test
import javafx.application.Application;
import javafx.stage.Stage;
import uchat.controller.WelcomeController;

public class Main extends Application
{
  public static final int PREF_WIDTH = 550;
  public static final int PREF_HEIGHT = 350;
  public static final int MIN_WIDTH = 300;
  public static final int MIN_HEIGHT = 350;
  public static final double OPACITY = 0.85;


  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setOpacity(OPACITY);
    primaryStage.setMinWidth(MIN_WIDTH);
    primaryStage.setMinHeight(MIN_HEIGHT);
    new WelcomeController().launch(primaryStage);
  }


  public static void main(String[] args) {
    launch(args);
  }
}
