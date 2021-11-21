package com.yc.qa.as400;

import static com.yc.qa.as400.util.Constants.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.yc.qa.as400.util.BaseUtils;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;
import org.tn5250j.Session5250;

import com.yc.qa.as400.exception.IllegalLengthFieldAs400Exceptions;

import org.tn5250j.SessionConfig;
import org.tn5250j.SessionPanel;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.interfaces.ConfigureFactory;
import org.tn5250j.keyboard.KeyMnemonic;

import org.tn5250j.TN5250jConstants;
import org.tn5250j.event.ScreenOIAListener;
import org.tn5250j.event.SessionChangeEvent;
import org.tn5250j.event.SessionListener;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.framework.tn5250.ScreenOIA;

/**
 * @author limit (Yurii Chukhrai)
 */
@Log4j
public class TerminalDriver implements TerminalElementsMethods {

	private Session5250 session;
	private boolean connected;
	private boolean informed;
	private int informChange;
	private Screen5250 screen;
	private SessionBean sessionBean;
	private final String as400Host;
	private final String username;
	private final String password;

	/**
	 * If the Port was not provided by CLI apply default value [992]
	 * */
	private final String sessionPort;

	/**
	 * If the SSL type was not provided by CLI apply default value [TSL]
	 * */
	private final String sslType;
	private JFrame frame;
	private SessionPanel sessGui;
	private JPanel main;
	private SessionConfig config;
	private final boolean visible;
	private final int widthDefault = 1024;
	private final int heightDefault = 768;

	/** Constructor */
	public TerminalDriver(final String host, final String user, final String pass, boolean visible, final String sessionPort, final String sslType) {
		this.username = user;
		this.password = pass;
		this.as400Host = host;

		this.visible = visible;
		this.sessionPort =  BaseUtils.isEmpty(sessionPort) ? "992" : sessionPort;
		this.sslType = BaseUtils.isEmpty(sslType) ? "TLS" : sslType.toUpperCase();
	}

	@Step("Open terminal window and create session.")
	public TerminalDriver openTN5250J() throws IOException {

		initSession();
		initFrame();
		initPanel();

		return this;
	}

	@Step("Is AS/400 Terminal Connected")
	public boolean isTerminalConnected() {
		return sessionBean.isConnected();
	}

	@Step("Close terminal session and panel")
	public TerminalDriver closeTN5250J() {

		sessionBean.signoff();
		sessionBean.disconnect();
		sessGui.closeDown();
		main.removeAll();

		log.info(String.format("TID [%d] Sign off from AS/400", Thread.currentThread().getId()));
		
		try {
		Thread.sleep(TIME_SLEEP_3000);

		int safeCount = 0;
		while (!sessionBean.isOnSignOnScreen()) {

			if (safeCount >= 10) {
				break;
			}
			
			Thread.sleep(TIME_SLEEP_500);

			safeCount++;
		}

		safeCount = 0;
		while (sessionBean.isConnected()) {

			if (safeCount >= 10) {
				break;
			}

			Thread.sleep(TIME_SLEEP_500);
			safeCount++;
		}
		} catch (InterruptedException e) {
			log.error("Can't 'sleep' for [closeTN5250J()]");
			e.printStackTrace();
		}
		log.info(String.format("TID [%d] Disconnected from AS/400", Thread.currentThread().getId()));

		frame.setVisible(false);
		log.info(String.format("TID [%d] Hide GUI frame.", Thread.currentThread().getId()));
		frame.dispose();
		log.info(String.format("TID [%d] Close GUI window.", Thread.currentThread().getId()));

		// MSG
		log.info(String.format("TID [%d] GUI TN5250J - was closed completely.", Thread.currentThread().getId()));
		return this;
	}

	@Step("Create terminal session")
	private TerminalDriver initSession() throws IOException {

		System.setProperty("emulator.settingsDirectory", File.createTempFile("tn5250j", "settings").getAbsolutePath());
		ConfigureFactory.getInstance();
		org.tn5250j.tools.LangTool.init();

		Properties properties = new Properties();
		config = new SessionConfig(as400Host, as400Host);
		// config.setProperty("font", "Lucida Sans Typewriter Regular"); //example
		config.setProperty("font", "Consolas");

		properties.put(TN5250jConstants.SESSION_HOST_PORT, this.sessionPort);
		properties.put(TN5250jConstants.SSL_TYPE, this.sslType);
		properties.put(TN5250jConstants.SESSION_SCREEN_SIZE, TN5250jConstants.SCREEN_SIZE_27X132_STR); // -132
		properties.put("height", heightDefault);
		properties.put("width", widthDefault);

		session = new Session5250(properties, as400Host, as400Host, config);
		session.addSessionListener(new SessionListener() {
			@Override
			public void onSessionChanged(SessionChangeEvent changeEvent) {
				connected = changeEvent.getState() == TN5250jConstants.STATE_CONNECTED;
			}
		});

		sessionBean = new SessionBean(session);
		sessionBean.setHostName(as400Host);
		sessionBean.setCodePage("Cp37");

		sessionBean.setNoSaveConfigFile();
		sessionBean.setScreenSize("27x132");
		sessionBean.setDeviceName("devNameTest");

		sessionBean.setSignonUser(username);
		sessionBean.setSignonPassword(password);
		screen = session.getScreen();

		session.getScreen().getOIA().addOIAListener(new ScreenOIAListener() {
			@Override
			public void onOIAChanged(final ScreenOIA oia, int change) {
				log.debug(String.format("TID [%d] OIA %d", Thread.currentThread().getId(), change));
				informed = informed | informChange == change;
			}
		});

		return this;
	}

	@Step("Initialize the frame for [TN5250J]")
	private TerminalDriver initFrame() {
		frame = new JFrame("TN5250J");

		/* Test Settings */
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);

		frame.setSize(widthDefault, heightDefault);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				sessionBean.signoff();
				sessionBean.disconnect();
			}
		});
		return this;
	}

	@Step("Initialize the panel for [TN5250J]")
	private TerminalDriver initPanel() throws ConnectException {
		// TODO
		//  try --> catch
		sessGui = new SessionPanel(sessionBean.getSession());
		main = new JPanel(new BorderLayout());
		main.add(sessGui, BorderLayout.CENTER);
		frame.setContentPane(main);

		frame.setVisible(visible);
		
		try {
		Thread.sleep(TIME_SLEEP_1000);

		frame.setAutoRequestFocus(true);

		sessionBean.connect();
		Thread.sleep(TIME_SLEEP_1000);

		int count = 0;

		while (!sessionBean.isConnected()) {
			// (4*250~1000ms)
			if (count < TIME_SLEEP_MULTIPLIER_7) {
				count++;
				Thread.sleep(TIME_SLEEP_500);
			} else {
				final String msg = String.format("TID [%d] Can't Connect to HOST [%s]", Thread.currentThread().getId(), as400Host);
				log.error(msg);
				throw new ConnectException(msg);
			}
		}
		} catch (InterruptedException e) {
			log.error("Can't 'sleep' for [initPanel()]");
			e.printStackTrace();
		}
		
		return this;
	}

	// TerminalElementsMethods Implementations

	@Step("Send Key Mnemonic [{0}]")
	@Override
	public TerminalDriver sendKeys(final KeyMnemonic keyMnemonic) {
		screen.sendKeys(keyMnemonic);
		log.info(String.format("TID [%d] sendKeys [%s]", Thread.currentThread().getId(), keyMnemonic));
		return this;
	}

	@Step("Press 'Enter'")
	@Override
	public TerminalDriver sendEnter() {
		sendKeys(KeyMnemonic.ENTER).waitForUnlock();
		return this;
	}

	@Step("Fill the field with label [{0}], text [{1}]")
	@Override
	public TerminalDriver fillFieldWith(final String label, final String text) {
		fillTextField(label, text, false);
		return this;
	}

	@Step("Fill the field with ID [{0}], value [{1}]")
	@Override
	public TerminalDriver fillFieldWith(final int idField, final String text) {

		ScreenField field;
		String textClean;

		if (Objects.nonNull(text)) {
			textClean = text.replaceAll(NBSP_REGEX, "").trim();

			field = screen.gotoFieldID(idField);

			if (field.getFieldLength() < textClean.length()) {
				throw new IllegalLengthFieldAs400Exceptions(String.format(EMPTY_INPUT_MSG, Thread.currentThread().getId(), field.getFieldLength(), textClean, textClean.length()));
			}

			// Clean string from space and fill in the field
			field.setString(text);
			log.info(String.format("TID [%s] Fill field with ID [%s]. Text [%s]", Thread.currentThread().getId(), idField, textClean ));

		} else {
			log.error(EMPTY_INPUT_MSG);
		}

		return this;
	}

	@Step("Send command '===>' [{0}]")
	@Override
	public void sendCommand(final String command) throws IllegalLengthFieldAs400Exceptions {
		BaseUtils.makeScreenCapture(String.format("Before. Enter Command [%s]", command), this);
		fillFieldWith("===>", command).sendEnter();
		BaseUtils.makeScreenCapture(String.format("After. Enter Command [%s]", command), this);
	}

	/** Implicit Wait, try to find text on the screen use RegEx */
	@Step("Trying to find text on the screen [{1}]. Regex pattern [{0}]. Explicit time wait.")
	@Override
	public boolean isTextPresent(final String pattern, final String matcher) {
		boolean status = false;

		for (int i = 0; i < TIME_SLEEP_MULTIPLIER_15; i++) {

			if (isContentPresent(pattern, matcher)) {
				status = true;
				break;
			}
			try {
				Thread.sleep(TIME_SLEEP_500);
			} catch (InterruptedException e) {
				log.error(String.format("Can't 'sleep' for [isTextPresent()]. Pattern [%s]. Matcher [%s]", pattern, matcher));
				e.printStackTrace();
			}
		}

		if (!status) {
			throw new NoSuchElementException(String.format("TID [%d] Can't find [%s] Screen", Thread.currentThread().getId(), matcher));
		}

		return status;
	}

	@Step("Try to find text on the screen [{0}]. Explicit time wait")
	@Override
	public boolean isTextPresent(final String matcher) {
		boolean status = false;

		for (int i = 0; i < TIME_SLEEP_MULTIPLIER_15; i++) {

			if (isContentPresent(matcher)) {
				status = true;
				break;
			}
			try {
				Thread.sleep(TIME_SLEEP_1000);
			} catch (InterruptedException e) {
				log.error(String.format("Can't 'sleep' for [isTextPresent()]. Matcher [%s]", matcher));
				e.printStackTrace();
			}
		}
		if (!status) {
			throw new NoSuchElementException(String.format("TID [%d] Can't find [%s] on the Screen", Thread.currentThread().getId(), matcher));
		}

		return status;
	}

	@Step("How many fields on the Screen.")
	@Override
	public int getQtyFields() {
		return screen.getScreenFields().getSize();
	}

	@Step("Press key [{1}] until matcher [{0}] will is present")
	@Override
	public TerminalDriver pressKeyUntilPresent(final String matcher, KeyMnemonic key) {
		int count = 0;

		while (!isContentPresent(matcher)) {
			if (count < TIME_SLEEP_MULTIPLIER_7) {
				sendKeys(key).waitForUnlock();
				count++;

				log.info("TID [" + Thread.currentThread().getId() + "] " + "Enter Wait Screen [" + count + "]");
			} else {
				break;
			}
		}

		if (isContentPresent(matcher)) {
			BaseUtils.makeScreenCapture(String.format("TID [%d] Screen: [%s] was found", Thread.currentThread().getId(), matcher), this);
		} else {
			BaseUtils.makeScreenCapture(String.format("TID [%d] Screen: [%s] was NOT found", Thread.currentThread().getId(), matcher),
					this);
		}

		return this;
	}

	@Step("Try to find content [{0}] on the screen.")
	public boolean isContentPresent(final String MATCHER) {
		boolean status = false;
		List<String> linesAll = getScreenContent().getLines();
		final String CLEAN_MATCHER = BaseUtils.replaceStringRegex("", MATCHER, "");

		for (String eachLine : linesAll) {
			if (eachLine.toLowerCase().contains(CLEAN_MATCHER.toLowerCase())) {
				status = true;
				break;
			}
		}

		log.info("TID [" + Thread.currentThread().getId() + "] " + "Is Content present. Status [" + status
				+ "]. Matcher [" + CLEAN_MATCHER + "]");
		return status;
	}

	@Step("Try to find content [{0}] on the screen.")
	public boolean isContentPresent(final String matcher, int times) {
		boolean status = false;
		int count = 0;
		final String cleanMatcher = BaseUtils.replaceStringRegex("", matcher, "");

		while (!status) {
			List<String> linesAll = getScreenContent().getLines();
			if (count < times) {

				for (String eachLine : linesAll) {
					if (eachLine.toLowerCase().contains(cleanMatcher.toLowerCase())) {
						status = true;
						break;
					}
				}

			} else if (count > times) {
				log.warn(String.format("TID [%d] Content is not present. Timeout [%s]", Thread.currentThread().getId(), cleanMatcher));
				break;
			}

			count++;

			try {
				Thread.sleep(TIME_SLEEP_1000);
			} catch (InterruptedException e) {
				log.error(String.format("Can't 'sleep' for [isContentPresent()]. Matcher [%s]. Counter [%d]", matcher, times));
				e.printStackTrace();
			}
		}

		log.info(String.format("TID [%s] Is Content present. Status [%b]. Matcher [%s]", Thread.currentThread().getId(), status, cleanMatcher));
		return status;
	}

	@Step("Try to find content [{1}] on the screen, using regex pattern [{0}]")
	public boolean isContentPresent(final String pattern, final String matcher) {
		boolean status = false;
		final List<String> LinesAll = getScreenContent().getLines();
		final String CLEAN_MATCHER = BaseUtils.replaceStringRegex("", matcher, "");

		for (String inter : LinesAll) {
			String currentLine = BaseUtils.regExp(pattern, inter, 1);

			if (Objects.nonNull( currentLine)) {
				status = true;
				break;
			}
		}

		log.info("TID [" + Thread.currentThread().getId() + "] " + "Is Content present. Status [" + status + "]. Pattern [" + pattern + "]. Matcher [" + CLEAN_MATCHER + "]");
		return status;
	}

	/* Use for 'ENTER' */
	@Override
	@Step("Wait to unlock Screen.")
	public boolean waitForUnlock() {

		int safeCount = 0;
		if (connected) {
			informed = false;
			informChange = ScreenOIAListener.OIA_CHANGED_KEYBOARD_LOCKED;
			while (!informed) {

				if (safeCount > 600) {
					break;
				}
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				++safeCount;
			}
			getScreenContentOrig().dumpScreen();
		}

		log.info("TID [" + Thread.currentThread().getId() + "] " + "wait for Screen UnLocked.");
		return true;
	}

	@Step("Get JFrame for [TN5250J]")
	public JFrame getFrame() {
		try {
			Thread.sleep(50L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return frame;
	}

	/* Utility for Screen content */


	@Step("Call method 'getScreenContentOrig();' from session")
	private ScreenContent getScreenContentOrig() {
		return new ScreenContent(session);
	}

	private void fillTextField(String label, final String text, boolean isHide) {

		ScreenContent content = getScreenContent();
		int index = content.indexOf(label);
		ScreenField field = null;

		String textClean;

		if (Objects.nonNull(text)) {
			textClean = text.replaceAll(NBSP_REGEX, "").trim();

			while (field == null && index < content.length()) {

				field = screen.getScreenFields().findByPosition(index++);
			}
			checkFieldExc(field, textClean, content, label, text, isHide);
		} else {
			log.error(String.format("TID [%d] Input parameter = 'null' or 'Empty'", Thread.currentThread().getId()));
		}
	}

	@Step("Check the filed with label [{3}] and input text [{4}], isHide [{5}]")
	private void checkFieldExc(final ScreenField field, final String textClean, final ScreenContent content, final String label, final String text, boolean isHide) {

		if (Objects.isNull(field)) {
			throw new IllegalStateException(
					String.format("TID [%d] Could not find field *after* label %s", Thread.currentThread().getId(), label));
		}

		if (!BaseUtils.isEmpty(textClean) && field.getFieldLength() < textClean.length()) {
			content.dumpScreen();

			throw new IllegalLengthFieldAs400Exceptions(
					String.format(LONG_INPUT_STRING_MSG,
							Thread.currentThread().getId(), field.getFieldLength(), textClean, textClean.length()));
		}

		// Clean string from space and enter in the field
		field.setString(textClean);
		final String msg = isHide
				? String.format("TID [%d] Fill field with text label [%s]. Text [*****]", Thread.currentThread().getId(),
						label)
				: String.format("TID [%d] Fill field with text label [%s]. Text [%s]", Thread.currentThread().getId(), label,
						text);
		log.info(String.format("TID [%d] %s", Thread.currentThread().getId(), msg));
	}

	@Step("Call method 'getScreenContent()' from session")
	private ScreenContent getScreenContent() {

		List<String> linesAll = getScreenContentOrig().getLines();

		if (linesAll.get(0).toLowerCase().contains(DISPLAY_MESSAGES.toLowerCase())
				|| linesAll.get(1).toLowerCase().contains(DISPLAY_MESSAGES.toLowerCase())
				|| linesAll.get(2).toLowerCase().contains(DISPLAY_MESSAGES.toLowerCase())) {

			log.info("TID [" + Thread.currentThread().getId() + "] " + "Was found [" + DISPLAY_MESSAGES + "]");
		}

		return new ScreenContent(session);
	}

	@Override
	public String toString() {
		return String.format("TerminalDriver [connected=%b, host=%s, username=%s, frame=%s, vsblt=%b]", connected,
				as400Host, username, frame, visible);
	}

}// END OF CLASS
