"""Shared pytest fixtures."""
import sqlite3
from pathlib import Path

import pytest

ROOT = Path(__file__).resolve().parents[2]
SQL_DIR = ROOT / "sql"


@pytest.fixture
def db(tmp_path):
    """Temporary database built from schema.sql and seed.sql; removed after each test."""
    conn = sqlite3.connect(tmp_path / "test.db")
    conn.row_factory = sqlite3.Row  # services access columns by name, like db.get_connection()
    conn.executescript((SQL_DIR / "schema.sql").read_text(encoding="utf-8"))
    conn.executescript((SQL_DIR / "seed.sql").read_text(encoding="utf-8"))
    yield conn
    conn.close()
