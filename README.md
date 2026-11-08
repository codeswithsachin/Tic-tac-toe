# Tic-Tac-Toe — Java Swing

A clean, dark-themed Tic-Tac-Toe game with a smart AI opponent.

## Features

- **2-Player mode** — pass the keyboard between friends
- **vs AI mode** — unbeatable minimax AI with alpha-beta pruning
- **Ghost preview** — hover a cell to see where your symbol will land
- **Win highlighting** — winning cells glow green
- **Score tracking** — persists across games in the same session
- **Pixel-perfect rendering** — custom-painted cells, no default Swing chrome

---

## Prerequisites

You need a **JDK (Java Development Kit) 8 or later**.

| OS | Recommended download |
|---|---|
| Windows / Mac / Linux | https://adoptium.net (Temurin — free, LTS) |
| Windows alternative | https://www.oracle.com/java/technologies/downloads/ |

Verify after installing:
```
java -version
javac -version
```
Both commands must print a version ≥ 8.

---

## Quick Start (3 steps)

### Step 1 — Compile
```bash
# Navigate to the folder containing TicTacToe.java
cd path/to/tictactoe

# Compile
javac TicTacToe.java
```
This produces `.class` files (bytecode).

### Step 2 — Run directly
```bash
java TicTacToe
```

### Step 3 — Or build a runnable JAR (portable)
```bash
# Unix / macOS
chmod +x build.sh
./build.sh

# Windows — double-click build.bat  OR  run in CMD:
build.bat
```
This creates **TicTacToe.jar** — a single self-contained file.

---

## Running the JAR

```bash
# Any OS with Java installed
java -jar TicTacToe.jar
```

**Windows / macOS double-click:**
- Open "Default apps" / "Open With" and associate `.jar` with `javaw.exe`
- Then double-clicking `TicTacToe.jar` launches the window directly

---

## How to Play

| Action | Result |
|--------|--------|
| Click a cell | Place your symbol (X or O) |
| Hover a cell | See a ghost preview |
| **New Game** | Clear the board (scores kept) |
| **vs Human / vs AI ✓** | Toggle opponent mode |

X always goes first. In AI mode, you play as X.

---

## IDE Setup

### IntelliJ IDEA (recommended)
1. File → New → Project → Java → Next
2. Name the project `TicTacToe`, click Finish
3. Copy `TicTacToe.java` into `src/`
4. Right-click `TicTacToe.java` → **Run 'TicTacToe.main()'**

To build a JAR:
- File → Project Structure → Artifacts → `+` → JAR → From modules with dependencies
- Set Main Class to `TicTacToe` → OK
- Build → Build Artifacts → Build

### Eclipse
1. File → New → Java Project → name it `TicTacToe`
2. Drag `TicTacToe.java` into `src/` in Package Explorer
3. Right-click → Run As → Java Application

### VS Code
Install the **Extension Pack for Java** (Microsoft).
Then:
1. Open the folder containing `TicTacToe.java`
2. Click the ▶ Run button in the top-right of the editor

---

## Creating a Native Executable (Java 14+)

`jpackage` bundles a JRE + your app into a native installer (.exe, .dmg, .deb).

```bash
# 1. Compile first
javac TicTacToe.java

# 2. Create JAR
jar cfe TicTacToe.jar TicTacToe TicTacToe.class \
    'TicTacToe$Cell.class' 'TicTacToe$ScoreBox.class'

# 3. Package (Windows — produces .exe installer)
jpackage --input . --name TicTacToe --main-jar TicTacToe.jar \
         --main-class TicTacToe --type exe --win-shortcut

# 3. Package (macOS — produces .dmg)
jpackage --input . --name TicTacToe --main-jar TicTacToe.jar \
         --main-class TicTacToe --type dmg

# 3. Package (Linux — produces .deb)
jpackage --input . --name TicTacToe --main-jar TicTacToe.jar \
         --main-class TicTacToe --type deb
```

The output goes to the `./output/` directory. No Java installation needed
on the end-user's machine!

---

## Project Structure

```
tictactoe/
├── TicTacToe.java      ← single source file (all classes inside)
├── build.sh            ← compile + jar (Unix/Mac)
├── build.bat           ← compile + jar (Windows)
└── README.md           ← this file
```

After compiling, `.class` files appear beside the source. After running
the build script, `TicTacToe.jar` is also created.

---

## How the AI Works

The AI uses **Minimax** with **Alpha-Beta Pruning**:

- Every possible game tree is explored recursively.
- The AI (O) tries to **maximise** its score; the human (X) **minimises** it.
- A win for O scores `+10 − depth` (faster wins preferred).
- A win for X scores `depth − 10` (delaying loss).
- Alpha-Beta pruning discards branches that can't affect the result, making it instant.
- **The AI never loses** — at best you can draw by playing perfectly.

---

## Requirements Summary

| Requirement | Minimum | Notes |
|---|---|---|
| Java | 8+ | JDK for compiling; JRE for running JAR |
| `jpackage` | 14+ | Only needed for native exe/dmg/deb packaging |
| OS | Any | Windows, macOS, Linux — all supported |
| Dependencies | None | Pure Java — no external libraries |
