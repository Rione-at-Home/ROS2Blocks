package course.ros2blocks;

import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class Workspace extends Pane {

    public Workspace() {

        this.setStyle("-fx-background-color: #F9F9F9; -fx-border-color: #E5E5E5; -fx-border-width: 2;");

        DraggableBlock startBlock = new DraggableBlock(new StartBlockCommand());
        startBlock.setLayoutX(150);
        startBlock.setLayoutY(50);
        this.getChildren().add(startBlock);
    }

    public void addCommand(RobotCommand cmd) {

        DraggableBlock newBlock = new DraggableBlock(cmd);

        newBlock.setLayoutX(400);
        newBlock.setLayoutY(50);
        this.getChildren().add(newBlock);
    }

    public String exportToPython() {
        StringBuilder sb = new StringBuilder();
        DraggableBlock startNode = null;

        for (var node : this.getChildren()) {
            if (node instanceof DraggableBlock block && block.getCommand() instanceof StartBlockCommand) {

                startNode = block;

                break;

            }
        }

        if (startNode != null) {
            buildScriptChain(startNode, sb);

        }

        return sb.toString();
    }

    private void buildScriptChain(DraggableBlock current, StringBuilder sb) {
        sb.append(current.getCommand().getPythonCommand()).append("\n");

        for (DraggableBlock child : current.getSnapChildren()) {

            buildScriptChain(child, sb);
        }
    }
}