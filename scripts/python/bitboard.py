"""
Helper class to help generate valid positions and to find a position's key.
This is the same bitboard implementation as the Java version found in the backend

"""

class Bitboard():
    def _bottom_mask(width, height):
        mask = 0
        for i in range(width):
            mask += 1 << (height + 1) * i
        return mask
    def __init__(self):
        self.clear()
        
        self.BOARD_HEIGHT = 6
        self.BOARD_WIDTH = 7
        
        self.BOTTOM_MASK = sum(1 << ((self.BOARD_HEIGHT + 1) * i) for i in range(self.BOARD_WIDTH))
        self.BOARD_MASK = self.BOTTOM_MASK * ((1 << self.BOARD_HEIGHT) - 1)

    def clear(self):
        self.player_board = 0
        self.opponent_board = 0
        self.mask = 0
        

    def place_disc(self, col, player):
        self.mask |= self.mask + (1 << (col * (self.BOARD_HEIGHT + 1)))

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

        p = (board << (self.BOARD_HEIGHT + 1)) & (board << 2 * (self.BOARD_HEIGHT + 1))

        r |= p & (board << 3 * (self.BOARD_HEIGHT + 1))
        r |= p & (board >> (self.BOARD_HEIGHT + 1))
        p >>= 3*(self.BOARD_HEIGHT + 1)
        r |= p & (board << (self.BOARD_HEIGHT + 1))
        r |= p & (board >> 3 * (self.BOARD_HEIGHT + 1))

    
        p = (board << (self.BOARD_HEIGHT + 2)) & (board << 2 * (self.BOARD_HEIGHT + 2))
        r |= p & (board << 3*(self.BOARD_HEIGHT+2))
        r |= p & (board >> (self.BOARD_HEIGHT+2))
        p >>= 3*(self.BOARD_HEIGHT+2)
        r |= p & (board << (self.BOARD_HEIGHT+2))
        r |= p & (board >> 3*(self.BOARD_HEIGHT+2))

        p = (board << self.BOARD_HEIGHT) & (board << 2*self.BOARD_HEIGHT)
        r |= p & (board << 3*self.BOARD_HEIGHT)
        r |= p & (board >> self.BOARD_HEIGHT)
        p >>= 3*self.BOARD_HEIGHT
        r |= p & (board << self.BOARD_HEIGHT)
        r |= p & (board >> 3*self.BOARD_HEIGHT)

        
        return r & (self.BOARD_MASK ^ self.mask)
        
    def possible(self):
        return (self.mask + self.BOTTOM_MASK) & self.BOARD_MASK

    def can_win_next(self, player):
        return self.winning_position(player) & self.possible() != 0
    
    def key(self):
        return self.player_board + self.BOTTOM_MASK + self.mask

    def __str__(self):
        return f"Red:    {bin(self.player_board)}\nYellow: {bin(self.opponent_board)}\nMask:   {bin(self.mask)}"
 