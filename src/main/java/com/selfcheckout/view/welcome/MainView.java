package com.selfcheckout.view.welcome;

import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

public class MainView {
    private JFrame root;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;


    public MainView(){
        root = new JFrame();
        root.setBackground(Color.decode("#F3F1E9"));
        root.setTitle("self-checkout");
        root.setLayout(new BorderLayout());
        root.setSize(1400, 900);
        root.setLocationRelativeTo(null);

        initTopPanel();
        initBottomPanel();
        initContentPanel();

        root.setVisible(true);
    }

    public void initTopPanel(){
        topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(1500, 90));
        topPanel.setBackground(Color.decode("#FFFFFF"));
        topPanel.setLayout(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.putClientProperty(FlatClientProperties.STYLE, "arc:15");
        logoPanel.setLayout(new GridBagLayout());
        logoPanel.setBackground(Color.decode("#2D4A0F"));
        logoPanel.setPreferredSize(new Dimension(55,50));

        JLabel logoLabel = new JLabel("SC");
        logoLabel.setForeground(Color.decode("#FFFFFF"));
        logoLabel.setFont(new Font("Arial", Font.BOLD, 22));
        logoPanel.add(logoLabel);


        // Text Panel & Logo
        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.decode("#FFFFFF"));
        textPanel.setLayout(new GridBagLayout());
        textPanel.setPreferredSize(new Dimension(300, 30));

        JLabel textLabel = new JLabel("self-checkout");
        textLabel.setForeground(Color.decode("#1F2A1A"));
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));

        textPanel.add(logoPanel, gbc);
        textPanel.add(textLabel);

        topPanel.add(textPanel, BorderLayout.WEST);

        // Machine Number
        JPanel machinePanel = new JPanel(new GridBagLayout());
        machinePanel.setBackground(Color.decode("#FFFFFF"));
        machinePanel.setPreferredSize(new Dimension(100, 20));

        JLabel machineNumberLabel = new JLabel("Caixa 01");
        machineNumberLabel.setForeground(Color.decode("#8A9086"));
        machineNumberLabel.setFont(new Font("Arial", Font.BOLD, 16));
        machinePanel.add(machineNumberLabel);

        topPanel.add(machinePanel, BorderLayout.EAST);

        root.add(topPanel, BorderLayout.NORTH);

    }

    public void initBottomPanel(){
        bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setPreferredSize(new Dimension(1500, 90));
        bottomPanel.setBackground(Color.decode("#F7F5EA"));

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel textLabel = new JLabel("precisa de ajuda? chame um atendente");
        textLabel.setForeground(Color.decode("#6B6F64"));
        textLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        bottomPanel.add(textLabel, gbc);

        root.add(bottomPanel, BorderLayout.SOUTH);


    }

    public void initContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(Color.decode("#F3F1E9"));
        WelcomePanel wp = new WelcomePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(wp, gbc);
        root.add(contentPanel, BorderLayout.CENTER);
    }


    public static void main(String[] args) {

        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            new MainView();
        });
    }

}
