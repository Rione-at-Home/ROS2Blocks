package course.ros2blocks;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RobotUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Workspace workspace = new Workspace();

        // TOP CONTROL BAR (With Execution Green Flag Button)
        HBox topBar = new HBox(15);
        topBar.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 10; -fx-border-color: #E5E5E5; -fx-border-width: 0 0 1 0;");

        Button btnRunFlag = new Button("🟢 Run Script");
        btnRunFlag.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnRunFlag.setOnAction(e -> {
            System.out.println("--- Executing Script Chain ---");
            System.out.println(workspace.exportToPython());
        });

        topBar.getChildren().add(btnRunFlag);
        root.setTop(topBar);

        // PALETTE CONTAINER (Left Distinct Workspace Sidebar)
        VBox palette = new VBox(12);
        palette.setPadding(new Insets(15));

        palette.setStyle("-fx-background-color: #2A2E3D; -fx-min-width: 220; -fx-max-width: 220;");

        // Populate items directly as authentic rendered Block shapes rather than buttons
        palette.getChildren().addAll(
                new DraggableBlock(new BaseCommand("forward", 1.62), true),
                new DraggableBlock(new BaseCommand("backward", 1.62), true),
                new DraggableBlock(new BaseCommand("left", 30), true),
                new DraggableBlock(new BaseCommand("right", 30), true),
                new DraggableBlock(new BaseCommand("wait", 5), true),

                new DraggableBlock(new ArmCommand("home"), true),
                new DraggableBlock(new ArmCommand("pick_can"), true),
                new DraggableBlock(new ArmCommand("lift"), true),
                new DraggableBlock(new ArmCommand("place_left"), true),
                new DraggableBlock(new ArmCommand("place_right"), true),
                new DraggableBlock(new ArmCommand("catapult"), true)
        );

        root.setLeft(palette);
        root.setCenter(workspace);

        primaryStage.setScene(new Scene(root, 950, 650));
        primaryStage.setTitle("Ri-One@Home Scratch UI");
        primaryStage.show();
    }
}