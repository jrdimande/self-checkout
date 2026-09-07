package com.selfcheckout.view.welcome;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class WelcomePanel extends JPanel {
    private JPanel logoPanel;
    private JPanel startPanel;

    public WelcomePanel(CardLayout cardLayout, JPanel contentPanel){
        setLayout(new GridBagLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(1300, 700));
        putClientProperty(FlatClientProperties.STYLE, "arc:16");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 25, 10, 0);
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);

        logoPanel = new JPanel(new GridBagLayout());
        logoPanel.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        logoPanel.setPreferredSize(new Dimension(640, 700));
        logoPanel.setBackground(Color.decode("#FFFFFF"));

        JPanel cartPanel = new JPanel(new GridBagLayout());
        cartPanel.setBackground(Color.decode("#EFF6FF"));
        cartPanel.putClientProperty(FlatClientProperties.STYLE, "arc:360");
        cartPanel.setPreferredSize(new Dimension(300, 300));
        logoPanel.add(cartPanel);

        FlatSVGIcon icon = new FlatSVGIcon("icons/cart.svg", 320, 320);
        JLabel imageLabel = new JLabel(icon);
        cartPanel.add(imageLabel);

        startPanel = new JPanel(new GridBagLayout());
        startPanel.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        startPanel.setPreferredSize(new Dimension(640, 700));
        startPanel.setBackground(Color.decode("#FFFFFF"));

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setBackground(Color.decode("#FFFFFF"));
        textPanel.setPreferredSize(new Dimension(580, 300));

        JLabel text1 = new JLabel("Bem-vindo(a) !");
        text1.setForeground(Color.decode("#0F172A"));
        text1.setFont(new Font("Arial", Font.BOLD, 50));

        JLabel text2 = new JLabel(
                "<html>Escaneie seus produtos e finalize<br>" +
                        "sua compra com facilidade</html>"
        );
        text2.setForeground(Color.decode("#475569"));
        text2.setFont(new Font("Arial", Font.BOLD, 21));

        GridBagConstraints textGbc = new GridBagConstraints();
        textGbc.insets = new Insets(10,0,0,220);
        textGbc.anchor = GridBagConstraints.WEST;

        textGbc.gridx = 0;
        textGbc.gridy = 0;
        textPanel.add(text1, textGbc);

        textGbc.gridx = 0;
        textGbc.gridy = 1;
        textPanel.add(text2, textGbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        startPanel.add(textPanel, gbc);

        FlatSVGIcon arrowIcon = new FlatSVGIcon("icons/arrow-right.svg", 30, 30);
        arrowIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE));

        JButton startButton = new JButton("iniciar compra");
        startButton.setIcon(arrowIcon);
        startButton.setHorizontalTextPosition(SwingConstants.LEFT);
        startButton.setHorizontalAlignment(SwingConstants.CENTER);
        startButton.setBackground(Color.decode("#1E3A8A"));
        startButton.setForeground(Color.decode("#FFFFFF"));
        startButton.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        startButton.setFont(new Font("Arial", Font.BOLD, 25));
        startButton.setPreferredSize(new Dimension(200, 80));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cardLayout.show(contentPanel, "CHECKOUT");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        startPanel.add(startButton, gbc);

        JButton cardButton = new JButton("Usar cartão fidelidade");
        cardButton.setForeground(Color.decode("#2E2E2E"));
        cardButton.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        cardButton.setFont(new Font("Arial", Font.BOLD, 25));
        cardButton.setPreferredSize(new Dimension(200, 80));

        gbc.gridx = 0;
        gbc.gridy = 2;
        startPanel.add(cardButton, gbc);

        add(logoPanel);
        add(separator);
        add(startPanel);
    }
}