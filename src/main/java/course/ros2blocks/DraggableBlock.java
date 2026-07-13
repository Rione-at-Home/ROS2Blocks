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

    private DraggableBlock targetParent = null;
    private final List<DraggableBlock> children = new ArrayList<>();

    private double mouseAnchorX;
    private double mouseAnchorY;
    private final boolean isPaletteTemplate;

    public DraggableBlock(RobotCommand command, boolean isPaletteTemplate) {
        this.command = command;
        this.isPaletteTemplate = isPaletteTemplate;

        // Block Category colors
        String color = (command instanceof StartBlockCommand) ? "#4C97FF" : // Event Blue
                (command instanceof BaseCommand) ? "#4a90e2" : "#9966FF"; // Motion Blue / Arm Purple

        this.setStyle("-fx-background-color: " + color + "; " +
                "-fx-padding: 8 15 8 15; " +
                "-fx-background-radius: 10 10 10 10; " +
                "-fx-border-color: java.lang.Math.max(0,1) == 1 ? " + color + " : #333333; " +
                "-fx-border-width: 1; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 3, 0, 0, 2);");

        this.setPrefHeight(40);
        this.setMaxHeight(40);

        Label label = new Label(command.getDisplayName());
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Arial';");
        this.getChildren().add(label);

        if (command instanceof BaseCommand && !(command.getDisplayName().contains("wait"))) {
            inputField = new TextField(String.valueOf(((BaseCommand) command).getValue()));
            inputField.setPrefWidth(45);
            inputField.setStyle("-fx-background-radius: 5; -fx-padding: 2; -fx-border-width: 0;");
            this.getChildren().add(inputField);
        }

        initDragAndDrop();
    }

    private void initDragAndDrop() {

        // Event: if Block clicked from palette
        this.setOnMousePressed(event -> {
            mouseAnchorX = event.getX();
            mouseAnchorY = event.getY();
            this.toFront();

            if (targetParent != null) {
                targetParent.children.remove(this);
                targetParent = null;

            }
        });

        // Event: if Block dragged from palette into workspace
        this.setOnMouseDragged(event -> {
            if (isPaletteTemplate) {
                Workspace workspace = (Workspace) this.getScene().lookup("#workspace");
                if (workspace != null) {
                    double sceneX = event.getSceneX();
                    double sceneY = event.getSceneY();
                    Bounds wsBounds = workspace.localToScene(workspace.getBoundsInLocal());

                    DraggableBlock clone = workspace.spawnCommandAt(command, sceneX - wsBounds.getMinX() - mouseAnchorX, sceneY - wsBounds.getMinY() - mouseAnchorY);
                    event.consume();
                }
                return;
            }

            double newX = this.getLayoutX() + event.getX() - mouseAnchorX;
            double newY = this.getLayoutY() + event.getY() - mouseAnchorY;

            this.setLayoutX(newX);
            this.setLayoutY(newY);

            moveChildren(event.getX() - mouseAnchorX, event.getY() - mouseAnchorY);
            checkSnapOrTrash();

        });
    }

    private void moveChildren(double deltaX, double deltaY) {

        for (DraggableBlock child : children) {
            child.setLayoutX(child.getLayoutX() + deltaX);
            child.setLayoutY(child.getLayoutY() + deltaY);
            child.moveChildren(deltaX, deltaY);

        }
    }

    private void checkSnapOrTrash() {

        Workspace workspace = (Workspace) this.getParent();
        if (workspace == null) return;

        // Trash Can Collision Zone Check
        Bounds myBounds = this.getBoundsInParent();

        if (workspace.isOverTrash(myBounds)) {
            this.setStyle(this.getStyle() + "-fx-opacity: 0.5;");
            return;

        } else {
            this.setStyle(this.getStyle().replace("-fx-opacity: 0.5;", ""));

        }

        double snapThreshold = 35.0;
        for (var node : workspace.getChildren()) {

            if (node instanceof DraggableBlock && node != this && !this.children.contains(node)) {
                DraggableBlock potentialParent = (DraggableBlock) node;
                Bounds parentBounds = potentialParent.getBoundsInParent();

                double distanceX = Math.abs(myBounds.getMinX() - parentBounds.getMinX());
                double distanceY = Math.abs(myBounds.getMinY() - parentBounds.getMaxY());

                if (distanceX < snapThreshold && distanceY < snapThreshold) {
                    this.setLayoutX(parentBounds.getMinX());
                    this.setLayoutY(parentBounds.getMaxY() + 1);
                    this.targetParent = potentialParent;

                    if (!potentialParent.children.contains(this)) {
                        potentialParent.children.add(this);

                    }

                    break;
                }
            }
        }
    }

    public void handleMouseReleased() {
        Workspace workspace = (Workspace) this.getParent();
        if (workspace != null && workspace.isOverTrash(this.getBoundsInParent())) {
            workspace.removeBlockChain(this);
        }
    }

    public List<DraggableBlock> getSnapChildren() { return children; }
    public RobotCommand getCommand() { return command; }
}