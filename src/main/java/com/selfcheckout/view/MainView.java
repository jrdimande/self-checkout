package com.selfcheckout.view;

import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.selfcheckout.service.ArduinoBoard;
import com.selfcheckout.service.CheckoutService;
import com.selfcheckout.view.checkout.CheckoutPanel;
import com.selfcheckout.view.payment.PaymentPanel;
import com.selfcheckout.view.welcome.WelcomePanel;

public class MainView {

    private JFrame root;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel contentPanel;

    public MainView() {

        root = new JFrame();

        root.setTitle("self-checkout");
        root.setLayout(new BorderLayout(0, 10));
        root.setSize(1400, 900);
        root.setLocationRelativeTo(null);
        root.setResizable(false);
        root.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        root.getContentPane().setBackground(Color.decode("#F1F5F9"));

        initTopPanel();
        initBottomPanel();
        initContentPanel();

        root.setVisible(true);
    }

    public void initTopPanel() {

        topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(1500, 90));
        topPanel.setBackground(Color.decode("#FFFFFF"));
        topPanel.setLayout(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        // Logótipo
        JPanel logoPanel = new JPanel(new GridBagLayout());

        logoPanel.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:15"
        );

        logoPanel.setBackground(Color.decode("#1E3A8A"));
        logoPanel.setPreferredSize(new Dimension(55, 50));

        JLabel logoLabel = new JLabel("SC");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 22));

        logoPanel.add(logoLabel);

        // Nome da aplicação
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setBackground(Color.WHITE);
        textPanel.setPreferredSize(new Dimension(350, 70));

        JLabel textLabel = new JLabel("self-checkout");
        textLabel.setForeground(Color.decode("#0F172A"));
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));

        textPanel.add(logoPanel, gbc);
        textPanel.add(textLabel);

        topPanel.add(textPanel, BorderLayout.WEST);

        // Número da caixa
        JPanel machinePanel = new JPanel(new GridBagLayout());
        machinePanel.setBackground(Color.WHITE);
        machinePanel.setPreferredSize(new Dimension(150, 50));

        JLabel machineNumberLabel = new JLabel("Caixa 01");
        machineNumberLabel.setForeground(Color.decode("#64748B"));
        machineNumberLabel.setFont(new Font("Arial", Font.BOLD, 16));

        machinePanel.add(machineNumberLabel);

        topPanel.add(machinePanel, BorderLayout.EAST);

        root.add(topPanel, BorderLayout.NORTH);
    }

    public void initBottomPanel() {

        bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setPreferredSize(new Dimension(1500, 80));
        bottomPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel textLabel = new JLabel(
                "Precisa de ajuda? Chame um atendente"
        );

        textLabel.setForeground(Color.decode("#475569"));
        textLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        bottomPanel.add(textLabel, gbc);

        root.add(bottomPanel, BorderLayout.SOUTH);
    }

    public void initContentPanel() {

        CardLayout cardLayout = new CardLayout();

        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Um único serviço partilhado por todas as telas
        CheckoutService checkoutService = new CheckoutService();

        // Uma única placa Arduino (RFID + sensor de presença + buzzer)
        ArduinoBoard board = new ArduinoBoard("/dev/ttyACM0", 9600);

        // Tela de checkout
        CheckoutPanel checkoutPanel = new CheckoutPanel(
                cardLayout,
                contentPanel,
                checkoutService,
                board
        );

        // Tela de pagamento
        PaymentPanel paymentPanel = new PaymentPanel(
                cardLayout,
                contentPanel,
                checkoutService,
                board,
                () -> {
                    checkoutPanel.resetCart();                 // novo carrinho vazio
                    cardLayout.show(contentPanel, "WELCOME");  // volta ao início
                }
        );

        // Tela inicial
        contentPanel.add(
                new WelcomePanel(cardLayout, contentPanel),
                "WELCOME"
        );

        contentPanel.add(checkoutPanel, "CHECKOUT");
        contentPanel.add(paymentPanel, "PAYMENT");

        // Mostrar a tela inicial
        cardLayout.show(contentPanel, "WELCOME");

        // Abre a porta serial uma só vez (reconecta sozinha se falhar)
        board.start();

        root.add(contentPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {

        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            new MainView();
        });
    }
}