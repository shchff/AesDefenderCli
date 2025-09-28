package com.shchff;

import java.util.Arrays;
import java.util.Optional;

public enum Command
{
    PREPARE("prepare"),
    ENCODE("encode"),
    TRANSLATE("translate"),
    DECODE("decode");

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
