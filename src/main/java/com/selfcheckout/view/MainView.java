package com.selfcheckout.view;

import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.selfcheckout.view.payment.PaymentPanel;
import com.selfcheckout.view.welcome.CheckoutPanel;
import com.selfcheckout.view.welcome.WelcomePanel;

public class MainView {
    private JFrame root;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;

    public MainView(){
        root = new JFrame();
        root.setBackground(Color.decode("#F1F5F9"));
        root.setTitle("self-checkout");
        root.setLayout(new BorderLayout(0, 10));
        root.setSize(1400, 900);
        root.setLocationRelativeTo(null);

        // Ensures the root content pane respects the soft gray background theme
        root.getContentPane().setBackground(Color.decode("#F1F5F9"));

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

        // Application Logo Icon Container
        JPanel logoPanel = new JPanel();
        logoPanel.putClientProperty(FlatClientProperties.STYLE, "arc:15");
        logoPanel.setLayout(new GridBagLayout());
        logoPanel.setBackground(Color.decode("#1E3A8A"));
        logoPanel.setPreferredSize(new Dimension(55,50));

        JLabel logoLabel = new JLabel("SC");
        logoLabel.setForeground(Color.decode("#FFFFFF"));
        logoLabel.setFont(new Font("Arial", Font.BOLD, 22));
        logoPanel.add(logoLabel);

        // Brand Title Panel
        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.decode("#FFFFFF"));
        textPanel.setLayout(new GridBagLayout());
        textPanel.setPreferredSize(new Dimension(300, 30));

        JLabel textLabel = new JLabel("self-checkout");
        textLabel.setForeground(Color.decode("#0F172A"));
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));

        textPanel.add(logoPanel, gbc);
        textPanel.add(textLabel);

        topPanel.add(textPanel, BorderLayout.WEST);

        // Terminal / Register Identification Number Indicator
        JPanel machinePanel = new JPanel(new GridBagLayout());
        machinePanel.setBackground(Color.decode("#FFFFFF"));
        machinePanel.setPreferredSize(new Dimension(100, 20));

        JLabel machineNumberLabel = new JLabel("Caixa 01");
        machineNumberLabel.setForeground(Color.decode("#64748B"));
        machineNumberLabel.setFont(new Font("Arial", Font.BOLD, 16));
        machinePanel.add(machineNumberLabel);

        topPanel.add(machinePanel, BorderLayout.EAST);

        root.add(topPanel, BorderLayout.NORTH);
    }

    public void initBottomPanel(){
        bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setPreferredSize(new Dimension(1500, 80));
        bottomPanel.setBackground(Color.decode("#FFFFFF"));

        GridBagConstraints gbc = new GridBagConstraints();

        // Customer Support Prompt Banner
        JLabel textLabel = new JLabel("precisa de ajuda? chame um atendente");
        textLabel.setForeground(Color.decode("#475569"));
        textLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        bottomPanel.add(textLabel, gbc);

        root.add(bottomPanel, BorderLayout.SOUTH);
    }

    public void initContentPanel() {
        // CardLayout manager to switch seamlessly between application views/screens
        CardLayout cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.decode("#FFFFFF"));

        // Registering workflow views
        contentPanel.add(new WelcomePanel(cardLayout, contentPanel), "WELCOME");
        contentPanel.add(new CheckoutPanel(cardLayout, contentPanel), "CHECKOUT");
        contentPanel.add(new PaymentPanel(cardLayout, contentPanel), "PAYMENT");

        // Set default initial view
        cardLayout.show(contentPanel, "WELCOME");

        root.add(contentPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Initialize FlatLaf modern UI look and feel
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            new MainView();
        });
    }
}