package com.selenium.scrape.gui;

import javax.swing.*;

public class MainUI extends JFrame {

    public static final int MODE_OPEN = 1;
    public static final int MODE_SAVE = 2;

    public MainUI() {
        // TODO Auto-generated constructor stub
    }

    public MainUI(String appName) {
        super(appName);
        init();
    }

    private void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        JComponent component = buildPanel();
        // add the panel to this frame
        add(component);
        pack();
        setLocationRelativeTo(null);
    }

    private static JComponent buildPanel() {
        return new TakeAudiencePanel().buildTakeAudSize();
    }

}
