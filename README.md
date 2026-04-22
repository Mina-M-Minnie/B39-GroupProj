# SwiftBot Integrated Program

## Overview
This project is a group integration task where multiple SwiftBot programs have been combined into one unified Java application.

The program uses a Command Line Interface (CLI) menu that allows the user to select and run different tasks developed by each group member.


## Features
- CLI menu system
- Multiple integrated programs


## Requirements
- Java JDK 17 or higher
- Maven (optional, if using pom.xml)
- SwiftBot device (if required)




## How to Run

### Option 1: Using an IDE
1. Open the project in IntelliJ or Eclipse
2. Navigate to:
   src/main/java/swiftbot/Main.java
3. Run the Main class

### Option 2: Using Maven
```
mvn clean compile
mvn exec:java -Dexec.mainClass="swiftbot.Main"
```

### Option 3: Using Terminal
Compile:
```
javac -d out src/main/java/swiftbot/**/*.java
```

Run:
```
java -cp out swiftbot.Main
```




## How to Use
When you run the program, a menu will appear:

```
SwiftBot Menu

1. SpyBot
2. Traffic Light
3. Snakes and Ladders
...
10. Exit
```

Steps:
1. Enter a number
2. Press Enter
3. The selected program runs
4. Return to the menu after completion



## Contributors
- Nafiul – SpyBot
- Eman – Traffic Light
- Mina – Snakes and Ladders
- Aaron – Search for Light
- Noor – Noughts and Crosses
- Saskia – Master Mind
- Arkin – Draw a Shape
- Naimul – ZigZag
- Dhruvesh – #