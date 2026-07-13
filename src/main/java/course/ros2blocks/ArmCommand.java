package course.ros2blocks;

public class ArmCommand extends RobotCommand {
    private final String method;

    public ArmCommand(String method) {
        this.method = method;

    }

    @Override
    public String getPythonCommand() {
        return String.format("self.robot.arm.%s()", method);

    }

    @Override
    public String getDisplayName() {
        return method;
    }

    @Override
    public double getValue() {
        return 0;
    }


}