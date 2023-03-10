package org.syndiate.FPCurate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FPDataTest {

	@Test
	void searchExistingGame() {
		String searchData = FPData.getSearchData("Dr. Null");
		assertFalse(searchData.equals(""));
		System.out.println(searchData);
	}
	
	@Test
	void searchNonexistentGame() {
		assertTrue(FPData.getSearchData("fjeughrugherugherugrehguerhguih").equals(""));
	}
	
	@Test
	void searchNull() {
		assertInstanceOf(String.class, FPData.getSearchData(null));
	}
	
	@Test
	void getExistingGame() {
		String searchData = FPData.getCurationData("1145f802-c76f-6675-902d-e4e3fd654380");
		assertFalse(searchData.equals(""));
		System.out.println(searchData);
	}
	
	@Test
	void getNonexistentGame() {
		assertTrue(FPData.getCurationData("jfh").equals(""));
	}
	
	@Test
	void getNullGame() {
		assertInstanceOf(String.class, FPData.getCurationData(null));
	}

}
