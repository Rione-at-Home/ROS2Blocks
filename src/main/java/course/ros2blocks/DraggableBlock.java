package course.ros2blocks;

import javafx.scene.layout.HBox;
import javafx.scene.control.Label;

public class DraggableBlock extends HBox {
    private final RobotCommand command;

    public DraggableBlock(RobotCommand command) {
        this.command = command;
        this.setStyle("-fx-background-color: #add8e6; -fx-padding: 10; -fx-border-color: #000000; -fx-margin: 5;");
        this.getChildren().add(new Label(command.getDisplayName()));

    }

    public RobotCommand getCommand() { return command; }
}