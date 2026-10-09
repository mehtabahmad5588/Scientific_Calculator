# Scientific Calculator PRO — Java Swing

A desktop Java Swing scientific calculator inspired by the supplied calculator UI screenshot.

## Requirements
- Java 11+ (Java 17+ recommended)
- No external libraries

## Run
### Windows
Double-click `run.bat`, or:
```bat
javac -d out src\com\example\scientificcalculator\Main.java
java -cp out com.example.scientificcalculator.Main
```

### Linux/macOS
```bash
chmod +x run.sh
./run.sh
```

## Included functionality
- Dark calculator-style Swing UI with display, mode/status bar, SHIFT/ALPHA controls, and orange DEL/AC keys.
- Arithmetic: `+ - * / % ^`
- Parentheses and constants `π`, `e`, `Ans`
- Trigonometric functions: sin, cos, tan and inverse functions
- `sqrt`, `abs`, `log`, `ln`, `exp`
- Square, reciprocal, factorial, sign toggle
- Scientific notation display toggle
- Basic answer/memory behavior
- Keyboard: Enter = evaluate, Esc = clear

Some advanced labels from the reference (matrix, vector, calculus, equation solving, etc.) are represented as UI entry points/placeholders rather than full CAS implementations.
