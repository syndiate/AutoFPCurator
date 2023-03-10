package org.syndiate.FPCurate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CommonMethodsTest {

	
	@Test
	void resourceNonexistent() {
		assertNull(CommonMethods.getResource("this_file_does_not_exist.txt"));
	}
	
	@Test
	void resourceStr() {
		assertTrue(CommonMethods.getResource("resourceTest.txt").equals("This is a test"));
	}
	
	@Test
	void resourceByte() {
		
	}

}
