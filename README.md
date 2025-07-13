# Elevator App

This is a simple elevator app that simulates the operation of an elevator system. It allows users to request the elevator to go to different floors and provides feedback on the current floor and direction of travel The elevator moves at a rate of 2000m per floor.

## How to Run

1. Clone the repository:
   ```bash
   git clone git@github.com:nateblain/elevator.git
   
2. Navigate to the project directory:
   ```bash
   cd elevator
   ```
   
3. Make sure you are have [Open JDK 24](https://jdk.java.net/24/) downloaded and installed.

4. From the project root, compile the Java files with:
   ```bash
    ./gradlew installDist   
   ```
   
5. Run the application with:
   ```bash
    ./build/install/elevator/bin/elevator
    ```
   
## Usage
- Possible commands are:
- `call <floor> up`: Request the elevator at a specific floor to go up.
- `call <floor> up`: Request the elevator at a specific floor to go down.
- `select <floor>`: Select a specific floor to go to.
- `status`: Get the current status of the elevator.
- `exit`: Exit the application.
- `help`: Display the help message with available commands.

While the elevator is moving, you new floors can be added to the elevator queues via the command line. The elevator exhausts an entire direction before moving in the opposite direction.
