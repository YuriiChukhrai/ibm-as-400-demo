package com.yc.qa.as400;

import com.yc.qa.as400.exception.IllegalLengthFieldAs400Exceptions;
import org.tn5250j.keyboard.KeyMnemonic;

/**
 * @author limit (Yurii Chukhrai)
 */
public interface TerminalElementsMethods {
	boolean waitForUnlock();
	TerminalDriver sendEnter() throws InterruptedException;
	TerminalDriver sendKeys(final KeyMnemonic keyMnemonic);
	TerminalDriver fillFieldWith(final String label, final String text) throws IllegalLengthFieldAs400Exceptions;
	TerminalDriver fillFieldWith(final int idField, final String text) throws IllegalLengthFieldAs400Exceptions;
	void sendCommand(final String command) throws InterruptedException, IllegalLengthFieldAs400Exceptions;
	boolean isTextPresent(final String pattern, final String matcher) throws InterruptedException;
	boolean isTextPresent(final String matcher);
	int getQtyFields();
	TerminalDriver pressKeyUntilPresent(final String matcher, KeyMnemonic key);
}
