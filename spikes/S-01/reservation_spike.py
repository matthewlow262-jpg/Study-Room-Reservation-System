"""S-01: concurrent reservation architecture spike (Python standard library only)."""
import argparse
import concurrent.futures
import csv
import sqlite3
import statistics
import tempfile
import time
from pathlib import Path

ROOM = 'Room A'
START = '2026-11-09T14:00:00'
END = '2026-11-09T15:00:00'


def setup_database(path):
    connection = sqlite3.connect(path)
    try:
        connection.execute('''CREATE TABLE reservations (
            id INTEGER PRIMARY KEY,
            room TEXT NOT NULL,
            start_time TEXT NOT NULL,
            end_time TEXT NOT NULL,
            student_id INTEGER NOT NULL
        )''')
        connection.commit()
    finally:
        connection.close()


def attempt(path, student_id):
    connection = sqlite3.connect(path, timeout=10, isolation_level=None)
    try:
        connection.execute('BEGIN IMMEDIATE')
        conflict = connection.execute('''SELECT 1 FROM reservations
            WHERE room = ? AND start_time < ? AND end_time > ? LIMIT 1''',
            (ROOM, END, START)).fetchone()
        if conflict:
            connection.execute('ROLLBACK')
            return 'rejected'
        connection.execute('''INSERT INTO reservations (room, start_time, end_time, student_id)
            VALUES (?, ?, ?, ?)''', (ROOM, START, END, student_id))
        connection.execute('COMMIT')
        return 'success'
    except sqlite3.Error:
        if connection.in_transaction:
            connection.execute('ROLLBACK')
        return 'error'
    finally:
        connection.close()


def run(trials, students, output):
    records = []
    with tempfile.TemporaryDirectory() as temp:
        for trial in range(1, trials + 1):
            path = str(Path(temp) / f'trial_{trial}.db')
            setup_database(path)
            start = time.perf_counter()
            with concurrent.futures.ThreadPoolExecutor(max_workers=students) as pool:
                results = list(pool.map(lambda sid: attempt(path, sid), range(1, students + 1)))
            elapsed_ms = (time.perf_counter() - start) * 1000
            connection = sqlite3.connect(path)
            try:
                stored = connection.execute('SELECT COUNT(*) FROM reservations').fetchone()[0]
            finally:
                connection.close()
            record = {'trial': trial, 'successes': results.count('success'),
                      'rejections': results.count('rejected'), 'errors': results.count('error'),
                      'stored_reservations': stored, 'elapsed_ms': round(elapsed_ms, 3)}
            records.append(record)
            if trial % 10 == 0 or trial == trials:
                print(f'Completed {trial}/{trials} trials')
    output.parent.mkdir(parents=True, exist_ok=True)
    with output.open('w', newline='', encoding='utf-8') as file:
        writer = csv.DictWriter(file, fieldnames=records[0].keys())
        writer.writeheader()
        writer.writerows(records)
    passing = sum(r['successes'] == 1 and r['rejections'] == students - 1
                  and r['errors'] == 0 and r['stored_reservations'] == 1 for r in records)
    durations = [r['elapsed_ms'] for r in records]
    print(f'Passing trials: {passing}/{trials}')
    print(f'Total successes: {sum(r["successes"] for r in records)}')
    print(f'Total rejections: {sum(r["rejections"] for r in records)}')
    print(f'Total database errors: {sum(r["errors"] for r in records)}')
    print(f'Mean trial duration: {statistics.mean(durations):.2f} ms')
    print(f'Max trial duration: {max(durations):.2f} ms')
    print(f'CSV saved to: {output}')
    return passing == trials


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--trials', type=int, default=100)
    parser.add_argument('--students', type=int, default=20)
    parser.add_argument('--output', type=Path, default=Path('results.csv'))
    args = parser.parse_args()
    if args.trials < 1 or args.students < 1:
        parser.error('trials and students must be positive')
    raise SystemExit(0 if run(args.trials, args.students, args.output) else 1)
