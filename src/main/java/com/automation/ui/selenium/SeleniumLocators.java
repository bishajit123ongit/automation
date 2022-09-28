package com.automation.ui.selenium;

import com.automation.ui.methods.FrameworkSubRoutine;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SeleniumLocators extends FrameworkSubRoutine {
    WebDriver driver;
    public SeleniumLocators(WebDriver driver){
        this.driver = driver;
    }
    public By getObject(String locatorValue, String objectLocator) throws Exception{

        if(locatorValue.startsWith("Gui")){
            objectProperties = FrmSubActiveObjectRepositoryGet(locatorValue);
            locatorValue = objectProperties[1];
        }

        if(objectLocator.equalsIgnoreCase("XPATH")){
            return By.xpath(locatorValue);
        }

        else if(objectLocator.equalsIgnoreCase("CLASSNAME")){
            return By.className(locatorValue);
        }

        else if(objectLocator.equalsIgnoreCase("NAME")){
            return By.name(locatorValue);
        }

        else if(objectLocator.equalsIgnoreCase("ID")){
            return By.id(locatorValue);
        }

        else if(objectLocator.equalsIgnoreCase("CSS")){
            return By.cssSelector(locatorValue);
        }

        else if(objectLocator.equalsIgnoreCase("LINK")){
            return By.linkText(locatorValue);
        }

        else if(objectLocator.equalsIgnoreCase("PARTIALLINK")){
            return By.partialLinkText(locatorValue);
        }

        else if(objectLocator.equalsIgnoreCase("TAGNAME")){
            return By.tagName(locatorValue);
        }

        else {
            throw new Exception("Wrong object locator");
        }
    }

}
