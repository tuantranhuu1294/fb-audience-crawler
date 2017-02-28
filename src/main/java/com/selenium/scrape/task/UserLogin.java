package com.selenium.scrape.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserLogin {

    private WebDriver driver;
    private String userName;
    private String password;

    private static Logger LOG = LogManager.getLogger(UserLogin.class);

    public UserLogin() {
    }

    public UserLogin(WebDriver driver, String userName, String password) {
        this.driver = driver;
        this.userName = userName;
        this.password = password;
    }

    public void login() {
        driver.get("https://facebook.com/ads/manager/");

        driver.findElement(By.id("email")).sendKeys(userName);
        driver.findElement(By.id("pass")).sendKeys(password);
        driver.findElement(By.id("loginbutton")).click();

        LOG.info("Login successful");
    }

    private static final String DEFAULT_KW_PROMOTE_YOUR_PATE = "Brand Awareness";
    private static final String PROMOTE_YOUR_PAGE_XPATH = "//table/tbody/tr[contains(@class, '_51mx')]/td/div/div/ul/div/div";
    private static final String CREATE_AD_ACCOUNT_XPATH = "//div/button/em[contains(text(),'Create Advert Account')]";
    private static final String START_OVER_BUTTON_XPATH = "//div[contains(@class, '_5aj')]/div/div/div/button/em[contains(text(),"
            + " 'Start Over')]";
    private static final String CONTINUE_BUTTON_XPATH = "//div/div[2]/div/button/em[contains(text(),'Continue')]";

    public void createCampain() {
        // if old campaign dialog display. Click Start Over button

        try {
            Thread.sleep(7000);
            WebElement startOverButton = driver.findElement(By.xpath(START_OVER_BUTTON_XPATH));
            startOverButton.click();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        // New version element
        List<WebElement> list = new ArrayList<>();
        if (driver.findElements(By.xpath(PROMOTE_YOUR_PAGE_XPATH)).size() != 0) {
            list = driver.findElements(By.xpath(PROMOTE_YOUR_PAGE_XPATH));
        }
        LOG.debug("List marketing " + list.size());

        for (WebElement webElement : list) {
            WebElement labelElement = webElement.findElement(By.xpath(".//li[1]/label/div[contains(@class, '_2ddk')]/em"));
            if (DEFAULT_KW_PROMOTE_YOUR_PATE.equals(labelElement.getText())) {
                webElement.click();
                break;
            }
        }

        // Step 2: Click to 'Create Ad Account'
        driver.findElement(By.xpath(CREATE_AD_ACCOUNT_XPATH)).click();

        WebElement submitButton;
        if (driver.findElements(By.xpath(CONTINUE_BUTTON_XPATH)).size() != 0)
            submitButton = driver.findElement(By.xpath(CONTINUE_BUTTON_XPATH));
        else
            submitButton = driver.findElement(By.xpath("//div/div[2]/button/em[contains(text(),'Set Audience & Budget')]"));

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

    public static void main(String[] args) throws InterruptedException, IOException {
        System.setProperty("webdriver.gecko.driver", "/home/huutuan/Downloads/Programs/geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        UserLogin login = new UserLogin(driver, "nguyen.duyhung205@outlook.com", "123abc123");
        login.login();
        login.createCampain();
    }
}
