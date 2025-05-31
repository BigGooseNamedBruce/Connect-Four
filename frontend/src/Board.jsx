import { useState } from "react";
import axios from "axios";
import './Board.css'

//function Board() {
const Board = ({board, setBoard, player, setPlayer}) => {
	//const [board, setBoard] = useState(Array(6).fill(null).map(() => Array(7).fill(null)));
	//const [player, setCurrentPlayer] = useState("red");
	console.log(board);
  const dropPiece = (col) => {
    axios
      .post("http://localhost:8080/api/connectfour/move", {
        player: player, // Send the current player (either "red" or "yellow")
        column: col,
      })
      .then((response) => {
        setBoard(response.data);
        setPlayer(player === "red" ? "yellow" : "red"); // Switch player
      })
      .catch((error) => {
        console.error("Error making move", error);
      });
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