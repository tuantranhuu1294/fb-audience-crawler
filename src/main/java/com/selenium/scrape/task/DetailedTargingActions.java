package com.selenium.scrape.task;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DetailedTargingActions {

    private static Logger LOG = LogManager.getLogger(DetailedTargingActions.class);

    private WebDriver driver;

    public DetailedTargingActions() {
    }

    public DetailedTargingActions(WebDriver driver) {
        this.driver = driver;
    }

    private static final String SEARCH_AREA_XPATH = "//div[contains(@class, '_2ipo')]/input[contains(@placeholder," +
            "'Search job titles')]";

    public void enterText(String word) {
        try {
            LOG.info("Enter word: " + word);
            WebElement enterText = driver.findElement(By.xpath(SEARCH_AREA_XPATH));
            enterText.sendKeys(word);
        } catch (Exception e) {
            LOG.error("search area not found. Caused by " + e.getMessage());
        }
    }

    public void clearText() {
        try {
            WebElement enterText = driver.findElement(By.xpath(SEARCH_AREA_XPATH));
            enterText.clear();
        } catch (Exception e) {
            LOG.error("Cannot clear enter text. Caused by " + e.getMessage());
        }
    }

    public void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void click(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public static final String SUGGEST_ELEMENT_XPATH = "//div[contains(@class, 'uiContextualLayerPositioner uiLayer')]" +
            "/div/div/div/div/div/div/div/ul/li[contains(@class, 'clearfix _2-ss')]";
    public static final String TARGETING_DETAILED_AREA_XPATH = "//h4/div[contains(text(), 'Detailed targeting')]";
    public static final int DISPLAY_ELEMENTS_SUGGESTION_BOX = 6;

    public String selectElement(List<String> filterList) {
        try {
            List<WebElement> listSuggest = driver.findElements(By.xpath(SUGGEST_ELEMENT_XPATH));

            if (listSuggest != null) {
                String rowSelected;

                // Scroll to first suggest element
                if (filterList.size() == 0 && listSuggest.size() > DISPLAY_ELEMENTS_SUGGESTION_BOX) {
                    scrollTo(listSuggest.get(0));
                    Thread.sleep(600);
                    scrollTo(driver.findElement(By.xpath(TARGETING_DETAILED_AREA_XPATH)));
                    Thread.sleep(400);
                }

                if (listSuggest.size() > DISPLAY_ELEMENTS_SUGGESTION_BOX &&
                        filterList.size() == DISPLAY_ELEMENTS_SUGGESTION_BOX) {

                    int tempCount = 0;
                    for (WebElement webElement : listSuggest) {
                        tempCount += 1;
                        String row = webElement.getText();

                        // Scroll to hidden element
                        if (tempCount == DISPLAY_ELEMENTS_SUGGESTION_BOX) {
                            scrollTo(webElement);
                            Thread.sleep(1000);
                        }

                        if (!filterList.contains(row)) {
                            WebElement targetArea = driver.findElement(By.xpath(TARGETING_DETAILED_AREA_XPATH));
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
            LOG.error("Cannot select any element. Caused by " + e.getMessage());
        }

        return null;
    }


    public void selectExactlyElement(String strElement) {
        try {
            List<WebElement> listSuggest = driver.findElements(By.xpath(SUGGEST_ELEMENT_XPATH));

            if (listSuggest != null) {
                // Scroll to first suggest element
                if (listSuggest.size() > DISPLAY_ELEMENTS_SUGGESTION_BOX) {
                    scrollTo(listSuggest.get(0));
                    Thread.sleep(600);
                    scrollTo(driver.findElement(By.xpath(TARGETING_DETAILED_AREA_XPATH)));
                    Thread.sleep(400);
                }

                for (WebElement webElement : listSuggest) {
                    String row = webElement.getText();
                    LOG.info("Element suggested: " + row);
                    if (strElement.equalsIgnoreCase(row)) {
                        webElement.click();

                        System.out.println("Selected: " + row);
                        LOG.info("Selected: " + row);
                        break;
                    }
                }

            }
        } catch (Exception e) {
            LOG.error("Cannot select any element. Caused by " + e.getMessage());
        }
    }


    public static final String POTENTIAL_REACH_ELEMENT_XPATH = "//div/div[contains(@class,'_4qxz')]/div[contains(@class," +
            "'_1ga8')]";

    public int getPotentialReach() {
        try {
            WebElement element = driver.findElement(By.xpath(POTENTIAL_REACH_ELEMENT_XPATH));

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


    private static final String REMOVE_BUTTON_AREA_XPATH = "//div/ul[contains(@class, '_43n_')]/li[2]/ul/div";

    public void removeElementSelectedNotEqual() {
        // Remove value has selected
        try {
            List<WebElement> selectedElement = driver.findElements(By.xpath(REMOVE_BUTTON_AREA_XPATH));
            for (WebElement element : selectedElement) {
                WebElement tempElement = element.findElement(By.xpath("./li/button"));
                click(tempElement);
                break;
            }

            LOG.info("Element has removed");
        } catch (Exception e) {
            LOG.error("cannot remove element selected. Caused by " + e.getMessage());
        }
    }

    public void removeElementSelectedNotEqual(String element) {
        // Remove value has selected
        try {
            LOG.info("Delete element not equal " + element);
            List<WebElement> selectedElements = driver.findElements(By.xpath(REMOVE_BUTTON_AREA_XPATH));
            for (WebElement selectedElement : selectedElements) {
                String value = selectedElement.findElement(By.xpath("./li/div")).getText();
                if(!value.equalsIgnoreCase(element))
                    click(selectedElement.findElement(By.xpath("./li/button")));
            }
        } catch (Exception e) {
            LOG.error("Cannot remove element selected. Caused by " + e.getMessage());
        }
    }

    private static final String ELEMENT_SELECTED = "//div/ul[contains(@class, '_43n_')]/li[2]/ul/div[1]/li/div";

    public String getElementSelectedName() {
        try {
            WebElement element = driver.findElement(By.xpath(ELEMENT_SELECTED));
            String value = element.getText();
            if (!StringUtils.isEmpty(value))
                return value;
        } catch (Exception e) {
            LOG.error("Cannot get content of element has selected. Caused by " + e.getMessage());
        }

        return null;
    }

    private static final String ELEMENT_SELECTED_AREA = "//div/ul[contains(@class, '_43n_')]/li[2]/ul/div/li/div";

    public int getIndexOfElementInSuggestList(String element) {
        try {
            List<WebElement> elements = driver.findElements(By.xpath(ELEMENT_SELECTED_AREA));
            for (int i = 0; i < elements.size(); i++) {
                String value = elements.get(i).getText();
                if (value.equalsIgnoreCase(element)) {
                    LOG.info("Index of element " + element + " is " + i);
                    return i;
                }
            }

            return -1;
        } catch (Exception e) {
            LOG.error("Element " + element + " not found. Caused by " + e.getMessage());
        }

        return 0;
    }


    private static final String DEFAULT_SUGGESTED_ELEMENT_XPATH = "//div[contains(@class, '_5bcc uiContextualLayerParent')]/" +
            "div/div/div/div/ul[contains(@class, '_43n_')]";

    public void removeDefaultSuggested() {
        try {
            LOG.info("Delete elements suggested by default");
            List<WebElement> selectedElement = driver.findElements(By.xpath(DEFAULT_SUGGESTED_ELEMENT_XPATH));
            LOG.info("Elements suggestion " + selectedElement.size());
            for (WebElement element : selectedElement) {
                WebElement tempElement = element.findElement(By.xpath("./li[1]/button"));
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", tempElement);
            }

        } catch (Exception e) {
            LOG.error("Cannot remove default suggested elements. Caused by " + e.getMessage());
        }
    }


    private static final String BROWSE_BUTTON_XPATH = "//div/button/em[contains(text(), 'Browse')]";
    private static final String DEMOGRAPHICS_DROP_DOWN_XPATH = "//div/ul/li[1]/div/div[contains(text(), 'Demographics')]";
    private static final String WORK_DROP_DOWN_XPATH = "//li/div/div[contains(text(), 'Work')]";
    private static final String HOME_DROP_DOWN_XPATH = "//li/div/div[contains(text(), 'Home')]";
    private static final String JOB_TITLES_XPATH = "//li/div[contains(text(), 'Job Titles')]";

    public void selectWorkJob() {
        try {
            LOG.info("Select to work job");
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Click to browse button
            driver.findElement(By.xpath(BROWSE_BUTTON_XPATH)).click();
            Thread.sleep(1000);

            // Click to Demographics drop down
            WebElement demographicElement = driver.findElement(By.xpath(DEMOGRAPHICS_DROP_DOWN_XPATH));
            LOG.info("Demographics element found");
            js.executeScript("arguments[0].click();", demographicElement);
            Thread.sleep(1000);

            // Click to Work drop down
            WebElement workElement = driver.findElement(By.xpath(WORK_DROP_DOWN_XPATH));
            WebElement homeElement = driver.findElement(By.xpath(HOME_DROP_DOWN_XPATH));
            LOG.info("Work element found");
            js.executeScript("arguments[0].scrollIntoView(true);", homeElement);
            Thread.sleep(1000);

            js.executeScript("arguments[0].click();", workElement);
            Thread.sleep(1000);

            // Click to Job titles element
            WebElement jobTitleElement = driver.findElement(By.xpath(JOB_TITLES_XPATH));
            LOG.info("Job titles element found");
            js.executeScript("arguments[0].click();", jobTitleElement);
            Thread.sleep(1000);

            // Scroll to working area
            WebElement detailedElement = driver.findElement(By.xpath(TARGETING_DETAILED_AREA_XPATH));
            js.executeScript("arguments[0].scrollIntoView(true);", detailedElement);
            Thread.sleep(1000);

        } catch (Exception e) {
            LOG.error("Cannot choose work job titles. Caused by " + e.getMessage());
        }
    }


}
