package com.yc.qa.as400.exception;

import lombok.extern.log4j.Log4j;

/**
 * @author limit (Yurii Chukhrai) on [Nov 2017]
 */
@Log4j
public class IllegalLengthFieldAs400Exceptions extends RuntimeException {

	private static final long serialVersionUID = -6423330935345283215L;

	public IllegalLengthFieldAs400Exceptions(String msg) {
		super(msg);
		log.error(msg);
	}

	public IllegalLengthFieldAs400Exceptions(String msg, Throwable throwable) {
		super(String.format("Chain exception. %s", msg), throwable);
		log.error(String.format("Chain exception. %s", msg));
	}
}
