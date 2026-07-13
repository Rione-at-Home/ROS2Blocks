package course.ros2blocks;

import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class Workspace extends VBox {
    private final List<RobotCommand> script = new ArrayList<>();

    public Workspace() {
        this.setStyle("-fx-border-color: gray; -fx-border-width: 2;");

        this.setOnDragOver(event -> {
            if (event.getGestureSource() != this) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        this.setOnDragDropped(event -> {
            // Logic to instantiate the block based on the dropped type
            // Add to workspace list
            event.setDropCompleted(true);
            event.consume();
        });
    }

    public void addCommand(RobotCommand cmd) {
        script.add(cmd);
        this.getChildren().add(new DraggableBlock(cmd));

    }


    public String exportToPython() {
        StringBuilder sb = new StringBuilder();

        for (RobotCommand cmd : script) {
            sb.append(cmd.getPythonCommand()).append("\n");

        }

        return sb.toString();

    }
}