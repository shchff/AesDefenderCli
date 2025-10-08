import java.util.Arrays;
import java.util.Optional;

public enum Command
{
    HIDE("hide"), EXTRACT("extract");

    private final String commandName;

    Command(String commandName)
    {
        this.commandName = commandName;
    }

    public String getCommandName()
    {
        return commandName;
    }

    public static Command fromCommandName(String commandName)
    {
        Optional<Command> command = Arrays.stream(values())
                .filter(c -> c.commandName.equals(commandName))
                .findAny();

        return command.orElseThrow(() -> new IllegalArgumentException("Такой команды нет"));
    }
}
