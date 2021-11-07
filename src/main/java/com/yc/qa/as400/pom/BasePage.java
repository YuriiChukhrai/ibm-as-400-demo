package com.yc.qa.as400.pom;

/**
 * @author limit (Yurii Chukhrai)
 */
public interface BasePage {

    BasePage isTextPresent(final String value);
    BasePage sendCommand(final String command);
    MainMenuPage goToMainMenu();
    BasePage processingDisplayMessages();
}
