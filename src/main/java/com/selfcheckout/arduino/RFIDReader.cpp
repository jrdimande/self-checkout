```cpp
#include <SPI.h>
#include <MFRC522.h>

// RFID reader pins
#define SS_PIN 10
#define RST_PIN 9

// Component pins
#define BUZZER_PIN 5
#define LED_PIN 6

MFRC522 rfid(SS_PIN, RST_PIN);

String lastUID = "";
unsigned long lastReadTime = 0;

const unsigned long READ_INTERVAL = 1500;

void setup() {
  Serial.begin(9600);

  pinMode(BUZZER_PIN, OUTPUT);
  pinMode(LED_PIN, OUTPUT);

  digitalWrite(BUZZER_PIN, LOW);
  digitalWrite(LED_PIN, LOW);

  SPI.begin();
  rfid.PCD_Init();

  delay(100);

  Serial.println("SELF_CHECKOUT_READY");
}

void loop() {

  // Check if a new card is present
  if (!rfid.PICC_IsNewCardPresent()) {
    return;
  }

  if (!rfid.PICC_ReadCardSerial()) {
    return;
  }

  // Build the card UID
  String uid = "";

  for (byte i = 0; i < rfid.uid.size; i++) {

    if (rfid.uid.uidByte[i] < 0x10) {
      uid += "0";
    }

    uid += String(rfid.uid.uidByte[i], HEX);
  }

  uid.toUpperCase();

  unsigned long currentTime = millis();

  // Prevent repeated processing of the same tag
  if (uid != lastUID ||
      currentTime - lastReadTime >= READ_INTERVAL) {

    lastUID = uid;
    lastReadTime = currentTime;

    // Visual signal
    digitalWrite(LED_PIN, HIGH);

    // Sound signal
    digitalWrite(BUZZER_PIN, HIGH);
    delay(150);
    digitalWrite(BUZZER_PIN, LOW);

    // Send the UID to the computer
    Serial.print("TAG:");
    Serial.println(uid);

    delay(200);

    digitalWrite(LED_PIN, LOW);
  }

  // Stop communication with the card
  rfid.PICC_HaltA();
  rfid.PCD_StopCrypto1();
}
