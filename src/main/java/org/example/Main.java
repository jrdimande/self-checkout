//package org.example;
//
//import com.selfcheckout.arduino.RFIDReader;
//
//public class Main {
//
//    public static void main(String[] args) {
//
//        // O RFIDReader já imprime o UID sozinho (método imprimirUid),
//        // por isso aqui nem precisamos de passar um listener --
//        // basta null, ou um listener vazio se quiseres reagir a algo.
//        RFIDReader rfidReader = new RFIDReader(
//                "/dev/ttyACM0",
//                uid -> {
//                    // opcional: usa aqui se quiseres fazer mais alguma
//                    // coisa além de imprimir (ex: chamar CheckoutService)
//                }
//        );
//
//        rfidReader.start();
//
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            System.out.println("Encerrando...");
//            rfidReader.stop();
//        }));
//
//        System.out.println("Self Checkout iniciado. Aguardando cartões...");
//
//        while (true) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
//
//        rfidReader.stop();
//    }
//}