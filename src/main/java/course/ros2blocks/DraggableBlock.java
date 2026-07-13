package course.ros2blocks;

import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.geometry.Bounds;
import java.util.ArrayList;
import java.util.List;

public class DraggableBlock extends HBox {
    private final RobotCommand command;
    private TextField inputField;

    // Snapping hierarchy
    private DraggableBlock targetParent = null;
    private final List<DraggableBlock> children = new ArrayList<>();

    private double mouseAnchorX;
    private double mouseAnchorY;

    public DraggableBlock(RobotCommand command) {
        this.command = command;

        // Puzzle piece to allow blocks to snap onto each other
        String color = (command instanceof StartBlockCommand) ? "#4C97FF" :
                (command instanceof BaseCommand) ? "#5CB1D6" : "#9966FF";

        this.setStyle("-fx-background-color: " + color + "; " +
                "-fx-padding: 10 20 10 20; " +
                "-fx-background-radius: 8 8 8 8; " +
                "-fx-border-color: #333333; " +
                "-fx-border-radius: 8 8 8 8; " +
                "-fx-border-width: 1.5;");

        this.setPrefHeight(45);
        this.setMaxHeight(45);

        this.getChildren().add(new Label(command.getDisplayName()));

        if (command instanceof BaseCommand && !(command.getDisplayName().contains("wait"))) {

            inputField = new TextField(String.valueOf(((BaseCommand) command).getValue()));
            inputField.setPrefWidth(50);

            inputField.setStyle("-fx-background-radius: 4; -fx-padding: 2;");
            this.getChildren().add(inputField);

        }

        if (!(command instanceof StartBlockCommand)) {

            initDragAndDrop();

        }
    }

    private void initDragAndDrop() {

        this.setOnMousePressed(event -> {
            mouseAnchorX = event.getX();
            mouseAnchorY = event.getY();
            this.toFront();

            // Detach if previously snapped
            if (targetParent != null) {
                targetParent.children.remove(this);
                targetParent = null;

            }
        });

        this.setOnMouseDragged(event -> {

            double newX = this.getLayoutX() + event.getX() - mouseAnchorX;
            double newY = this.getLayoutY() + event.getY() - mouseAnchorY;

            this.setLayoutX(newX);
            this.setLayoutY(newY);

            // Move child blocks dynamically with the drag sequence
            moveChildren(event.getX() - mouseAnchorX, event.getY() - mouseAnchorY);

            checkSnapOpportunity();
        });
    }

    private void moveChildren(double deltaX, double deltaY) {

        for (DraggableBlock child : children) {

            child.setLayoutX(child.getLayoutX() + deltaX);
            child.setLayoutY(child.getLayoutY() + deltaY);

            child.moveChildren(deltaX, deltaY);

        }
    }

    private void checkSnapOpportunity() {

        Workspace workspace = (Workspace) this.getParent();
        if (workspace == null) return;

        double snapThreshold = 30.0;

        for (var node : workspace.getChildren()) {

            if (node instanceof DraggableBlock && node != this && !this.children.contains(node)) {

                DraggableBlock potentialParent = (DraggableBlock) node;

                Bounds parentBounds = potentialParent.getBoundsInParent();
                Bounds myBounds = this.getBoundsInParent();

                // Check proximity to bottom edge of target parent
                double distanceX = Math.abs(myBounds.getMinX() - parentBounds.getMinX());
                double distanceY = Math.abs(myBounds.getMinY() - parentBounds.getMaxY());

                if (distanceX < snapThreshold && distanceY < snapThreshold) {
                    // Snapping action execution
                    this.setLayoutX(parentBounds.getMinX());
                    this.setLayoutY(parentBounds.getMaxY() + 2); // 2px margin padding

                    this.targetParent = potentialParent;

                    if (!potentialParent.children.contains(this)) {
                        potentialParent.children.add(this);

                    }
                    break;
                }
            }
        }
    }

    public List<DraggableBlock> getSnapChildren() { return children; }
    public RobotCommand getCommand() { return command; }
}