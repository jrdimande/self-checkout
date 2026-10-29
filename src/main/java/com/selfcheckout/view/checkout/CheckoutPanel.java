package com.selfcheckout.view.checkout;

import com.fazecast.jSerialComm.SerialPort;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.selfcheckout.model.Product;
import com.selfcheckout.service.CheckoutService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CheckoutPanel extends JPanel {

    // Serviço de checkout
    private final CheckoutService checkoutService = new CheckoutService();

    // Componentes da interface
    private DefaultTableModel tableModel;
    private JTable productTable;

    private JLabel totalText;
    private JLabel totalValue;
    private JLabel statusLabel;

    private JButton payButton;
    private JButton cancelButton;

    // Navegação entre os painéis
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    // Leitor RFID
    private SerialPort serialPort;
    private Thread rfidThread;
    private volatile boolean reading = false;

    private static final String RFID_PORT = "/dev/ttyACM0";
    private static final int RFID_BAUD_RATE = 9600;

    // Identifica a linha associada a cada UID
    private final Map<String, Integer> productRows = new HashMap<>();

    public CheckoutPanel(CardLayout cardLayout, JPanel contentPanel) {

        this.cardLayout = cardLayout;
        this.contentPanel = contentPanel;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setOpaque(true);

        putClientProperty(FlatClientProperties.STYLE, "arc:16");

        setPreferredSize(new Dimension(1300, 700));

        createInterface();

        checkoutService.startCheckout();

        updateTotal();

        startRFIDReader();
    }

    // =====================================================
    // INTERFACE PRINCIPAL
    // =====================================================

    private void createInterface() {

        GridBagConstraints main = new GridBagConstraints();

        main.gridy = 0;
        main.fill = GridBagConstraints.BOTH;
        main.weighty = 1.0;
        main.insets = new Insets(0, 0, 0, 0);

        // LADO ESQUERDO
        JPanel illustrationPanel = createLeftPanel();

        main.gridx = 0;
        main.weightx = 0.46;
        add(illustrationPanel, main);

        // SEPARADOR CENTRAL
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(Color.decode("#E2E8F0"));

        main.gridx = 1;
        main.weightx = 0;
        main.fill = GridBagConstraints.VERTICAL;

        add(separator, main);

        // LADO DIREITO
        JPanel tablePanel = createRightPanel();

        main.gridx = 2;
        main.weightx = 0.54;
        main.fill = GridBagConstraints.BOTH;

        add(tablePanel, main);
    }

    // =====================================================
    // PAINEL ESQUERDO
    // =====================================================

    private JPanel createLeftPanel() {

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(590, 700));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 25, 10, 25);

        // Indicador do leitor
        JPanel statusBadge = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );

        statusBadge.putClientProperty(
                FlatClientProperties.STYLE,
                "background: #DCFCE7; arc:999"
        );

        statusBadge.setBorder(
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        );

        JPanel dot = new JPanel();

        dot.setPreferredSize(new Dimension(10, 10));
        dot.setBackground(Color.decode("#22C55E"));

        dot.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:999"
        );

        statusLabel = new JLabel("A INICIAR LEITOR...");

        statusLabel.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        statusLabel.setForeground(Color.decode("#15803D"));

        statusBadge.add(dot);
        statusBadge.add(statusLabel);

        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        panel.add(statusBadge, gbc);

        // Título
        JLabel title = new JLabel("Passe seus produtos no leitor");

        title.setForeground(Color.decode("#0F172A"));
        title.setFont(new Font("Arial", Font.BOLD, 28));

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 25, 5, 25);

        panel.add(title, gbc);

        // Subtítulo
        JLabel subtitle = new JLabel(
                "<html>Identifique cada produto e acompanhe "
                        + "a sua compra em tempo real.</html>"
        );

        subtitle.setForeground(Color.decode("#64748B"));
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 25, 15, 25);

        panel.add(subtitle, gbc);

        // Ilustração SVG
        JPanel svgCard = new JPanel(new GridBagLayout());
        svgCard.setOpaque(false);

        try {

            FlatSVGIcon icon = new FlatSVGIcon(
                    "icons/checkout.svg",
                    440,
                    240
            );

            JLabel illustration = new JLabel(icon);

            illustration.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            svgCard.add(illustration);

        } catch (Exception e) {

            JLabel fallback = new JLabel("LEITOR RFID");

            fallback.setFont(
                    new Font("Arial", Font.BOLD, 32)
            );

            fallback.setForeground(
                    Color.decode("#1E3A8A")
            );

            svgCard.add(fallback);
        }

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 25, 20, 25);

        panel.add(svgCard, gbc);

        // Caixa de dicas
        JPanel helpNotice = new JPanel(new GridBagLayout());

        helpNotice.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:16; background: #F8FAFC; "
                        + "border: 1,1,1,1,#E2E8F0"
        );

        GridBagConstraints h = new GridBagConstraints();

        h.gridx = 0;
        h.gridy = 0;
        h.anchor = GridBagConstraints.WEST;
        h.insets = new Insets(16, 20, 16, 10);

        JLabel infoIcon = new JLabel("i");

        infoIcon.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        infoIcon.setForeground(
                Color.decode("#2563EB")
        );

        helpNotice.add(infoIcon, h);

        JPanel textContainer = new JPanel();

        textContainer.setLayout(
                new BoxLayout(textContainer, BoxLayout.Y_AXIS)
        );

        textContainer.setOpaque(false);

        JLabel noticeTitle = new JLabel("Dica de Leitura");

        noticeTitle.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        noticeTitle.setForeground(
                Color.decode("#0F172A")
        );

        JLabel noticeDesc = new JLabel(
                "<html>Aguarde a confirmação do sistema "
                        + "antes de passar o próximo produto.</html>"
        );

        noticeDesc.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        noticeDesc.setForeground(
                Color.decode("#475569")
        );

        textContainer.add(noticeTitle);
        textContainer.add(Box.createVerticalStrut(4));
        textContainer.add(noticeDesc);

        h.gridx = 1;
        h.weightx = 1.0;
        h.fill = GridBagConstraints.HORIZONTAL;
        h.insets = new Insets(16, 5, 16, 20);

        helpNotice.add(textContainer, h);

        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 25, 20, 25);

        panel.add(helpNotice, gbc);

        return panel;
    }

    // =====================================================
    // PAINEL DIREITO
    // =====================================================

    private JPanel createRightPanel() {

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(700, 700));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título do carrinho
        JLabel title = new JLabel("Resumo do Carrinho");

        title.setForeground(Color.decode("#0F172A"));
        title.setFont(new Font("Arial", Font.BOLD, 28));

        gbc.gridy = 0;
        gbc.insets = new Insets(74, 25, 15, 25);

        panel.add(title, gbc);

        // Linha separadora
        JSeparator separator = new JSeparator();

        separator.setForeground(Color.decode("#E2E8F0"));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 25, 10, 25);

        panel.add(separator, gbc);

        // Tabela
        tableModel = new DefaultTableModel(
                new Object[]{
                        "Item",
                        "Preço Uni.",
                        "Qtd."
                },
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        productTable = new JTable(tableModel);

        productTable.setBackground(Color.WHITE);
        productTable.setFont(
                new Font("Arial", Font.PLAIN, 18)
        );

        productTable.setRowHeight(56);
        productTable.setShowGrid(false);
        productTable.setIntercellSpacing(
                new Dimension(0, 0)
        );

        productTable.setFillsViewportHeight(true);

        productTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        productTable.getTableHeader().setBackground(
                Color.decode("#F8FAFC")
        );

        productTable.getTableHeader().setForeground(
                Color.decode("#64748B")
        );

        productTable.getTableHeader().setPreferredSize(
                new Dimension(0, 44)
        );

        productTable.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0,
                        Color.decode("#E2E8F0")
                )
        );

        productTable.getTableHeader().setDefaultRenderer(
                (table, value, selected, focus, row, column) -> {

                    JLabel label = new JLabel(
                            value == null ? "" : value.toString()
                    );

                    label.setFont(
                            new Font("Arial", Font.BOLD, 15)
                    );

                    label.setForeground(
                            Color.decode("#475569")
                    );

                    label.setOpaque(true);
                    label.setBackground(
                            Color.decode("#F8FAFC")
                    );

                    label.setBorder(
                            BorderFactory.createEmptyBorder(
                                    0, 15, 0, 15
                            )
                    );

                    if (column == 1 || column == 2) {
                        label.setHorizontalAlignment(
                                SwingConstants.CENTER
                        );
                    } else {
                        label.setHorizontalAlignment(
                                SwingConstants.LEFT
                        );
                    }

                    return label;
                }
        );

        productTable.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean selected,
                            boolean focus,
                            int row,
                            int column
                    ) {

                        JLabel label = (JLabel)
                                super.getTableCellRendererComponent(
                                        table,
                                        value,
                                        selected,
                                        focus,
                                        row,
                                        column
                                );

                        label.setFont(
                                new Font("Arial", Font.PLAIN, 18)
                        );

                        label.setForeground(
                                Color.decode("#1E293B")
                        );

                        label.setBorder(
                                BorderFactory.createEmptyBorder(
                                        0, 15, 0, 15
                                )
                        );

                        if (selected) {
                            label.setBackground(
                                    Color.decode("#DBEAFE")
                            );
                        } else if (row % 2 == 0) {
                            label.setBackground(Color.WHITE);
                        } else {
                            label.setBackground(
                                    Color.decode("#F8FAFC")
                            );
                        }

                        if (column == 1 || column == 2) {
                            label.setHorizontalAlignment(
                                    SwingConstants.CENTER
                            );
                        } else {
                            label.setHorizontalAlignment(
                                    SwingConstants.LEFT
                            );
                        }

                        return label;
                    }
                }
        );

        JScrollPane scrollPane = new JScrollPane(productTable);

        scrollPane.setPreferredSize(
                new Dimension(640, 200)
        );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        Color.decode("#F1F5F9"),
                        1
                )
        );

        scrollPane.getViewport().setBackground(Color.WHITE);

        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 25, 15, 25);

        panel.add(scrollPane, gbc);

        // Painel do total
        JPanel totalPanel = new JPanel(
                new BorderLayout(20, 0)
        );

        totalPanel.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:16; background: #EFF6FF; "
                        + "border: 1,1,1,1,#BFDBFE"
        );

        totalPanel.setPreferredSize(
                new Dimension(640, 100)
        );

        totalPanel.setBorder(
                new EmptyBorder(0, 25, 0, 25)
        );

        totalText = new JLabel(
                "TOTAL DA COMPRA (0 itens)"
        );

        totalText.setFont(
                new Font("Arial", Font.BOLD, 15)
        );

        totalText.setForeground(
                Color.decode("#1D4ED8")
        );

        totalValue = new JLabel("0,00 MT");

        totalValue.setFont(
                new Font("Arial", Font.BOLD, 32)
        );

        totalValue.setForeground(
                Color.decode("#1E3A8A")
        );

        totalPanel.add(totalText, BorderLayout.WEST);
        totalPanel.add(totalValue, BorderLayout.EAST);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 25, 15, 25);

        panel.add(totalPanel, gbc);

        // Botão de pagamento
        payButton = new JButton(
                "Ir para o pagamento →"
        );

        payButton.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:16; "
                        + "background: #1E3A8A; "
                        + "focusedBackground: #1E3A8A; "
                        + "hoverBackground: #1E40AF; "
                        + "borderWidth: 0"
        );

        payButton.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        payButton.setForeground(Color.WHITE);

        payButton.setPreferredSize(
                new Dimension(640, 75)
        );

        payButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        payButton.addActionListener(e -> continuePayment());

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 25, 12, 25);

        panel.add(payButton, gbc);

        // Botão de cancelamento
        cancelButton = new JButton("Cancelar compra");

        cancelButton.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:16; "
                        + "background: #FFFFFF; "
                        + "hoverBackground: #FEF2F2; "
                        + "borderWidth: 1; "
                        + "borderColor: #FECACA"
        );

        cancelButton.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        cancelButton.setForeground(
                Color.decode("#DC2626")
        );

        cancelButton.setPreferredSize(
                new Dimension(640, 65)
        );

        cancelButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        cancelButton.addActionListener(e -> cancelCheckout());

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 25, 20, 25);

        panel.add(cancelButton, gbc);

        return panel;
    }

    // =====================================================
    // LEITOR RFID
    // =====================================================

    private synchronized void startRFIDReader() {

        if (reading) {
            return;
        }

        if (rfidThread != null && rfidThread.isAlive()) {
            return;
        }

        rfidThread = new Thread(() -> {

            try {

                serialPort = SerialPort.getCommPort(RFID_PORT);

                serialPort.setBaudRate(RFID_BAUD_RATE);
                serialPort.setNumDataBits(8);
                serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
                serialPort.setParity(SerialPort.NO_PARITY);

                serialPort.setComPortTimeouts(
                        SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                        0,
                        0
                );

                if (!serialPort.openPort()) {

                    updateStatus(
                            "Não foi possível abrir a porta RFID.",
                            Color.RED
                    );

                    return;
                }

                reading = true;

                updateStatus(
                        "LEITOR ATIVO • PRONTO PARA ESCANEAR",
                        Color.decode("#15803D")
                );

                try (
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        serialPort.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
                ) {

                    String line;

                    while (
                            reading
                                    && !Thread.currentThread().isInterrupted()
                                    && (line = reader.readLine()) != null
                    ) {

                        line = line.trim();

                        if (line.isEmpty()) {
                            continue;
                        }

                        System.out.println("RFID recebido: " + line);

                        if (line.equalsIgnoreCase(
                                "SELF_CHECKOUT_PRONTO"
                        )) {
                            continue;
                        }

                        if (line.startsWith("TAG:")) {

                            String uid = line.substring(4).trim();

                            if (!uid.isEmpty()) {
                                processScannedProduct(uid);
                            }
                        }
                    }
                }

            } catch (Exception e) {

                if (reading) {
                    e.printStackTrace();

                    updateStatus(
                            "Erro na comunicação RFID.",
                            Color.RED
                    );
                }

            } finally {

                reading = false;

                if (serialPort != null && serialPort.isOpen()) {
                    serialPort.closePort();
                }
            }

        }, "RFID-Reader-Thread");

        rfidThread.setDaemon(true);
        rfidThread.start();
    }

    // =====================================================
    // PROCESSAR PRODUTO
    // =====================================================

    private void processScannedProduct(String uid) {

        SwingUtilities.invokeLater(() -> {

            try {

                Product product = checkoutService.processRFID(uid);

                if (product == null) {

                    updateStatus(
                            "Produto não encontrado: " + uid,
                            Color.RED
                    );

                    Toolkit.getDefaultToolkit().beep();

                    return;
                }

                checkoutService.addItem(uid);

                updateProductTable(product, uid);

                updateTotal();

                updateStatus(
                        "Produto adicionado: " + product.getName(),
                        Color.decode("#15803D")
                );

                Toolkit.getDefaultToolkit().beep();

            } catch (Exception e) {

                e.printStackTrace();

                updateStatus(
                        "Erro ao adicionar produto.",
                        Color.RED
                );
            }
        });
    }

    // =====================================================
    // ATUALIZAR TABELA
    // =====================================================

    private void updateProductTable(Product product, String uid) {

        Integer rowIndex = productRows.get(uid);

        double price = product.getPrice();

        if (
                rowIndex != null
                        && rowIndex < tableModel.getRowCount()
        ) {

            int quantity = Integer.parseInt(
                    tableModel.getValueAt(rowIndex, 2).toString()
            );

            quantity++;

            tableModel.setValueAt(
                    quantity,
                    rowIndex,
                    2
            );

        } else {

            int row = tableModel.getRowCount();

            tableModel.addRow(new Object[]{
                    product.getName(),
                    String.format("%.2f MT", price),
                    1
            });

            productRows.put(uid, row);
        }
    }

    // =====================================================
    // ATUALIZAR TOTAL
    // =====================================================

    private void updateTotal() {

        double total = checkoutService.getTotal();

        totalValue.setText(
                String.format("%.2f MT", total)
        );

        int itemCount = 0;

        for (int row = 0; row < tableModel.getRowCount(); row++) {

            itemCount += Integer.parseInt(
                    tableModel.getValueAt(row, 2).toString()
            );
        }

        totalText.setText(
                "TOTAL DA COMPRA (" + itemCount + " itens)"
        );

        payButton.setEnabled(total > 0);
    }

    // =====================================================
    // ATUALIZAR ESTADO DO LEITOR
    // =====================================================

    private void updateStatus(String message, Color color) {

        SwingUtilities.invokeLater(() -> {

            if (statusLabel != null) {

                statusLabel.setText(message);
                statusLabel.setForeground(color);
            }
        });
    }

    // =====================================================
    // CONTINUAR PARA PAGAMENTO
    // =====================================================

    private void continuePayment() {

        if (checkoutService.getTotal() <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Adicione pelo menos um produto antes de continuar.",
                    "Carrinho vazio",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        stopRFIDReader();

        cardLayout.show(contentPanel, "PAYMENT");
    }

    // =====================================================
    // CANCELAR COMPRA
    // =====================================================

    private void cancelCheckout() {

        int option = JOptionPane.showConfirmDialog(
                this,
                "Deseja cancelar a compra atual?",
                "Cancelar compra",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        stopRFIDReader();

        tableModel.setRowCount(0);
        productRows.clear();

        checkoutService.startCheckout();

        updateTotal();

        cardLayout.show(contentPanel, "WELCOME");
    }

    // =====================================================
    // PARAR LEITOR RFID
    // =====================================================

    public void stopRFIDReader() {

        reading = false;

        if (rfidThread != null) {
            rfidThread.interrupt();
        }

        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }
}