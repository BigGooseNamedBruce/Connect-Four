import "./WinScreen.css";

const WinScreen = ({ winner, onPlayAgain }) => {
  if (!winner) {
    return null;
  }

  const isDraw = winner === "draw";
  const title = isDraw ? "It's a draw!" : `${cap(winner)} wins!`;

  return (
    <div className="win-overlay">
      <div className="win-content">
        {!isDraw && <span className={`win-disc ${winner}`} aria-hidden="true" />}
        <p className="win-title">{title}</p>
        <button type="button" className="play-again-button" onClick={onPlayAgain}>
          Play again
        </button>
      </div>
    </div>
  );
};

const cap = (s) => s.charAt(0).toUpperCase() + s.slice(1);

export default WinScreen;
