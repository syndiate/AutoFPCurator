package org.syndiate.FPCurate;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

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
		
	@Test
	void nullStr() {
		assertNull(CommonMethods.getResource(null));
	}
	
	@Test
	void jsonObj() {
		assertInstanceOf(Object.class, CommonMethods.parseJSONStr("{\"test\": \"yes\"}"));
	}
	
	@Test
	void malformedObj() {
		assertNull(CommonMethods.parseJSONStr("{SSD']"));
	}
	
	@Test
	void nullObj() {
		assertNull(CommonMethods.parseJSONStr(null));
	}
	
	@Test
	void fileExt() {
		assertTrue(CommonMethods.getFileExtension(new File("C:/Test/image.jpeg")).equals("jpeg"));
	}
	
	@Test
	void dirExt() {
		assertTrue(CommonMethods.getFileExtension(new File("C:/")).equals(""));
	}
	
	@Test
	void invalidFileExt() {
		assertTrue(CommonMethods.getFileExtension(new File("")).equals(""));
	}
	
	@Test
	void invalidFileExtTwo() {
		assertTrue(CommonMethods.getFileExtension(new File("fjdiusjfisa..sa//aw3")).equals(""));
	}
	
	@Test
	void nullExt() {
		assertTrue(CommonMethods.getFileExtension(null).equals(""));
	}
	
	@Test
	void year() {
		assertTrue(CommonMethods.isValidDate("2010"));
	}
	
	@Test
	void yearAndMonth() {
		assertTrue(CommonMethods.isValidDate("2010-01"));
	}
	
	@Test
	void yearMonthDay() {
		assertTrue(CommonMethods.isValidDate("2010-01-01"));
	}
	
	@Test
	void invalidDate() {
		assertFalse(CommonMethods.isValidDate("3212-01-01-01"));
	}
	
	@Test
	void nullDate() {
		assertFalse(CommonMethods.isValidDate(null));
	}
	
	@Test
	void runExe() {
		
		CommonMethods.runExecutable("programs/7za.exe", "a \"C:\\Users\\syndi\\Documents\\AutoFPCurator\\Working\\681eeb1e-4078-4015-b276-4c4841eee703.7z\"  \"C:\\Users\\syndi\\Documents\\AutoFPCurator\\Working\\681eeb1e-4078-4015-b276-4c4841eee703\\*\"", true, true);
		if (!new File("C:\\Users\\syndi\\Documents\\AutoFPCurator\\Working\\681eeb1e-4078-4015-b276-4c4841eee703.7z").exists()) {
			fail("Zip file was not created.");
		}
	}


}
