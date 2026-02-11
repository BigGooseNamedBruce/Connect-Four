from binary_file import read_binary_file, write_binary_file
from bitboard import Bitboard
import os
from collections import defaultdict

MOVE_LENGTH = 8
POSITION_FILEPATH = f"../data/opening_book_{MOVE_LENGTH}_moves_positions.bin"
KEY_FILEPATH = f"../data/opening_book_{MOVE_LENGTH}_moves.bin"
CURRENT_PATH = os.path.dirname(os.path.abspath(__file__))
INPUT_PATH = os.path.join(CURRENT_PATH, POSITION_FILEPATH)
OUTPUT_PATH = os.path.join(CURRENT_PATH, KEY_FILEPATH)


def main():
    print("Starting")
    positions = read_binary_file(INPUT_PATH, key_size=4, value_size=1)
    keys = defaultdict(lambda: float("-inf"))
    board = Bitboard()

    print("Finish loading in positions. Now Converting positions to keys")
    for position, score in positions.items():
        board.load(str(position))
        key = board.key()
        board.clear()

        # Takes the max score of positions with the same key due to some scores being wrong
        keys[key] = max(keys[key], score)
        print(f"Converting {position} -> {key}")
        
    print("Starting to write")
    for key, score in keys.items():
        print(f"Writing {key}: {score}")
        write_binary_file(OUTPUT_PATH, key, score, key_size=8, value_size=1)

if __name__ == "__main__":
    main()
