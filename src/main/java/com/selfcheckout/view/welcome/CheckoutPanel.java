package com.selfcheckout.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CheckoutPanel extends JPanel {

    private DefaultTableModel model;
    private JPanel totalPanel;
    private JLabel totalText;

    public CheckoutPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(1300, 700));
        putClientProperty(FlatClientProperties.STYLE, "arc:16");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Illustration Panel
        JPanel illustrationPanel = new JPanel(new GridBagLayout());
        illustrationPanel.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        illustrationPanel.setPreferredSize(new Dimension(590, 700));
        illustrationPanel.setBackground(Color.decode("#FFFFFF"));

        GridBagConstraints ilgbc = new GridBagConstraints();
        ilgbc.fill = GridBagConstraints.BOTH;
        ilgbc.insets = new Insets(15, 15, 10, 15);

        // Title
        JLabel scanTextLabel = new JLabel("Instruções");
        scanTextLabel.setFont(new Font("Arial", Font.BOLD, 25));
        ilgbc.gridx = 0;
        ilgbc.gridy = 0;
        ilgbc.weightx = 1.0;
        ilgbc.weighty = 0;
        illustrationPanel.add(scanTextLabel, ilgbc);

        // SVG Icon
        FlatSVGIcon illustrationIcon = new FlatSVGIcon("icons/checkout.svg", 520, 340);
        JLabel illustrationLabel = new JLabel(illustrationIcon);
        ilgbc.gridx = 0;
        ilgbc.gridy = 1;
        ilgbc.weighty = 0;
        ilgbc.insets = new Insets(5, 15, 5, 15);
        illustrationPanel.add(illustrationLabel, ilgbc);

        // Explanation text
        JLabel explanationLabel = new JLabel(
                "<html>"
                        + "<div style='text-align: left; font-family: Arial; font-size: 14px; color: #333;'>"
                        + "Retire o produto do carrinho, posicione o código de barras "
                        + "na leitora da máquina, aguarde a bipagem e confirme o item na tela.<br><br>"
                        + "Repita o processo com os demais produtos e finalize o pagamento."
                        + "</div>"
                        + "</html>"
        );
        explanationLabel.setVerticalAlignment(SwingConstants.TOP);

        ilgbc.gridx = 0;
        ilgbc.gridy = 2;
        ilgbc.weighty = 1.0;
        ilgbc.fill = GridBagConstraints.BOTH;
        ilgbc.insets = new Insets(10, 20, 20, 20);
        illustrationPanel.add(explanationLabel, ilgbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(illustrationPanel, gbc);

        // JSeparator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        JSeparator separator1 = new JSeparator();
        separator1.setForeground(Color.decode("#E5E7EB"));
        separator1.setPreferredSize(new Dimension(640, 2));

        // Table Panel
        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        tablePanel.setPreferredSize(new Dimension(700, 700));
        tablePanel.setBackground(Color.decode("#FFFFFF"));

        GridBagConstraints tblgbc = new GridBagConstraints();
        tblgbc.insets = new Insets(15, 10, 15, 10);

        // Title Label
        JLabel textLabel = new JLabel("seus itens");
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        tblgbc.gridx = 0;
        tblgbc.gridy = 0;
        tblgbc.fill = GridBagConstraints.HORIZONTAL;
        tblgbc.weightx = 1.0;
        tablePanel.add(textLabel, tblgbc);

        tblgbc.gridx = 0;
        tblgbc.gridy = 1;
        tblgbc.fill = GridBagConstraints.HORIZONTAL;
        tblgbc.weightx = 1.0;
        tablePanel.add(separator1, tblgbc);

        // Create Table
        model = new DefaultTableModel(
                new Object[]{"item", "preço", "quantidade"}, 0
        );

        JTable table = new JTable(model);
        table.setBackground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 25));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        table.getTableHeader().setDefaultRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setFont(new Font("Arial", Font.BOLD, 18));
            label.setForeground(Color.decode("#555555"));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            return label;
        });
        table.setRowHeight(48);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        table.getTableHeader().setBackground(Color.WHITE);

        // Cell Renderer
        table.setDefaultRenderer(Object.class, (table1, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setFont(new Font("Arial", Font.PLAIN, 20));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            if (column == 1 || column == 2) {
                label.setHorizontalAlignment(SwingConstants.CENTER);
            }
            return label;
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(640, 200));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        model.addRow(new Object[]{
                "Coca-Cola 400ml",
                "80,00 MT", 2
        });

        tblgbc.gridx = 0;
        tblgbc.gridy = 2;
        tblgbc.fill = GridBagConstraints.BOTH;
        tblgbc.weightx = 1.0;
        tblgbc.weighty = 1.0;
        tablePanel.add(scrollPane, tblgbc);

        // Total price panel
        totalPanel = new JPanel(new BorderLayout(20, 0));
        totalPanel.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        totalPanel.setBackground(Color.decode("#E8F5E9"));
        totalPanel.setPreferredSize(new Dimension(640, 100));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));

        totalText = new JLabel("total (1 item)");
        totalText.setFont(new Font("Arial", Font.PLAIN, 20));
        totalText.setForeground(Color.decode("#2E7D32"));

        JLabel totalValue = new JLabel("160,00 MT");
        totalValue.setFont(new Font("Arial", Font.BOLD, 28));
        totalValue.setForeground(Color.decode("#1B5E20"));

        totalPanel.add(totalText, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);

        tblgbc.gridx = 0;
        tblgbc.gridy = 3;
        tblgbc.fill = GridBagConstraints.HORIZONTAL;
        tblgbc.weightx = 1.0;
        tblgbc.weighty = 0;
        tablePanel.add(totalPanel, tblgbc);

        // Pay Button
        JButton payButton = new JButton("ir para pagamento  →");
        payButton.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        payButton.setFont(new Font("Arial", Font.BOLD, 25));
        payButton.setForeground(Color.WHITE);
        payButton.setBackground(Color.decode("#2D4A0F"));
        payButton.setPreferredSize(new Dimension(640, 80));
        payButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tblgbc.gridx = 0;
        tblgbc.gridy = 4;
        tblgbc.fill = GridBagConstraints.HORIZONTAL;
        tblgbc.weightx = 1.0;
        tablePanel.add(payButton, tblgbc);

        // Cancel Button
        JButton cancelButton = new JButton("cancelar compra");
        cancelButton.putClientProperty(FlatClientProperties.STYLE, "arc:16");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 22));
        cancelButton.setForeground(Color.decode("#444444"));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC")));
        cancelButton.setPreferredSize(new Dimension(640, 80));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tblgbc.gridx = 0;
        tblgbc.gridy = 5;
        tblgbc.fill = GridBagConstraints.HORIZONTAL;
        tblgbc.weightx = 1.0;
        tablePanel.add(cancelButton, tblgbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        add(separator);
        add(tablePanel, gbc);
    }
}