package com.yc.qa.as400.test;

import com.yc.qa.as400.util.BaseConfig;
import com.yc.qa.as400.util.BaseUtils;
import org.testng.annotations.*;

import java.io.IOException;

import com.yc.qa.as400.util.ThreadLocalStore;

/**
 * @author limit (Yurii Chukhrai)
 */
public class BaseTest {

    @BeforeTest(alwaysRun = true)
    public void beforeTest() throws IOException {
        ThreadLocalStore.setTn5250jScreenContentContainer();
        ThreadLocalStore.setTerminalDriverAs400(BaseConfig.getProperty(BaseConfig.Prop.URI_AS400));
        ThreadLocalStore.getTerminalDriverAs400().openTN5250J();
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        BaseUtils.handleClosingTerminal();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        BaseUtils.handleClosingTerminal();
        ThreadLocalStore.cleanAllThreadLocalStores();
    }
}
