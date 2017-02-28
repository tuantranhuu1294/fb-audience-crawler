package com.selenium.scrape.gui;

import javax.swing.*;

/**
 * Created by huutuan on 04/01/2017.
 */
public class Application {

    public static void main(String[] args) {
        // set look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MainUI("Facebook Audience Crawler V2.1").setVisible(true));
    }
}
