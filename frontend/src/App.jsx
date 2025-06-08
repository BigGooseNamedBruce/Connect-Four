import { useState, useEffect } from "react";
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import React from 'react';
import Board from './components/Board/Board';
import axios from "axios";
import WinScreen from './components/WinScreen/WinScreen';

function App() {
  const [board, setBoard] = useState(Array(6).fill(null).map(() => Array(7).fill(null)));
  const [player, setPlayer] = useState("red");
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
      setWinScreenOpen(null);
    }).catch((error) => {
      console.error("Error resetting", error);
    });
  }

  const openWinScreen = () => setWinScreenOpen(true);
  const closeWinScreen = () => setWinScreenOpen(false);



  useEffect(() => {
    reset();
    closeWinScreen();
  }, []);


  useEffect(() => {
    fetchWinner();
  }, [board]);



  return (
    <div>
      <Board 
        board={board} 
        setBoard={setBoard}
        player={player}
        setPlayer={setPlayer}
      />
      <WinScreen isOpen={isWinScreenOpen} colour={winnerColour}/>
    </div>
    
  );
}

export default App;

