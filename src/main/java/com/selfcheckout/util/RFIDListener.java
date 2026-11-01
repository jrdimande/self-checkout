package com.selfcheckout.util;

import com.fazecast.jSerialComm.SerialPort;
import com.selfcheckout.model.Product;
import com.selfcheckout.service.CheckoutService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RFIDListener {

    private static final String PORT_NAME = "/dev/ttyACM0";
    private static final int BAUD_RATE = 9600;

    public static void main(String[] args) {

        SerialPort port = SerialPort.getCommPort(PORT_NAME);

        port.setBaudRate(BAUD_RATE);
        port.setNumDataBits(8);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        port.setParity(SerialPort.NO_PARITY);

        // Esperar continuamente pelos dados enviados pelo Arduino.
        port.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                0,
                0
        );

        CheckoutService checkoutService = new CheckoutService();

        System.out.println("======================================");
        System.out.println("       SELF-CHECKOUT - RFID");
        System.out.println("======================================");

        System.out.println("Porta série: " + PORT_NAME);
        System.out.println("Velocidade: " + BAUD_RATE);

        if (!port.openPort()) {
            System.err.println("ERRO: Não foi possível abrir a porta série.");
            System.err.println("Verifica a ligação USB do Arduino.");
            return;
        }

        System.out.println("Leitor conectado com sucesso!");

        try {
            // O Arduino pode reiniciar ao abrir a porta série.
            Thread.sleep(2000);

            System.out.println("Aguardando cartões RFID...\n");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            port.getInputStream(),
                            StandardCharsets.UTF_8
                    ))) {

                while (true) {

                    String mensagem = reader.readLine();

                    if (mensagem == null) {
                        continue;
                    }

                    mensagem = mensagem.trim();

                    // Ignorar linhas vazias.
                    if (mensagem.isEmpty()) {
                        continue;
                    }

                    // Ignorar mensagens de estado do Arduino.
                    if (mensagem.equalsIgnoreCase("SELF_CHECKOUT_PRONTO")
                            || mensagem.equalsIgnoreCase("RFID_READY")) {

                        System.out.println("Arduino pronto.");
                        continue;
                    }

                    System.out.println("\nMensagem recebida: [" + mensagem + "]");

                    // Remover o prefixo TAG: enviado pelo Arduino.
                    String uid = mensagem;

                    if (uid.regionMatches(true, 0, "TAG:", 0, 4)) {
                        uid = uid.substring(4).trim();
                    }

                    // Normalizar o UID.
                    uid = uid.toUpperCase();

                    if (uid.isEmpty()) {
                        System.out.println("UID vazio. Ignorado.");
                        continue;
                    }

                    System.out.println("UID processado: [" + uid + "]");
                    System.out.println("Comprimento: " + uid.length());

                    // Consultar o produto através do CheckoutService.
                    System.out.println("A consultar a base de dados...");

                    Product product = checkoutService.processRFID(uid);

                    if (product != null) {

                        System.out.println("PRODUTO ENCONTRADO!");
                        System.out.println("UID: " + product.getUuid());
                        System.out.println("Nome: " + product.getName());

                        System.out.printf(
                                "Preço: %.2f MT%n",
                                product.getPrice()
                        );

                    } else {

                        System.out.println(
                                "Produto não encontrado na base de dados."
                        );
                    }

                    System.out.println("--------------------------------------");
                }
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            System.out.println("Leitura interrompida.");

        } catch (IOException e) {

            System.err.println("Erro na comunicação com o Arduino:");
            e.printStackTrace();

        } finally {

            if (port.isOpen()) {
                port.closePort();
            }

            System.out.println("Porta série encerrada.");
        }
    }
}
