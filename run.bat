@echo off
if not exist out mkdir out
javac -d out src\com\example\scientificcalculator\Main.java
if errorlevel 1 pause & exit /b 1
java -cp out com.example.scientificcalculator.Main
