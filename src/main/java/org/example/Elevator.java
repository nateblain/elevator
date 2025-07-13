package org.example;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

public class Elevator {
    private int currentFloor = 0;
    private Direction direction = Direction.IDLE;
    private DoorStatus doorStatus = DoorStatus.CLOSED;
    private final Logger logger;

    public Elevator(Terminal terminal, LineReader lineReader) {
        this.logger = new Logger(terminal, lineReader);
    }

    public void moveUp() {
        direction = Direction.UP;
        currentFloor++;
    }

    public void moveDown() {
        direction = Direction.DOWN;
        currentFloor--;
    }

    public void stop() {
        direction = Direction.IDLE;
    }

    public void openDoor() {
        this.logger.log(String.format("Arrived at floor %d...%nDoors opening...%n", currentFloor));
        doorStatus = DoorStatus.OPEN;
    }

    public void closeDoor() {
        this.logger.log("Doors closing...");
        doorStatus = DoorStatus.CLOSED;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public DoorStatus getDoorStatus() {
        return doorStatus;
    }
}