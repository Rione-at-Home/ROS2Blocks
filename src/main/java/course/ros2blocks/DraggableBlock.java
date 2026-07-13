package course.ros2blocks;

import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DraggableBlock extends HBox {
    private final RobotCommand command;
    private TextField inputField;

    public DraggableBlock(RobotCommand command) {
        this.command = command;
        this.setStyle("-fx-background-color: #add8e6; -fx-padding: 10; -fx-border-color: black;");
        this.getChildren().add(new Label(command.getDisplayName()));

        // Add input field if the command is parameterized (e.g., Forward, Left)
        if (command instanceof BaseCommand) {
            inputField = new TextField(String.valueOf(((BaseCommand) command).getValue()));
            inputField.setPrefWidth(50);
            this.getChildren().add(inputField);
        }

        // Setup Drag Detection
        this.setOnDragDetected(event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(command.getClass().getSimpleName()); // Simplified for demo
            db.setContent(content);
            event.consume();
        });
    }

    public double getInputValue() {
        return inputField != null ? Double.parseDouble(inputField.getText()) : 0;
    }
}