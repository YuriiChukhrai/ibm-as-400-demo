package com.yc.qa.as400.pom;

import io.qameta.allure.Step;

import static com.yc.qa.as400.pom.UserTasksImpl.USER_MENU_ID;
import static com.yc.qa.as400.pom.UserTasksImpl.USER_TASKS_LABEL;
import static com.yc.qa.as400.util.ObjectSupplier.$;

public final class MainMenuPageImpl extends BasePageImpl implements MainMenuPage {

    @Step("Go to the [1. User tasks]")
    @Override
    public UserTasks goToUserTask() {
        isTextPresent(MAIN_MENU_ID)
                .isTextPresent(MAIN_MENU_LABEL)
                .sendCommand("1")
                .isTextPresent(USER_MENU_ID)
                .isTextPresent(USER_TASKS_LABEL);

        return $(UserTasksImpl.class);
    }

    @Step("Sign Off [90]")
    @Override
    public void signOff() {
        isTextPresent(MAIN_MENU_ID)
                .isTextPresent(MAIN_MENU_LABEL)
                .sendCommand("90")
                .isTextPresent(START_SCREEN_LABEL);
    }
}
