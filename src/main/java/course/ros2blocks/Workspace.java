package course.ros2blocks;

import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.geometry.Bounds;
import java.util.ArrayList;

public class Workspace extends Pane {
    private Label trashCan;

    public Workspace() {
        this.setId("workspace");
        this.setStyle("-fx-background-color: #F0F4F7; -fx-border-color: #D0D7DE; -fx-border-width: 2;");

        trashCan = new Label("🗑️ Trash");
        trashCan.setStyle("-fx-background-color: #FF4D4D; -fx-text-fill: white; -fx-padding: 15; " +
                "-fx-font-weight: bold; -fx-background-radius: 8;");
        trashCan.setLayoutX(20);
        trashCan.setLayoutY(500);
        this.getChildren().add(trashCan);

        DraggableBlock startBlock = new DraggableBlock(new StartBlockCommand(), false);
        startBlock.setLayoutX(100);
        startBlock.setLayoutY(60);
        this.getChildren().add(startBlock);

        this.setOnMouseReleased(event -> {

            for (var node : new ArrayList<>(this.getChildren())) {
                if (node instanceof DraggableBlock block) {
                    block.handleMouseReleased();
                }
            }
        });
    }

    public DraggableBlock spawnCommandAt(RobotCommand cmd, double x, double y) {
        DraggableBlock newBlock = new DraggableBlock(cmd, false);
        newBlock.setLayoutX(x);
        newBlock.setLayoutY(y);
        this.getChildren().add(newBlock);
        return newBlock;
    }

    public boolean isOverTrash(Bounds blockBounds) {
        return blockBounds.intersects(trashCan.getBoundsInParent());
    }

    public void removeBlockChain(DraggableBlock block) {
        for (DraggableBlock child : new ArrayList<>(block.getSnapChildren())) {
            removeBlockChain(child);
        }
        this.getChildren().remove(block);
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