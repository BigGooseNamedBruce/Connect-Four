"""
This file will write all valid positions to a file

"""

import itertools
import os
from collections import Counter

MOVELENGTH = 6
OUTPUTFILE = "../data/moves.txt"

class Bitboard():

    BOARD_HEIGHT = 6
    BOARD_WIDTH = 7

    def __init__(self):
        self.clear()

    def clear(self):
        self.player_board = 0
        self.opponent_board = 0
        self.mask = 0

        self.BOTTOM_MASK = 0
        for i in range(self.__class__.BOARD_WIDTH):
            self.BOTTOM_MASK += 1 << (self.__class__.BOARD_HEIGHT + 1) * i

        self.BOARD_MASK = self.BOTTOM_MASK * ((1 << self.__class__.BOARD_HEIGHT) - 1)

    def place_disc(self, col, player):
        self.mask |= self.mask + (1 << (col * (self.__class__.BOARD_HEIGHT + 1)))

        if player == "r":
            self.player_board = self.mask ^ self.opponent_board
        else:
            self.opponent_board = self.mask ^ self.player_board

    
    def load(self, position, player="r"):
        for i in position:
            self.place_disc(int(i) - 1, player)
            if player == "r":
                player = "y"
            else:
                player = "r"

        return player
    


    def winning_position(self, player):
        if player == "r":
            board = self.player_board
        else:
            board = self.opponent_board

        r = (board << 1) & (board << 2) & (board << 3)

        p = (board << (self.__class__.BOARD_HEIGHT + 1)) & (board << 2 * (self.__class__.BOARD_HEIGHT + 1))

        r |= p & (board << 3 * (self.__class__.BOARD_HEIGHT + 1))
        r |= p & (board >> (self.__class__.BOARD_HEIGHT + 1))
        p >>= 3*(self.__class__.BOARD_HEIGHT + 1)
        r |= p & (board << (self.__class__.BOARD_HEIGHT + 1))
        r |= p & (board >> 3 * (self.__class__.BOARD_HEIGHT + 1))

    
        p = (board << (self.__class__.BOARD_HEIGHT + 2)) & (board << 2 * (self.__class__.BOARD_HEIGHT + 2))
        r |= p & (board << 3*(self.__class__.BOARD_HEIGHT+2))
        r |= p & (board >> (self.__class__.BOARD_HEIGHT+2))
        p >>= 3*(self.__class__.BOARD_HEIGHT+2)
        r |= p & (board << (self.__class__.BOARD_HEIGHT+2))
        r |= p & (board >> 3*(self.__class__.BOARD_HEIGHT+2))

        p = (board << self.__class__.BOARD_HEIGHT) & (board << 2*self.__class__.BOARD_HEIGHT)
        r |= p & (board << 3*self.__class__.BOARD_HEIGHT)
        r |= p & (board >> self.__class__.BOARD_HEIGHT)
        p >>= 3*self.__class__.BOARD_HEIGHT
        r |= p & (board << self.__class__.BOARD_HEIGHT)
        r |= p & (board >> 3*self.__class__.BOARD_HEIGHT)

        
        return r & (self.BOARD_MASK ^ self.mask)
        

    def possible(self):
        return (self.mask + self.BOTTOM_MASK) & self.BOARD_MASK

    def can_win_next(self, player):
        return self.winning_position(player) & self.possible() != 0
    

    def __str__(self):
        return f"{bin(self.player_board)}\n{bin(self.opponent_board)}\n{bin(self.mask)}"
    

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
        if max(Counter(position).values()) >= 7:
            continue

        board.load(position)
        if not (board.can_win_next('r') or board.can_win_next('y')):
            valid_positions.append(position)
        board.clear()

    return valid_positions






def main():
    all_positions = find_all_positions(MOVELENGTH)
    valid_positions = find_valid_positions(all_positions)

    current_path = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(current_path, OUTPUTFILE)

    with open(output_path, "w") as file:
        for item in valid_positions:
            file.write(f"{item}\n")


if __name__ == "__main__":
    main()

