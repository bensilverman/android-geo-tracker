package com.webenomic.geotracker;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataHelper {

	private static final String DATABASE_NAME = "webenomic";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "geo";

	private Context context;
	private SQLiteDatabase db;

	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " + TABLE_NAME
			+ "(created, lat, lon, accuracy, provider) values (?, ?, ?, ?, ?)";

	public DataHelper(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		this.insertStmt = this.db.compileStatement(INSERT);
	}

	public void close() {
		this.db.close();
	}

	public long insert(Long created, String lat, String lon, Float accuracy, String provider) {
		this.insertStmt.bindLong(1, created);
		this.insertStmt.bindString(2, lat);
		this.insertStmt.bindString(3, lon);
		this.insertStmt.bindDouble(4, accuracy);
		this.insertStmt.bindString(5, provider);
		return this.insertStmt.executeInsert();
	}

	public void deleteAll() {
		this.db.delete(TABLE_NAME, null, null);
	}
	
	public void deleteRow(int id) {
		Log.d("DataHelper", "In deleteRow");
		String[] whereArgs;
		whereArgs = new String[1];
		whereArgs[0] = String.valueOf(id);
		this.db.delete(TABLE_NAME, "id = ?", whereArgs);
		Log.d("DataHelper", "Delete record " + id);
	}

	public List<String> selectAll() {
		List<String> list = new ArrayList<String>();
		Cursor cursor = this.db.query(TABLE_NAME, new String[] { "created" },
				null, null, null, null, "id desc");
		if (cursor.moveToFirst()) {
			do {
				list.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public Cursor getCursor() throws Exception {
		try {
			Cursor c = this.db.query("geo", new String[] { "id", "created",
					"lat", "lon", "accuracy", "provider" }, null, null, null, null, null);
			return c;
		} catch (Exception e) {
			throw e;
		}
	}

	public SQLiteDatabase getDb() {
		return this.db;
	}

	private static class OpenHelper extends SQLiteOpenHelper {
		final String TAG = "OpenHelper";

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db
					.execSQL("CREATE TABLE "
							+ TABLE_NAME
							+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, created TIMESTAMP, lat TEXT, lon TEXT, accuracy FLOAT, provider TEXT)");
			Log.d(TAG, "onCreate");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Example",
					"Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
}