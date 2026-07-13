package course.ros2blocks;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RobotUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Workspace workspace = new Workspace();

        // Palette
        VBox palette = new VBox(10);
        Button btnForward = new Button("Forward (1.62m)");
        btnForward.setOnAction(e -> workspace.addCommand(new BaseCommand("forward", 1.62)));

        Button btnPick = new Button("Pick Can");
        btnPick.setOnAction(e -> workspace.addCommand(new ArmCommand("pick_can")));

        palette.getChildren().addAll(btnForward, btnPick);

        root.setLeft(palette);
        root.setCenter(workspace);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Ri-One@Home Scratch UI");
        primaryStage.show();
    }
}