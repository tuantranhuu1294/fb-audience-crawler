package com.selenium.scrape.task;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.selenium.scrape.util.ConfigUtils.getString;

public class DetailedTargetingActions {

    private static Logger LOG = LogManager.getLogger(DetailedTargetingActions.class);

    private WebDriver driver;
    private Configuration config;

    public DetailedTargetingActions() {
    }

    public DetailedTargetingActions(WebDriver driver, Configuration config) {
        this.driver = driver;
        this.config = config;
    }

    public void enterText(String word) {
        try {
            LOG.info("Enter word: " + word);
            WebElement enterText = driver.findElement(By.xpath(getString(config, "fb.xpath.target.search_area")));
            enterText.sendKeys(word);
        } catch (Exception e) {
            LOG.error("Search area not found.", e);
        }
    }

    public void clearText() {
        try {
            WebElement enterText = driver.findElement(By.xpath(getString(config, "fb.xpath.target.search_area")));
            enterText.clear();
        } catch (Exception e) {
            LOG.error("Cannot clear enter text.", e);
        }
    }

    public void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void click(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }


    public String selectElement(List<String> filterList) {
        try {
            List<WebElement> listSuggest = driver.findElements(By.xpath(getString(config, "fb.xpath.target.suggest_element")));

            if (listSuggest != null) {
                String rowSelected;

                // Scroll to first suggest element
                if (filterList.size() == 0 && listSuggest.size() > config.getInt("fb.value.target" +
                        ".display_element_suggestion_box")) {
                    scrollTo(listSuggest.get(0));
                    Thread.sleep(600);
                    scrollTo(driver.findElement(By.xpath(getString(config, "fb.xpath.target" +
                            ".targeting_detailed_area"))));
                    Thread.sleep(400);
                }

                if (listSuggest.size() > config.getInt("fb.value.target.display_element_suggestion_box") &&
                        filterList.size() == config.getInt("fb.value.target.display_element_suggestion_box")) {

                    int tempCount = 0;
                    for (WebElement webElement : listSuggest) {
                        tempCount += 1;
                        String row = webElement.getText();

                        // Scroll to hidden element
                        if (tempCount == config.getInt("fb.value.target.display_element_suggestion_box")) {
                            scrollTo(webElement);
                            Thread.sleep(1000);
                        }

                        if (!filterList.contains(row)) {
                            WebElement targetArea = driver.findElement(By.xpath(getString(config, "fb.xpath.target" +
                                    ".targeting_detailed_area")));
                            scrollTo(targetArea);
                            Thread.sleep(1000);

                            webElement.click();
                            rowSelected = row;

                            System.out.println("Selected: " + row);
                            LOG.info("Selected: " + row);

                            return rowSelected;
                        }
                    }

                } else {
                    for (WebElement webElement : listSuggest) {
                        String row = webElement.getText();

                        if (!filterList.contains(row)) {
                            webElement.click();
                            rowSelected = row;

                            System.out.println("Selected: " + row);
                            LOG.info("Selected: " + row);

                            return rowSelected;
                        }
                    }
                }

            }
        } catch (Exception e) {
            LOG.error("Cannot select any element.", e);
        }

        return null;
    }

    public int getPotentialReach() {
        try {
            WebElement element = driver.findElement(By.xpath(getString(config, "fb.xpath.target.potential_reach_element")));

            String value = element.getText();
            value = value.replaceAll("[^0-9.]", "").replace(",", "").replaceAll("\\s+", "");

            System.out.println("Potential Reach: " + value);
            LOG.info("Potential Reach: " + value);

            return Integer.parseInt(value);
        } catch (Exception e) {
            LOG.error("Cannot get Potential reach. Caused by " + e.getMessage());
        }

        return 0;
    }


    public void removeElementSelectedNotEqual() {
        // Remove value has selected
        try {
            List<WebElement> selectedElement = driver.findElements(By.xpath(getString(config, "fb.xpath.target" +
                    ".remove_button_area")));
            for (WebElement element : selectedElement) {
                WebElement tempElement = element.findElement(By.xpath("./li/button"));
                click(tempElement);
                break;
            }

            LOG.info("Element has removed");
        } catch (Exception e) {
            LOG.warn("Cannot remove element selected.", e);
        }
    }

    public void removeElementSelectedNotEqual(String element) {
        // Remove value has selected
        try {
            LOG.info("Delete element not equal " + element);
            List<WebElement> selectedElements = driver.findElements(By.xpath(getString(config, "fb.xpath.target" +
                    ".remove_button_area")));
            for (WebElement selectedElement : selectedElements) {
                String value = selectedElement.findElement(By.xpath("./li/div")).getText();
                if (!value.equalsIgnoreCase(element))
                    click(selectedElement.findElement(By.xpath("./li/button")));
            }
        } catch (Exception e) {
            LOG.error("Cannot remove element selected.", e);
        }
    }

    public String getElementSelectedName() {
        try {
            WebElement element = driver.findElement(By.xpath(getString(config, "fb.xpath.target.element_selected")));
            String value = element.getText();
            if (!StringUtils.isEmpty(value))
                return value;
        } catch (Exception e) {
            LOG.error("Cannot get content of element has selected. Caused by " + e.getMessage());
        }

        return null;
    }


    public void removeDefaultSuggested() {
        try {
            LOG.info("Delete elements suggested by default");
            List<WebElement> selectedElement = driver.findElements(By.xpath(getString(config, "fb.xpath.target" +
                    ".default_suggested_element")));
            LOG.info("Number of elements suggested " + selectedElement.size());
            for (WebElement element : selectedElement) {
                WebElement tempElement = element.findElement(By.xpath("./li[1]/button"));
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", tempElement);
            }

        } catch (Exception e) {
            LOG.error("Cannot remove default suggested elements.", e);
        }
    }


    public void selectWorkJob() {
        try {
            LOG.info("Select to work job");
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Click to browse button
            driver.findElement(By.xpath(getString(config, "fb.xpath.target.browse_button"))).click();
            Thread.sleep(1000);

            // Click to Demographics drop down
            WebElement demographicElement = driver.findElement(By.xpath(getString(config, "fb.xpath.target" +
                    ".demographics_drop_down")));
            LOG.debug("Demographics element has found");
            js.executeScript("arguments[0].click();", demographicElement);
            Thread.sleep(1000);

            // Click to Work drop down
            WebElement workElement = driver.findElement(By.xpath(getString(config, "fb.xpath.target.work_drop_down")));
            WebElement homeElement = driver.findElement(By.xpath(getString(config, "fb.xpath.target.home_drop_down")));
            LOG.debug("Work element has found");
            js.executeScript("arguments[0].scrollIntoView(true);", homeElement);
            Thread.sleep(1000);

            js.executeScript("arguments[0].click();", workElement);
            Thread.sleep(1000);

            // Click to Job titles element
            WebElement jobTitleElement = driver.findElement(By.xpath(getString(config, "fb.xpath.target.job_title_element")));
            LOG.debug("Job titles element has found");
            js.executeScript("arguments[0].click();", jobTitleElement);
            Thread.sleep(1000);

            // Scroll to working area
            WebElement detailedElement = driver.findElement(By.xpath(getString(config, "fb.xpath.target" +
                    ".targeting_detailed_area")));
            js.executeScript("arguments[0].scrollIntoView(true);", detailedElement);
            Thread.sleep(1000);

        } catch (Exception e) {
            LOG.error("Cannot choose work job titles. Caused by " + e.getMessage());
        }
    }


}
