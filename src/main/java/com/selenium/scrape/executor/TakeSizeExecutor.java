package com.selenium.scrape.executor;

import com.selenium.scrape.task.DetailedTargingActions;
import com.selenium.scrape.task.GetAudience;
import com.selenium.scrape.task.UserLogin;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by huutuan on 04/01/2017.
 */
public class TakeSizeExecutor {

    private static Logger LOG = LogManager.getLogger(TakeSizeExecutor.class);
    public static final int DEFAULT_SLEEP_TIME = 40000;
    public static final int DEFAULT_IMPLICITLY_WAIT_TIME = 10;

    private int sleepTime;
    private int implicitlyWait;

    public TakeSizeExecutor() {
        sleepTime = DEFAULT_SLEEP_TIME;
        implicitlyWait = DEFAULT_IMPLICITLY_WAIT_TIME;
    }

    public TakeSizeExecutor(int sleepTime, int implicitlyWait) {
        this.sleepTime = sleepTime;
        this.implicitlyWait = implicitlyWait;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void setImplicitlyWait(int implicitlyWait) {
        this.implicitlyWait = implicitlyWait;
    }

    public void takeSize(String userName, String password, String dictFilePath, String outFilePath, String
            geckoDriverFilePath) {

        if (StringUtils.isEmpty(userName)) {
            LOG.error("Please enter user name or email.");
            System.out.println("Please enter user name or email.");
        } else if (StringUtils.isEmpty(password)) {
            LOG.error("Please enter password.");
            System.out.println("Please enter password.");
        } else if (StringUtils.isEmpty(dictFilePath)) {
            LOG.error("Please choose dictionary file.");
            System.out.println("Please choose dictionary file.");
        } else if (StringUtils.isEmpty(outFilePath)) {
            LOG.error("Please choose output file.");
            System.out.println("Please choose output file.");
        } else {
            try {
                execute(userName, password, dictFilePath, outFilePath, geckoDriverFilePath);
            } catch (IOException | InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void execute(String userName, String password, String dictFilePath, String outFilePath, String
            geckoDriverFilePath)
            throws IOException, InterruptedException {
        System.setProperty("webdriver.gecko.driver", geckoDriverFilePath);
        WebDriver driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        UserLogin login = new UserLogin(driver, userName, password);
        login.login();
        login.createCampain();

        LOG.info("Sleeping 30s for setting up detailed targeting components");
        Thread.sleep(30000);

        DetailedTargingActions action = new DetailedTargingActions(driver);

        // Remove default element suggestion
        WebElement targetArea = driver.findElement(By.xpath("//h4/div[contains(text(), 'Detailed targeting')]"));
        action.scrollTo(targetArea);
        Thread.sleep(2000);

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        action.removeDefaultSuggested();

        Thread.sleep(1000);
        action.selectWorkJob();

        // Select default job title, then get potential reach
        LOG.info("Time to select default job for potential reach counting process");
        Thread.sleep(15000);
        int defaultPotentialReach = action.getPotentialReach();
        LOG.info("Default potential reach " + defaultPotentialReach);

        String defaultWorkJob = action.getElementSelectedName();
        LOG.info("Default job title selected: " + defaultWorkJob);
        if (defaultPotentialReach > 1000000)
            defaultPotentialReach = 0;

        // Set implicitly wait time
        driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);

        LOG.info("Crawling is running");
        long start = System.currentTimeMillis();
        int countLoop = 0;

        GetAudience process = new GetAudience(outFilePath);
        if (defaultPotentialReach > 0)
            process.setDefaultPotentialReach(defaultPotentialReach);
        if (defaultWorkJob != null)
            process.setDefaultWorkJob(defaultWorkJob);

        BufferedReader reader = new BufferedReader(new FileReader(new File(dictFilePath)));
        String line = reader.readLine();
        while (line != null) {
            if (countLoop > 10) {
                countLoop = 0;
                LOG.info("Sleeping " + sleepTime / 1000 + " seconds");
                Thread.sleep(sleepTime);
            }
            process.selectPotentialReach(driver, line);
            countLoop++;
            line = reader.readLine();
        }

        reader.close();
        process.close();
        driver.close();

        long end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.000000");
        LOG.info("Execution time is: " + formatter.format((end - start) / 1000d) + " seconds");
        LOG.info("Finished!");
        System.out.println("Crawl job executed successful with " + formatter.format((end - start) / 1000d) + " seconds");
    }


}
