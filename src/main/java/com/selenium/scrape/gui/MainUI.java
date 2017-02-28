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

        JComponent component = buildPanel2();
        // add the panel to this frame
        add(component);
        pack();
        setLocationRelativeTo(null);
    }

    protected static JComponent buildPanel() {
        // TODO Auto-generated method stub

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);

        // tabbedPane.add("Crawl Audience", CrawlAudienceName.buildCrawlAud());
        // tabbedPane.add("Take Audience Size",
        // TakeAudienceSize.buildTakeAudSize());
        tabbedPane.add("Crawl Audience", new TakeAudience().buildTakeAudSize());

        return tabbedPane;

    }

    private static JComponent buildPanel2() {
        return new TakeAudience().buildTakeAudSize();
    }

}
