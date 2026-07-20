import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import Board from "./components/Board/Board";
import Controls from "./components/Controls/Controls";
import WinScreen from "./components/WinScreen/WinScreen";
import { API_BASE, opposite, emptyBoard } from "./config";
import "./styles/App.css";

function App() {
  const [mode, setMode] = useState("pve"); // "pve" | "pvp"
  const [humanColour, setHumanColour] = useState("red");
  const [difficulty, setDifficulty] = useState(5); // 1..5
  const [board, setBoard] = useState(emptyBoard);
  const [moveCount, setMoveCount] = useState(0); // total discs played (source of truth for turn)
  const [winner, setWinner] = useState(null); // null | "red" | "yellow" | "draw"
  const [isThinking, setIsThinking] = useState(false);
  const [isBusy, setIsBusy] = useState(false); // an animation/AI move is in flight
  const [gameId, setGameId] = useState(0);
  const [settingsOpen, setSettingsOpen] = useState(false);

  const gameOver = winner !== null;
  const aiColour = opposite(humanColour);
  const currentTurn = moveCount % 2 === 0 ? "red" : "yellow"; // red always moves first

  // Resets the backend, then applies all new game state in a single batch. Any change to mode or
  // colour must go through here so there is never a transitional render where the colour has
  // changed but the board/turn have not (that window used to fire the AI on stale state and freeze
  // the board). Optional mode/humanColour overrides are applied together with the reset.
  const startGame = useCallback(async (opts = {}) => {
    try {
      await axios.post(`${API_BASE}/reset`, {});
    } catch (error) {
      console.error("Error resetting game", error);
    }
    if (opts.mode !== undefined) setMode(opts.mode);
    if (opts.humanColour !== undefined) setHumanColour(opts.humanColour);
    setBoard(emptyBoard());
    setMoveCount(0);
    setWinner(null);
    setIsThinking(false);
    setIsBusy(false);
    setGameId((id) => id + 1);
  }, []);

  const newGame = useCallback(() => startGame(), [startGame]);

  useEffect(() => {
    startGame();
    // Run once on mount.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const undo = useCallback(async () => {
    if (isBusy || isThinking || moveCount === 0) return;
    // In Vs-AI mode, take back the human move and the AI reply so it is the human's turn again.
    const count = mode === "pve" ? Math.min(2, moveCount) : 1;
    try {
      const response = await axios.post(`${API_BASE}/undo?count=${count}`);
      setBoard(response.data);
      setMoveCount((c) => Math.max(0, c - count));
      setWinner(null);
    } catch (error) {
      console.error("Error undoing move", error);
    }
  }, [isBusy, isThinking, moveCount, mode]);

  // Changing mode or colour starts a fresh game; difficulty applies to the AI's next move.
  const handleModeChange = (nextMode) => {
    if (nextMode === mode) return;
    startGame({ mode: nextMode });
  };
  const handleColourChange = (nextColour) => {
    if (nextColour === humanColour) return;
    startGame({ humanColour: nextColour });
  };

  return (
    <div className="app">
      <button
        type="button"
        className="gear-button"
        aria-label="Settings"
        onClick={() => setSettingsOpen(true)}
      >
        <GearIcon />
      </button>

      <h1 className="app-title">Connect Four</h1>

      <StatusBar
        mode={mode}
        humanColour={humanColour}
        currentTurn={currentTurn}
        winner={winner}
        isThinking={isThinking}
      />

      <Board
        board={board}
        setBoard={setBoard}
        mode={mode}
        humanColour={humanColour}
        aiColour={aiColour}
        difficulty={difficulty}
        currentTurn={currentTurn}
        gameOver={gameOver}
        setWinner={setWinner}
        onMovePlayed={() => setMoveCount((c) => c + 1)}
        isThinking={isThinking}
        setIsThinking={setIsThinking}
        setIsBusy={setIsBusy}
        gameId={gameId}
      />

      <div className="action-bar">
        <button
          type="button"
          className="action-button"
          onClick={undo}
          disabled={isBusy || isThinking || moveCount === 0}
        >
          Undo move
        </button>
        <button
          type="button"
          className="action-button"
          onClick={newGame}
          disabled={isBusy}
        >
          Reset board
        </button>
      </div>

      {settingsOpen && (
        <div className="settings-overlay" onClick={() => setSettingsOpen(false)}>
          <div className="settings-panel" onClick={(e) => e.stopPropagation()}>
            <div className="settings-header">
              <h2>Settings</h2>
              <button
                type="button"
                className="settings-close"
                aria-label="Close settings"
                onClick={() => setSettingsOpen(false)}
              >
                &times;
              </button>
            </div>
            <Controls
              mode={mode}
              onModeChange={handleModeChange}
              humanColour={humanColour}
              onColourChange={handleColourChange}
              difficulty={difficulty}
              onDifficultyChange={setDifficulty}
            />
          </div>
        </div>
      )}

      <WinScreen winner={winner} onPlayAgain={newGame} />
    </div>
  );
}

function StatusBar({ mode, humanColour, currentTurn, winner, isThinking }) {
  let text;
  if (winner === "draw") {
    text = "It's a draw";
  } else if (winner) {
    text = `${cap(winner)} wins!`;
  } else if (isThinking) {
    text = "AI is thinking…";
  } else if (mode === "pve") {
    text = currentTurn === humanColour ? "Your turn" : "AI's turn";
  } else {
    text = `${cap(currentTurn)}'s turn`;
  }

  const showDisc = winner !== "draw";
  const activeColour = winner && winner !== "draw" ? winner : currentTurn;

  return (
    <div className="status-bar">
      {showDisc && <span className={`status-disc ${activeColour}`} aria-hidden="true" />}
      <span className="status-text">{text}</span>
    </div>
  );
}

function GearIcon() {
  return (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        fill="currentColor"
        d="M19.14 12.94a7.9 7.9 0 0 0 .06-.94 7.9 7.9 0 0 0-.06-.94l2.03-1.58a.5.5 0 0 0 .12-.64l-1.92-3.32a.5.5 0 0 0-.6-.22l-2.39.96a7.03 7.03 0 0 0-1.62-.94l-.36-2.54a.5.5 0 0 0-.5-.42h-3.84a.5.5 0 0 0-.5.42l-.36 2.54c-.59.24-1.13.56-1.62.94l-2.39-.96a.5.5 0 0 0-.6.22L2.29 8.84a.5.5 0 0 0 .12.64L4.44 11.06c-.04.31-.06.62-.06.94 0 .32.02.63.06.94l-2.03 1.58a.5.5 0 0 0-.12.64l1.92 3.32c.14.24.42.32.66.22l2.39-.96c.49.38 1.03.7 1.62.94l.36 2.54c.05.24.25.42.5.42h3.84c.25 0 .45-.18.5-.42l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.24.1.52.02.66-.22l1.92-3.32a.5.5 0 0 0-.12-.64l-2.03-1.58ZM12 15.5A3.5 3.5 0 1 1 12 8.5a3.5 3.5 0 0 1 0 7Z"
      />
    </svg>
  );
}

const cap = (s) => s.charAt(0).toUpperCase() + s.slice(1);

export default App;
