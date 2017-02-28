package com.selenium.scrape.task;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GetAudience {

    private int defaultPotentialReach;
    private String defaultWorkJob;

    private static Logger LOG = Logger.getLogger(GetAudience.class.getName());
    private static BufferedWriter writer = null;

    public GetAudience() {
        defaultPotentialReach = 0;
        try {
            writer = new BufferedWriter(new FileWriter(new File("resource/fb-audience-size.txt")));
            LOG.info("Ouput file created!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GetAudience(String outputFilePath) {
        defaultPotentialReach = 0;
        try {
            writer = new BufferedWriter(new FileWriter(new File(outputFilePath)));
            LOG.info("Ouput file created!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultPotentialReach(int defaultPotentialReach) {
        this.defaultPotentialReach = defaultPotentialReach;
    }

    public void setDefaultWorkJob(String defaultWorkJob) {
        this.defaultWorkJob = defaultWorkJob;
    }

    public void selectPotentialReach(WebDriver driver, String word) throws InterruptedException, IOException {

        DetailedTargingActions action = new DetailedTargingActions(driver);

        // Enter word
        action.enterText(word);
        Thread.sleep(1500);

        // List row has suggested and select a value
        List<String> listCheck = new ArrayList<>();
        while (true) {
            String rowSelected = action.selectElement(listCheck);

            if (rowSelected != null) {
                listCheck.add(rowSelected);

                Thread.sleep(1000);
                int potentialReach = action.getPotentialReach();
                if (defaultPotentialReach > 0)
                    potentialReach = potentialReach - defaultPotentialReach;

                writer.write(rowSelected + "\t" + potentialReach);
                writer.newLine();
                writer.flush();

                if (defaultWorkJob != null && !StringUtils.isEmpty(defaultWorkJob)) {
                    action.removeElementSelectedNotEqual(defaultWorkJob);
                } else
                    action.removeElementSelectedNotEqual();
            } else {
                action.clearText();
                break;
            }
        }

    }

    public void close() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getLessWordSort(String str1, String str2) {
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();

        if (str1.equalsIgnoreCase(str2))
            return str1;

        char[] charArr1 = str1.toCharArray();
        char[] charArr2 = str2.toCharArray();

        int i = 0;
        while (true) {
            if (charArr1.length < i + 1)
                return str1;
            if (charArr2.length < i + 1)
                return str2;

            if (charArr1[i] < charArr2[i])
                return str1;
            else if (charArr2[i] < charArr1[i])
                return str2;

            i++;
        }
    }
}
