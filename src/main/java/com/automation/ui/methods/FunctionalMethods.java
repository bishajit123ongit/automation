package com.automation.ui.methods;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;

public class FunctionalMethods extends GenericMethods {
    public static boolean AppLaunch(String BrowserType, String CheckFirst, String ExtraParam) {
        if (BrowserType.equals("DEFAULT") || BrowserType.equals("GblBrowserType")) {
            BrowserType = currentGlobalParams.getProperty("GblBrowserType").split("~")[0];
        }
        try {
            switch (CheckFirst.toUpperCase()) {

                case "TRUE":
                case "YES":
                case "DEFAULT":

                    if (driver != null) {
                        tabs = new ArrayList<>(driver.getWindowHandles());
                        if (tabs.size() > 1) {
                            for (Object tab : tabs) {
                                if (tab.equals(tabsArray.get(HomePageTabName))) {
                                    driver.switchTo().window(tabsArray.get(HomePageTabName));
                                    ObjectVisibilityAssert("GuiHomeMenuButton", "DEFAULT", "NULL");
                                    break;
                                } else if (tab.equals(tabsArray.get(LoginPageTabName))) {
                                    driver.switchTo().window(tabsArray.get(LoginPageTabName));
                                    ObjectVisibilityAssert("GuiUserName", "DEFAULT", "NULL");
                                    AppLogin(null, null, null, null);
                                    break;
                                } else {
                                    driver.close();
                                }
                            }
                        } else if (tabs.size() == 1) {
                            if (tabs.get(0).equals(tabsArray.get(HomePageTabName))) {
                                driver.switchTo().window(tabsArray.get(HomePageTabName));
                                ObjectVisibilityAssert("GuiHomeMenuButton", "DEFAULT", "NULL");
                            } else if (tabs.get(0).equals(tabsArray.get(LoginPageTabName))) {
                                driver.switchTo().window(tabsArray.get(LoginPageTabName));
                                ObjectVisibilityAssert("GuiUserName", "DEFAULT", "NULL");
                                AppLogin(null, null, null, null);
                            } else {
                                driver.get(currentGlobalParams.getProperty("GblAppWebUrl").split("~")[0]);
                                tabsArray.put(LoginPageTabName, driver.getWindowHandle());
                                tabsTable.put(LoginPageTabName, 1, driver.getWindowHandle());
                            }
                        }
                    } else {
                        FrmSubBrowserDriverSetUp(BrowserType);
                        driver.get(currentGlobalParams.getProperty("GblAppWebUrl").split("~")[0]);
                        tabsArray.put(LoginPageTabName, driver.getWindowHandle());
                        tabsTable.put(LoginPageTabName, 1, driver.getWindowHandle());
                    }
                    break;

                case "FALSE":
                case "NO":

                    FrmSubBrowserDriverSetUp(BrowserType);
                    driver.get(currentGlobalParams.getProperty("GblAppWebUrl").split("~")[0]);
                    tabsArray.put(LoginPageTabName, driver.getWindowHandle());
                    tabsTable.put(LoginPageTabName, 1, driver.getWindowHandle());
                    break;
                default:
                    message = "Check first value is incorrect!";
                    FrmSubErrorMsgArrayInsert(message, null);
                    message = "";
                    return false;
            }
            return true;
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    //Application login functionality
    public static boolean AppLogin(String UserName, String PassWord, String CheckFirst, String ExtraParam) throws Exception {

        if (UserName.toUpperCase().equals("NULL")) {
            UserName = currentGlobalParams.getProperty("GblLoginUserName").split("~")[0];
        }
        if (PassWord.toUpperCase().equals("NULL")) {
            PassWord = currentGlobalParams.getProperty("GblLoginPassWord").split("~")[0];
        }

        try {
            switch (CheckFirst.toUpperCase()) {

                case "TRUE":
                case "YES":
                case "DEFAULT":

                    if (driver != null) {
                        tabs = new ArrayList<>(driver.getWindowHandles());
                        for (Object tab : tabs) {
                            if (tab.equals(tabsArray.get(HomePageTabName))) {
                                driver.switchTo().window(tabsArray.get(HomePageTabName));
                                ObjectVisibilityAssert("GuiHomeMenuButton", "DEFAULT", "NULL");
                                objectProperties = FrmSubActiveObjectRepositoryGet("GuiLoggedInUserName");

                                if (driver.findElement(By.xpath(objectProperties[1])).getText().contains(UserName)) {
                                    break;
                                } else {
                                    AppLogout("HOMEPAGE", "YES", null);
                                    ObjectVisibilityAssert("GuiUserName", "DEFAULT", "NULL");
                                    ObjectActionSet("GuiUserName", "VALUESET", UserName, null);
                                    ObjectActionSet("GuiPassWord", "VALUESET", PassWord, null);
                                    ObjectActionSet("GuiLogin", "LCLICK", "NULL", null);
                                    ObjectVisibilityAssert("GuiMainMenu", "DEFAULT", "NULL");
                                    tabsArray.put(driver.getTitle(), driver.getWindowHandle());
                                    tabsTable.put(driver.getTitle(), tabsTable.row(driver.getTitle()).size() + 1, driver.getWindowHandle());
                                    break;
                                }
                            } else if (tab.equals(tabsArray.get(LoginPageTabName))) {
                                ObjectVisibilityAssert("GuiUserName", "DEFAULT", "NULL");
                                ObjectActionSet("GuiUserName", "VALUESET", UserName, null);
                                ObjectActionSet("GuiPassWord", "VALUESET", PassWord, null);
                                ObjectActionSet("GuiLogin", "LCLICK", "NULL", null);
                                ObjectVisibilityAssert("GuiMainMenu", "DEFAULT", "NULL");
                                tabsArray.put(driver.getTitle(), driver.getWindowHandle());
                                tabsTable.put(driver.getTitle(), tabsTable.row(driver.getTitle()).size() + 1, driver.getWindowHandle());
                                break;
                            } else {
                                driver.close();
                            }
                        }
                    }
                    break;

                case "FALSE":
                case "NO":

                    ObjectVisibilityAssert("GuiUserName", "DEFAULT", "NULL");
                    ObjectActionSet("GuiUserName", "VALUESET", UserName, null);
                    ObjectActionSet("GuiPassWord", "VALUESET", PassWord, null);
                    ObjectActionSet("GuiLogin", "LCLICK", "NULL", null);
                    ObjectVisibilityAssert("GuiMainMenu", "DEFAULT", "NULL");
                    tabsArray.put(driver.getTitle(), driver.getWindowHandle());
                    tabsTable.put(driver.getTitle(), tabsTable.row(driver.getTitle()).size() + 1, driver.getWindowHandle());
                    break;
                default:
                    message = "Check first value is incorrect!";
                    FrmSubErrorMsgArrayInsert(message, null);
                    message = "";
                    return false;
            }
            return true;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    //Application logout functionality
    public static boolean AppLogout(String LogOutOption, String CheckFirst, String ExtraParam) {

        try {
            switch (CheckFirst.toUpperCase()) {

                case "TRUE":
                case "YES":
                case "DEFAULT":

                    if (driver != null) {
                        if (LogOutOption.toUpperCase().equals("HOMEPAGE")) {
                            AppPageMenuSelect("HOMEMENU", "Logout", null);
                        } else if (LogOutOption.toUpperCase().equals("MODULEPAGE")) {
                            AppPageMenuSelect("USERMENU", "Logout", null);
                        }
                    }
                    break;

                case "FALSE":
                case "NO":

                    if (LogOutOption.toUpperCase().equals("HOMEPAGE")) {
                        AppPageMenuSelect("HOMEMENU", "Logout", null);
                    } else if (LogOutOption.toUpperCase().equals("MODULEPAGE")) {
                        AppPageMenuSelect("USERMENU", "Logout", null);
                    }
                    break;
                default:
                    message = "Check first value is incorrect!";
                    FrmSubErrorMsgArrayInsert(message, null);
                    message = "";
                    return false;
            }
            tabsArray.remove(HomePageTabName);
            FrmSubSwitchToCurrentWindow();
            return true;

        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }

    public static boolean AppPageMenuSelect(String MenuType, String MenuPath, String ExtraParam) throws Exception {
        try {
            switch (MenuType.toUpperCase()) {
                case "HOMEMENU":
                    ObjectVisibilityAssert("GuiMainMenu", "DEFAULT", null);
                    partialXpath = commonObjects.getProperty("GuiMainMenu").split("~");
                    ObjectActionSet("GuiMainMenu", "DEFAULT", "NULL", null);
                    if (MenuPath != null) {
                        if (FrmSubIsElementPresent(By.xpath(partialXpath[1] + utilityObjects.getProperty("GuiLogoutPath").split("~")[1] + MenuPath + utilityObjects.getProperty("GuiLogoutPath").split("~")[2]))) {
                            WebElement element = driver.findElement(operation.getObject(partialXpath[1] + utilityObjects.getProperty("GuiLogoutPath").split("~")[1] + MenuPath + utilityObjects.getProperty("GuiLogoutPath").split("~")[2], "XPATH"));
                            element.click();
                        } else {
                            message = "Xpath is incorrect!";
                            FrmSubErrorMsgArrayInsert(message, null);
                            message = "";
                            return false;
                        }
                    } else {
                        message = "Menu Path is incorrect!";
                        FrmSubErrorMsgArrayInsert(message, null);
                        message = "";
                        return false;
                    }
                    break;
                case "MAINMENU":
                    UtlSyncTimeWait("1", null);
                    partialXpath = utilityObjects.getProperty("GuiMainMenuPath").split("~");
                    if (MenuPath != null) {
                        if (FrmSubIsElementPresent(By.xpath(partialXpath[1] + MenuPath + partialXpath[2]))) {
                            WebElement element = driver.findElement(operation.getObject(partialXpath[1] + MenuPath + partialXpath[2], "XPATH"));
                            element.click();
                        } else {
                            message = "Xpath is incorrect!";
                            FrmSubErrorMsgArrayInsert(message, null);
                            message = "";
                            return false;
                        }
                    } else {
                        message = "Menu Path is incorrect!";
                        FrmSubErrorMsgArrayInsert(message, null);
                        message = "";
                        return false;
                    }
                    break;
                case "DROPMENU":
                    splitedValue = MenuPath.split("\\|");
                    partialXpath = utilityObjects.getProperty("GuiDropMenu").split("~");
                    if (FrmSubIsElementPresent(By.xpath(partialXpath[1] + splitedValue[0] + partialXpath[2]))) {
                        xpath = partialXpath[1] + splitedValue[0] + partialXpath[2];
                        driver.findElement(By.xpath(xpath)).click();
                        if (FrmSubIsElementPresent(By.xpath(xpath + partialXpath[3] + splitedValue[1] + partialXpath[4]))) {
                            xpath = xpath + partialXpath[3] + splitedValue[1] + partialXpath[4];
                            driver.findElement(By.xpath(xpath)).click();
                        }
                        else {
                            message = "Could not found the list data";
                            FrmSubErrorMsgArrayInsert(message, null);
                            message = "";
                            return false;
                        }
                    }
                    else {
                        message = "Path is incorrect!";
                        FrmSubErrorMsgArrayInsert(message, null);
                        message = "";
                        return false;
                    }
                    break;

            }
            return true;
        } catch (Exception e) {
            FrmSubErrorMsgArrayInsert(FrmSubErrorStackTracePrint(e), null);
            return false;
        }
    }
}
