package org.firstinspires.ftc.teamcode.components.subComponents;

public class SequentialCommand implements Command {
    private Command[] commands;
    private int commandIndex;
    SequentialCommand(Command... commands) {
        this.commands = commands;
        commandIndex = 0;
    }

    /**
     * Start the first command
     */
    @Override
    public void start() {
        commands[commandIndex].start();
    }

    /**
     * Stop the entire command sequence
     */
    @Override
    public void stop() {
        for (Command command : commands) {
            command.stop();
        }
    }

    /**
     * Stop the current command
     * @return true if the command sequence has completed, false if it's still running
     */
    public boolean skipCommand() {
        commands[commandIndex++].stop();
        // start the next command
        if (commandIndex < commands.length) {
            commands[commandIndex].start();
        }
        return commandIndex >= commands.length;
    }

    /**
     * Update the current command, then iterate to the next command if it completed
     * @return true if the command sequence has completed, false if it's still running
     */
    @Override
    public boolean update() {
        boolean finished = commands[commandIndex].update();
        // start the next command on completion
        if (finished) {
            commandIndex++;
            if (commandIndex < commands.length) {
                commands[commandIndex].start();
            }
        }
        return commandIndex >= commands.length;
    }

}
