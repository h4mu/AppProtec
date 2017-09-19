package io.github.h4mu.appprotec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class AppDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AppDB.db";
    public static final String TABLE_NAME = "apps";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PACKAGE = "package";

    public AppDbHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" + COLUMN_PACKAGE + " text primary key, " + COLUMN_NAME + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean replaceApp(String name, String packageName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PACKAGE, packageName);
        db.replace(TABLE_NAME, null, contentValues);
        return true;
    }
    
    public boolean isTrusted(String packageName) {
        Cursor cursor = getReadableDatabase().query(TABLE_NAME, new String[]{COLUMN_PACKAGE},
                COLUMN_PACKAGE + " = ?", new String[]{packageName}, null, null, null);
        return cursor.getCount() > 0;
    }

    public Cursor getData(String packageName) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_PACKAGE + " = ?", new String[]{packageName});
    }

    public int numberOfRows(){
        SQLiteDatabase db = getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public boolean updateApp(String name, String packageName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PACKAGE, packageName);
        db.update(TABLE_NAME, contentValues, COLUMN_PACKAGE + " = ? ", new String[] { packageName } );
        return true;
    }

    public boolean deleteApp(String packageName) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_PACKAGE + " = ? ", new String[] { packageName }) > 0;
    }

    public HashMap<String, HashMap<String, String>> getAllApps() {
        HashMap<String, HashMap<String, String>> apps = new HashMap<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            HashMap<String, String> row = new HashMap<>();
            row.put(COLUMN_PACKAGE, res.getString(res.getColumnIndex(COLUMN_PACKAGE)));
            row.put(COLUMN_NAME, res.getString(res.getColumnIndex(COLUMN_NAME)));
            apps.put(row.get(COLUMN_PACKAGE), row);
            res.moveToNext();
        }
        return apps;
    }
}
