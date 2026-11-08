"use strict";

// Same logic as TicTacToe.java: minimax + alpha-beta pruning. O is the AI.

const LINES = [
  [0, 1, 2], [3, 4, 5], [6, 7, 8],
  [0, 3, 6], [1, 4, 7], [2, 5, 8],
  [0, 4, 8], [2, 4, 6],
];

const state = {
  board: Array(9).fill(""),
  turn: "X",
  over: false,
  ai: true,
  level: "hard",
  busy: false,
  score: { X: 0, O: 0, D: 0 },
};

// ---------- pure game logic ----------
function findWinner(b) {
  for (const [a, c, d] of LINES) {
    if (b[a] && b[a] === b[c] && b[c] === b[d]) return { player: b[a], line: [a, c, d] };
  }
  return null;
}

const isFull = (b) => b.every(Boolean);

function minimax(b, depth, isMax, alpha, beta) {
  const w = findWinner(b);
  if (w) return { score: w.player === "O" ? 10 - depth : depth - 10, move: -1 };
  if (isFull(b)) return { score: 0, move: -1 };

  let best = isMax ? -Infinity : Infinity;
  let move = -1;
  for (let i = 0; i < 9; i++) {
    if (b[i]) continue;
    b[i] = isMax ? "O" : "X";
    const s = minimax(b, depth + 1, !isMax, alpha, beta).score;
    b[i] = "";
    if (isMax) {
      if (s > best) { best = s; move = i; }
      alpha = Math.max(alpha, best);
    } else {
      if (s < best) { best = s; move = i; }
      beta = Math.min(beta, best);
    }
    if (beta <= alpha) break;
  }
  return { score: best, move };
}

function randomMove(b) {
  const free = b.map((v, i) => (v ? -1 : i)).filter((i) => i >= 0);
  return free[Math.floor(Math.random() * free.length)];
}

function chooseAIMove(b, level) {
  // Easy: always random. Medium: smart 50% of the time. Unbeatable: always smart.
  if (level === "easy") return randomMove(b);
  if (level === "medium" && Math.random() < 0.5) return randomMove(b);
  return minimax(b.slice(), 0, true, -Infinity, Infinity).move;
}

// ---------- UI ----------
const $ = (id) => document.getElementById(id);
const boardEl = $("board");
const statusEl = $("status");
const cellEls = [];

const markSVG = (p, cls) =>
  p === "X"
    ? `<svg viewBox="0 0 100 100" aria-hidden="true"><path class="mark x ${cls}" pathLength="100" d="M26 26 L74 74 M74 26 L26 74"/></svg>`
    : `<svg viewBox="0 0 100 100" aria-hidden="true"><circle class="mark o ${cls}" pathLength="100" cx="50" cy="50" r="24"/></svg>`;

function buildBoard() {
  for (let i = 0; i < 9; i++) {
    const btn = document.createElement("button");
    btn.className = "cell empty";
    btn.setAttribute("role", "gridcell");
    btn.setAttribute("aria-label", `Cell ${i + 1}, empty`);
    btn.addEventListener("click", () => onCell(i));
    boardEl.appendChild(btn);
    cellEls.push(btn);
  }
}

function saveScore() {
  try { localStorage.setItem("ttt-score", JSON.stringify(state.score)); } catch (e) { /* storage blocked: ignore */ }
}

function loadScore() {
  try {
    const s = JSON.parse(localStorage.getItem("ttt-score"));
    if (s && [s.X, s.O, s.D].every(Number.isInteger)) state.score = s;
  } catch (e) { /* ignore */ }
}

function renderScore() {
  $("sx").textContent = state.score.X;
  $("so").textContent = state.score.O;
  $("sd").textContent = state.score.D;
  $("lx").textContent = state.ai ? "X (You)" : "X";
  $("lo").textContent = state.ai ? "O (AI)" : "O";
}

function setStatus(text, color) {
  statusEl.textContent = text;
  statusEl.style.color = color || "";
}

function turnText() {
  if (state.ai) return state.turn === "X" ? "Your turn" : "Computer is thinking...";
  return `Player ${state.turn}'s turn`;
}

function paintGhosts() {
  cellEls.forEach((el, i) => {
    el.disabled = state.over || state.busy || !!state.board[i];
    if (!state.board[i]) {
      el.innerHTML = `<span class="ghost">${markSVG(state.turn, "")}</span>`;
    }
  });
}

function placeMark(i, p) {
  state.board[i] = p;
  const el = cellEls[i];
  el.classList.remove("empty");
  el.innerHTML = markSVG(p, "");
  el.setAttribute("aria-label", `Cell ${i + 1}, ${p}`);

  const w = findWinner(state.board);
  if (w) {
    state.over = true;
    w.line.forEach((k) => cellEls[k].classList.add("win"));
    state.score[w.player]++;
    const who = state.ai ? (w.player === "X" ? "You win!" : "Computer wins!") : `Player ${w.player} wins!`;
    setStatus(who + " 🎉", "var(--win)");
  } else if (isFull(state.board)) {
    state.over = true;
    state.score.D++;
    setStatus("It's a draw! 🤝", "var(--dim)");
  } else {
    state.turn = p === "X" ? "O" : "X";
    setStatus(turnText(), state.turn === "X" ? "var(--x)" : "var(--o)");
  }
  saveScore();
  renderScore();
  paintGhosts();
}

function onCell(i) {
  if (state.over || state.busy || state.board[i]) return;
  placeMark(i, state.turn);
  if (!state.over && state.ai && state.turn === "O") {
    state.busy = true;
    paintGhosts();
    setTimeout(() => {
      state.busy = false;
      if (!state.over) placeMark(chooseAIMove(state.board, state.level), "O");
    }, 380);
  }
}

function newGame() {
  state.board = Array(9).fill("");
  state.turn = "X";
  state.over = false;
  state.busy = false;
  cellEls.forEach((el, i) => {
    el.className = "cell empty";
    el.setAttribute("aria-label", `Cell ${i + 1}, empty`);
  });
  setStatus(turnText(), "var(--x)");
  paintGhosts();
}

function setMode(ai) {
  state.ai = ai;
  $("modeAI").classList.toggle("active", ai);
  $("modeHuman").classList.toggle("active", !ai);
  $("modeAI").setAttribute("aria-pressed", ai);
  $("modeHuman").setAttribute("aria-pressed", !ai);
  $("levels").classList.toggle("hidden", !ai);
  renderScore();
  newGame();
}

function setLevel(level) {
  state.level = level;
  document.querySelectorAll("[data-level]").forEach((b) => {
    const on = b.dataset.level === level;
    b.classList.toggle("active", on);
    b.setAttribute("aria-pressed", on);
  });
  newGame();
}

// ---------- start ----------
buildBoard();
loadScore();
renderScore();
$("modeAI").addEventListener("click", () => setMode(true));
$("modeHuman").addEventListener("click", () => setMode(false));
document.querySelectorAll("[data-level]").forEach((b) =>
  b.addEventListener("click", () => setLevel(b.dataset.level))
);
$("newGame").addEventListener("click", newGame);
$("resetScore").addEventListener("click", () => {
  state.score = { X: 0, O: 0, D: 0 };
  saveScore();
  renderScore();
  newGame();
});
newGame();
