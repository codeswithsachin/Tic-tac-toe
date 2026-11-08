# Tic-Tac-Toe

Tic-Tac-Toe with an unbeatable AI. Two versions of the same game:

- **Web version** (HTML, CSS, JavaScript) in the root folder. Play it in any browser.
- **Java Swing version** in the `java/` folder (desktop window).

## Features

- Play **vs Computer** or **vs Friend**
- 3 difficulty levels: Easy, Medium, Unbeatable
- AI uses **Minimax with Alpha-Beta Pruning**
- Score board (X, O, Draw) saved in the browser
- Ghost preview on hover, green highlight on the winning line
- Works on mobile and desktop, keyboard friendly

## Run the web version

Open `index.html` in your browser. No install needed.

## Run the Java version

You need JDK 8 or later.

```
cd java
javac TicTacToe.java
java TicTacToe
```

## Project structure

```
index.html     page
style.css      design
script.js      game logic + AI
java/          Java Swing version
```

## How the AI works

- It tries every possible game from the current board.
- O (AI) picks the move with the highest score, X (you) with the lowest.
- A faster win scores higher, a slower loss scores better.
- Alpha-Beta Pruning skips branches that cannot change the result.
- On **Unbeatable**, the best you can do is a draw.

## Live demo

Add your Vercel link here after deploying.
