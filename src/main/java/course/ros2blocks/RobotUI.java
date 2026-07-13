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

        // Base Commands
        Button btnForward = new Button("Forward (1.62m)");
        btnForward.setOnAction(e -> workspace.addCommand(new BaseCommand("forward", 1.62)));

        Button btnBackward = new Button("Backward (1.62m");
        btnBackward.setOnAction(e -> workspace.addCommand(new BaseCommand("backward", 1.62)));

        Button btnLeft = new Button("Left (30)");
        btnLeft.setOnAction(e -> workspace.addCommand(new BaseCommand("left", 30)));

        Button btnRight = new Button("Right(30)");
        btnRight.setOnAction(e -> workspace.addCommand(new BaseCommand("right", 30)));

        Button btnWait = new Button("Wait(5)");
        btnWait.setOnAction(e -> workspace.addCommand(new BaseCommand("wait", 5)));


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