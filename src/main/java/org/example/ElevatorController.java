package org.example;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

public class ElevatorController {
    private final Elevator elevator;
    private final ConcurrentSkipListSet<Integer> upFloorQueue = new ConcurrentSkipListSet<>();
    private final ConcurrentSkipListSet<Integer> downFloorQueue = new ConcurrentSkipListSet<>(Comparator.reverseOrder());
    private Thread worker;
    private final Logger logger;


    public ElevatorController(Elevator elevator, Terminal terminal, LineReader lineReader) {
        this.elevator = elevator;
        this.logger = new Logger(terminal, lineReader);
    }

    public synchronized void call(int floor, Direction dir) {
        if (floor == elevator.getCurrentFloor()) {
            this.logger.log(String.format("Elevator already at floor %d%n", floor));
            return;
        }
        if (floor < 0) {
            this.logger.log("Floor cannot be negative");
            return;
        }
        if (dir == Direction.UP) {
            upFloorQueue.add(floor);
        } else if (dir == Direction.DOWN) {
            downFloorQueue.add(floor);
        }
        // move the elevator if it's idle
        moveIfIdle();
    }

    public synchronized void selectFloor(int floor) {
        int currentFloor = elevator.getCurrentFloor();

        if (floor == currentFloor) {
            this.logger.log(String.format("Elevator already at floor %d%n", floor));
            return;
        }
        if (floor < 0) {
            this.logger.log("Floor cannot be negative");
            return;
        }
        this.logger.log(String.format("Selecting floor %d and current: %d%n", floor, currentFloor));
        if (floor > currentFloor) {
            upFloorQueue.add(floor);
        } else {
            downFloorQueue.add(floor);
        }
        // move the elevator if it's idle
        moveIfIdle();
    }

    public synchronized void moveIfIdle() {
        if (worker != null && worker.isAlive()) return;
        if (upFloorQueue.isEmpty() && downFloorQueue.isEmpty()) {
            return;
        }

        worker = new Thread(() -> {
            boolean isFirstRun = true;
            while (!upFloorQueue.isEmpty() || !downFloorQueue.isEmpty()) {
                Integer nextFloor = getNextFloor();
                int currentFloor = elevator.getCurrentFloor();

                if (nextFloor != null) {
                    if (nextFloor > currentFloor) {
                        elevator.moveUp();
                    } else if (nextFloor < currentFloor) {
                        elevator.moveDown();
                    }

                    boolean isStopInCurrentDirection =
                            (elevator.getDirection() == Direction.UP && upFloorQueue.contains(currentFloor)) ||
                                    (elevator.getDirection() == Direction.DOWN && downFloorQueue.contains(currentFloor));

                    if (isFirstRun) {
                        this.logger.log(String.format("Elevator starting at floor %d, moving %s...%n", currentFloor, elevator.getDirection()));
                        isFirstRun = false;
                    } else if (currentFloor != nextFloor && !isStopInCurrentDirection) {
                        this.logger.log(String.format("Passing floor %d [%s]%n", currentFloor, elevator.getDirection()));
                    }

                    if (isStopInCurrentDirection) {
                        arriveAtFloor(currentFloor);
                        elevator.openDoor();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        elevator.closeDoor();
                    }
                } else {
                    elevator.stop();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            elevator.stop();
        });
        worker.start();
    }

    private synchronized Integer getNextFloor() {
        Direction currentDirection = elevator.getDirection();
        int currentFloor = elevator.getCurrentFloor();

        if (currentDirection == Direction.UP) {
            Integer next = upFloorQueue.lower(currentFloor);
            if (upFloorQueue.contains(currentFloor)) {
                return currentFloor;
            } else if (next != null) {
                return next;
            }
            if (upFloorQueue.isEmpty() && !downFloorQueue.isEmpty()) {
                elevator.setDirection(Direction.DOWN);
                return downFloorQueue.first();
            }
            // Increment current floor as a base case so that we don't get stuck in an infinite loop/idle state when we reach floor in the opposite direction queue
            return currentFloor + 1;
        } else if (currentDirection == Direction.DOWN) {
            this.logger.log("currentFloor: " + currentFloor);
            Integer next = downFloorQueue.lower(currentFloor);
            if (downFloorQueue.contains(currentFloor)) {
                return currentFloor;
            } else if (next != null) {
                return next;
            }
            if (downFloorQueue.isEmpty() && !upFloorQueue.isEmpty()) {
                elevator.setDirection(Direction.UP);
                return upFloorQueue.first();
            }
            // Decrement current floor as a base case so that we don't get stuck in an infinite loop/idle state when we reach floor in the opposite direction queue
            return currentFloor - 1;
        } else {
            if (!upFloorQueue.isEmpty()) {
                return upFloorQueue.first();
            }
            if (!downFloorQueue.isEmpty()) {
                return downFloorQueue.first();
            }
        }
        return null;
    }

    private void arriveAtFloor(int floor) {
        upFloorQueue.remove(floor);
        downFloorQueue.remove(floor);
    }

    public void printStatus() {
        this.logger.log(String.format("Current floor: %d | Direction: %s | Door: %s%n",
                elevator.getCurrentFloor(), elevator.getDirection(), elevator.getDoorStatus()));
        this.logger.log("Up queue: " + upFloorQueue);
        this.logger.log("Down queue: " + downFloorQueue);
    }
}