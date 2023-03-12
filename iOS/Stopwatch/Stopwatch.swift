import CoreBluetooth

protocol StopwatchDelegate: AnyObject {
    func stopwatch(_ stopwatch: Stopwatch, didReceiveTimestamp timestamp: UInt32)
}

final class Stopwatch: NSObject, CBCentralManagerDelegate, CBPeripheralDelegate {
    
    private let serviceUUID = CBUUID(string: "a129eaa4-28f6-4a61-a2b7-cb150dfcb92e")
    private let characteristicUUID = CBUUID(string: "e5881cf2-7b51-4dd5-a1e4-3514d2fc3235")
    private var manager: CBCentralManager!
    private var connectedPeripheral: CBPeripheral?
    
    weak var delegate: StopwatchDelegate?
    
    override init() {
        super.init()
        manager = CBCentralManager(delegate: self, queue: nil)
    }
    
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        debugPrint("centralManagerDidUpdateState")
        if case .poweredOn = central.state {
            manager.scanForPeripherals(withServices: [serviceUUID])
        } else {
            debugPrint(central.state)
        }
    }
    
    func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
        debugPrint("didDiscover")
        connectedPeripheral = peripheral
        peripheral.delegate = self
        central.connect(peripheral)
        central.stopScan()
    }
    
    func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        debugPrint("didConnect")
        peripheral.discoverServices([serviceUUID])
    }
    
    func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        debugPrint("didDisconnectPeripheral")
    }
    
    func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        debugPrint("didFailToConnect")
    }
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        if let error = error {
            debugPrint(error)
        } else if let services = peripheral.services {
            discoverCharacteristic(peripheral: peripheral, services: services)
        } else {
            debugPrint("no services")
        }
    }
    
    private func discoverCharacteristic(peripheral: CBPeripheral, services: [CBService]) {
        let service = services.first { $0.uuid == serviceUUID }
        discoverCharacteristic(peripheral: peripheral, service: service)
    }
    
    private func discoverCharacteristic(peripheral: CBPeripheral, service: CBService?) {
        debugPrint("discoverCharacteristic")
        guard let service = service else {
            return
        }
        peripheral.discoverCharacteristics([characteristicUUID], for: service)
    }
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        debugPrint("didDiscoverCharacteristicsFor")
        let characteristic = service.characteristics?.first { $0.uuid == characteristicUUID }
        if let characteristic = characteristic {
            peripheral.setNotifyValue(true, for: characteristic)
        }
    }
    
    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        if let data = characteristic.value {
            let value = data.withUnsafeBytes { $0.load(as: UInt32.self) }
            delegate?.stopwatch(self, didReceiveTimestamp: value)
        } else {
            debugPrint("no data")
        }
    }
}
