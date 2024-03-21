package edu.put.inf151883

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){

    companion object {
        private val DATABASE_VERSION = 2
        private val DATABASE_NAME = "BoardGameCollector.db"
        val TABLE_GAMES = "games"
        val TABLE_EXPANSIONS = "expansions"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_YEAR = "year"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GAMES_TABLE = ("CREATE TABLE " + TABLE_GAMES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                COLUMN_YEAR + " INTEGER" + ")")
        db.execSQL(CREATE_GAMES_TABLE)
        val CREATE_EXPANSIONS_TABLE = ("CREATE TABLE " + TABLE_EXPANSIONS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                COLUMN_YEAR + " INTEGER" + ")")
        db.execSQL(CREATE_EXPANSIONS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPANSIONS)
        onCreate(db)
    }

    fun addGame(game: Game) {
        val values = ContentValues()
        values.put(COLUMN_ID, game.id)
        values.put(COLUMN_NAME, game.name)
        values.put(COLUMN_YEAR, game.year)
        val db = this.writableDatabase
        db.insert(TABLE_GAMES, null, values)
        db.close()
    }

    fun addExpansion(game: Game) {
        val values = ContentValues()
        values.put(COLUMN_ID, game.id)
        values.put(COLUMN_NAME, game.name)
        values.put(COLUMN_YEAR, game.year)
        val db = this.writableDatabase
        db.insert(TABLE_EXPANSIONS, null, values)
        db.close()
    }

    fun showGames() : MutableList<Game> {
        val db = this.writableDatabase
        val games: MutableList<Game> = ArrayList()
        val query = "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_YEAR FROM $TABLE_GAMES"
        val cursor = db.rawQuery(query, null)

        cursor.moveToFirst()
        do {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val year = cursor.getInt(2)
            games.add(Game(id,name,year))
        } while(cursor.moveToNext())
        cursor.close()
        db.close()
        return games
    }
    fun showExpansions() : MutableList<Game> {
        val db = this.writableDatabase
        val games: MutableList<Game> = ArrayList()
        val query = "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_YEAR FROM $TABLE_EXPANSIONS"
        val cursor = db.rawQuery(query, null)

        cursor.moveToFirst()
        do {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val year = cursor.getInt(2)
            games.add(Game(id,name,year))
        } while(cursor.moveToNext())
        cursor.close()
        db.close()
        return games
    }
    fun sortGames(columnName: String): MutableList<Game> {
        val db = this.writableDatabase
        val games: MutableList<Game> = ArrayList()
        val query = "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_YEAR FROM $TABLE_GAMES ORDER BY $columnName"
        val cursor = db.rawQuery(query, null)

        cursor.moveToFirst()
        do {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val year = cursor.getInt(2)
            games.add(Game(id,name,year))
        } while(cursor.moveToNext())
        cursor.close()
        db.close()
        return games
    }
    fun sortExpansions(columnName: String): MutableList<Game> {
        val db = this.writableDatabase
        val games: MutableList<Game> = ArrayList()
        val query = "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_YEAR FROM $TABLE_EXPANSIONS ORDER BY $columnName"
        val cursor = db.rawQuery(query, null)

        cursor.moveToFirst()
        do {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val year = cursor.getInt(2)
            games.add(Game(id,name,year))
        } while(cursor.moveToNext())
        cursor.close()
        db.close()
        return games
    }
    fun findGame(id: Int?) : Game? {
        val db = this.writableDatabase
        val query = "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_YEAR FROM $TABLE_GAMES WHERE $COLUMN_ID LIKE \"$id\""
        val cursor = db.rawQuery(query, null)
        var game: Game? = null

        if(cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val year = cursor.getInt(2)
            game = Game(id,name,year)
        }
        cursor.close()
        db.close()
        return game
    }
    fun findExpansion(id: Int?) : Game? {
        val db = this.writableDatabase
        val query = "SELECT $COLUMN_ID, $COLUMN_NAME, $COLUMN_YEAR FROM $TABLE_EXPANSIONS WHERE $COLUMN_ID LIKE \"$id\""
        val cursor = db.rawQuery(query, null)
        var game: Game? = null

        if(cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val year = cursor.getInt(2)
            game = Game(id,name,year)
        }
        cursor.close()
        db.close()
        return game
    }
    fun clearDatabase() {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPANSIONS)
        onCreate(db)
    }
}