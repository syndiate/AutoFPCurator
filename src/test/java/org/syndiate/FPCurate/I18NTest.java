package org.syndiate.FPCurate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class I18NTest {

	@Test
	void existingStr() {
		assertInstanceOf(String.class, I18N.getStrings("test").get("thisIs"));
	}
	
	@Test
	void nonExistentStr() {
		assertNull(I18N.getStrings("test").get("this_does_not_exist"));
	}
	
	@Test
	// uncomment the malformed json (i18n/test-bad/en.json) when running unit tests
	void malformedJson() {
		assertNull(I18N.getStrings("test-bad"));
	}
	
	@Test
	void nonExistentFile() {
		assertNull(I18N.getStrings("nothing"));
	}
	
	@Test
	void nonExistentNamespace() {
		assertNull(I18N.getStrings("this-namespace-does-not-exist"));
	}

}
