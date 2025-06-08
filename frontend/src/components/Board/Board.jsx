import { useState } from "react";
import axios from "axios";
import './Board.css'


const Board = ({board, setBoard, player, setPlayer}) => {

    const dropPiece = async (col) => {
      
      try {
        const response = await axios.post("http://localhost:8080/api/connectfour/move", {
         player: player,
         column: col,
        });
        setBoard(response.data);
        setPlayer(player === "red" ? "yellow" : "red");

        console.log(response.data)
      } catch (error) {
        console.error('Error during computation:', error);
        alert('Something went wrong!');
      }
    };
  


    const bestMove = async () => {
      
      try {
        const response = await axios.post("http://localhost:8080/api/connectfour/compute", {
         player: player === "red" ? "yellow" : "red"
        });

        console.log(response.data)
      } catch (error) {
        console.error('Error during computation:', error);
        alert('Something went wrong!');
      } finally {
        console.log("done");
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
            onClick={() => {
              dropPiece(colIndex);
              bestMove();
            }}
          ></div>
        ))
      ))}
    </div>
  );
}

export default Board;