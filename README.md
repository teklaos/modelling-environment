# Modelling Environment

**Modelling Environment** is an application designed to help users create and simulate various models with specific data, enabling predictions of potential future changes.

## Key Features

- Uses Java reflection to easily add new models without modifying the core engine logic.
- Implements a custom annotation system to bind model fields to controller logic.
- Supports Groovy scripts for defining custom behavior and logic, allowing runtime extensibility and experimentation without recompilation.

## Installation

Follow these steps to install and run the **Modelling Environment** project:

1. Clone the repository to your local machine:
```
git clone https://github.com/teklaos/modelling-environment.git
```

2. Navigate into the project directory:
```
cd modelling-environment
```

3. Compile the code:
```
javac -cp lib/groovy-5.0.0-beta-1.jar -d out/production/ModellingEnvironment src/models/*.java src/*.java
```

4. Run the program:
```
java -cp "out/production/ModellingEnvironment;lib/*" Main
```
