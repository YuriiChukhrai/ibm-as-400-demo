package com.yc.qa.as400.test;

import com.yc.qa.as400.util.BaseConfig;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.Objects;

import com.yc.qa.as400.util.ThreadLocalStore;

/**
 * @author limit (Yurii Chukhrai)
 */
public class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() throws IOException {
        ThreadLocalStore.setTn5250jScreenContentContainer();
        ThreadLocalStore.setTerminalDriverAs400(BaseConfig.getProperty(BaseConfig.Prop.URI_AS400));
        ThreadLocalStore.getTerminalDriverAs400().openTN5250J();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (Objects.nonNull(ThreadLocalStore.getTerminalDriverAs400()) && ThreadLocalStore.getTerminalDriverAs400().isTerminalConnected()) {
            ThreadLocalStore.getTerminalDriverAs400().closeTN5250J();
        }
        ThreadLocalStore.cleanAllThreadLocalStores();
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        ThreadLocalStore.getTerminalDriverAs400Container().remove();
    }

	@AfterSuite(alwaysRun = true)
	public void afterSuite() {
		if (Objects.nonNull(ThreadLocalStore.getTerminalDriverAs400()) && ThreadLocalStore.getTerminalDriverAs400().isTerminalConnected()) {
			ThreadLocalStore.getTerminalDriverAs400().closeTN5250J();
		}
		ThreadLocalStore.cleanAllThreadLocalStores();
	}
}
