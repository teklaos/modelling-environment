# Modelling Environment

**Modelling Environment** is an application designed to help users create and simulate models using specific data values, enabling predictions of potential future changes.

## Key Features

- Leverages Java reflection to make it easy to add new models without changing the core engine logic
- Includes a custom annotation system for binding model fields to controller logic
- Supports Groovy scripts to define custom behavior and logic, enabling runtime extensibility and experimentation without recompilation

## Installation

Steps to install and run the **Modelling Environment** project:

1. Clone the repository to your local machine:
```
git clone https://github.com/teklaos/modelling-environment.git
```

2. Navigate to the project directory:
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
