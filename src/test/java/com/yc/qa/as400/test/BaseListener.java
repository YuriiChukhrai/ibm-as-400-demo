package com.yc.qa.as400.test;

import static com.yc.qa.as400.util.BaseUtils.attachText;
import static com.yc.qa.as400.util.ThreadLocalStore.*;
import static java.lang.String.format;

import com.yc.qa.as400.util.BaseUtils;
import lombok.extern.log4j.Log4j;
import org.testng.*;

import java.util.Objects;

/**
 * @author limit (Yurii Chukhrai)
 * This Class provide Actions if something happened in Test Classes
 * (PASS-FAIL-RUN)
 */
@Log4j
public class BaseListener implements ITestListener {

	@Override
	public void onTestSuccess(ITestResult result) {
		attachTestArtifacts("PASS");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		attachTestArtifacts("FAIL");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		BaseUtils.handleClosingTerminal();
	}

	/* Methods of Interface 'ITestListener' */
	@Override
	public void onTestStart(ITestResult result) {
		//NOP
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

	/**
	* Attach a) last screenshot of th5250j; b) screens (texts).
	* Try to safe Surefire booster process.
	**/
	private void attachTestArtifacts(final String testState){
		try {
			BaseUtils.makeScreenCapture(String.format(" @Test - %s ", testState), Objects.nonNull(getTerminalDriverAs400()) ? getTerminalDriverAs400() : null);
			attachText(" AS400 Screens text", BaseUtils.concatCollectionToString(getTn5250jScreenContent(), false));
		} catch (Throwable e) {
			log.error(format("TID [%d] ERROR - Can't attache (screenshot/screen text context) test artifacts", Thread.currentThread().getId()));
			e.printStackTrace();
		}
	}
}
