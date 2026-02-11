"""
This file will write all valid positions to a file

"""

import itertools
import os
from collections import Counter
from bitboard import Bitboard

MOVE_LENGTH = 8
OUTPUT_FILE = f"../data/moves_{MOVE_LENGTH}.txt"


def find_all_positions(move_length=8):
    positions = []

    for i in range(1, move_length + 1):
        combinations = itertools.product("1234567", repeat=i)
        combinations = ["".join(comb) for comb in combinations]
        positions += combinations

    return positions

def find_valid_positions(positions):

    valid_positions = []
    board = Bitboard()

    for position in positions:
        # Skips computing position if column is played more than its height
        if max(Counter(position).values()) > board.BOARD_HEIGHT:
            continue

        board.load(position)
        if not (board.can_win_next('r') or board.can_win_next('y')):
            valid_positions.append(position)
        board.clear()

    return valid_positions



def main():
    print("Starting")
    all_positions = find_all_positions(MOVE_LENGTH)
    valid_positions = find_valid_positions(all_positions)
    
    print("Finish finding all valid positions")
    
    current_path = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(current_path, OUTPUT_FILE)

    with open(output_path, "w") as file:
        for item in valid_positions:
            print(f"Writing: {item}")
            file.write(f"{item}\n")



if __name__ == "__main__":
    main()

