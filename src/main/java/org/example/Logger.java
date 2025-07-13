package org.example;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

public class Logger {
    private final Terminal terminal;
    private final LineReader lineReader;

    public Logger(Terminal terminal, LineReader lineReader) {
        this.terminal = terminal;
        this.lineReader = lineReader;
    }

    public void log(String message) {
        terminal.writer().flush();
        lineReader.printAbove(message);
    }
}
