import { useState, useEffect } from "react";
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import React from 'react';
import Board from './Board';
import axios from "axios";
import WinScreen from './WinScreen';

function App() {
  const [board, setBoard] = useState(Array(6).fill(null).map(() => Array(7).fill(null)));
  const [currentPlayer, setCurrentPlayer] = useState("red");
  const [isWinScreenOpen, setWinScreenOpen] = useState(true);
  const [winnerColour, setWinnerColour] = useState(null);


  const fetchBoardData = () => {
    axios
      .get("http://localhost:8080/api/connectfour/position")
      .then((response) => {
        setBoard(response.data); // Assuming the response contains the board as a 2D array
      })
      .catch((error) => {
        console.error("Error fetching board", error);
      });
  }

  const fetchWinner = () => {
    axios
      .get("http://localhost:8080/api/connectfour/winner")
      .then((response) => {
        console.log(response.data)
        if (response.data == "red") {
          setWinScreenOpen(true)
          setWinnerColour("Red")
        } else if (response.data == "yellow") {
          setWinScreenOpen(true)
          setWinnerColour("Yellow")
        }
      })
      .catch((error) => {
        console.error("Error fetching board", error);
      });
  }

  const reset = () => {
    axios
    .post("http://localhost:8080/api/connectfour/reset", {})
    .then(() => {
      setBoard(Array(6).fill(null).map(() => Array(7).fill(null)));
    }).catch((error) => {
      console.error("Error resetting", error);
    });
  }

  const dropPiece = (col) => {
    axios
      .post("http://localhost:8080/api/connectfour/move", {
        player: currentPlayer, // Send the current player (either "red" or "yellow")
        column: col,
      })
      .then((response) => {
        setBoard(response.data);
        setCurrentPlayer(currentPlayer === "red" ? "yellow" : "red"); // Switch player
      })
      .catch((error) => {
        console.error("Error making move", error);
      });
  };

  const openWinScreen = () => setWinScreenOpen(true);
  const closeWinScreen = () => setWinScreenOpen(false);


  // Fetch the current board state from the backend
  useEffect(() => {
    //fetchBoardData();
    reset();
    closeWinScreen();
  }, []); // Empty dependency array to run only once after component mount


  useEffect(() => {
    fetchWinner();
  }, [board]);  // Only run when the board changes



  return (
    <div className="board">
      {board.map((row, rowIndex) => (
        row.map((cell, colIndex) => (
          <div
            key={`${rowIndex}-${colIndex}`}
            className={`cell ${cell || ""}`} // Add cell color based on the player
            data-row={rowIndex}
            data-col={colIndex}
            onClick={() => dropPiece(colIndex)}
          ></div>
        ))
      ))}
      <WinScreen isOpen={isWinScreenOpen} colour={winnerColour}/>
    </div>
  );
}

export default App;

