package com.yc.qa.as400.pom;

import com.yc.qa.as400.TerminalDriver;
import com.yc.qa.as400.exception.IllegalLengthFieldAs400Exceptions;
import com.yc.qa.as400.util.BaseUtils;
import com.yc.qa.as400.util.ThreadLocalStore;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;
import org.tn5250j.keyboard.KeyMnemonic;

import static com.yc.qa.as400.util.ObjectSupplier.$;

/**
 * @author limit (Yurii Chukhrai)
 */
@Log4j
public class BasePageImpl implements BasePage {

    public final static String START_SCREEN_LABEL = "Welcome to PUB400.com";
    public final static String DISPLAY_MESSAGES = "Display Messages";

    public static final String MAIN_MENU_ID = "MAIN";
    public static final String MAIN_MENU_LABEL = "IBM i Main Menu";

    private final TerminalDriver terminalDriver;

    public BasePageImpl() {
        this.terminalDriver = ThreadLocalStore.getTerminalDriverAs400();
    }

    public BasePageImpl(TerminalDriver terminalDriver) {
        this.terminalDriver = terminalDriver;
    }

    //Encapsulation TerminalDriver
    @Step("Is text [{0}] present on the Screen. BasePage")
    @Override
    public final BasePageImpl isTextPresent(final String value) {
            processingDisplayMessages();
           terminalDriver.isTextPresent(value);
           return this;
    }

    @Step("Send command '===>' [{0}]. BasePage")
    @Override
    public BasePageImpl sendCommand(final String command) throws IllegalLengthFieldAs400Exceptions {
        processingDisplayMessages();
        terminalDriver.fillFieldWith("===>", command).sendEnter();
        return this;
    }

    @Step("Go back to the Main menu. BasePage")
    @Override
    public MainMenuPage goToMainMenu() {
        processingDisplayMessages();
        terminalDriver.pressKeyUntilPresent(MAIN_MENU_LABEL, KeyMnemonic.PF3);
        return $(MainMenuPageImpl.class);
    }

    /* Display Messages OR find text [OR06U0REJ] */
    @Step("Processing [Display Messages] Popup. BasePage")
    @Override
    public final BasePageImpl processingDisplayMessages() {

        if(terminalDriver.isContentPresent(DISPLAY_MESSAGES)){
            if (terminalDriver.isContentPresent(DISPLAY_MESSAGES, 60)) {
                BaseUtils.makeScreenCapture("Screen ID: Display Messages.", terminalDriver);
                // Cancel messages
                terminalDriver.sendKeys(KeyMnemonic.PF3).waitForUnlock();
                log.warn(String.format("[%s] was found.", DISPLAY_MESSAGES));
            }
        }

        return this;
    }


}//BasePage
