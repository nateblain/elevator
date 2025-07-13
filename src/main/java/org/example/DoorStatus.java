package org.example;

public class DoorStatus {
    public static final DoorStatus OPEN = new DoorStatus("Open");
    public static final DoorStatus CLOSED = new DoorStatus("Closed");

    private final String status;

    private DoorStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
