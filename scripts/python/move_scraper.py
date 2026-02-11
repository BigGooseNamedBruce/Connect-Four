"""
Given a file containing positions, this file will scrape the scores from https://connect4.gamesolver.org/

"""


import asyncio
import os
import asyncio
import aiohttp
import os
from playwright.sync_api import sync_playwright
from bitboard import Bitboard
from binary_file import read_binary_file, write_binary_file

MOVE_LENGTH = 8
COMPUTED_POSITIONS_FILENAME = f"../data/opening_book_{MOVE_LENGTH}_moves_positions.bin"#"../../backend/src/main/resources/opening_book.bin"
VALID_POSITIONS_FILENAME = f"../data/moves_{MOVE_LENGTH}.txt"

# Initializes the paths for the filenames this file will read from
CURRENT_PATH = os.path.dirname(__file__)
COMPUTED_POSITIONS_FILEPATH = os.path.join(CURRENT_PATH, COMPUTED_POSITIONS_FILENAME)
VALID_POSITIONS_FILEPATH = os.path.join(CURRENT_PATH, VALID_POSITIONS_FILENAME)

SEMAPHORES_COUNT = 10      # how many requests in flight at once
TOTAL_CONNECTION_LIMIT = 20    # total open connections
PER_HOST_LIMIT = 20      # connections per host
REQUEST_TIMEOUT = 180     # seconds
MAX_RETRIES = 4
BASE_BACKOFF = 0.5  # seconds

HEADERS = {"Accept": "application/json"}
URL = "https://connect4.gamesolver.org/solve"

semaphore = asyncio.Semaphore(SEMAPHORES_COUNT)
file_lock = asyncio.Lock()




def load_computed_positions(filepath):
    """This function returns a set of all the positions whose scores have already been scraped"""

    # There are no already computed positions
    if not os.path.exists(filepath):
        return {}

    computed_positions = read_binary_file(filepath, key_size=4, value_size=1)
    computed_positions = {str(k): v for k, v in computed_positions.items()}
    return computed_positions


def load_positions(filepath, computed_positions={}):
    "This function will return a list of all valid positions whose scores have not been scraped yet"
    positions = []

    with open(filepath, "r") as file:
        for line in file:
            position = line.strip()
            if position not in computed_positions:
                positions.append(position)
        
    return positions

async def write(filename, position, score):
    async with file_lock:
        print(f"Writing {position} {score}")
        write_binary_file(filename, position, score, key_size=4, value_size=1)


async def call_api(session, position, board, keys):

    board.load(position)
    key = board.key()
    board.clear()

    params = {"pos": position}

    for attempt in range(1, MAX_RETRIES + 1):
        # Doesn't need to call the api since the score has already been found
        if key in keys:
            return position, keys[key]
        
        try:
            async with semaphore:
                async with session.get(URL, params=params) as resp:
                    if resp.status == 200:
                        data = await resp.json()
                        return position, data
                    # Server timed out
                    if resp.status == 524:
                        # exponential backoff
                        delay = BASE_BACKOFF * (2 ** (attempt - 1))
                        await asyncio.sleep(delay)
                        continue
                    else:
                        return position, f"Error {resp.status}"

        except Exception as e:
            return position, str(e)


async def process_all_positions(session, valid_positions, board, keys):
    tasks = [call_api(session, p, board, keys) for p in valid_positions]

    for coro in asyncio.as_completed(tasks):
        position, score = await coro

        # Score was found in the dictionary of already computed keys
        if isinstance(score, int):
            try:
                await write(COMPUTED_POSITIONS_FILEPATH, int(position), score)
            except Exception as e:
                print(f"Error processing result for {position}: {e}")

        # Score was found through the API
        elif isinstance(score, dict) and "score" in score:
            try:
                best_score = max(x for x in score["score"] if x < 50)
                # Adds score to the dictionary of computed keys
                board.load(position)
                key = board.key()
                board.clear()
                keys[key] = best_score

                await write(COMPUTED_POSITIONS_FILEPATH, int(position), best_score)
            except Exception as e:
                print(f"Error processing result for {position}: {e}")
        else:
            print(f"API error for {position}: {score}")


async def main():
    print("Staring program")

    # Gets list of all valid positions and removes all positions that have already been scraped
    computed_positions = load_computed_positions(COMPUTED_POSITIONS_FILEPATH)
    valid_positions = load_positions(VALID_POSITIONS_FILEPATH, computed_positions)

    print(f"Finish loading valid positions. {len(valid_positions)} positions to go")

    board = Bitboard()
    keys = {}

    # Calculate
    for position, score in computed_positions.items():
        position = str(position)
        board.load(position)
        keys[board.key()] = score
        board.clear()

    print("Finish loading keys")

    timeout = aiohttp.ClientTimeout(total=REQUEST_TIMEOUT)

    connector = aiohttp.TCPConnector(
        limit=TOTAL_CONNECTION_LIMIT,
        limit_per_host=PER_HOST_LIMIT,
        ttl_dns_cache=300,
    )

    async with aiohttp.ClientSession(
        connector=connector,
        timeout=timeout,
        headers=HEADERS,
    ) as session:
        await process_all_positions(session, valid_positions, board, keys)


if __name__ == "__main__":
    asyncio.run(main())