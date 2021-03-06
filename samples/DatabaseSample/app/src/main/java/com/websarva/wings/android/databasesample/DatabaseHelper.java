package com.websarva.wings.android.databasesample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;

import static android.database.sqlite.SQLiteDatabase.OPEN_READWRITE;

/**
 * 『Androidアプリ開発の教科書』
 * 第10章
 * データベースサンプル
 *
 * データベースヘルパークラス。
 * https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase
 * @author Shinzo SAITO
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	/**
	 * データベースファイル名の定数フィールド。
	 */
	private static final String DATABASE_NAME = "cocktailmemo.db";
	/**
	 * バージョン情報の定数フィールド。
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * コンストラクタ。
	 */
	public DatabaseHelper(Context context) {
		//親クラスのコンストラクタの呼び出し。
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//テーブル作成用SQL文字列の作成。
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE cocktailmemo (");
		sb.append("_id INTEGER PRIMARY KEY,");
		sb.append("name TEXT,");
		sb.append("note TEXT");
		sb.append(");");
		String sql = sb.toString();

		//SQLの実行。
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public SQLiteDatabase CheakDataBase(){
		String path = "testdatabase";
		SQLiteDatabase checkDb = null;
		try {
			checkDb = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLiteException e) {
			Log.i("test", e.getMessage());
		}
		return checkDb;
	}

}
