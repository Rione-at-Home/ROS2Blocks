package course.ros2blocks;

import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class Workspace extends VBox {
    private final List<RobotCommand> script = new ArrayList<>();

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