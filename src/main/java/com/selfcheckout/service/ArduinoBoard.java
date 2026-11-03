package com.selfcheckout.service;

import com.fazecast.jSerialComm.SerialPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ArduinoBoard {

    private static final Logger logger =
            LoggerFactory.getLogger(ArduinoBoard.class);

    private static final long RETRY_MS = 3000;

    private final String portName;
    private final int baudRate;

    private final Object writeLock = new Object();

    private volatile SerialPort serialPort;
    private volatile boolean running = false;
    private volatile boolean connected = false;

    private Thread thread;

    private volatile Consumer<String> tagListener;
    private volatile Consumer<Boolean> presenceListener;
    private volatile BiConsumer<String, Boolean> statusListener;


    public ArduinoBoard(String portName, int baudRate) {

        this.portName = portName;
        this.baudRate = baudRate;

        logger.info(
                "ArduinoBoard inicializado. Porta: {} | Baud Rate: {}",
                portName,
                baudRate
        );
    }


    // =====================================================
    // LISTENERS
    // =====================================================

    public void setTagListener(Consumer<String> listener) {

        this.tagListener = listener;

        logger.debug("Listener RFID actualizado.");
    }


    public void setPresenceListener(Consumer<Boolean> listener) {

        this.presenceListener = listener;

        logger.debug("Listener de presença actualizado.");
    }


    public void setStatusListener(BiConsumer<String, Boolean> listener) {

        this.statusListener = listener;

        logger.debug("Listener de estado actualizado.");
    }


    public boolean isConnected() {

        return connected;
    }


    // =====================================================
    // CICLO DE VIDA
    // =====================================================

    public synchronized void start() {

        if (running) {

            logger.warn("ArduinoBoard já está em execução.");

            return;
        }

        running = true;

        logger.info("A iniciar comunicação com Arduino...");

        thread = new Thread(this::loop, "Arduino-Board-Thread");

        thread.setDaemon(true);

        thread.start();
    }


    public synchronized void stop() {

        logger.info("A encerrar comunicação com Arduino...");

        running = false;

        SerialPort port = serialPort;

        if (port != null && port.isOpen()) {

            port.closePort();

            logger.info("Porta serial {} encerrada.", portName);
        }

        if (thread != null && thread != Thread.currentThread()) {

            thread.interrupt();

            try {

                thread.join(500);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                logger.warn("Interrupção ao aguardar encerramento da thread.");
            }
        }

        thread = null;

        logger.info("Comunicação com Arduino encerrada.");
    }


    // =====================================================
    // ENVIO DE COMANDOS
    // =====================================================

    public void send(String command) {

        SerialPort port = serialPort;

        if (port == null || !port.isOpen()) {

            logger.warn(
                    "Não foi possível enviar comando ao Arduino. Porta desconectada. Comando: {}",
                    command
            );

            return;
        }

        byte[] data = (command + "\n").getBytes(StandardCharsets.UTF_8);

        synchronized (writeLock) {

            try {

                int bytesWritten = port.writeBytes(data, data.length);

                if (bytesWritten == data.length) {

                    logger.debug("Comando enviado ao Arduino: {}", command);

                } else {

                    logger.error(
                            "Falha ao enviar comando. Comando: {} | Bytes enviados: {}",
                            command,
                            bytesWritten
                    );
                }

            } catch (Exception e) {

                logger.error(
                        "Erro ao enviar comando ao Arduino: {}",
                        command,
                        e
                );
            }
        }
    }


    // =====================================================
    // THREAD DE LEITURA
    // =====================================================

    private void loop() {

        while (running) {

            SerialPort port = SerialPort.getCommPort(portName);

            try {

                logger.info("A tentar estabelecer ligação à porta {}...", portName);

                port.setBaudRate(baudRate);

                port.setNumDataBits(8);

                port.setNumStopBits(SerialPort.ONE_STOP_BIT);

                port.setParity(SerialPort.NO_PARITY);

                port.setComPortTimeouts(
                        SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                        0,
                        0
                );

                if (!port.openPort()) {

                    logger.error(
                            "Não foi possível abrir a porta serial: {}",
                            portName
                    );

                    notifyStatus(
                            "Não foi possível abrir a porta " + portName + ".",
                            false
                    );

                    sleep(RETRY_MS);

                    continue;
                }

                serialPort = port;

                connected = true;

                logger.info(
                        "Arduino conectado com sucesso. Porta: {} | Baud Rate: {}",
                        portName,
                        baudRate
                );

                notifyStatus(
                        "LEITOR ATIVO • PRONTO PARA ESCANEAR",
                        true
                );

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                port.getInputStream(),
                                StandardCharsets.UTF_8
                        ))) {

                    String line;

                    while (running && (line = reader.readLine()) != null) {

                        handleLine(line.trim());
                    }
                }

                if (running) {

                    logger.warn("Comunicação serial interrompida.");
                }

            } catch (Exception e) {

                if (running) {

                    logger.error(
                            "Erro durante a comunicação com Arduino.",
                            e
                    );
                }

            } finally {

                connected = false;

                serialPort = null;

                if (port.isOpen()) {

                    port.closePort();
                }

                logger.warn("Ligação com Arduino encerrada.");
            }

            if (running) {

                logger.info(
                        "Nova tentativa de conexão em {} segundos...",
                        RETRY_MS / 1000
                );

                notifyStatus(
                        "Erro na comunicação RFID.",
                        false
                );

                sleep(RETRY_MS);
            }
        }

        logger.info("Thread de comunicação Arduino finalizada.");
    }


    // =====================================================
    // PROCESSAMENTO DAS MENSAGENS
    // =====================================================

    private void handleLine(String line) {

        if (line.isEmpty()) {

            return;
        }


        // Leitura RFID
        if (line.startsWith("TAG:")) {

            String uid = line.substring(4).trim();

            if (!uid.isEmpty()) {

                logger.info("Cartão RFID detectado: {}", uid);

                Consumer<String> listener = tagListener;

                if (listener != null) {

                    listener.accept(uid);

                } else {

                    logger.warn(
                            "Cartão RFID recebido, mas não existe listener registado."
                    );
                }

            } else {

                logger.warn("Foi recebido um cartão RFID com UID vazio.");
            }


            // Sensor de presença
        } else if (line.equals("PRESENCA:1") || line.equals("PRESENCA:0")) {

            boolean presenceDetected = line.equals("PRESENCA:1");

            logger.info(
                    "Estado do sensor de presença: {}",
                    presenceDetected ? "PRESENÇA DETECTADA" : "SEM PRESENÇA"
            );

            Consumer<Boolean> listener = presenceListener;

            if (listener != null) {

                listener.accept(presenceDetected);

            } else {

                logger.debug("Não existe listener de presença registado.");
            }


        } else {

            logger.debug("Mensagem recebida do Arduino: {}", line);
        }
    }


    // =====================================================
    // NOTIFICAÇÃO DE ESTADO
    // =====================================================

    private void notifyStatus(String message, boolean ok) {

        BiConsumer<String, Boolean> listener = statusListener;

        if (listener != null) {

            listener.accept(message, ok);
        }

        logger.info(
                "Estado Arduino: {} | Conectado: {}",
                message,
                ok
        );
    }


    // =====================================================
    // PAUSA ENTRE TENTATIVAS
    // =====================================================

    private void sleep(long millis) {

        try {

            Thread.sleep(millis);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}