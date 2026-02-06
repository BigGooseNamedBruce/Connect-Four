import { useState, useEffect, useRef } from "react";
import axios from "axios";
import './Board.css'
import LoadingScreen from '../LoadingScreen/LoadingScreen'


const Board = ({board, setBoard, player, setPlayer, isScreenFrozen, isWinScreenOpen, setIsScreenFrozen}) => {
        //const [isLoadingScreenOpen, setLoadingScreenOpen] = useState(false);
    //const isLoadingScreenOpen = useRef(false);
    const [isLoadingScreenOpen, setLoadingScreenOpen] = useState(false);

    const animationRef = useRef(null);
    const boardRef = useRef(null);
    const [lastMove, setLastMove] = useState(null);
    const discLayerRef = useRef(null);
    let frozenScreen = false;

    const updateBoard = (newBoard) => {
      boardRef.current = newBoard;
      setBoard(newBoard);
    }

    const deepcopy = (board) => {
      let boardCopy = [...board];
      for (let i = 0; i < boardCopy.length; i++) {
        boardCopy[i] = [...boardCopy[i]];
      }
      return boardCopy;
    }

    const drop = (board, player, col) => {
      let boardCopy = deepcopy(board);
      for (let row = boardCopy.length - 1; row >= 0; row--) {
        if (boardCopy[row][col] === null) {

          //boardCopy[row][col] = player;
          //setBoard(boardCopy);
          //updateBoard(boardCopy);
          //return boardCopy;
          return row;
        }
        
      }
      return -1;
    }

//     const animateDiscDrop = (col, row, color, onComplete) => {
//   if (!boardRef.current) return;

//   // Select the top cell in the target column to get size and position
//   const firstCell = boardRef.current.querySelector(`[data-row="0"][data-col="${col}"]`);
//   if (!firstCell) return;

//   // Create the disc element with correct color class
//   const disc = document.createElement("div");
//   disc.className = `disc ${color}`;

//   // Calculate size and gap for positioning
//   const cellSize = firstCell.offsetHeight;
//   const gapSize = parseFloat(getComputedStyle(boardRef.current).gap) || 0;

//   // Initial styles for disc (start above the board)
//   disc.style.position = "absolute";
//   disc.style.width = `${cellSize}px`;
//   disc.style.height = `${cellSize}px`;
//   disc.style.borderRadius = "50%";
//   disc.style.left = `${firstCell.offsetLeft}px`;
//   disc.style.top = `-12vh`; // start above board (adjust if needed)
//   disc.style.zIndex = 10;
//   disc.style.transition = "top 0.5s ease-out";

//   // Append the disc to the board container
//   boardRef.current.appendChild(disc);

//   // Calculate target top position for disc (row * (cellSize + gap))
//   const targetTop = row * (cellSize + gapSize);

//   // Trigger the animation on next frame
//   requestAnimationFrame(() => {
//     disc.style.top = `${targetTop}px`;
//   });

//   // Cleanup after animation ends
//   disc.addEventListener("transitionend", () => {
//     disc.remove();
//     onComplete();
//   }, { once: true });
// };

    // const animateAndSetBoard = (row, col, player) => {
    //   return new Promise((resolve) => {
    //     if (!boardRef.current) {
    //       resolve();
    //       return;
    //     }

    //     const firstCell = boardRef.current.querySelector(`[data-row="0"][data-col="${col}"]`);
    //     if (!firstCell) {
    //       resolve();
    //       return;
    //     }

    //     const disc = document.createElement("div");
    //     disc.className = `disc ${player}`;

    //     const cellSize = firstCell.offsetHeight;
    //     const gapSize = parseFloat(getComputedStyle(boardRef.current).gap) || 0;

    //     disc.style.position = "absolute";
    //     disc.style.width = `${cellSize}px`;
    //     disc.style.height = `${cellSize}px`;
    //     disc.style.borderRadius = "50%";
    //     disc.style.left = `${firstCell.offsetLeft}px`;
    //     disc.style.top = `-60px`; // Start above board
    //     disc.style.zIndex = 10;
    //     disc.style.transition = "top 0.5s ease-out";

    //     boardRef.current.appendChild(disc);

    //     const targetTop = row * (cellSize + gapSize);

    //     requestAnimationFrame(() => {
    //       disc.style.top = `${targetTop}px`;
    //     });

    //     disc.addEventListener("transitionend", () => {
    //       disc.remove();

    //       // Update the board state
    //       setBoard(prevBoard => {
    //         const newBoard = deepcopy(prevBoard);
    //         newBoard[row][col] = player;
    //         return newBoard;
    //       });

    //       resolve();  // Animation done — resolve the promise!
    //     });
    //   });
    // };

//     const animateDiscDrop = (col, row, color, onComplete) => {
//   if (!boardRef.current) return;
//   row++;
//   // Select the target cell where the disc should land
//   const targetCell = boardRef.current.querySelector(`[data-row="${row}"][data-col="${col}"]`);
//   const firstCell = boardRef.current.querySelector(`[data-row="0"][data-col="${col}"]`);
//   if (!firstCell || !targetCell) return;

//   const disc = document.createElement("div");
//   disc.className = `disc ${color}`;

//   const cellSize = firstCell.offsetHeight;

//   // Positioning the disc absolutely relative to the board
//   disc.style.position = "absolute";
//   disc.style.width = `${cellSize}px`;
//   disc.style.height = `${cellSize}px`;
//   disc.style.borderRadius = "50%";
  
//   // Start position (above the board at the top cell's horizontal position)
//   disc.style.left = `${firstCell.offsetLeft}px`;
//   disc.style.top = `-12vh`;
//   disc.style.zIndex = 2;
//   disc.style.transition = "top 0.75s ease-out";

//   boardRef.current.appendChild(disc);

//   // Use exact offsetTop of the target cell relative to the board
//   const targetTop = targetCell.offsetTop;
  
//   // Animate falling disc
//   requestAnimationFrame(() => {
//     disc.style.top = `${targetTop}px`;
//   });

//   disc.addEventListener("transitionend", () => {
//     setTimeout(() => {
//     disc.remove();
//     onComplete();
//   }, 1000);
//   }, { once: true });
// };




// const animateDiscDrop = (col, row, color, onComplete) => {
//   if (!boardRef.current) return;

//   const board = boardRef.current;
//   const targetCell = board.querySelector(`[data-row="${row}"][data-col="${col}"]`);
//   if (!targetCell) return;

//   const disc = document.createElement("div");
//   disc.className = `disc ${color}`;

//   // Start above the board, horizontally aligned with the column
//   disc.style.left = `${targetCell.offsetLeft}px`;
//   disc.style.top = `-12vh`; // above board

//   board.appendChild(disc);

//   // Animate drop to target cell position
//   requestAnimationFrame(() => {
//     disc.style.top = `${targetCell.offsetTop}px`;
//   });

//   disc.addEventListener(
//     "transitionend",
//     () => {
//       disc.remove();
//       onComplete();
//     },
//     { once: true }
//   );
// };


// const animateDiscDrop = (col, row, color, newBoard, onComplete) => {
//   if (!boardRef.current) return;

//   const boardA = boardRef.current;

//   // Get the cell where the disc will land
//   const targetCell = boardA.querySelector(`[data-row="${row + 1}"][data-col="${col}"]`);
//   if (!targetCell) return;

//   // Create disc element
//   const disc = document.createElement("div");
//   disc.className = `disc ${color}`;
  

  

//   // ROUNDED pixel values for perfect alignment
//   const left = Math.round(targetCell.offsetLeft);
//   const top = targetCell.offsetTop;
//   const width = Math.round(targetCell.offsetWidth);
//   const height = Math.round(targetCell.offsetHeight);

//   // Set disc size and initial position above the board
//   // disc.style.width = `${width}px`;
//   // disc.style.height = `${height}px`;
//   // disc.style.left = `${left}px`;
//   // disc.style.top = `-20vh`; // start above board

//   const size = targetCell.offsetWidth; // Add 2px buffer

// // disc.style.width = `${size}px`;
// // disc.style.height = `${size}px`;
// disc.style.width = `${size}px`;
// disc.style.height = `${size}px`;


// // Adjust position back to center the slightly larger disc
// disc.style.left = `${targetCell.offsetLeft + 0.5}px`;
// disc.style.top = `-20vh`;
// disc.style.zIndex = 5000;

//   // Append to the board
//   //boardA.appendChild(disc);

//   // --- Overlay .board-background clone with transparent holes ---
//   const originalBackground = boardA.querySelector(".board-background");
//   let overlayBackground = null;

//   if (originalBackground) {
//     overlayBackground = originalBackground.cloneNode(true);

//     // Create SVG mask
//     const svgNS = "http://www.w3.org/2000/svg";
//     const svg = document.createElementNS(svgNS, "svg");
//     svg.setAttribute("width", "0");
//     svg.setAttribute("height", "0");
//     svg.style.position = "absolute";

//     const mask = document.createElementNS(svgNS, "mask");
//     mask.setAttribute("id", "boardMask");

//     const rect = document.createElementNS(svgNS, "rect");
//     rect.setAttribute("x", "0");
//     rect.setAttribute("y", "0");
//     rect.setAttribute("width", "100%");
//     rect.setAttribute("height", "100%");
//     rect.setAttribute("fill", "white");
//     mask.appendChild(rect);

//     // Mask hole setup (7 cols x 6 rows)
//     const cellSize = 10; // vh
//     const gap = 3; // vh
//     const padding = 2; // vh
//     const radius = cellSize / 2;

//     for (let r = 0; r < 6; r++) {
//       for (let c = 0; c < 7; c++) {
//         const cx = padding + radius + c * (cellSize + gap);
//         const cy = padding + radius + r * (cellSize + gap);

//         const circle = document.createElementNS(svgNS, "circle");
//         circle.setAttribute("cx", `${cx}vh`);
//         circle.setAttribute("cy", `${cy}vh`);
//         circle.setAttribute("r", `${radius}vh`);
//         circle.setAttribute("fill", "black");
//         mask.appendChild(circle);
//       }
//     }

//     svg.appendChild(mask);
//     document.body.appendChild(svg); // add to DOM once

//     // Apply the SVG mask to the overlay
//     overlayBackground.style.mask = "url(#boardMask)";
//     overlayBackground.style.webkitMask = "url(#boardMask)";

//     overlayBackground.style.position = "absolute";
//     overlayBackground.style.top = "0";
//     overlayBackground.style.left = "0";
//     overlayBackground.style.width = "100%";
//     overlayBackground.style.height = "100%";
//     overlayBackground.style.zIndex = "10000";

//     boardA.appendChild(overlayBackground);
//   }

//   //boardA.appendChild(overlayBackground);
//   if (overlayBackground) {
//   boardA.insertBefore(disc, overlayBackground);
// } else {
//   boardA.appendChild(disc); // fallback in case no overlay
// }

  
//   // Animate drop to target cell
//   requestAnimationFrame(() => {
//     disc.getBoundingClientRect();
//     disc.style.top = `${top + 0.5}px`;
//   });

//   // // Cleanup after animation completes
//   // disc.addEventListener(
//   //   "transitionend",
//   //   () => {
//   //     setBoard(newBoard);
//   //     disc.remove();
//   //     onComplete();
//   //   },
//   //   { once: true }
//   // );
//   // disc.addEventListener("transitionend", () => {
//   //   setTimeout(() => {
//   //   disc.remove();
//   //   onComplete();
//   // }, 1000);
//   // }, { once: true });
//   disc.addEventListener(
//     "transitionend",
//     () => {
//       disc.style.zIndex = "";
//       if (overlayBackground) {
//         overlayBackground.remove();

//         // Also remove SVG mask from DOM
//         const svg = document.querySelector("svg");
//         if (svg && svg.querySelector("#boardMask")) {
//           svg.remove();
//         }
//       }
//       setTimeout(() => {
//         setBoard(newBoard);
//         disc.remove();
//         onComplete();
//       }, 20); // small delay to avoid visual glitch
//     },
//     { once: true }
//   );
// };


const animateDiscDrop = (col, row, color, newBoard, onComplete) => {
  if (!boardRef.current) return;

  const boardA = boardRef.current;

  // Get the cell where the disc will land
  const targetCell = boardA.querySelector(`[data-row="${row + 1}"][data-col="${col}"]`);
  if (!targetCell) return;

  // Create disc element
  const disc = document.createElement("div");
  disc.className = `disc ${color}`;
  disc.style.position = "absolute";
  disc.style.pointerEvents = "none";

  // Use pixel dimensions for exact positioning
  const size = targetCell.offsetWidth;
  const leftPx = targetCell.offsetLeft;
  const topPx = targetCell.offsetTop;

  disc.style.width = `${size}px`;
  disc.style.height = `${size}px`;
  disc.style.left = `${leftPx+0.5}px`;
  disc.style.top = `-${size}px`;  // start above the board
  disc.style.zIndex = 5000; // below overlay

  // Create transparent overlays for all cells
  const cellElements = boardA.querySelectorAll(".cell");
  const transparentCells = [];

  const boardRect = boardA.getBoundingClientRect();

  cellElements.forEach((cell) => {
    const rect = cell.getBoundingClientRect();

    const overlayCell = document.createElement("div");
    overlayCell.className = "cell";  // reuse cell styles
    overlayCell.style.position = "absolute";
    overlayCell.style.pointerEvents = "none";
    overlayCell.style.backgroundColor = "transparent"; // semi-transparent tint
    overlayCell.style.opacity = "1"; // you can reduce this if desired
    overlayCell.style.left = `${rect.left - boardRect.left}px`;
    overlayCell.style.top = `${rect.top - boardRect.top}px`;
    overlayCell.style.width = `${rect.width}px`;
    overlayCell.style.height = `${rect.height}px`;
    overlayCell.style.zIndex = 40000;

    transparentCells.push(overlayCell);
    boardA.appendChild(overlayCell);
  });

  // Create overlay clone with SVG mask holes
  const originalBackground = boardA.querySelector(".board-background");
  let overlayBackground = null;

  if (originalBackground) {
    overlayBackground = originalBackground.cloneNode(true);
    overlayBackground.classList.add("board-background--holes");

    // Create SVG mask (pixel units)
    const svgNS = "http://www.w3.org/2000/svg";
    const svg = document.createElementNS(svgNS, "svg");
    svg.setAttribute("width", "0");
    svg.setAttribute("height", "0");
    svg.style.position = "absolute";

    const mask = document.createElementNS(svgNS, "mask");
    mask.setAttribute("id", "mask-board-discs");

    // White rect fills whole area (visible)
    const rect = document.createElementNS(svgNS, "rect");
    rect.setAttribute("x", "0");
    rect.setAttribute("y", "0");
    rect.setAttribute("width", boardA.offsetWidth);
    rect.setAttribute("height", boardA.offsetHeight);
    rect.setAttribute("fill", "white");
    mask.appendChild(rect);

    // Add black circles as holes for each cell
    const cells = boardA.querySelectorAll(".cell");
    cells.forEach((cell) => {
      const cellRect = cell.getBoundingClientRect();
      const boardRect = boardA.getBoundingClientRect();
      const cx = cellRect.left - boardRect.left + cellRect.width / 2;
      const cy = cellRect.top - boardRect.top + cellRect.height / 2;
      const r = cellRect.width / 2;

      const circle = document.createElementNS(svgNS, "circle");
      circle.setAttribute("cx", cx);
      circle.setAttribute("cy", cy);
      circle.setAttribute("r", r);
      circle.setAttribute("fill", "black");
      mask.appendChild(circle);
    });

    svg.appendChild(mask);
    document.body.appendChild(svg);

    // overlayBackground.style.mask = "url(#mask-board-discs)";
    // overlayBackground.style.webkitMask = "url(#mask-board-discs)";
    // overlayBackground.style.position = "absolute";
    // overlayBackground.style.top = "0";
    // overlayBackground.style.left = "0";
    // overlayBackground.style.width = "100%";
    // overlayBackground.style.height = "100%";
    // overlayBackground.style.zIndex = 10000;
    // overlayBackground.style.pointerEvents = "none";
    // overlayBackground.style.boxShadow = "0 4px 8px rgba(0, 0, 0, 0.3)";

    overlayBackground.style.mask = "url(#mask-board-discs)";
    overlayBackground.style.webkitMask = "url(#mask-board-discs)";
    overlayBackground.style.position = "absolute";
    overlayBackground.style.top = "0";
    overlayBackground.style.left = "0";
    overlayBackground.style.width = "100%";
    overlayBackground.style.height = "100%";
    overlayBackground.style.zIndex = 10000;
    overlayBackground.style.pointerEvents = "none";
    overlayBackground.style.boxShadow = "0 4px 8px rgba(0, 0, 0, 0.3)";

    boardA.appendChild(overlayBackground);
  }

  // Insert disc BELOW the overlay to keep it visible through holes
  if (overlayBackground) {
    boardA.insertBefore(disc, overlayBackground);
  } else {
    boardA.appendChild(disc);
  }

  // Animate drop by changing top position after forcing reflow
  requestAnimationFrame(() => {
    disc.getBoundingClientRect(); // force reflow
    //disc.style.transition = "top 0.75s cubic-bezier(0.22, 1, 0.36, 1)";
    disc.style.top = `${topPx + 0.5}px`;
  });

  // Cleanup on animation end
  disc.addEventListener("transitionend", () => {
    disc.style.zIndex = "";
    if (overlayBackground) {
      overlayBackground.remove();
      const svg = document.querySelector("svg");
      if (svg && svg.querySelector("#mask-board-discs")) {
        svg.remove();
      }
    }

    transparentCells.forEach(cell => cell.remove());

    setBoard(newBoard);
    setTimeout(() => {
      //setBoard(newBoard);
      disc.remove();
      onComplete();
    }, 20);
  }, { once: true });
};



    const dropPiece = async (col, player) => {
      try {
        const response = await axios.post("http://localhost:8080/api/connectfour/move", {
         player: player,
         column: col,
        },
      {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      });

        const newBoard = response.data.board;
        const row = drop(newBoard, player, col);
        await new Promise(resolve => {
              animateDiscDrop(col, row, player, newBoard, () => {
                //setBoard(newBoard);
                resolve();
              });
            });
      } catch (error) {
        console.error('Error during computation:', error);
        alert('Something went wrong!');
      }
    };
  


    const bestMove = async (player) => {
      //setPlayer(player === "red" ? "yellow" : "red");
      const newPlayer = player === "red" ? "yellow" : "red";
      //setPlayer(newPlayer);
      //isLoadingScreenOpen.current = true;
      setLoadingScreenOpen(true);
      
      
      console.log("loading screen: " + isLoadingScreenOpen);
      try {
        const response = await axios.post("http://localhost:8080/api/connectfour/compute", {
         player: player
        });
        const col = response.data.column;
        const newBoard = response.data.board;
        const row = drop(newBoard, player, col);
        setLoadingScreenOpen(false);
        await new Promise(resolve => {
              animateDiscDrop(col, row, player, newBoard, () => {
                //setBoard(newBoard);
                resolve();
              });
            });
        //setIsScreenFrozen(false);
        console.log(board);
        //isLoadingScreenOpen.current = false;
        
        console.log("loading screen: " + isLoadingScreenOpen);
      } catch (error) {
        console.error('Error during computation:', error);
        alert('Something went wrong!');
      } finally {
        console.log("done");
      }
    };

    const place = async (col, player) => {
      if (!isLoadingScreenOpen) {
        // Prevents playing a full column
        if (board[0][col] !== null) {
          return;
        }
        await dropPiece(col, player);
        if (isWinScreenOpen) {
          return;
        }
        setIsScreenFrozen(true);
        await bestMove(player === "red" ? "yellow" : "red");
        //console.log(board);
      }
    }
  

  return (
   
    <div className="board-grid" ref={boardRef}>
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
              if (!frozenScreen) {
                frozenScreen = true;;
                place(colIndex, player);
                frozenScreen = false;
                
                //drop('red', 3);
                //console.log(board);
                //drop('yellow', 3);
                //console.log(board);
              }
              //bestMove();
              //setLoadingScreenOpen(false);
              
            }}
          ></div>
        ))
      ))}
       <div className="board-background">
      <LoadingScreen isOpen={isLoadingScreenOpen}/>
    </div></div>
);


}

export default Board;


// import { useRef } from "react";
// import axios from "axios";
// import './Board.css';
// import LoadingScreen from '../LoadingScreen/LoadingScreen';

// const Board = ({ board, setBoard, player, setPlayer }) => {
//   const isLoadingScreenOpen = useRef(false);
//   const animationRef = useRef(null);
//   const boardRef = useRef(null);

//   const getDropRow = (oldBoard, newBoard, col) => {
//     for (let row = oldBoard.length - 1; row >= 0; row--) {
//       if (oldBoard[row][col] !== newBoard[row][col]) {
//         return row;
//       }
//     }
//     return -1;
//   };

//   const animateDiscDrop = (col, row, color, onComplete) => {
//     if (!boardRef.current) return;

//     const firstCell = boardRef.current.querySelector(`[data-row="0"][data-col="${col}"]`);
//     if (!firstCell) return;

//     const disc = document.createElement("div");
//     disc.className = `disc ${color}`;

//     const cellSize = firstCell.offsetHeight;
//     const gapSize = parseFloat(getComputedStyle(boardRef.current).gap) || 0;

//     disc.style.position = "absolute";
//     disc.style.width = `${cellSize}px`;
//     disc.style.height = `${cellSize}px`;
//     disc.style.borderRadius = "50%";
//     disc.style.left = `${firstCell.offsetLeft}px`;
//     disc.style.top = `-12vh`;
//     disc.style.zIndex = 10;
//     disc.style.transition = "top 0.5s ease-out";

//     animationRef.current = disc;
//     boardRef.current.appendChild(disc);

//     const targetTop = row * (cellSize + gapSize);

//     requestAnimationFrame(() => {
//       disc.style.top = `${targetTop}px`;
//     });

//     disc.addEventListener("transitionend", () => {
//       disc.remove();
//       animationRef.current = null;
//       onComplete();
//     });
//   };

//   const dropPiece = async (col, player) => {
//     const oldBoard = board.map(row => [...row]); // deep copy

//     try {
//       const response = await axios.post("http://localhost:8080/api/connectfour/move", {
//         player: player,
//         column: col,
//       });

//       const newBoard = response.data;
//       const dropRow = getDropRow(oldBoard, newBoard, col);

//       if (dropRow === -1) {
//         console.error("Could not detect row of dropped piece");
//         return;
//       }

//       await new Promise(resolve => {
//         animateDiscDrop(col, dropRow, player, () => {
//           setBoard(newBoard);
//           setPlayer(player === "red" ? "yellow" : "red");
//           resolve();
//         });
//       });

//     } catch (error) {
//       console.error("Error during dropPiece:", error);
//       alert("Something went wrong!");
//     }
//   };

//   const bestMove = async (player) => {
//     const nextPlayer = player;
//     isLoadingScreenOpen.current = true;

//     try {
//       const response = await axios.post("http://localhost:8080/api/connectfour/compute", {
//         player: nextPlayer,
//       });

//       setBoard(response.data);
//       setPlayer(player === "red" ? "yellow" : "red");

//     } catch (error) {
//       console.error("Error during bestMove:", error);
//       alert("Something went wrong!");
//     } finally {
//       isLoadingScreenOpen.current = false;
//     }
//   };

//   const place = async (col, player) => {
//     if (!isLoadingScreenOpen.current) {
//       if (board[0][col] !== null) {
//         return; // Column full
//       }

//       await dropPiece(col, player);
//       await bestMove(player === "red" ? "yellow" : "red");
//     }
//   };

//   return (
//     <div className="board" ref={boardRef}>
//       {board.map((row, rowIndex) =>
//         row.map((cell, colIndex) => (
//           <div
//             key={`${rowIndex}-${colIndex}`}
//             className={`cell ${cell || ""}`}
//             data-row={rowIndex}
//             data-col={colIndex}
//             onClick={() => place(colIndex, player)}
//           ></div>
//         ))
//       )}
//       <LoadingScreen isOpen={isLoadingScreenOpen.current} />
//     </div>
//   );
// };

// export default Board;