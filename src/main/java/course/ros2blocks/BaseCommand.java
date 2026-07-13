package course.ros2blocks;

public class BaseCommand extends RobotCommand {
    private final String method;
    private double value;

    public BaseCommand(String method, double value) {
        this.method = method;
        this.value = value;

    }

    @Override
    public String getPythonCommand() {
        return String.format("self.robot.base.%s(%f)", method, value);

    }

    @Override
    public String getDisplayName() { return method + " (" + value + ")"; }

}