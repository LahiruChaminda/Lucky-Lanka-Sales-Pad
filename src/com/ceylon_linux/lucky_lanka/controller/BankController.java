/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 21, 2014, 11:17:22 AM
 */
package com.ceylon_linux.lucky_lanka.controller;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.ceylon_linux.lucky_lanka.db.DbHandler;
import com.ceylon_linux.lucky_lanka.db.SQLiteDatabaseHelper;
import com.ceylon_linux.lucky_lanka.model.Bank;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class BankController extends AbstractController {
	private BankController() {
	}

	public static void downloadBanks(Context context, int positionId) throws IOException, JSONException {
		JSONObject banksJson = getJsonObject(BankURLPack.GET_BANKS, BankURLPack.getParameters(positionId), context);
		JSONArray bankJsonArray = banksJson.getJSONArray("banks");
		ArrayList<Bank> banks = new ArrayList<Bank>();
		for (int i = 0, BANK_LENGTH = bankJsonArray.length(); i < BANK_LENGTH; i++) {
			Bank bank = Bank.parseBank(bankJsonArray.getJSONObject(i));
			if (bank != null) {
				banks.add(bank);
			}
		}
		saveBanksToDb(banks, context);
	}

	private static void saveBanksToDb(ArrayList<Bank> banks, Context context) {
		String bankSQL = "replace into tbl_bank(bankCode, bankName) values(?,?)";
		String branchSQL = "replace into tbl_bank_branch(branchId, bankCode, branchName) values(?,?,?)";
		SQLiteDatabaseHelper databaseInstance = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseInstance.getWritableDatabase();
		SQLiteStatement bankStatement = database.compileStatement(bankSQL);
		SQLiteStatement branchStatement = database.compileStatement(branchSQL);
		database.beginTransaction();
		try {
			for (Bank bank : banks) {
				DbHandler.performExecuteInsert(bankStatement, new Object[]{bank.getBankCode(), bank.getBankName()});
				ArrayList<Bank.BankBranch> bankBranches = bank.getBankBranches();
				for (Bank.BankBranch bankBranch : bankBranches) {
					DbHandler.performExecuteInsert(branchStatement, new Object[]{bankBranch.getBranchId(), bank.getBankCode(), bankBranch.getBranchName()});
				}
			}
			database.setTransactionSuccessful();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			database.endTransaction();
		}
	}

	public static ArrayList<Bank> getBanks(Context context) {
		String bankSql = "select bankCode, bankName from tbl_bank";
		String branchSql = "select branchId, branchName from tbl_bank_branch where bankCode=?";
		SQLiteDatabaseHelper databaseInstance = SQLiteDatabaseHelper.getDatabaseInstance(context);
		SQLiteDatabase database = databaseInstance.getWritableDatabase();
		Cursor bankCursor = DbHandler.performRawQuery(database, bankSql, null);
		ArrayList<Bank> banks = new ArrayList<Bank>();
		for (bankCursor.moveToFirst(); !bankCursor.isAfterLast(); bankCursor.moveToNext()) {
			String bankCode = bankCursor.getString(0);
			String bankName = bankCursor.getString(1);
			Cursor branchCursor = DbHandler.performRawQuery(database, branchSql, new Object[]{bankCode});
			ArrayList<Bank.BankBranch> bankBranches = new ArrayList<Bank.BankBranch>();
			for (branchCursor.moveToFirst(); !branchCursor.isAfterLast(); branchCursor.moveToNext()) {
				int branchId = branchCursor.getInt(0);
				String branchName = branchCursor.getString(1);
				bankBranches.add(new Bank.BankBranch(branchId, branchName));
			}
			branchCursor.close();
			banks.add(new Bank(bankName, bankBranches));
		}
		bankCursor.close();
		return banks;
	}

}
