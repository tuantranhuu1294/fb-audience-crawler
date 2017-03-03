package com.selenium.scrape.task;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.selenium.scrape.util.ConfigUtils.getString;

public class UserLogin {

    private WebDriver driver;
    private String userName;
    private String password;
    private Configuration config;

    private static Logger LOG = LogManager.getLogger(UserLogin.class);

    public UserLogin() {
    }

    public UserLogin(WebDriver driver, Configuration config, String userName, String password) {
        this.driver = driver;
        this.userName = userName;
        this.password = password;
        this.config = config;
    }

    public void login() {
        driver.get(getString(config, "fb.xpath.login.url"));

        driver.findElement(By.id("email")).sendKeys(userName);
        driver.findElement(By.id("pass")).sendKeys(password);
        driver.findElement(By.id("loginbutton")).click();

        LOG.info("Login successful");
    }


    public void createCampain() {
        // if old campaign dialog display. Click Start Over button
        try {
            Thread.sleep(7000);
            WebElement startOverButton = driver.findElement(By.xpath(getString(config, "fb.xpath.campaign" +
                    ".start_over_button")));
            startOverButton.click();
        } catch (Exception e) {
            LOG.warn("Start over button not found");
        }

        // New version element
        List<WebElement> list = new ArrayList<>();
        if (driver.findElements(By.xpath(getString(config, "fb.xpath.campaign.marketing_objective"))).size() != 0) {
            list = driver.findElements(By.xpath(getString(config, "fb.xpath.campaign.marketing_objective")));
        }
        LOG.debug("List marketing " + list.size());

        for (WebElement webElement : list) {
            WebElement labelElement = webElement.findElement(By.xpath(getString(config, "fb.xpath.campaign" +
                    ".default_campaign_type")));
            if (getString(config, "fb.value.campaign.default_campaign_type_keyword").equals(labelElement.getText())) {
                webElement.click();
                break;
            }
        }

        // Step 2: Click to 'Create Ad Account'
        driver.findElement(By.xpath(getString(config, "fb.xpath.campaign.create_ad_account"))).click();

        WebElement submitButton;
        if (driver.findElements(By.xpath(getString(config, "fb.xpath.campaign.continue_button"))).size() != 0)
            submitButton = driver.findElement(By.xpath(getString(config, "fb.xpath.campaign.continue_button")));
        else
            submitButton = driver.findElement(By.xpath(getString(config, "fb.xpath.campaign.set_audience_budget_button")));

        submitButton.click();

        LOG.info("Campaign created");

    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
