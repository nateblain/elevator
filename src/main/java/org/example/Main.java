package org.example;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {
    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

        Elevator elevator = new Elevator(terminal, reader);
        ElevatorController controller = new ElevatorController(elevator, terminal, reader);

        while (true) {
            String line;
            try {
                line = reader.readLine("Enter command> ");
            } catch (UserInterruptException e) {
                // Ctrl-C, clean exit
                break;
            }

            if (line == null) continue;
            line = line.trim();
            if (line.equalsIgnoreCase("exit")) break;

            // parse & dispatch commands
            String[] parts = line.split("\\s+");
            try {
                if (parts[0].equalsIgnoreCase("call") && parts.length == 3) {
                    int floor = Integer.parseInt(parts[1]);
                    Direction dir = Direction.valueOf(parts[2].toUpperCase());
                    controller.call(floor, dir);
                } else if (parts[0].equalsIgnoreCase("select") && parts.length == 2) {
                    int floor = Integer.parseInt(parts[1]);
                    controller.selectFloor(floor);
                } else if (parts[0].equalsIgnoreCase("status")) {
                    controller.printStatus();
                } else if (parts[0].equalsIgnoreCase("help")) {
                    terminal.writer().println("Commands: call [floor] [UP/DOWN], select [floor], status, exit, help");
                } else {
                    terminal.writer().println("Invalid command");
                }
            } catch (Exception e) {
                terminal.writer().println("Error: " + e.getMessage());
            }
            terminal.flush();
        }

        terminal.close();
    }
}
