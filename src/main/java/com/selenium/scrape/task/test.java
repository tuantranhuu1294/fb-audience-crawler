package com.selenium.scrape.task;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class test {

    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.gecko.driver", "/home/huutuan/Downloads/Programs/geckodriver");
        WebDriver driver = new FirefoxDriver();

        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        UserLogin login = new UserLogin(driver, "nguyen.duyhung205@outlook.com", "123abc123");
        login.login();
        login.createCampain();

        DetailedTargingActions action = new DetailedTargingActions(driver);

        // Remove default element suggestion
        WebElement targetArea = driver.findElement(By.xpath("//h4/div[contains(text(), 'Detailed Targeting')]"));
        action.scrollTo(targetArea);
        Thread.sleep(2000);
        //action.removeDefaultSuggested();

        Thread.sleep(1000);
        action.selectWorkJob();

        GetAudience getSize = new GetAudience();

        try {
            Thread.sleep(30000);
            getSize.selectPotentialReach(driver, "engineer");
        } catch (InterruptedException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
