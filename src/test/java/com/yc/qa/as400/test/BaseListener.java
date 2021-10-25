package com.yc.qa.as400.test;

import static com.yc.qa.as400.util.BaseUtils.attachText;
import static com.yc.qa.as400.util.ThreadLocalStore.*;
import static java.lang.String.format;

import com.yc.qa.as400.util.BaseUtils;
import lombok.extern.log4j.Log4j;
import org.testng.*;

/**
 * @author limit (Yurii Chukhrai)
 * This Class provide Actions if something happened in Test Classes
 * (PASS-FAIL-RUN)
 */
@Log4j
public class BaseListener implements ITestListener {

	/* Methods of Interface 'ITestListener' */
	@Override
	public void onTestStart(ITestResult result) {
		//NOP
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		/* Try to safe Surefire booster process */
		try {
			BaseUtils.makeScreenCapture(" @Test - PASS ", getTerminalDriverAs400() != null ? getTerminalDriverAs400() : null);
			attachText("AS400 Screens text", BaseUtils.concatCollectionToString(getTn5250jScreenContent(), false));
		} catch (Throwable e) {
			log.error(format("TID [%d] ERROR - Can't precede 'onTestSuccess()'", Thread.currentThread().getId()));
			e.printStackTrace();
		}
	}

	@Override
	public void onTestFailure(ITestResult result) {

		/* Try to safe Surefire booster process */
		try {
			BaseUtils.makeScreenCapture(" @Test - FAIL ", getTerminalDriverAs400());
			attachText("AS400 Screens text", BaseUtils.concatCollectionToString(getTn5250jScreenContent(), false));
		} catch (Throwable e) {
			log.error(format("TID [%d] ERROR - Can't precede 'onTestFailure()'", Thread.currentThread().getId()));
			e.printStackTrace();
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		if (getTerminalDriverAs400() != null && getTerminalDriverAs400().isTerminalConnected()) {
			getTerminalDriverAs400().closeTN5250J();
		}
		getTerminalDriverAs400Container().remove();
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		//NOP
	}

	@Override
	public void onStart(ITestContext context) {
		//NOP
	}

	@Override
	public void onFinish(ITestContext context) {
		//NOP
	}
}
