package com.selenium.scrape.task;

import com.selenium.scrape.util.ConfigUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetAudience {

    private int defaultPotentialReach;
    private String defaultWorkJob;
    private Configuration config;

    private static Logger LOG = LogManager.getLogger(GetAudience.class);
    private static BufferedWriter writer = null;

    public GetAudience(Configuration config) throws IOException {
        defaultPotentialReach = 0;
        this.config = config;
        writer = new BufferedWriter(new FileWriter(new File(ConfigUtils.getString(config,"default.output.file"))));
        LOG.info("Default output file created at " + ConfigUtils.getString(config,"default.output.file"));
    }

    public GetAudience(String outputFilePath, Configuration config) throws IOException {
        defaultPotentialReach = 0;
        this.config = config;
        writer = new BufferedWriter(new FileWriter(new File(outputFilePath)));
        LOG.info("Output file created at " + outputFilePath);
    }

    public void setDefaultPotentialReach(int defaultPotentialReach) {
        LOG.info("Default potential reach: " + defaultPotentialReach);
        this.defaultPotentialReach = defaultPotentialReach;
    }

    public void setDefaultWorkJob(String defaultWorkJob) {
        LOG.info("Default work job title: " + defaultWorkJob);
        this.defaultWorkJob = defaultWorkJob;
    }

    public void selectPotentialReach(WebDriver driver, String word) throws InterruptedException, IOException {

        DetailedTargetingActions action = new DetailedTargetingActions(driver, config);

        // Enter word
        action.enterText(word);
        Thread.sleep(1500);

        // List row has suggested and select a value
        List<String> listCheck = new ArrayList<>();
        while (true) {
            String rowSelected = action.selectElement(listCheck);

            if (rowSelected != null) {
                listCheck.add(rowSelected);

                Thread.sleep(1200);
                int potentialReach = action.getPotentialReach();
                if (defaultPotentialReach > 0)
                    potentialReach = potentialReach - defaultPotentialReach;

                writer.write(rowSelected + "\t" + potentialReach);
                writer.newLine();
                writer.flush();

                if (!StringUtils.isEmpty(defaultWorkJob)) {
                    action.removeElementSelectedNotEqual(defaultWorkJob);
                } else
                    action.removeElementSelectedNotEqual();
            } else {
                action.clearText();
                break;
            }
        }

    }

    public void close() throws IOException {
        writer.flush();
        writer.close();
        LOG.info("Output file closed!");
    }
}
