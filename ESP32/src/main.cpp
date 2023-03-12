#include <Arduino.h>
#include "InputDebouncer.hpp"
#include "BLEConnection.hpp"
#include <BLEDevice.h>
#include <BLEUtils.h>

#define SERVICE_UUID "a129eaa4-28f6-4a61-a2b7-cb150dfcb92e"

#define RELEASE_BUTTON_PIN 15
#define PRESS_BUTTON_PIN 4

InputDebouncer releaseButton(RELEASE_BUTTON_PIN, 50, Falling);
InputDebouncer pressButton(PRESS_BUTTON_PIN, 50, Rising);
BLECharacteristic characteristic("e5881cf2-7b51-4dd5-a1e4-3514d2fc3235");
BLEConnection connection;

void notify(unsigned long time) {
    Serial.println("pressed");
    if (connection.isConnected()) {
        uint8_t *value = (uint8_t *)&time;
        characteristic.setValue(value, sizeof(time));
        characteristic.notify();
        Serial.println("notified");
    } else {
        Serial.println("not connected");
    }
}

void setup() {
    Serial.begin(9600);
    pinMode(RELEASE_BUTTON_PIN, INPUT_PULLDOWN);
    pinMode(PRESS_BUTTON_PIN, INPUT_PULLDOWN);
    pressButton.setCallback(notify);
    releaseButton.setCallback(notify);
    BLEDevice::init("StopwatchBLE");
    BLEServer *server = BLEDevice::createServer();
    server->setCallbacks(&connection);

    characteristic.setNotifyProperty(true);

    BLEService *service = server->createService(SERVICE_UUID);
    service->addCharacteristic(&characteristic);

    service->start();

    BLEAdvertising *advertising = BLEDevice::getAdvertising();
    advertising->addServiceUUID(SERVICE_UUID);
    server->getAdvertising()->start();
}

void loop() {
    pressButton.loop();
    releaseButton.loop();
}