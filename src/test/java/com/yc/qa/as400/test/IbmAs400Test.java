package com.yc.qa.as400.test;

import com.yc.qa.as400.pom.MainMenuPageImpl;
import io.qameta.allure.*;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.yc.qa.as400.util.ObjectSupplier.$;

/**
 * @author limit (Yurii Chukhrai)
 */
@Listeners(BaseListener.class)
public class IbmAs400Test extends BaseTest{

    @Feature("AS400")
    @Story("CIR-098")
    @TmsLink("TestCaseId: TC-001")
    @Issues({ @Issue("CIR-001"), @Issue("CIR-002") })
    @Owner("Yurii Chukhrai")
    @Links({ @Link(url="http://PUB400.COM", name="Public IBMi AS-400 server"), @Link(url="https://github.com/YuriiChukhrai/ibm-as-400-demo", name="GitHub"), @Link(url="https://www.linkedin.com/in/yurii-c-b55aa6174/", name="LinkedIn") })
    @Description("Test: Login to the public IBMi AS-400 server - [http://PUB400.COM]")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "AS-400", enabled = true)
    public void loginTest() {

        /*
        * TC:
        * 1. Go to the [User Tasks]
        * 2. Go back to the Main menu
        * 3. SignOff for PUB400.com
        * */
        $(MainMenuPageImpl.class).goToUserTask().goToMainMenu().signOff();
    }
}
