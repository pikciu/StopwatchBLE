#ifndef BLE_CONNECTION_H
#define BLE_CONNECTION_H

#include <BLEServer.h>

class BLEConnection: public BLEServerCallbacks {

private:
    bool status = false;

public:
    bool isConnected() {
        return status;
    }

    void onConnect(BLEServer* pServer) {
        status = true;
    }

    void onDisconnect(BLEServer* pServer) {
        status = false;
        pServer->getAdvertising()->start();
    }
};

#endif