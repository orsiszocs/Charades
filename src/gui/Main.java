package gui;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Charades");
        Controller controller=new Controller();
        HBox box=new HBox(20);
        box.setPadding(new Insets(10,10,10,10));
        GUI gui=new GUI(controller,box);
        primaryStage.setScene(new Scene(box, 620, 220));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
