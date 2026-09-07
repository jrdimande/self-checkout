package com.selfcheckout.view.welcome;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckoutPanel extends JPanel {

    private DefaultTableModel model;
    private JPanel totalPanel;
    private JLabel totalText;

    public CheckoutPanel(CardLayout cardLayout, JPanel contentPanel) {
        // Set main layout container with GridBagLayout for precise component positioning
        setLayout(new GridBagLayout());

        setBackground(Color.WHITE);
        setOpaque(true);

        setPreferredSize(new Dimension(1300, 700));
        putClientProperty(FlatClientProperties.STYLE, "arc:16");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Left Panel: Scanner instructions, status indicators, and helper tips
        JPanel illustrationPanel = new JPanel(new GridBagLayout());
        illustrationPanel.putClientProperty(FlatClientProperties.STYLE, "arc:16; background: #FFFFFF");
        illustrationPanel.setPreferredSize(new Dimension(590, 700));

        GridBagConstraints ilgbc = new GridBagConstraints();
        ilgbc.fill = GridBagConstraints.HORIZONTAL;
        ilgbc.insets = new Insets(15, 25, 10, 25);

        // Active barcode scanner indicator badge
        JPanel statusBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusBadge.putClientProperty(FlatClientProperties.STYLE, "background: #DCFCE7; arc:999");
        statusBadge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(10, 10));
        dot.setBackground(Color.decode("#22C55E"));
        dot.putClientProperty(FlatClientProperties.STYLE, "arc:999");

        JLabel statusLabel = new JLabel("LEITOR ATIVO • PRONTO PARA ESCANEAR");
        statusLabel.setForeground(Color.decode("#15803D"));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));

        statusBadge.add(dot);
        statusBadge.add(statusLabel);

        ilgbc.gridx = 0;
        ilgbc.gridy = 0;
        ilgbc.weightx = 1.0;
        ilgbc.fill = GridBagConstraints.NONE;
        ilgbc.anchor = GridBagConstraints.WEST;
        illustrationPanel.add(statusBadge, ilgbc);

        ilgbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel scanTextLabel = new JLabel("Passe seus produtos no leitor");
        scanTextLabel.setForeground(Color.decode("#0F172A"));
        scanTextLabel.setFont(new Font("Arial", Font.BOLD, 28));

        ilgbc.gridy = 1;
        ilgbc.insets = new Insets(10, 25, 5, 25);
        illustrationPanel.add(scanTextLabel, ilgbc);

        JLabel subTextLabel = new JLabel("Posicione o código de barras virado para a luz vermelha");
        subTextLabel.setForeground(Color.decode("#64748B"));
        subTextLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        ilgbc.gridy = 2;
        ilgbc.insets = new Insets(0, 25, 15, 25);
        illustrationPanel.add(subTextLabel, ilgbc);

        // SVG Illustration Container representing the scanner hardware
        JPanel svgCard = new JPanel(new GridBagLayout());
        svgCard.setOpaque(false);

        FlatSVGIcon illustrationIcon = new FlatSVGIcon("icons/checkout.svg", 440, 240);
        JLabel illustrationLabel = new JLabel(illustrationIcon);
        illustrationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        svgCard.add(illustrationLabel);

        ilgbc.gridy = 3;
        ilgbc.insets = new Insets(10, 25, 20, 25);
        illustrationPanel.add(svgCard, ilgbc);

        // Helpful reading tip instruction box for shoppers
        JPanel helpNotice = new JPanel(new GridBagLayout());
        helpNotice.putClientProperty(FlatClientProperties.STYLE,
                "arc:16; background: #F8FAFC; border: 1,1,1,1,#E2E8F0;");

        GridBagConstraints hgbc = new GridBagConstraints();
        hgbc.insets = new Insets(16, 20, 16, 20);

        JLabel infoIconLabel = new JLabel("");
        infoIconLabel.setFont(new Font("Arial", Font.PLAIN, 26));

        hgbc.gridx = 0;
        hgbc.gridy = 0;
        hgbc.anchor = GridBagConstraints.WEST;
        helpNotice.add(infoIconLabel, hgbc);

        JPanel textContainer = new JPanel();
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.setOpaque(false);

        JLabel noticeTitle = new JLabel("Dica de Leitura");
        noticeTitle.setFont(new Font("Arial", Font.BOLD, 16));
        noticeTitle.setForeground(Color.decode("#0F172A"));

        JLabel noticeDesc = new JLabel("Aguarde o 'bip' de confirmação antes de passar o próximo item.");
        noticeDesc.setFont(new Font("Arial", Font.PLAIN, 14));
        noticeDesc.setForeground(Color.decode("#475569"));

        textContainer.add(noticeTitle);
        textContainer.add(Box.createVerticalStrut(4));
        textContainer.add(noticeDesc);

        hgbc.gridx = 1;
        hgbc.weightx = 1.0;
        hgbc.fill = GridBagConstraints.HORIZONTAL;
        hgbc.insets = new Insets(16, 14, 16, 20);
        helpNotice.add(textContainer, hgbc);

        ilgbc.gridy = 4;
        ilgbc.weighty = 1.0;
        ilgbc.fill = GridBagConstraints.HORIZONTAL;
        ilgbc.anchor = GridBagConstraints.NORTH;
        ilgbc.insets = new Insets(0, 25, 20, 25);
        illustrationPanel.add(helpNotice, ilgbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(illustrationPanel, gbc);

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(Color.decode("#E2E8F0"));

        JSeparator separator1 = new JSeparator();
        separator1.setForeground(Color.decode("#E2E8F0"));
        separator1.setPreferredSize(new Dimension(640, 1));

        // Right Panel: Shopping cart items summary table and action controls
        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.putClientProperty(FlatClientProperties.STYLE, "arc:16; background: #FFFFFF");
        tablePanel.setPreferredSize(new Dimension(700, 700));

        GridBagConstraints tblgbc = new GridBagConstraints();
        tblgbc.insets = new Insets(74, 25, 15, 25);

        JLabel textLabel = new JLabel("Resumo do Carrinho");
        textLabel.setForeground(Color.decode("#0F172A"));
        textLabel.setFont(new Font("Arial", Font.BOLD, 28));

        tblgbc.gridx = 0;
        tblgbc.gridy = 0;
        tblgbc.fill = GridBagConstraints.HORIZONTAL;
        tblgbc.weightx = 1.0;
        tablePanel.add(textLabel, tblgbc);

        tblgbc.gridx = 0;
        tblgbc.gridy = 1;
        tblgbc.fill = GridBagConstraints.HORIZONTAL;
        tblgbc.weightx = 1.0;
        tblgbc.insets = new Insets(0, 25, 10, 25);
        tablePanel.add(separator1, tblgbc);

        // Shopping cart table data model initialization
        model = new DefaultTableModel(
                new Object[]{"Item", "Preço Uni.", "Qtd."}, 0
        );

        JTable table = new JTable(model);
        table.setBackground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.setRowHeight(56);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        table.getTableHeader().setBackground(Color.decode("#F8FAFC"));
        table.getTableHeader().setForeground(Color.decode("#64748B"));
        table.getTableHeader().setPreferredSize(new Dimension(0, 44));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));

        table.getTableHeader().setDefaultRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setFont(new Font("Arial", Font.BOLD, 15));
            label.setForeground(Color.decode("#475569"));
            label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
            if (column == 1 || column == 2) {
                label.setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                label.setHorizontalAlignment(SwingConstants.LEFT);
            }
            return label;
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Arial", Font.PLAIN, 18));
                label.setForeground(Color.decode("#1E293B"));
                label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

                if (row % 2 == 0) {
                    label.setBackground(Color.WHITE);
                } else {
                    label.setBackground(Color.decode("#F8FAFC"));
                }

                if (column == 1 || column == 2) {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(640, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#F1F5F9"), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        model.addRow(new Object[]{"Coca-Cola 400ml", "80,00 MT", "2x"});

        tblgbc.gridx = 0;
        tblgbc.gridy = 2;
        tblgbc.fill = GridBagConstraints.BOTH;
        tblgbc.weightx = 1.0;
        tblgbc.weighty = 1.0;
        tblgbc.insets = new Insets(0, 25, 15, 25);
        tablePanel.add(scrollPane, tblgbc);

        // Grand total display banner panel
        totalPanel = new JPanel(new BorderLayout(20, 0));
        totalPanel.putClientProperty(FlatClientProperties.STYLE,
                "arc:16; background: #EFF6FF; border: 1,1,1,1,#BFDBFE;");
        totalPanel.setPreferredSize(new Dimension(640, 100));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));

        totalText = new JLabel("TOTAL DA COMPRA (1 item)");
        totalText.setFont(new Font("Arial", Font.BOLD, 15));
        totalText.setForeground(Color.decode("#1D4ED8"));

        JLabel totalValue = new JLabel("160,00 MT");
        totalValue.setFont(new Font("Arial", Font.BOLD, 32));
        totalValue.setForeground(Color.decode("#1E3A8A"));

        totalPanel.add(totalText, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);

        tblgbc.gridx = 0;
        tblgbc.gridy = 3;
        tblgbc.fill = GridBagConstraints.HORIZONTAL;
        tblgbc.weightx = 1.0;
        tblgbc.weighty = 0;
        tblgbc.insets = new Insets(0, 25, 15, 25);
        tablePanel.add(totalPanel, tblgbc);

        // Proceed to Payment action button
        JButton payButton = new JButton("Ir para o pagamento →");
        payButton.putClientProperty(FlatClientProperties.STYLE,
                "arc:16; " +
                        "background: #1E3A8A; " +
                        "focusedBackground: #1E3A8A; " +
                        "hoverBackground: #1E40AF; " +
                        "borderWidth: 0;");
        payButton.setFont(new Font("Arial", Font.BOLD, 22));
        payButton.setForeground(Color.WHITE);
        payButton.setPreferredSize(new Dimension(640, 75));
        payButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cardLayout.show(contentPanel, "PAYMENT");
            }
        });

        tblgbc.gridx = 0;
        tblgbc.gridy = 4;
        tblgbc.fill = GridBagConstraints.HORIZONTAL;
        tblgbc.weightx = 1.0;
        tblgbc.insets = new Insets(0, 25, 12, 25);
        tablePanel.add(payButton, tblgbc);

        // Cancel transaction button returning to welcome screen
        JButton cancelButton = new JButton("Cancelar compra");
        cancelButton.putClientProperty(FlatClientProperties.STYLE,
                "arc:16; " +
                        "background: #FFFFFF; " +
                        "hoverBackground: #FEF2F2; " +
                        "borderWidth: 1; " +
                        "borderColor: #FECACA;");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 18));
        cancelButton.setForeground(Color.decode("#DC2626"));
        cancelButton.setPreferredSize(new Dimension(640, 65));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cardLayout.show(contentPanel, "WELCOME");
            }
        });

        tblgbc.gridx = 0;
        tblgbc.gridy = 5;
        tblgbc.fill = GridBagConstraints.HORIZONTAL;
        tblgbc.weightx = 1.0;
        tblgbc.insets = new Insets(0, 25, 20, 25);
        tablePanel.add(cancelButton, tblgbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        add(separator);
        add(tablePanel, gbc);
    }
}