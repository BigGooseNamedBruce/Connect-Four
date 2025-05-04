"""
Given a file containing positions, this file will scrape the scores from https://connect4.gamesolver.org/

"""


import asyncio
import os
from playwright.async_api import async_playwright, TimeoutError



COMPUTED_POSITIONS_PATH = "../../backend/src/main/resources/opening_book.bin"
VALID_POSITIONS_PATH = "../data/moves.txt"
TIMEOUT = 5000
RETRIES = 5
BATCH_SIZE = 35
SEMAPHORES_COUNT = 60

file_lock = asyncio.Lock()
semaphore = asyncio.Semaphore(SEMAPHORES_COUNT)



def load_computed_positions(filename):

    computed_positions = set()

    try:
        with open(filename, "rb") as file:
            while True:
                key_bytes = file.read(4)
                if not key_bytes or len(key_bytes) < 4:
                    break  # End of file

                value_bytes = file.read(2)
                if not value_bytes or len(value_bytes) < 2:
                    break  # Unexpected EOF

                position = int.from_bytes(key_bytes, byteorder='big')
                score = int.from_bytes(value_bytes, byteorder='big', signed=True)
                computed_positions.add(str(position))

    except FileNotFoundError as e:
        print(f"Ignoring {e}")

    return computed_positions


def load_positions(filename, computed_positions=set()):
    with open(filename, "r") as file:
        return [line.strip() for line in file if line.strip() not in computed_positions]


async def scrap_score(context, position):

    link = f"https://connect4.gamesolver.org/?pos={position}"
    page = await context.new_page()

    for tries in range(RETRIES):
        try:
            await page.goto(link, timeout=TIMEOUT)

            for i in range(7):
                await page.wait_for_selector(f'#sol{i}', timeout=TIMEOUT)
            
            for i in range(7):
                
                await page.locator(f"#sol{i}").wait_for(state="visible", timeout=TIMEOUT)

            

            scores = []
            for i in range(7):
                
                element_by_id = page.locator(f'#sol{i}')
                try:
                    value = int(await element_by_id.inner_text())
                    scores.append(value)
                except ValueError:
                    pass
            
            await page.close()
            return max(scores) if scores else None
        except TimeoutError:
            pass

        except Exception as e:
            #print(f"Attempt {attempt+1}/{retries} failed for position {position}: {e}")
            # Randomized sleep before retrying to avoid hitting the rate limit
            # await asyncio.sleep(random.uniform(2, 5))  # Randomized sleep between 2-5 seconds
            # attempt += 1
            # if attempt == retries:
            #     print(f"Failed to scrape position {position} after {retries} attempts.")
            #     await page.close()
            #     return None
            print(f"{position} had an error: {e}")
        



async def write_binary_file(filename, position, score):
    async with file_lock:
        print(f"Writing {position} {score}")
        with open(filename, "ab") as file:

            key = position.to_bytes(4, byteorder='big')
            value = score.to_bytes(2, byteorder='big', signed=True)

            file.write(key)
            file.write(value)



async def scrape(context, position, filepath):
    async with semaphore:
        score = await scrap_score(context, position)
        if score is not None:
            await write_binary_file(filepath, int(position), score)
            pass


async def main():

    current_path = os.path.dirname(__file__)
    computed_positions_path = os.path.join(current_path, COMPUTED_POSITIONS_PATH)
    valid_positions_path = os.path.join(current_path, VALID_POSITIONS_PATH)
    
    computed_positions = load_computed_positions(computed_positions_path)
    valid_positions = load_positions(valid_positions_path, computed_positions)

    async with async_playwright() as p:
        # Launch the browser and create a single page
        browser = await p.chromium.launch(headless=True)
        context = await browser.new_context()

        # Process positions in batches to avoid memory overload
        batch_size = BATCH_SIZE
        for i in range(0, len(valid_positions), batch_size):
            batch = valid_positions[i:i + batch_size]
            # Run the workers for this batch concurrently
            await asyncio.gather(*[scrape(context, position, computed_positions_path) for position in batch])

        await browser.close()




if __name__ == "__main__":
    asyncio.run(main())