package com.selenium.scrape.task;

import com.selenium.scrape.storage.JobTitlesMySql;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AudienceCrawler {

    private static final String DefaultJobTitle = "Job Titles";
    private static final long DefaultNumUsers = 0l;
    private static int count = 0;
    private static JobTitlesMySql mysql = null;
    private static Logger LOG = Logger.getLogger(AudienceCrawler.class.getName());

    public static void selectTargeting(WebDriver driver, String word) throws InterruptedException {

        if (mysql == null)
            try {
                mysql = JobTitlesMySql.getInstance();
                LOG.info("Mysql connected!");
            } catch (ClassNotFoundException | SQLException e1) {
                // TODO Auto-generated catch block
                LOG.warning("Cannot connect to mysql server");
                e1.printStackTrace();
            }

        driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);

        WebElement enterText = driver.findElement(
                By.xpath("//span/label/input[contains(@placeholder,'Add demographics, interests or behaviors')]"));

        boolean flag = true;
        List<String> checkList = new ArrayList<String>();

        while (flag) {

            Actions action = new Actions(driver);
            action.click(enterText).sendKeys(word).build().perform();

            // List row has suggested and select a value
            WebElement suggestAreaBefore = null;
            try {

                if (driver.findElements(By.cssSelector("div._hpt")).size() != 0) {

                    suggestAreaBefore = driver.findElement(By.cssSelector("div._hpt"));
                } else {

                    action.moveToElement(enterText).doubleClick().sendKeys(Keys.DELETE).build().perform();
                }

            } catch (NoSuchElementException e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            if (suggestAreaBefore != null) {

                String rowSelected = null;
                try {

                    rowSelected = filterLabel(driver, suggestAreaBefore, checkList);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (rowSelected != null) {

                    checkList.add(rowSelected);

                    collectSuggest(driver, rowSelected);
                    removeElementSelected(driver);

                } else {

                    flag = false;
                    action.doubleClick().sendKeys(Keys.DELETE).build().perform();
                }

            } else {
                flag = false;
                action.doubleClick().sendKeys(Keys.DELETE).build().perform();
            }

        }

    }

    public static void removeElementSelected(WebDriver driver) {
        // TODO Auto-generated method stub

        Actions action = new Actions(driver);

        // Remove value has selected
        try {
            action.moveToElement(driver.findElement(By.xpath("//div/ul/li[2]/ul/div/li[contains(@class, '_2pji')]")))
                    .perform();

            driver.findElement(
                    By.xpath("//div/ul/li[2]/ul/div/li[contains(@class, '_2pji')]/button[contains(@title,'Remove')]"))
                    .click();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public static void collectSuggest(WebDriver driver, String parentName) {
        // TODO Auto-generated method stub

        WebElement suggestAreaAfter = null;
        try {
            suggestAreaAfter = driver.findElement(By.xpath(
                    "//div[contains(@class,'uiContextualLayer uiContextualLayerBelowLeft')]/div/div/div/div/div/div/ul"));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        if (suggestAreaAfter != null) {
            List<WebElement> listSelectedSuggest = suggestAreaAfter.findElements(By.tagName("li"));

            for (WebElement webElement : listSelectedSuggest) {
                if (DefaultJobTitle.equals(webElement.findElement(By.xpath("./div/div[2]")).getText())) {

                    String audienceName = webElement.findElement(By.xpath("./div/div[1]")).getText();
                    String tmp = parentName + " " + audienceName;
                    long auId = tmp.hashCode();

                    try {
                        mysql.insertBatch(auId, parentName, audienceName, DefaultJobTitle, DefaultNumUsers);
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    LOG.info(auId + ", " + parentName + ", " + audienceName);
                    count++;
                }
            }
        }

    }

    public static String filterLabel(WebDriver driver, WebElement suggestAreaBefore, List<String> checkList)
            throws Exception {
        // TODO Auto-generated method stub

        List<WebElement> listSuggest = suggestAreaBefore.findElements(By.tagName("li"));

        String rowSelected = null;
        Actions action = new Actions(driver);

        for (WebElement webElement : listSuggest) {
            if (DefaultJobTitle.equals(webElement.findElement(By.xpath(".//div/div[2]")).getText())) {

                String row = webElement.findElement(By.xpath(".//div/div[1]")).getText();
                if (!checkList.contains(row)) {

                    action.moveToElement(webElement).click().build().perform();

                    rowSelected = row;
                    long auId = rowSelected.hashCode();
                    mysql.insertBatch(auId, rowSelected, rowSelected, DefaultJobTitle, DefaultNumUsers);

                    LOG.info("Select: " + row);

                    break;
                }

            }
        }

        return rowSelected;
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        WebDriver driver = new FirefoxDriver();
        // driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        int countLoop = 0;

        UserLogin login = new UserLogin(driver, "username", "password");
        login.login();
        login.createCampain();

        BufferedReader reader = new BufferedReader(new FileReader(new File("resource/english-dictionary.txt")));
        String line = reader.readLine();

        long start = System.currentTimeMillis();
        while (line != null) {

            if (countLoop > 5) {
                countLoop = 0;
                Thread.sleep(30000);
            }

            selectTargeting(driver, line);

            countLoop++;
            line = reader.readLine();

        }

        // Close mysql
        if (mysql != null)
            try {
                mysql.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        reader.close();

        driver.close();

        System.out.println("Total: " + count);

        long end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.000000");
        System.out.println("Execution time is: " + formatter.format((end - start) / 1000d) + " seconds");

    }
}
