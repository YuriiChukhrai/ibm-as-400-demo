package com.yc.qa.as400.util;

import static com.yc.qa.as400.util.BaseConfig.Prop.*;
import static com.yc.qa.as400.util.BaseConfig.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.extern.log4j.Log4j;
import com.yc.qa.as400.TerminalDriver;

/**
 * @author limit (Yurii Chukhrai)
 */
@Log4j
public final class ThreadLocalStore {

	private ThreadLocalStore() {
		throw new UnsupportedOperationException("Illegal access to private constructor");
	}

	/* AS-400 */
	private static final ThreadLocal<TerminalDriver> AS400_DRIVER_CONTAINER = new ThreadLocal<>();

	/* TN5250J */
	private static final ThreadLocal<List<String>> TN5250J_SCREEN_CONTENT_CONTAINER = new ThreadLocal<>();

	public static TerminalDriver getTerminalDriverAs400() {
		log.debug(String.format("TID [%d] - getDriverAs400()", Thread.currentThread().getId()));
		return AS400_DRIVER_CONTAINER.get();
	}

	public static synchronized void setTerminalDriverAs400(final String host) {
		log.debug(String.format("TID [%d] - setDriverAs400()", Thread.currentThread().getId()));
		AS400_DRIVER_CONTAINER.set(new TerminalDriver(getProperty(URI_AS400), getProperty(USR_AS400), getProperty(PSW_AS400), isProperty(HEADLESS_MODE_AS400), getProperty(SESSION_HOST_PORT_AS400), getProperty(SSL_TYPE_AS400) ));
	}

	public static ThreadLocal<TerminalDriver> getTerminalDriverAs400Container() {
		log.debug(String.format("TID [%d] - getDriverAs400Container()", Thread.currentThread().getId()));
		return AS400_DRIVER_CONTAINER;
	}

	public static ThreadLocal<List<String>> getTn5250jScreenContentContainer() {
		log.debug(String.format("TID [%d] - getTn5250jScreenContentContainer()", Thread.currentThread().getId()));
		return TN5250J_SCREEN_CONTENT_CONTAINER;
	}

	public static List<String> getTn5250jScreenContent() {
		log.debug(String.format("TID [%d] - getTn5250jScreenContent()", Thread.currentThread().getId()));
		return getTn5250jScreenContentContainer().get();
	}

	public static synchronized void setTn5250jScreenContentContainer() {
		log.info(String.format("TID [%d] - setTn5250jScreenContentContainer()", Thread.currentThread().getId()));
		getTn5250jScreenContentContainer().set(new ArrayList<String>(10_000));
	}

	public static synchronized void cleanTn5250jScreenContentContainer() {
		log.debug(String.format("TID [%d] - setTn5250jScreenContentContainer()", Thread.currentThread().getId()));
		if (Objects.nonNull(getTn5250jScreenContentContainer()) && Objects.nonNull(getTn5250jScreenContentContainer().get())) {
			getTn5250jScreenContentContainer().get().clear();
		}
		getTn5250jScreenContentContainer().remove();
	}

	public static void cleanAllThreadLocalStores() {
		cleanTn5250jScreenContentContainer();
		getTerminalDriverAs400Container().remove();
		log.info(String.format("TID [%d] - cleanAllThreadLocalStores()", Thread.currentThread().getId()));
	}

}// END CLASS
