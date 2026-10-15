package org.firstinspires.ftc.teamcode.components.core;

public class ParallelCommand implements Command {
    private Command[] commands;
    boolean[] commandStatus;

    ParallelCommand(Command... commands) {
        this.commands = commands;
        this.commandStatus = new boolean[commands.length];
    }

    /**
     * Starts every command
     */
    @Override
    public void start() {
        // start each command
        for (Command command : commands) {
            command.start();
        }
    }

    /**
     * Stops every command
     */
    @Override
    public void stop() {
        // stop each command
        for (Command command : commands) {
            command.stop();
        }
    }

    /**
     * Updates every command
     * @return true if every command has finished, false if any command is still running
     */
    @Override
    public boolean update() {
        // update each command
        boolean allFinished = true;
        for (int i = 0; i < commands.length; i++) {
            // run each command if it's still in action
            if (! commandStatus[i]) {
                boolean finished = commands[i].update();
                commandStatus[i] = finished;
                if (! finished) {
                    allFinished = false;
                }
            }
        }
        return allFinished;
    }
}
