package com.selfcheckout.service;

import com.fazecast.jSerialComm.SerialPort;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Comunicação única com a placa Arduino (leitor RFID + sensor de presença + buzzer).
 *
 * Recebe: TAG:<uid>  |  PRESENCA:1  |  PRESENCA:0
 * Envia:  BUZZER:ON  |  BUZZER:OFF
 *
 * A porta é aberta uma só vez (start) e reconecta sozinha se falhar.
 * Cada tela liga/desliga os seus próprios listeners.
 */
public class ArduinoBoard {

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
    }

    // =====================================================
    // LISTENERS (null = desligado)
    // =====================================================

    public void setTagListener(Consumer<String> listener) {
        this.tagListener = listener;
    }

    public void setPresenceListener(Consumer<Boolean> listener) {
        this.presenceListener = listener;
    }

    public void setStatusListener(BiConsumer<String, Boolean> listener) {
        this.statusListener = listener;
    }

    public boolean isConnected() {
        return connected;
    }

    // =====================================================
    // CICLO DE VIDA
    // =====================================================

    public synchronized void start() {
        if (running) {
            return;
        }

        running = true;

        thread = new Thread(this::loop, "Arduino-Board-Thread");
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        running = false;

        SerialPort port = serialPort;
        if (port != null && port.isOpen()) {
            port.closePort();   // desbloqueia o readLine()
        }

        if (thread != null && thread != Thread.currentThread()) {
            thread.interrupt();
            try {
                thread.join(500);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        thread = null;
    }

    // =====================================================
    // ENVIO DE COMANDOS
    // =====================================================

    /** Envia um comando (ex.: "BUZZER:ON") ao Arduino. */
    public void send(String command) {
        SerialPort port = serialPort;

        if (port == null || !port.isOpen()) {
            return;
        }

        byte[] data = (command + "\n").getBytes(StandardCharsets.UTF_8);

        synchronized (writeLock) {
            port.writeBytes(data, data.length);
        }
    }

    // =====================================================
    // THREAD DE LEITURA
    // =====================================================

    private void loop() {

        while (running) {

            SerialPort port = SerialPort.getCommPort(portName);

            try {
                port.setBaudRate(baudRate);
                port.setNumDataBits(8);
                port.setNumStopBits(SerialPort.ONE_STOP_BIT);
                port.setParity(SerialPort.NO_PARITY);
                port.setComPortTimeouts(
                        SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

                if (!port.openPort()) {
                    notifyStatus("Não foi possível abrir a porta " + portName + ".", false);
                    sleep(RETRY_MS);
                    continue;
                }

                serialPort = port;
                connected = true;

                notifyStatus("LEITOR ATIVO • PRONTO PARA ESCANEAR", true);

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(port.getInputStream(),
                                StandardCharsets.UTF_8))) {

                    String line;
                    while (running && (line = reader.readLine()) != null) {
                        handleLine(line.trim());
                    }
                }

            } catch (Exception e) {
                if (running) {
                    e.printStackTrace();
                }
            } finally {
                connected = false;
                serialPort = null;

                if (port.isOpen()) {
                    port.closePort();
                }
            }

            if (running) {
                notifyStatus("Erro na comunicação RFID.", false);
                sleep(RETRY_MS);
            }
        }
    }

    private void handleLine(String line) {

        if (line.isEmpty()) {
            return;
        }

        if (line.startsWith("TAG:")) {

            System.out.println("RFID recebido: " + line);

            String uid = line.substring(4).trim();
            Consumer<String> listener = tagListener;

            if (!uid.isEmpty() && listener != null) {
                listener.accept(uid);
            }

        } else if (line.equals("PRESENCA:1") || line.equals("PRESENCA:0")) {

            Consumer<Boolean> listener = presenceListener;

            if (listener != null) {
                listener.accept(line.equals("PRESENCA:1"));
            }
        }
        // Outras linhas (SELF_CHECKOUT_PRONTO, DIST:...) são ignoradas
    }

    private void notifyStatus(String message, boolean ok) {
        BiConsumer<String, Boolean> listener = statusListener;

        if (listener != null) {
            listener.accept(message, ok);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}