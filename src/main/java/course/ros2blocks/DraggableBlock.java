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


    private DraggableBlock activeClone = null; // Purpose is to handle Ghos versions

    public DraggableBlock(RobotCommand command, boolean isPaletteTemplate) {
        this.command = command;
        this.isPaletteTemplate = isPaletteTemplate;

        String color = (command instanceof StartBlockCommand) ? "#4C97FF" :
                (command instanceof BaseCommand) ? "#4a90e2" : "#9966FF";

        this.setStyle("-fx-background-color: " + color + "; " +
                "-fx-padding: 8 15 8 15; " +
                "-fx-background-radius: 10 10 10 10; " +
                "-fx-border-color: " + color + "; " +
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

        // Handle clicking palette blocks to spawn and snap them automatically
        this.setOnMouseClicked(event -> {
            if (isPaletteTemplate && event.isStillSincePress()) {
                Workspace workspace = (Workspace) this.getScene().lookup("#workspace");
                if (workspace != null) {
                    // Find the bottom-most block in the active script chain
                    DraggableBlock bottomBlock = workspace.findBottomMostBlock();

                    double spawnX;
                    double spawnY;

                    if (bottomBlock != null) {

                        Bounds bounds = bottomBlock.getBoundsInParent();
                        spawnX = bounds.getMinX();
                        spawnY = bounds.getMaxY() + 1;

                    } else {
                        spawnX = 350;
                        spawnY = 100;

                    }

                    DraggableBlock spawned = workspace.spawnCommandAt(command, spawnX, spawnY);

                    if (bottomBlock != null) {
                        spawned.checkSnapOrTrash();
                    }
                }
                event.consume();
            }
        });

        this.setOnMousePressed(event -> {
            mouseAnchorX = event.getX();
            mouseAnchorY = event.getY();

            if (!isPaletteTemplate) {
                this.toFront();
                if (targetParent != null) {
                    targetParent.children.remove(this);
                    targetParent = null;
                }
            }
        });

        this.setOnMouseDragged(event -> {
            if (isPaletteTemplate) {
                Workspace workspace = (Workspace) this.getScene().lookup("#workspace");

                if (workspace != null) {
                    double sceneX = event.getSceneX();
                    double sceneY = event.getSceneY();
                    Bounds wsBounds = workspace.localToScene(workspace.getBoundsInLocal());

                    double spawnX = sceneX - wsBounds.getMinX() - mouseAnchorX;
                    double spawnY = sceneY - wsBounds.getMinY() - mouseAnchorY;

                    if (activeClone == null) {
                        activeClone = workspace.spawnCommandAt(command, spawnX, spawnY);

                    } else {
                        activeClone.setLayoutX(spawnX);
                        activeClone.setLayoutY(spawnY);
                        activeClone.checkSnapOrTrash();

                    }
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

        this.setOnMouseReleased(event -> {
            if (isPaletteTemplate && activeClone != null) {
                activeClone.handleMouseReleased();
                activeClone = null;
            }
        });
    }

    private void moveChildren(double deltaX, double deltaY) {

        for (DraggableBlock child : children) {
            child.setLayoutX(child.getLayoutX() + deltaX);
            child.setLayoutY(child.getLayoutY() + deltaY);
            child.moveChildren(deltaX, deltaY);

        }
    }

    public void checkSnapOrTrash() {
        Workspace workspace = (Workspace) this.getParent();

        if (workspace == null) return;

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