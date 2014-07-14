/*
 * Intellectual properties of Supun Lakshan Wanigarathna Dissanayake
 * Copyright (c) 2014, Supun Lakshan Wanigarathna Dissanayake. All rights reserved.
 * Created on : Jun 21, 2014, 11:17:22 AM
 */
package com.ceylon_linux.lucky_lanka.controller;


import com.ceylon_linux.lucky_lanka.model.Bank;

import java.util.ArrayList;

/**
 * @author Supun Lakshan Wanigarathna Dissanayake
 * @mobile +94711290392
 * @email supunlakshan.xfinity@gmail.com
 */
public class BankController {
	private BankController() {
	}

	public static ArrayList<Bank> getBanks() {
		ArrayList<Bank> banks = new ArrayList<Bank>();
		banks.add(new Bank("Bank of Ceylon"));
		banks.add(new Bank("Commercial Bank of Ceylon PLC"));
		banks.add(new Bank("DFCC Bank"));
		banks.add(new Bank("DFCC Vardhana Bank PLC"));
		banks.add(new Bank("Hatton National Bank PLC"));
		banks.add(new Bank("MBSL Savings Bank Ltd"));
		banks.add(new Bank("National Development Bank PLC"));
		banks.add(new Bank("National Savings Bank"));
		banks.add(new Bank("Nations Trust Bank PLC"));
		banks.add(new Bank("Pan Asia Banking Corporation PLC"));
		banks.add(new Bank("People's Bank"));
		banks.add(new Bank("Sampath Bank PLC"));
		banks.add(new Bank("Seylan Bank PLC"));
		banks.add(new Bank("The Hongkong and Shanghai Banking Corporation Ltd [HSBC]"));
		banks.add(new Bank("Union Bank of Colombo PLC"));
		return banks;
	}
}
