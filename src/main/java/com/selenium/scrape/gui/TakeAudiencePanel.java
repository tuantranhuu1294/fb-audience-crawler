package com.selenium.scrape.gui;

import com.selenium.scrape.executor.TakeSizeExecutor;
import com.selenium.scrape.output.TextAreaOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;

public class TakeAudiencePanel {

    private static Logger LOG = LogManager.getLogger(TakeAudiencePanel.class);

    private static JTextField textFileChoose;
    private static JTextField textOutFileChoose;
    private static JTextField textGeckoDriverFileChoose;
    private static JTextField textConfigFileChoose;
    private static JTextField textUsername;
    private static JTextField textSleepTime;
    private static JTextField textImplicitlyWait;
    private static JPasswordField fieldPassword;

    public TakeAudiencePanel() {
    }

    public JPanel buildTakeAudSize() {
        // TODO Auto-generated method stub

        JLabel labelUsername = new JLabel("Enter username: ");
        JLabel labelPassword = new JLabel("Enter password: ");
        JLabel labelSleepTime = new JLabel("Sleeping time (seconds): ");
        JLabel labelImplicitlyWait = new JLabel("Implicitly Wait (seconds): ");
        JLabel labelFileChoose = new JLabel("Dictionary file: ");
        JLabel labelOutputFileChoose = new JLabel("Output file: ");
        JLabel labelGeckoDriverFileChoose = new JLabel("Gecko driver file: ");
        JLabel labelConfigFileChoose = new JLabel("Config file: ");

        JTextArea console = new JTextArea(10, 60);
        console.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        console.setBackground(Color.BLACK);
        console.setForeground(Color.GREEN);
        console.setFont(new Font("Verdana", Font.LAYOUT_LEFT_TO_RIGHT, 12));
        console.setEditable(true);
        console.setAutoscrolls(true);
        console.setText("Log Console\n");
        console.setLineWrap(true);
        console.setWrapStyleWord(true);

        // Print output to console
        PrintStream out = new PrintStream(new TextAreaOutputStream(console));
        //PrintStream out = new PrintStream(consoleOutputStream);
        System.setOut(out);
        System.setErr(out);


        JScrollPane scroll = new JScrollPane(console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        textFileChoose = new JTextField(25);
        textOutFileChoose = new JTextField(25);
        textGeckoDriverFileChoose = new JTextField(25);
        textConfigFileChoose = new JTextField(25);
        textUsername = new JTextField(30);
        fieldPassword = new JPasswordField(30);
        textSleepTime = new JTextField(5);
        textImplicitlyWait = new JTextField(5);

        JButton btnCrawlAudSize = new JButton("Run");
        JButton btnBrowse = new JButton("Browse...");
        btnBrowse.addActionListener(e -> {
            // TODO Auto-generated method stub
            buttonActionPerformed(e, textFileChoose, MainUI.MODE_OPEN);
        });

        JButton btnOutFileBrowse = new JButton("Browse...");
        btnOutFileBrowse.addActionListener(e -> {
            // TODO Auto-generated method stub
            buttonActionPerformed(e, textOutFileChoose, MainUI.MODE_SAVE);

        });

        JButton btnGeckoDriverFileBrowse = new JButton("Browse...");
        btnGeckoDriverFileBrowse.addActionListener(e -> {
            // TODO Auto-generated method stub
            buttonActionPerformed(e, textGeckoDriverFileChoose, MainUI.MODE_OPEN);

        });

        JButton btnConfigFileBrowse = new JButton("Browse...");
        btnConfigFileBrowse.addActionListener(e -> {
            // TODO Auto-generated method stub
            buttonActionPerformed(e, textConfigFileChoose, MainUI.MODE_OPEN);

        });

        // create a new panel with GridBagLayout manager
        JPanel newPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

		/* add components to the panel */

        // Username area
        constraints.gridx = 0;
        constraints.gridy = 0;
        newPanel.add(labelUsername, constraints);

        constraints.gridx = 1;
        newPanel.add(textUsername, constraints);

        // Password area
        constraints.gridx = 0;
        constraints.gridy = 1;
        newPanel.add(labelPassword, constraints);

        constraints.gridx = 1;
        newPanel.add(fieldPassword, constraints);

        // Input file chooser area
        constraints.gridx = 0;
        constraints.gridy = 2;
        newPanel.add(labelFileChoose, constraints);

        constraints.gridx = 1;
        newPanel.add(textFileChoose, constraints);

        constraints.gridx = 2;
        newPanel.add(btnBrowse, constraints);

        // Output file chooser area
        constraints.gridx = 0;
        constraints.gridy = 3;
        newPanel.add(labelOutputFileChoose, constraints);

        constraints.gridx = 1;
        newPanel.add(textOutFileChoose, constraints);

        constraints.gridx = 2;
        newPanel.add(btnOutFileBrowse, constraints);

        // Gecko driver file chooser area
        constraints.gridx = 0;
        constraints.gridy = 4;
        newPanel.add(labelGeckoDriverFileChoose, constraints);

        constraints.gridx = 1;
        newPanel.add(textGeckoDriverFileChoose, constraints);

        constraints.gridx = 2;
        newPanel.add(btnGeckoDriverFileBrowse, constraints);

        // Config file chooser area
        constraints.gridx = 0;
        constraints.gridy = 5;
        newPanel.add(labelConfigFileChoose, constraints);

        constraints.gridx = 1;
        newPanel.add(textConfigFileChoose, constraints);

        constraints.gridx = 2;
        newPanel.add(btnConfigFileBrowse, constraints);

        // Sleep time area
        constraints.gridx = 0;
        constraints.gridy = 6;
        newPanel.add(labelSleepTime, constraints);

        constraints.gridx = 1;
        newPanel.add(textSleepTime, constraints);

        // Sleep time area
        constraints.gridx = 0;
        constraints.gridy = 7;
        newPanel.add(labelImplicitlyWait, constraints);

        constraints.gridx = 1;
        newPanel.add(textImplicitlyWait, constraints);

        // Log console area
        constraints.gridx = 0;
        constraints.gridy = 8;
        constraints.gridwidth = 4;
        newPanel.add(scroll, constraints);

        // Run button area
        constraints.gridx = 0;
        constraints.gridy = 9;
        constraints.anchor = GridBagConstraints.CENTER;
        btnCrawlAudSize.setToolTipText("Take Audience");
        btnCrawlAudSize.addActionListener(takeAudienceSize());
        newPanel.add(btnCrawlAudSize, constraints);

        // set border for the panel
        newPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Audience Panel"));

        return newPanel;
    }

    public void buttonActionPerformed(ActionEvent e, JTextField textFileChoose, int mode) {
        // TODO Auto-generated method stub

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:/"));
        if (mode == MainUI.MODE_OPEN) {
            if (fileChooser.showOpenDialog(new MainUI()) == JFileChooser.APPROVE_OPTION) {
                textFileChoose.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        } else if (mode == MainUI.MODE_SAVE) {
            if (fileChooser.showSaveDialog(new MainUI()) == JFileChooser.APPROVE_OPTION) {
                textFileChoose.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }

    }

    private ActionListener takeAudienceSize() {
        return e -> {
            // Set log console
            LOG.info("System initializing ...");

            String userName = textUsername.getText();
            String password = String.valueOf(fieldPassword.getPassword());
            String dictFilePath = textFileChoose.getText();
            String outFilePath = textOutFileChoose.getText();
            String geckoDriverFilePath = textGeckoDriverFileChoose.getText();
            String configFilePath = textConfigFileChoose.getText();
            String sleepTime = textSleepTime.getText();
            String implicitlyWait = textImplicitlyWait.getText();

            new Thread(() -> {
                TakeSizeExecutor executor = new TakeSizeExecutor(configFilePath);
                if (!StringUtils.isEmpty(sleepTime) && NumberUtils.isNumber(sleepTime))
                    executor.setSleepTime(Integer.parseInt(sleepTime) * 1000);

                if (!StringUtils.isEmpty(implicitlyWait) && NumberUtils.isNumber(implicitlyWait))
                    executor.setImplicitlyWait(Integer.parseInt(implicitlyWait));

                executor.takeSize(userName, password, dictFilePath, outFilePath, geckoDriverFilePath);
            }).start();

        };
    }


}
