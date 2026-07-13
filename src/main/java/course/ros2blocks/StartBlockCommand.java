package course.ros2blocks;


public class StartBlockCommand extends RobotCommand {

    @Override
    public String getPythonCommand() { return "# Start of Script\n"; }

    @Override
    public String getDisplayName() { return "When Green Flag Clicked 🟢"; }

    @Override
    public double getValue() { return 0; }
}