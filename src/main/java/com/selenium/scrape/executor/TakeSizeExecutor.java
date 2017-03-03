package com.selenium.scrape.executor;

import com.selenium.scrape.task.DetailedTargetingActions;
import com.selenium.scrape.task.GetAudience;
import com.selenium.scrape.task.UserLogin;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by huutuan on 04/01/2017.
 */
public class TakeSizeExecutor {

    private static Logger LOG = LogManager.getLogger(TakeSizeExecutor.class);
    private static final int DEFAULT_SLEEP_TIME = 40000;
    private static final int DEFAULT_IMPLICITLY_WAIT_TIME = 10;

    private int sleepTime;
    private int implicitlyWait;
    private String configFile;

    public TakeSizeExecutor(String configFile) {
        this.configFile = configFile;
        sleepTime = DEFAULT_SLEEP_TIME;
        implicitlyWait = DEFAULT_IMPLICITLY_WAIT_TIME;
    }

    public TakeSizeExecutor(String configFile, int sleepTime, int implicitlyWait) {
        this.configFile = configFile;
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
        } else if (StringUtils.isEmpty(configFile)) {
            LOG.error("Please choose config file.");
            System.out.println("Please choose config file.");
        } else {
            try {
                execute(userName, password, dictFilePath, outFilePath, geckoDriverFilePath);
            } catch (IOException | InterruptedException | ConfigurationException e) {
                // TODO Auto-generated catch block
                LOG.warn("Execution exception", e);
            }
        }
    }

    private void execute(String userName, String password, String dictFilePath, String outFilePath, String
            geckoDriverFilePath) throws IOException, InterruptedException, ConfigurationException {

        Configuration config = new PropertiesConfiguration(configFile);

        System.setProperty("webdriver.gecko.driver", geckoDriverFilePath);
        WebDriver driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

        UserLogin login = new UserLogin(driver, config, userName, password);
        login.login();
        login.createCampain();

        LOG.info("Sleeping 30s for setting up detailed targeting components");
        Thread.sleep(30000);

        DetailedTargetingActions action = new DetailedTargetingActions(driver, config);

        // Remove default element suggestion
        WebElement targetArea = driver.findElement(By.xpath(config.getString("fb.xpath.target.targeting_detailed_area")));
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

        LOG.info("Crawler started");
        long start = System.currentTimeMillis();
        int countLoop = 0;

        GetAudience process = new GetAudience(outFilePath, config);
        if (defaultPotentialReach > 0)
            process.setDefaultPotentialReach(defaultPotentialReach);
        if (defaultWorkJob != null)
            process.setDefaultWorkJob(defaultWorkJob);


        // Execute select potential reach for all words in dictionary file
        List<String> words = loadDictionary(dictFilePath);
        for (String word : words) {
            if (countLoop > 10) {
                countLoop = 0;
                LOG.info("Sleeping " + sleepTime / 1000 + " seconds");
                Thread.sleep(sleepTime);
            }
            process.selectPotentialReach(driver, word);
            countLoop++;
        }

        process.close();
        driver.close();

        long end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.000000");
        LOG.info("Execution time is: " + formatter.format((end - start) / 1000d) + " seconds");
        LOG.info("Finished!");
        System.out.println("Crawl job executed successful with " + formatter.format((end - start) / 1000d) + " seconds");
    }


    private List<String> loadDictionary(String file) throws IOException {
        LOG.info("Loading dictionary file ...");
        List<String> words = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
        String word = reader.readLine();
        while (word != null) {
            words.add(word);
            word = reader.readLine();
        }
        reader.close();

        return words;
    }
}
