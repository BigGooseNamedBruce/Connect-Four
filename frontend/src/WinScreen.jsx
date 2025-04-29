import React from 'react';
import './WinScreen.css';

const WinScreen = ({isOpen, colour}) => {
    if (!isOpen) {
        return null;
    }

    return (
        <div className="win-overlay">
            <div className="win-content">
                <p>{colour} wins!</p>
            </div>
        </div>
    )
}

export default WinScreen;