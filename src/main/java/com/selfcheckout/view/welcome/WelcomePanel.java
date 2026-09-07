package com.selfcheckout.view.welcome;
import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
public class WelcomePanel extends JPanel {
    private JPanel logoPanel;
    private JPanel startPanel;
    public WelcomePanel(){
        setLayout(new GridBagLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(1300, 650));
        putClientProperty(FlatClientProperties.STYLE, "arc:16");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 25, 25, 0);
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);


        //  Panel 1
        logoPanel = new JPanel(new GridBagLayout());
        logoPanel.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        logoPanel.setPreferredSize(new Dimension(640, 620));
        logoPanel.setBackground(Color.decode("#FFFFFF"));

        JPanel cartPanel = new JPanel();
        cartPanel.setBackground(Color.decode("#E3EDD3"));
        cartPanel.putClientProperty(FlatClientProperties.STYLE, "arc:360");
        cartPanel.setPreferredSize(new Dimension(300, 300));
        logoPanel.add(cartPanel);









        // Panel 2
        startPanel = new JPanel(new GridBagLayout());
        startPanel.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        startPanel.setPreferredSize(new Dimension(640, 620));
        startPanel.setBackground(Color.decode("#FFFFFF"));

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setBackground(Color.decode("#FFFFFF"));
        textPanel.setPreferredSize(new Dimension(560, 150));

        JLabel text1 = new JLabel("Bem-vindo(a)!");
        text1.setForeground(Color.decode("#1F2A1A"));
        text1.setFont(new Font("Arial", Font.BOLD, 45));

        JLabel text2 = new JLabel(
                "<html>Escaneie seus produtos e finalize<br>" +
                        "sua compra com facilidade</html>"
        );
        text2.setForeground(Color.decode("#5C6B57"));
        text2.setFont(new Font("Arial", Font.BOLD, 20));

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

        JButton startButton = new JButton("Iniciar Compra");
        startButton.setBackground(Color.decode("#2D4A0F"));
        startButton.setForeground(Color.decode("#FFFFFF"));
        startButton.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setPreferredSize(new Dimension(200, 70));

        gbc.gridx = 0;
        gbc.gridy = 1;
        startPanel.add(startButton, gbc);

        JButton cardButton = new JButton("Usar cartão fidelidade");
        cardButton.setForeground(Color.decode("#2E2E2E"));
        cardButton.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        cardButton.setFont(new Font("Arial", Font.BOLD, 20));
        cardButton.setPreferredSize(new Dimension(200, 70));


        gbc.gridx = 0;
        gbc.gridy = 2;
        startPanel.add(cardButton, gbc);





        add(logoPanel);
        add(separator);
        add(startPanel);
    }
}