/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 11, 2014, 8:19:33 PM
 */

package com.ceylon_linux.lucky_lanka.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class DbHandler {

	public static void performExecute(SQLiteDatabase database, String sql, Object[] parameters) throws SQLException {
		SQLiteStatement compiledStatement = getCompiledStatement(database, sql, parameters);
		compiledStatement.execute();
	}

	public static long performExecuteInsert(SQLiteDatabase database, String sql, Object[] parameters) throws SQLException {
		SQLiteStatement compiledStatement = getCompiledStatement(database, sql, parameters);
		return compiledStatement.executeInsert();
	}

	public static boolean performExecuteUpdateDelete(SQLiteDatabase database, String sql, Object[] parameters) throws SQLException {
		SQLiteStatement compiledStatement = getCompiledStatement(database, sql, parameters);
		return compiledStatement.executeUpdateDelete() > 0;
	}

	public static void performExecute(SQLiteStatement sqLiteStatement, Object[] parameters) throws SQLException {
		SQLiteStatement compiledStatement = bindParameters(sqLiteStatement, parameters);
		compiledStatement.execute();
	}

	public static long performExecuteInsert(SQLiteStatement sqLiteStatement, Object[] parameters) throws SQLException {
		SQLiteStatement compiledStatement = bindParameters(sqLiteStatement, parameters);
		return compiledStatement.executeInsert();
	}

	public static boolean performExecuteUpdateDelete(SQLiteStatement sqLiteStatement, Object[] parameters) throws SQLException {
		SQLiteStatement compiledStatement = bindParameters(sqLiteStatement, parameters);
		return compiledStatement.executeUpdateDelete() > 0;
	}

	public static Cursor performRawQuery(SQLiteDatabase database, String sql, Object[] parameters) {
		return database.rawQuery(sql, convertToStringArray(parameters));
	}

	private static SQLiteStatement getCompiledStatement(SQLiteDatabase database, String sql, Object[] parameters) throws SQLException {
		SQLiteStatement sqLiteStatement = database.compileStatement(sql);
		String[] stringParameters = convertToStringArray(parameters);
		if (stringParameters != null) {
			sqLiteStatement.bindAllArgsAsStrings(stringParameters);
		}
		return sqLiteStatement;
	}

	private static SQLiteStatement bindParameters(SQLiteStatement sqLiteStatement, Object[] parameters) {
		String[] stringParameters = convertToStringArray(parameters);
		if (stringParameters != null) {
			sqLiteStatement.bindAllArgsAsStrings(stringParameters);
		}
		return sqLiteStatement;
	}

	private static String[] convertToStringArray(Object[] parameters) {
		if (parameters == null) {
			return null;
		}
		final int PARAMETERS_LENGTH = parameters.length;
		if (PARAMETERS_LENGTH == 0) {
			return null;
		}
		String[] stringParameters = new String[PARAMETERS_LENGTH];
		for (int i = 0; i < PARAMETERS_LENGTH; i++) {
			stringParameters[i] = String.valueOf(parameters[i]);
		}
		return stringParameters;
	}
}
