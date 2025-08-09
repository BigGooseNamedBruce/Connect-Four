import React from 'react';
import './LoadingScreen.css';

const LoadingScreen = ({isOpen}) => {

    if (!isOpen) {
        return null;
    }

    return (
        <div className="loading">
        <div className="loading-content">
            {"thinking...".split("").map((char, index) => (
            <span key={index} className="wave" style={{ animationDelay: `${index * 0.1}s` }}>
                {char}
            </span>
            ))}
        </div>
        </div>
    )
}

export default LoadingScreen;