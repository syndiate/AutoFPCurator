package org.syndiate.FPCurate.gui.common.dialog;

import java.io.IOException;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import com.google.gson.stream.MalformedJsonException;


class ErrorDialogTest {
	
	@Before
	void createErrDialog(Exception e) throws InterruptedException {
		ErrorDialog dialog = new ErrorDialog(e);
		
		int seconds = 0;
		while (dialog.isVisible()) {
			if (seconds == 4) break;
			Thread.sleep(1000);
			seconds++;
		}
	}

	@Test
	void stringInEx() throws InterruptedException {
		createErrDialog(new MalformedJsonException("Unable to parse data."));
	}
	
	@Test
	void genericEx() throws InterruptedException {
		createErrDialog(new IOException());
	}
	
	@Test
	void nullEx() throws InterruptedException {
		createErrDialog(null);
	}

}
