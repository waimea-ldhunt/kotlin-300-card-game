# Success Criteria Checklist

This checklist can be used to gauge progress towards different levels of success. Note that to pass, *all* of the
Achieve Criteria must be met; for Merit, you need all Achieve, plus all the Merit Criteria, and so on for Excellence
too.

---

## Achieve Criteria

### Program Functionality

The game functions correctly and meets the specification:

- [x] The game is **playable**
- [x] The game has a **GUI** with a range of controls
- [x] The **instructions** for the game are available via the GUI
- [x] Player provides game **inputs via the GUI** (e.g. moves, actions)
- [x] The **current status** of the game is **shown in the GUI**, updated each player input
- [x] The game is based on a **non-trivial map**
- [x] The player can **move between locations**
- [x] The player has a **clear purpose** they can attempt to achieve
- [x] A **win / end / loss state** is possible

### Program Code

The program stores data using:

- [x] **Variables** of at least two types (e.g. int, text, boolean)

The program structure:

- [x] Defines and implements a **graphical user interface (GUI)**
- [x] Makes use of user-defined OOP **classes** from which **objects** are instantiated
- [x] Uses **functions** / **methods** to break up the code logically
- [x] Contains **sequences** of instructions
- [x] Uses **conditional** instructions to control the program flow
- [x] Uses program **branches** (if...else, when, etc.)
- [x] Uses **iteration** (loops) to repeat things (for, while, etc.)

### Program Documentation

The program code:

- [x] Is **indented** correctly
- [x] Is **laid-out clearly**
- [x] Contains **comments** that help in understanding how it works

### Program Testing

The program has been tested and there is documented evidence that:

- [x] The game is **fully functional**, showing testing of:
    - [x] game setup
    - [x] player movement
    - [x] other player actions
    - [x] scoring (if any)
    - [x] player win / lose states
    - [x] final feedback (if appropriate)
- [x] **Valid (expected) data** has been used to test **all data inputs**
- [x] Where tests have failed, **fixes** are discussed and **tests re-run**

---

## Merit Criteria

### Program Code

The program stores data using variables, where:

- [x] Variable **names are well-chosen** (relating to their purpose)
- [x] Variable names **begin with lowercase**
- [x] Naming is consistent, either **camelCase or snake_case**
- [x] Variables are **scoped appropriately** (global or local to a function)
- [x] **Global** variables are only used if **genuinely needed**

The program uses OOP classes and objects appropriately, where:

- [x] OOP class **names are well-chosen**, relating to their functionality
- [x] OOP class names **begin with uppercase**
- [x] OOP classes contain **well-chosen data fields**
- [x] OOP classes contain **well-chosen methods**

The program uses functions appropriately, where:

- [x] Function **names are well-chosen**, relating to their functionality
- [x] Functions use **parameters** to pass data into them as needed
- [x] Functions provide a **return value** to the calling code if needed

### Game GUI

The game GUI:

- [x] Adheres to **common conventions** (e.g. buttons look like buttons, etc.)

### Program Documentation

The program code is well-commented:

- [x] **JavaDoc Block comments** are used at the top of each **file** and **function**
- [x] Comments accurately describe the **function and behaviour** of the code:
    - [x] Defining the **purpose** of functions / key blocks of code
    - [x] Explaining the **reason** (the '**why**') of key parts of the code
    - [x] Defining function **parameters** and/or **return values**

### Program Testing

The program has been tested using:

- [x] A test plan **prepared in advance** of the coding
- [x] A test plan that **defines test data values** to be used
- [x] Test values that cover **boundary** (limit / edge) cases

---

## Excellence Criteria

### Program Code

Program flexibility and maintainability is aided by:

- [x] **Constants** being used to define key values, and used throughout the program
- [x] Variable values being **derived at run-time** (from other variables / constants)
- [x] Literal values only being used if they **make sense**, and they don't impact program flexibility

Program flexibility and maintainability is aided by good program structure:

- [x] Conditions, branching, loops and functions are used **effectively**
- [x] Procedures are **efficient** (minimal iterations, etc.)
- [x] **Minimal duplication of code** (instead using loops, or having multiple calls to a single function)
- [x] OOP classes are **well-chosen and logical**, with a clearly defined purpose
- [x] Functions are **well-chosen and logical**, with a clearly defined purpose
- [x] The classes and functions chosen represent a **logical decomposition** of the task

### Game GUI

The game GUI:

- [x] Is **well laid out** with care and attention paid to details
- [x] Gives a **good uer experience (UX)**

### Program Documentation

The program code:

- [x] Code is laid-out and organised **logically and concisely**

### Program Testing

The program has been tested to be **robust**:

- [x] With a **comprehensive and thorough** test plan
- [x] Testing **regularly** throughput development, allowing **time for debugging**
- [x] Using test values that cover **invalid** (unexpected) cases
- [x] Resulting in a program that **copes appropriately** with invalid inputs