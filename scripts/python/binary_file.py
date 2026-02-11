"""
This file will the read the contents of a binary file given the first 4 bytes is the 
position and the following 2 bytes is the score

"""

import os
import time

FILEPATH = "../data/opening_book_8_moves_original.bin"

def read_binary_file(filename, key_size=4, value_size=1):
    try:

        moves = {}

        with open(filename, "rb") as file:
            while True:
                
                key_bytes = file.read(key_size)
                if not key_bytes or len(key_bytes) < key_size:
                    raise ValueError("Invalid number of bytes read for the key")

                value_bytes = file.read(value_size)
                if not value_bytes or len(value_bytes) < value_size:
                    raise ValueError("Invalid number of bytes read for the value")

                position = int.from_bytes(key_bytes, byteorder='big')
                score = int.from_bytes(value_bytes, byteorder='big', signed=True)

                moves[position] = score

    except FileNotFoundError as e:
        print(f"ERROR: {e}")

    finally:
        return moves


def write_binary_file(filename, position, score, key_size=4, value_size=1):
    with open(filename, "ab") as file:

        key = position.to_bytes(key_size, byteorder='big')
        value = score.to_bytes(value_size, byteorder='big', signed=True)

        file.write(key)
        file.write(value)


def main():
    current_path = os.path.dirname(os.path.abspath(__file__))
    read_path = os.path.join(current_path, FILEPATH)

    moves = read_binary_file(read_path, key_size=4, value_size=2)

    for k, v in moves.items():
        print(k, v)

if __name__ == "__main__":
    main()