#!/usr/bin/env bash
set -e
mkdir -p out
javac -d out src/com/example/scientificcalculator/Main.java
java -cp out com.example.scientificcalculator.Main
