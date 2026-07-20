import { useEffect, useRef } from "react";
import { flushSync } from "react-dom";
import axios from "axios";
import "./Board.css";
import { API_BASE, isBoardFull } from "../../config";

const COLS = 7;

const Board = ({
  board,
  setBoard,
  mode,
  humanColour,
  aiColour,
  difficulty,
  currentTurn,
  gameOver,
  setWinner,
  onMovePlayed,
  isThinking,
  setIsThinking,
  setIsBusy,
  gameId,
}) => {
  const boardRef = useRef(null);
  const discLayerRef = useRef(null);
  const fxLayerRef = useRef(null); // holds the animated falling disc (not reconciled by React)
  const busyRef = useRef(false); // synchronous mutex: one move/animation at a time
  const gameIdRef = useRef(gameId); // latest game id, for discarding stale AI moves

  const clearFxLayer = () => {
    const fx = fxLayerRef.current;
    if (fx) {
      while (fx.firstChild) {
        fx.removeChild(fx.firstChild);
      }
    }
  };

  useEffect(() => {
    gameIdRef.current = gameId;
  }, [gameId]);

  // A new game clears the move mutex and removes any leftover animation discs, so a stale
  // in-flight operation can never freeze input or leave floating pieces on the board.
  useEffect(() => {
    busyRef.current = false;
    clearFxLayer();
  }, [gameId]);

  // Remove any animation discs when the board unmounts.
  useEffect(() => {
    return () => clearFxLayer();
  }, []);

  // Row index of the most recently placed disc in a column (top-most filled cell).
  const landingRow = (b, col) => {
    for (let r = 0; r < b.length; r++) {
      if (b[r][col] !== null) {
        return r;
      }
    }
    return -1;
  };

  // Animates a single disc dropping into its landing cell. The disc element lives between the
  // white hole layer and the blue grid overlay, so it visually falls through the slots.
  const animateDrop = (col, colour, newBoard, token) =>
    new Promise((resolve) => {
      const fx = fxLayerRef.current;
      const layer = discLayerRef.current;
      const row = landingRow(newBoard, col);
      const target = layer
        ? layer.querySelector(`.hole[data-r="${row}"][data-c="${col}"]`)
        : null;

      if (!fx || !target) {
        if (token === undefined || token === gameIdRef.current) {
          setBoard(newBoard);
        }
        resolve();
        return;
      }

      const size = target.offsetWidth;
      const startTop = -size; // start just above the top of the board
      const endTop = target.offsetTop; // landing cell

      // --- Animation tunables: change these to tweak how the disc falls ------
      const SECONDS_PER_SQRT_PX = 0.024; // overall pace; higher = slower fall
      const MIN_DURATION = 0.28; // shortest drop (top row), seconds
      const MAX_DURATION = 0.75; // longest drop (bottom row), seconds
      // Timing curve. Try these:
      //   "cubic-bezier(0.3, 0.1, 0.4, 1)"  accelerate then soft landing (default)
      //   "cubic-bezier(0.25, 0.4, 0.4, 1)" gentle, almost-constant speed
      //   "cubic-bezier(0.5, 0, 0.75, 0)"   strong gravity (fast slam)
      //   "ease-in"                          simple built-in acceleration
      const EASING = "cubic-bezier(0.3, 0.1, 0.4, 1)";
      // ----------------------------------------------------------------------
      // Duration scales with sqrt(distance) so every disc falls under the same
      // "gravity" - far drops take longer instead of just moving faster.
      const distance = endTop - startTop;
      const duration = Math.min(
        MAX_DURATION,
        Math.max(MIN_DURATION, SECONDS_PER_SQRT_PX * Math.sqrt(distance))
      );

      const disc = document.createElement("div");
      disc.className = `disc falling ${colour}`;
      disc.style.width = `${size}px`;
      disc.style.height = `${size}px`;
      disc.style.left = `${target.offsetLeft}px`;
      disc.style.top = `${startTop}px`;
      disc.style.transition = `top ${duration}s ${EASING}`;
      fx.appendChild(disc);

      let finished = false;
      let safetyTimer = null;
      const finish = () => {
        if (finished) return;
        finished = true;
        if (safetyTimer !== null) {
          clearTimeout(safetyTimer);
        }
        // Only commit the settled disc if the game wasn't reset mid-drop, otherwise a
        // stale animation would overwrite the fresh (empty) board.
        if (token === undefined || token === gameIdRef.current) {
          // flushSync commits the settled disc to the DOM synchronously, so the static disc
          // is painted before we remove the floating one. Without this there is a one-frame
          // gap where neither exists, flashing the empty white hole.
          flushSync(() => setBoard(newBoard));
        }
        if (disc.parentNode) {
          disc.parentNode.removeChild(disc);
        }
        resolve();
      };

      requestAnimationFrame(() => {
        disc.getBoundingClientRect(); // force reflow so the transition runs
        disc.style.top = `${endTop}px`;
      });

      disc.addEventListener("transitionend", finish, { once: true });
      // Safety net in case transitionend does not fire (e.g. tab backgrounded).
      safetyTimer = setTimeout(finish, duration * 1000 + 300);
    });

  const checkWinner = async () => {
    try {
      const response = await axios.get(`${API_BASE}/winner`);
      return response.data || null;
    } catch (error) {
      console.error("Error fetching winner", error);
      return null;
    }
  };

  // After a disc settles: record the move (advances the turn) and end the game if it is over.
  // setWinner and onMovePlayed are batched into one render, so the AI effect sees the final
  // gameOver value and never replies on top of a winning move.
  const settle = async (newBoard) => {
    const winner = await checkWinner();
    if (winner) {
      setWinner(winner);
    } else if (isBoardFull(newBoard)) {
      setWinner("draw");
    }
    onMovePlayed();
  };

  const handleColumnClick = async (col) => {
    if (busyRef.current || gameOver) return;
    if (board[0][col] !== null) return; // column full
    const moverColour = mode === "pvp" ? currentTurn : humanColour;
    if (mode === "pve" && currentTurn !== humanColour) return;

    const startGameId = gameId;
    busyRef.current = true;
    setIsBusy(true);
    try {
      const response = await axios.post(
        `${API_BASE}/move`,
        { player: moverColour, column: col },
        { headers: { "Content-Type": "application/json", Accept: "application/json" } }
      );
      const newBoard = response.data.board;
      await animateDrop(col, moverColour, newBoard, startGameId);
      if (startGameId === gameIdRef.current) {
        await settle(newBoard);
      }
    } catch (error) {
      console.error("Error making move", error);
    } finally {
      if (gameIdRef.current === startGameId) {
        busyRef.current = false;
        setIsBusy(false);
      }
    }
  };

  // Drives the AI whenever it is the AI's turn (including the opening move when the human is yellow).
  useEffect(() => {
    if (mode !== "pve" || gameOver) return;
    if (currentTurn !== aiColour) return;
    if (busyRef.current) return;

    const startGameId = gameId;
    busyRef.current = true;
    setIsBusy(true);
    setIsThinking(true);

    (async () => {
      try {
        const response = await axios.post(`${API_BASE}/compute`, {
          player: aiColour,
          difficulty,
        });
        if (startGameId !== gameIdRef.current) return; // reset happened mid-think
        const col = response.data.column;
        const newBoard = response.data.board;
        await animateDrop(col, aiColour, newBoard, startGameId);
        if (startGameId !== gameIdRef.current) return;
        await settle(newBoard);
      } catch (error) {
        console.error("Error computing AI move", error);
      } finally {
        if (gameIdRef.current === startGameId) {
          setIsThinking(false);
          busyRef.current = false;
          setIsBusy(false);
        }
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [gameId, currentTurn, mode, aiColour, gameOver]);

  const inputLocked =
    gameOver || isThinking || busyRef.current || (mode === "pve" && currentTurn !== humanColour);

  return (
    <div className="board" ref={boardRef}>
      <div className="disc-layer" ref={discLayerRef}>
        {board.map((row, r) =>
          row.map((cell, c) => (
            <div key={`${r}-${c}`} className="hole" data-r={r} data-c={c}>
              {cell && <div className={`disc-static ${cell}`} />}
            </div>
          ))
        )}
      </div>

      {/* Falling discs are appended here imperatively. React never reconciles this layer's
          children, so manual DOM writes can't clash with React (which caused the glitch). */}
      <div className="fx-layer" ref={fxLayerRef} aria-hidden="true" />

      <div className="grid-overlay" aria-hidden="true" />

      <div className="click-layer">
        {Array.from({ length: COLS }).map((_, c) => (
          <button
            key={c}
            type="button"
            className="column-button"
            aria-label={`Drop in column ${c + 1}`}
            disabled={inputLocked || board[0][c] !== null}
            onClick={() => handleColumnClick(c)}
          />
        ))}
      </div>
    </div>
  );
};

export default Board;
