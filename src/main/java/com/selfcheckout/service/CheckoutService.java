package com.selfcheckout.service;

import com.selfcheckout.dao.*;
import com.selfcheckout.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CheckoutService {

    private static final Logger logger =
            LoggerFactory.getLogger(CheckoutService.class);

    private final Cart cart;

    private final ProductDAO productDAO;
    private final CustomerDAO customerDAO;
    private final SaleDAO saleDAO;
    private final SaleItemDAO saleItemDAO;

    private final PaymentService paymentService;
    private final CustomerService customerService;

    private Sale sale;


    public CheckoutService() {

        this.cart = new Cart();

        this.productDAO = new ProductDAO();
        this.customerDAO = new CustomerDAO();
        this.saleDAO = new SaleDAO();
        this.saleItemDAO = new SaleItemDAO();

        this.paymentService = new PaymentService();
        this.customerService = new CustomerService();

        logger.info("CheckoutService inicializado com sucesso.");
    }


    // Iniciar compra
    public void startCheckout() {

        sale = new Sale();

        cart.clear();

        logger.info("Nova compra iniciada.");
    }


    // Processar leitura RFID
    public Product processRFID(String uid) {

        logger.info("A processar leitura RFID: {}", uid);

        Product product = productDAO.findByUuid(uid);

        if (product == null) {

            logger.warn("Produto não encontrado. RFID: {}", uid);

            return null;
        }

        int stock = productDAO.getStock(uid);

        if (stock <= 0) {

            logger.warn(
                    "Produto sem stock disponível. Produto: {}",
                    product.getName()
            );

            return null;
        }

        logger.info(
                "Produto identificado: {} | Stock disponível: {}",
                product.getName(),
                stock
        );

        return product;
    }


    // Adicionar produto ao carrinho
    public void addItem(String uuid) {

        logger.info("Tentativa de adicionar produto ao carrinho: {}", uuid);

        Product product = productDAO.findByUuid(uuid);

        if (product == null) {

            logger.warn("Não foi possível adicionar. Produto inexistente: {}", uuid);

            return;
        }

        int stock = productDAO.getStock(uuid);

        if (stock <= 0) {

            logger.warn("Stock insuficiente para o produto: {}", uuid);

            return;
        }

        int quantityInCart = 0;

        for (SaleItem item : cart.getItems()) {

            if (item.getProduct().getUuid().equals(uuid)) {
                quantityInCart++;
            }
        }

        if (quantityInCart >= stock) {

            logger.warn(
                    "Stock insuficiente. Produto: {} | Stock: {} | Carrinho: {}",
                    uuid,
                    stock,
                    quantityInCart
            );

            return;
        }

        SaleItem saleItem = new SaleItem(0, product);

        cart.addItem(saleItem);

        logger.info(
                "Produto adicionado ao carrinho: {} | Preço: {}",
                product.getName(),
                product.getPrice()
        );
    }


    // Remover produto do carrinho
    public void removeItem(String uuid) {

        cart.removeItem(uuid);

        logger.info("Produto removido do carrinho: {}", uuid);
    }


    // Calcular total
    public double getTotal() {

        cart.calculateTotal();

        double total = cart.getTotal();

        logger.debug("Total actual do carrinho: {}", total);

        return total;
    }


    // Verificar stock de todos os produtos
    private boolean checkStock() {

        logger.info("A verificar stock dos produtos da compra.");

        Map<String, Integer> quantities = new HashMap<>();

        for (SaleItem item : cart.getItems()) {

            String uuid = item.getProduct().getUuid();

            quantities.put(
                    uuid,
                    quantities.getOrDefault(uuid, 0) + 1
            );
        }

        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {

            int availableStock = productDAO.getStock(entry.getKey());

            if (availableStock < entry.getValue()) {

                logger.warn(
                        "Stock insuficiente. Produto: {} | Disponível: {} | Solicitado: {}",
                        entry.getKey(),
                        availableStock,
                        entry.getValue()
                );

                return false;
            }
        }

        logger.info("Verificação de stock concluída com sucesso.");

        return true;
    }


    // Concluir compra
    public boolean checkout(String customerUuid) {

        logger.info("Início do processo de checkout.");

        if (sale == null || cart.getItems().isEmpty()) {

            logger.warn("Checkout cancelado: não existe uma compra activa.");

            return false;
        }

        // Procurar cliente
        Customer customer = customerDAO.findByUuid(customerUuid);

        if (customer == null) {

            logger.warn(
                    "Checkout cancelado: cartão do cliente não reconhecido. RFID: {}",
                    customerUuid
            );

            return false;
        }

        logger.info("Cliente identificado: {}", customer.getUuid());

        cart.calculateTotal();

        double total = cart.getTotal();

        logger.info("Total da compra: {}", total);

        // Verificar saldo
        if (customer.getBalance() < total) {

            logger.warn(
                    "Saldo insuficiente. Cliente: {} | Saldo: {} | Total: {}",
                    customer.getUuid(),
                    customer.getBalance(),
                    total
            );

            return false;
        }

        // Verificar stock
        if (!checkStock()) {

            logger.warn("Checkout cancelado devido à falta de stock.");

            return false;
        }

        // Agrupar quantidades por produto
        Map<String, Integer> quantities = new HashMap<>();

        for (SaleItem item : cart.getItems()) {

            String uuid = item.getProduct().getUuid();

            quantities.put(
                    uuid,
                    quantities.getOrDefault(uuid, 0) + 1
            );
        }

        // Diminuir stock
        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {

            logger.info(
                    "A actualizar stock do produto: {} | Quantidade: {}",
                    entry.getKey(),
                    entry.getValue()
            );

            boolean updated = productDAO.decreaseStock(
                    entry.getKey(),
                    entry.getValue()
            );

            if (!updated) {

                logger.error(
                        "Erro ao actualizar stock do produto: {}",
                        entry.getKey()
                );

                return false;
            }
        }

        try {

            // Preparar venda
            sale.setCustomer_uuid(customer.getUuid());

            sale.setTotal(total);

            // Guardar venda
            int saleId = saleDAO.save(sale);

            logger.info("Venda registada com sucesso. ID: {}", saleId);

            // Guardar produtos da venda
            for (SaleItem item : cart.getItems()) {

                item.setSale_id(saleId);

                saleItemDAO.save(item);

                logger.debug(
                        "Produto registado na venda: {}",
                        item.getProduct().getName()
                );
            }

            logger.info("Produtos da venda registados com sucesso.");

            // Registar pagamento
            paymentService.savePayment(
                    saleId,
                    total,
                    "CARD"
            );

            logger.info(
                    "Pagamento registado. Venda: {} | Método: CARD | Valor: {}",
                    saleId,
                    total
            );

            // Actualizar saldo do cliente
            double previousBalance = customer.getBalance();

            customer.setBalance(
                    previousBalance - total
            );

            customerService.updateBalance(customer);

            logger.info(
                    "Saldo do cliente actualizado. Anterior: {} | Actual: {}",
                    previousBalance,
                    customer.getBalance()
            );

            // Limpar carrinho
            cart.clear();

            sale = null;

            logger.info(
                    "COMPRA CONCLUÍDA COM SUCESSO. Venda: {} | Total: {}",
                    saleId,
                    total
            );

            System.out.println("================================");
            System.out.println("COMPRA CONCLUÍDA COM SUCESSO!");
            System.out.println("Total: " + total);
            System.out.println("================================");

            return true;

        } catch (Exception e) {

            logger.error(
                    "Erro ao concluir compra. Cliente: {} | Total: {}",
                    customerUuid,
                    total,
                    e
            );

            return false;
        }
    }
}