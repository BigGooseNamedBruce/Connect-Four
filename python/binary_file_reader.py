"""
This file will the read the contents of a binary file given the first 4 bytes is the 
position and the following 2 bytes is the score

"""

import os

FILEPATH = "../backend/src/main/resources/opening_book.bin"

def read_file(filename):
    try:

        moves = {}

        with open(filename, "rb") as file:
            while True:
                
                key_bytes = file.read(4)
                if not key_bytes or len(key_bytes) < 4:
                    raise ValueError("Invalid number of bytes read for the key")

                value_bytes = file.read(2)
                if not value_bytes or len(value_bytes) < 2:
                    raise ValueError("Invalid number of bytes read for the value")

                position = int.from_bytes(key_bytes, byteorder='big')
                score = int.from_bytes(value_bytes, byteorder='big', signed=True)

                moves[position] = score

    except FileNotFoundError as e:
        print(f"ERROR: {e}")
    except ValueError as e:
        print(f"ERROR: {e}")

    finally:
        return moves


def main():
    current_path = os.path.dirname(__file__)
    read_path = os.path.join(current_path, FILEPATH)

    moves = read_file(read_path)

    for k, v in moves.items():
        print(k, v)


if __name__ == "__main__":
    main()