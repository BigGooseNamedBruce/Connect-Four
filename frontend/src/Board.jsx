import React, { useState } from "react";
import './Board.css'

function Board() {

    const [board, setBoard] = useState(Array(6).fill(Array(7).fill(null)));
    const [currentPlayer, setCurrentPlayer] = useState("red");

    const dropPiece = (col) => {
        for (let row = 5; row >= 0; row--) {
            if (!board[row][col]) {
                const newBoard = board.map((r, i) =>
                i === row
                    ? r.map((cell, j) => (j === col ? currentPlayer : cell))
                    : r
                );
                setBoard(newBoard);
                setCurrentPlayer(currentPlayer === "red" ? "yellow" : "red");
                return;
            }
        }
    };

    return (
        <div className="board">
            {board.map((row, rowIndex) => (
                row.map((cell, colIndex) => (
                <div
                    key={`${rowIndex}-${colIndex}`}
                    className={`cell ${cell || ""}`}
                    data-row={rowIndex}
                    data-col={colIndex}
                    onClick={() => dropPiece(colIndex)}
                ></div>
                ))
            ))}
        </div>
    );
}

export default Board;