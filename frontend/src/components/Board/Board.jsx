import { useState, useEffect, useRef } from "react";
import axios from "axios";
import './Board.css'
import LoadingScreen from '../LoadingScreen/LoadingScreen'


const Board = ({board, setBoard, player, setPlayer}) => {
    //const [isLoadingScreenOpen, setLoadingScreenOpen] = useState(false);
    const isLoadingScreenOpen = useRef(false);
    const dropPiece = async (col, player) => {
      
      try {
        const response = await axios.post("http://localhost:8080/api/connectfour/move", {
         player: player,
         column: col,
        });
        setBoard(response.data);
        //setPlayer(player === "red" ? "yellow" : "red");

        console.log(response.data)
      } catch (error) {
        console.error('Error during computation:', error);
        alert('Something went wrong!');
      }
    };
  


    const bestMove = async (player) => {
      //setPlayer(player === "red" ? "yellow" : "red");
      const newPlayer = player === "red" ? "yellow" : "red";
      //setPlayer(newPlayer);
      isLoadingScreenOpen.current = true;
      
      console.log("HERE: " + player + " " + newPlayer);
      try {
        const response = await axios.post("http://localhost:8080/api/connectfour/compute", {
         player: player
        });
        //setPlayer(player === "red" ? "yellow" : "red");
        
        setBoard(response.data);
        //setPlayer(player === "red" ? "yellow" : "red");
        //dropPiece(response.data, newPlayer)
        console.log(response.data)
        isLoadingScreenOpen.current = false;
      } catch (error) {
        console.error('Error during computation:', error);
        alert('Something went wrong!');
      } finally {
        console.log("done");
      }
    };

    const place = (col, player) => {
      if (!isLoadingScreenOpen.current) {
        dropPiece(col, player);
        bestMove(player === "red" ? "yellow" : "red");
      }
    }
  

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
              //dropPiece(colIndex, player);
              //console.log(player);  
              //setLoadingScreenOpen(true);
              place(colIndex, player);
              //bestMove();
              //setLoadingScreenOpen(false);
              
            }}
          ></div>
        ))
      ))}
      <LoadingScreen isOpen={isLoadingScreenOpen.current}/>
    </div>
    
  );
}

export default Board;