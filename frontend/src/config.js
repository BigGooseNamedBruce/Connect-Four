export const API_BASE = "http://localhost:8080/api/connectfour";

export const opposite = (colour) => (colour === "red" ? "yellow" : "red");

export const emptyBoard = () =>
  Array.from({ length: 6 }, () => Array(7).fill(null));

export const isBoardFull = (board) => board[0].every((cell) => cell !== null);
