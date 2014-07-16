/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 21, 2014, 11:18:38 AM
 */
package com.ceylon_linux.lucky_lanka.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class Bank {
	private String bankCode;
	private String bankName;
	private ArrayList<BankBranch> bankBranches;

	public Bank(String bankCode, String bankName, ArrayList<BankBranch> bankBranches) {
		this.bankCode = bankCode;
		this.bankName = bankName;
		this.bankBranches = bankBranches;
	}

	public Bank(String bankName, ArrayList<BankBranch> bankBranches) {
		this.bankName = bankName;
		this.bankBranches = bankBranches;
	}

	public static final Bank parseBank(JSONObject bankJsonObject) throws JSONException {
		String bankCode = bankJsonObject.getString("b_code");
		String bankName = bankJsonObject.getString("b_name");
		JSONArray branchesJsonArray = bankJsonObject.getJSONArray("brances");
		ArrayList<BankBranch> bankBranches = new ArrayList<BankBranch>();
		for (int i = 0, BRANCH_LENGTH = branchesJsonArray.length(); i < BRANCH_LENGTH; i++) {
			JSONObject branchJsonObject = branchesJsonArray.getJSONObject(i);
			int bankBranchId = branchJsonObject.getInt("idbank_branch");
			String branchName = branchJsonObject.getString("bb_name");
			bankBranches.add(new BankBranch(bankBranchId, branchName));
		}
		return bankBranches.size() == 0 ? null : new Bank(bankCode, bankName, bankBranches);
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public ArrayList<BankBranch> getBankBranches() {
		return bankBranches;
	}

	public void setBankBranches(ArrayList<BankBranch> bankBranches) {
		this.bankBranches = bankBranches;
	}

	@Override
	public String toString() {
		return bankName;
	}

	public static class BankBranch {
		int branchId;
		String branchName;

		public BankBranch(int branchId, String branchName) {
			this.branchId = branchId;
			this.branchName = branchName;
		}

		public int getBranchId() {
			return branchId;
		}

		public void setBranchId(int branchId) {
			this.branchId = branchId;
		}

		public String getBranchName() {
			return branchName;
		}

		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}

		@Override
		public String toString() {
			return branchName;
		}
	}
}
