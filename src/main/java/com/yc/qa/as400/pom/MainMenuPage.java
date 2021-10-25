package com.yc.qa.as400.pom;

public interface MainMenuPage extends BasePage {
    UserTasks goToUserTask();

    /*
    <T> T goToOfficeTask(Class<T> pageObject);
    <T> T goToGeneralSystemTask(Class<T> pageObject);
    <T> T goToFilesLibrariesFoldes(Class<T> pageObject);
    <T> T goToProgramming(Class<T> pageObject);
    <T> T goToCommunications(Class<T> pageObject);
    <T> T goToDefineOrChangeSystem(Class<T> pageObject);
    <T> T goToProblemHandling(Class<T> pageObject);
    <T> T goToDisplayMenu(Class<T> pageObject);
    <T> T goToInformationAssistantOptions(Class<T> pageObject);
    <T> T goToIbmiAccessTask(Class<T> pageObject);
     */

     void signOff();
}
