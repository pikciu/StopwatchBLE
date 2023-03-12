import UIKit

final class ViewController: UIViewController, StopwatchDelegate {
    
    enum State {
        struct Started {
            let time = Date()
            let timestamp: UInt32
        }
        
        struct Stop {
            let startTime: Date
            let startTimestamp: UInt32
            let stopTimestamp: UInt32
            let stopTime = Date()
        }
        
        case ready
        case started(Started)
        case stop(Stop)
        
        mutating func next(with timestamp: UInt32) {
            switch self {
            case .ready:
                self = .started(Started(timestamp: timestamp))
            case .started(let started):
                self = .stop(Stop(startTime: started.time, startTimestamp: started.timestamp, stopTimestamp: timestamp))
            case .stop:
                self = .ready
            }
        }
    }
    
    var timer: Timer? {
        didSet {
            oldValue?.invalidate()
        }
    }
    let label = UILabel()
    let stopwatch = Stopwatch()
    var state = State.ready {
        didSet {
            updateState()
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupLabel()
        stopwatch.delegate = self
        display()
    }
    
    func stopwatch(_ stopwatch: Stopwatch, didReceiveTimestamp timestamp: UInt32) {
        state.next(with: timestamp)
    }
    
    func updateState() {
        configureTimer()
        display()
    }
    
    func display() {
        switch state {
        case .ready, .started:
            setText(with: 0)
        case .stop(let stop):
            let timeDiff = stop.stopTime.timeIntervalSince(stop.startTime)
            print(timeDiff)
            let timestampDiff = stop.stopTimestamp - stop.startTimestamp
            print(timestampDiff)
            setText(with: Double(timestampDiff) / 1000.0)
        }
    }
    
    func setupLabel() {
        label.font = .monospacedDigitSystemFont(ofSize: 140, weight: .semibold)
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textAlignment = .center
        view.addSubview(label)
        
        NSLayoutConstraint.activate([
            label.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            label.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor),
            label.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor),
            label.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor),
        ])
    }
    
    func configureTimer() {
        if case .started(let started) = state {
            timer = Timer.scheduledTimer(withTimeInterval: 0.01, repeats: true) { [unowned self] _ in
                let diff = Date().timeIntervalSince(started.time)
                setText(with: diff)
            }
        } else {
            timer = nil
        }
    }
    
    private func setText(with time: Double) {
        label.text = String(format: "%.2f", time)
    }
}
