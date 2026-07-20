import "./Controls.css";

const DIFFICULTY_LABELS = {
  1: "Beginner",
  2: "Easy",
  3: "Medium",
  4: "Hard",
  5: "Perfect",
};

const Controls = ({
  mode,
  onModeChange,
  humanColour,
  onColourChange,
  difficulty,
  onDifficultyChange,
}) => {
  return (
    <div className="controls">
      <div className="control-group">
        <span className="control-label">Mode</span>
        <div className="segmented">
          <button
            type="button"
            className={`segment ${mode === "pve" ? "active" : ""}`}
            onClick={() => onModeChange("pve")}
          >
            Vs AI
          </button>
          <button
            type="button"
            className={`segment ${mode === "pvp" ? "active" : ""}`}
            onClick={() => onModeChange("pvp")}
          >
            Vs Human
          </button>
        </div>
      </div>

      {mode === "pve" && (
        <div className="control-group">
          <span className="control-label">You play</span>
          <div className="segmented">
            <button
              type="button"
              className={`segment segment-colour ${humanColour === "red" ? "active" : ""}`}
              onClick={() => onColourChange("red")}
            >
              <span className="swatch red" /> Red
            </button>
            <button
              type="button"
              className={`segment segment-colour ${humanColour === "yellow" ? "active" : ""}`}
              onClick={() => onColourChange("yellow")}
            >
              <span className="swatch yellow" /> Yellow
            </button>
          </div>
          <span className="control-hint">Red always moves first</span>
        </div>
      )}

      {mode === "pve" && (
        <div className="control-group">
          <span className="control-label">
            Difficulty: <strong>{DIFFICULTY_LABELS[difficulty]}</strong>
          </span>
          <input
            type="range"
            min="1"
            max="5"
            step="1"
            value={difficulty}
            onChange={(e) => onDifficultyChange(Number(e.target.value))}
            className="difficulty-slider"
          />
          <span className="control-hint">Wider alpha-beta window = stronger AI</span>
        </div>
      )}
    </div>
  );
};

export default Controls;
