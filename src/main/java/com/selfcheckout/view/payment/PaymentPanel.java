package com.selfcheckout.view.payment;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PaymentPanel extends JPanel {

    private final Map<String, JPanel> optionCards = new HashMap<>();
    private final Map<String, JLabel> checkmarkLabels = new HashMap<>();

    private JPanel dynamicActionContainer;
    private JTextField phoneInputField;
    private JLabel totalValueLabel;
    private JButton processPaymentButton;

    private String selectedPaymentMethod = "MPESA";

    public PaymentPanel(CardLayout cardLayout, JPanel contentPanel) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(1300, 700));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Left Panel: Payment method selection
        JPanel methodsPanel = createRoundedPanel(20, Color.decode("#FFFFFF"));
        methodsPanel.setPreferredSize(new Dimension(520, 700));
        methodsPanel.setLayout(new BorderLayout());
        methodsPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Forma de Pagamento");
        titleLabel.setForeground(Color.decode("#1C1B16"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel subtitleLabel = new JLabel("Escolha como prefere efetuar o pagamento:");
        subtitleLabel.setForeground(Color.decode("#6B7280"));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        methodsPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel cardsContainer = new JPanel(new GridLayout(4, 1, 0, 15));
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        cardsContainer.add(createOptionCard("MPESA", "M-Pesa", "Carteira Móvel Vodacom", "icons/mpesa.svg"));
        cardsContainer.add(createOptionCard("EMOLA", "e-Mola", "Carteira Móvel Movitel", "icons/emola.svg"));
        cardsContainer.add(createOptionCard("CARTAO", "Cartão Débito / Crédito", "Contactless, Visa, Mastercard", "icons/mastercard.svg"));
        cardsContainer.add(createOptionCard("PAYPAL", "PayPal", "Pagamento rápido via QR Code", "icons/paypal.svg"));

        methodsPanel.add(cardsContainer, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.45;
        gbc.weighty = 1.0;
        add(methodsPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.02;
        add(Box.createHorizontalStrut(15), gbc);

        // Right Panel: Dynamic terminal and order summary
        JPanel summaryPanel = createRoundedPanel(20, Color.decode("#FFFFFF"));
        summaryPanel.setPreferredSize(new Dimension(720, 700));
        summaryPanel.setLayout(new GridBagLayout());

        GridBagConstraints sumgbc = new GridBagConstraints();
        sumgbc.insets = new Insets(20, 25, 10, 25);
        sumgbc.fill = GridBagConstraints.HORIZONTAL;
        sumgbc.weightx = 1.0;

        JPanel totalPanel = createRoundedPanel(16, Color.decode("#EFF6FF"));
        totalPanel.setLayout(new BorderLayout(20, 0));
        totalPanel.setPreferredSize(new Dimension(660, 80));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel totalText = new JLabel("Total a Pagar");
        totalText.setFont(new Font("Arial", Font.BOLD, 20));
        totalText.setForeground(Color.decode("#1D4ED8"));

        totalValueLabel = new JLabel("160,00 MT");
        totalValueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        totalValueLabel.setForeground(Color.decode("#1E3A8A"));

        totalPanel.add(totalText, BorderLayout.WEST);
        totalPanel.add(totalValueLabel, BorderLayout.EAST);

        sumgbc.gridx = 0;
        sumgbc.gridy = 0;
        summaryPanel.add(totalPanel, sumgbc);

        dynamicActionContainer = new JPanel(new BorderLayout());
        dynamicActionContainer.setOpaque(false);
        dynamicActionContainer.setPreferredSize(new Dimension(660, 370));

        sumgbc.gridy = 1;
        sumgbc.weighty = 1.0;
        sumgbc.fill = GridBagConstraints.BOTH;
        summaryPanel.add(dynamicActionContainer, sumgbc);

        processPaymentButton = new JButton("PAGAR AGORA");
        processPaymentButton.putClientProperty(FlatClientProperties.STYLE,
                "arc:16; background: #1E3A8A; focusedBackground: #1E3A8A; hoverBackground: #2563EB; borderWidth: 0;");
        processPaymentButton.setFont(new Font("Arial", Font.BOLD, 22));
        processPaymentButton.setForeground(Color.WHITE);
        processPaymentButton.setPreferredSize(new Dimension(660, 65));
        processPaymentButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sumgbc.gridy = 2;
        sumgbc.weighty = 0;
        sumgbc.fill = GridBagConstraints.HORIZONTAL;
        sumgbc.insets = new Insets(10, 25, 10, 25);
        summaryPanel.add(processPaymentButton, sumgbc);

        JButton backButton = new JButton("Cancelar / Voltar");
        backButton.putClientProperty(FlatClientProperties.STYLE,
                "arc:16; background: #FFFFFF; borderColor: #D1D5DB; borderWidth: 1;");
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setForeground(Color.decode("#4B5563"));
        backButton.setPreferredSize(new Dimension(660, 50));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> cardLayout.show(contentPanel, "CHECKOUT"));

        sumgbc.gridy = 3;
        summaryPanel.add(backButton, sumgbc);

        gbc.gridx = 2;
        gbc.weightx = 0.53;
        add(summaryPanel, gbc);

        selectMethod("MPESA");
    }

    private JPanel createOptionCard(String key, String title, String description, String iconPath) {
        JPanel card = createRoundedPanel(16, Color.decode("#F9FAFB"));
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel();
        Icon icon = loadIcon(iconPath, 44, 44);
        if (icon != null) {
            iconLabel.setIcon(icon);
        } else {
            iconLabel.setPreferredSize(new Dimension(44, 44));
        }
        card.add(iconLabel, BorderLayout.WEST);

        JPanel textContainer = new JPanel(new GridLayout(2, 1, 0, 3));
        textContainer.setOpaque(false);

        JLabel methodTitle = new JLabel(title);
        methodTitle.setFont(new Font("Arial", Font.BOLD, 18));
        methodTitle.setForeground(Color.decode("#111827"));

        JLabel methodDesc = new JLabel(description);
        methodDesc.setFont(new Font("Arial", Font.PLAIN, 13));
        methodDesc.setForeground(Color.decode("#6B7280"));

        textContainer.add(methodTitle);
        textContainer.add(methodDesc);
        card.add(textContainer, BorderLayout.CENTER);

        JLabel checkmarkLabel = new JLabel("●");
        checkmarkLabel.setFont(new Font("Arial", Font.BOLD, 22));
        checkmarkLabel.setForeground(Color.decode("#D1D5DB"));
        card.add(checkmarkLabel, BorderLayout.EAST);

        optionCards.put(key, card);
        checkmarkLabels.put(key, checkmarkLabel);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectMethod(key);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selectedPaymentMethod.equals(key)) {
                    card.setBackground(Color.decode("#F3F4F6"));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!selectedPaymentMethod.equals(key)) {
                    card.setBackground(Color.decode("#F9FAFB"));
                }
            }
        });

        return card;
    }

    private void selectMethod(String method) {
        this.selectedPaymentMethod = method;
        updateCardStyles();
        renderDynamicContent();
    }

    private void updateCardStyles() {
        optionCards.forEach((key, card) -> {
            boolean isSelected = key.equals(selectedPaymentMethod);
            JLabel checkmark = checkmarkLabels.get(key);

            if (isSelected) {
                card.setBackground(Color.decode("#EFF6FF"));
                card.putClientProperty(FlatClientProperties.STYLE, "arc:16; borderColor: #1E3A8A; borderWidth: 2;");
                if (checkmark != null) {
                    checkmark.setText("✓");
                    checkmark.setForeground(Color.decode("#1E3A8A"));
                }
            } else {
                card.setBackground(Color.decode("#F9FAFB"));
                card.putClientProperty(FlatClientProperties.STYLE, "arc:16; borderColor: #E5E7EB; borderWidth: 1;");
                if (checkmark != null) {
                    checkmark.setText("○");
                    checkmark.setForeground(Color.decode("#9CA3AF"));
                }
            }
            card.repaint();
        });
    }

    private void renderDynamicContent() {
        dynamicActionContainer.removeAll();

        if (selectedPaymentMethod.equals("MPESA") || selectedPaymentMethod.equals("EMOLA")) {
            dynamicActionContainer.add(buildMobileMoneyInputPanel(), BorderLayout.CENTER);
            processPaymentButton.setText("Enviar Pedido de Pagamento →");
            processPaymentButton.setEnabled(true);
        } else if (selectedPaymentMethod.equals("CARTAO")) {
            dynamicActionContainer.add(buildCardPaymentPanel(), BorderLayout.CENTER);
            processPaymentButton.setText("Aguardando Leitura do Cartão...");
            processPaymentButton.setEnabled(false);
        } else if (selectedPaymentMethod.equals("PAYPAL")) {
            dynamicActionContainer.add(buildPaypalQRPanel(), BorderLayout.CENTER);
            processPaymentButton.setText("Aguardando Confirmação Mobile...");
            processPaymentButton.setEnabled(false);
        }

        dynamicActionContainer.revalidate();
        dynamicActionContainer.repaint();
    }

    private JPanel buildMobileMoneyInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JPanel inputHeader = new JPanel(new BorderLayout(10, 0));
        inputHeader.setOpaque(false);

        JLabel label = new JLabel("Insira o número de telefone:");
        label.setFont(new Font("Arial", Font.BOLD, 17));
        label.setForeground(Color.decode("#1F2937"));

        phoneInputField = new JTextField(selectedPaymentMethod.equals("MPESA") ? "84 " : "86 ");
        phoneInputField.setFont(new Font("Arial", Font.BOLD, 26));
        phoneInputField.setHorizontalAlignment(JTextField.CENTER);
        phoneInputField.putClientProperty(FlatClientProperties.STYLE, "arc:12; background: #F9FAFB; borderWidth: 1; borderColor: #D1D5DB;");
        phoneInputField.setPreferredSize(new Dimension(300, 52));

        inputHeader.add(label, BorderLayout.NORTH);
        inputHeader.add(phoneInputField, BorderLayout.CENTER);

        panel.add(inputHeader, BorderLayout.NORTH);

        JPanel keyPad = new JPanel(new GridLayout(4, 3, 8, 8));
        keyPad.setOpaque(false);

        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "⌫"};
        for (String key : keys) {
            JButton btn = new JButton(key);
            btn.setFont(new Font("Arial", Font.BOLD, 22));
            btn.putClientProperty(FlatClientProperties.STYLE, "arc:12; background: #FFFFFF; borderColor: #E5E7EB; borderWidth: 1;");
            btn.setFocusable(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> {
                String currentText = phoneInputField.getText();
                if (key.equals("C")) {
                    phoneInputField.setText("");
                } else if (key.equals("⌫")) {
                    if (!currentText.isEmpty()) {
                        phoneInputField.setText(currentText.substring(0, currentText.length() - 1));
                    }
                } else {
                    if (currentText.length() < 12) {
                        phoneInputField.setText(currentText + key);
                    }
                }
            });

            keyPad.add(btn);
        }

        panel.add(keyPad, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCardPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel statusIcon = new JLabel();
        Icon icon = loadIcon("icons/mastercard.svg", 88, 88);
        if (icon != null) statusIcon.setIcon(icon);

        JLabel mainText = new JLabel("Aproxime ou Insira o Cartão");
        mainText.setFont(new Font("Arial", Font.BOLD, 22));
        mainText.setForeground(Color.decode("#111827"));

        JLabel subText = new JLabel("Siga as instruções no terminal de pagamento ao lado");
        subText.setFont(new Font("Arial", Font.PLAIN, 15));
        subText.setForeground(Color.decode("#6B7280"));

        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setPreferredSize(new Dimension(280, 8));

        panel.add(statusIcon, gbc);
        panel.add(mainText, gbc);
        panel.add(subText, gbc);
        panel.add(Box.createVerticalStrut(10), gbc);
        panel.add(progress, gbc);

        return panel;
    }

    private JPanel buildPaypalQRPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(8, 8, 8, 8);

        FlatSVGIcon qr = new FlatSVGIcon("icons/qrcode.svg", 170, 170);
        JLabel qrBox = new JLabel(qr);
        qrBox.setPreferredSize(new Dimension(170, 170));
        qrBox.setOpaque(true);
        qrBox.setBackground(Color.WHITE);
        qrBox.setHorizontalAlignment(SwingConstants.CENTER);
        qrBox.setFont(new Font("Arial", Font.BOLD, 16));
        qrBox.setBorder(BorderFactory.createLineBorder(Color.decode("#D1D5DB"), 2));

        JLabel title = new JLabel("Digitalize com o App PayPal");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel info = new JLabel("O pagamento será confirmado automaticamente");
        info.setFont(new Font("Arial", Font.PLAIN, 15));
        info.setForeground(Color.decode("#6B7280"));

        panel.add(qrBox, gbc);
        panel.add(title, gbc);
        panel.add(info, gbc);

        return panel;
    }

    private JPanel createRoundedPanel(int cornerRadius, Color bgColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBackground(bgColor);
        return panel;
    }

    private Icon loadIcon(String path, int width, int height) {
        try {
            if (path.toLowerCase().endsWith(".svg")) {
                return new FlatSVGIcon(path, width, height);
            } else {
                URL imgUrl = getClass().getClassLoader().getResource(path);
                if (imgUrl != null) {
                    ImageIcon origIcon = new ImageIcon(imgUrl);
                    Image scaledImg = origIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}