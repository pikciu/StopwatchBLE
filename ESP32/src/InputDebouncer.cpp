#include "InputDebouncer.hpp"

InputDebouncer::InputDebouncer(uint8_t pin, unsigned long debounce, InputMode mode) {
    this->pin = pin;
    this->debounce = debounce;
    this->mode = mode;
    this->actionTime = 0;
    this->state = defaultState();
}

void InputDebouncer::setCallback(void (*callback)(unsigned long)) {
    this->callback = callback;
}

void InputDebouncer::loop() {
    unsigned long currentTime = millis();
    if (isTriggered(digitalRead(pin))) {
        if (isChanged()) {
            callback(currentTime);
            state = newState();
        }
        actionTime = currentTime + debounce;
    } else if (actionTime < currentTime) {
        state = defaultState();
    }
}

bool InputDebouncer::isTriggered(int state) {
    if (mode == Rising) {
        return state == HIGH;
    } else {
        return state == LOW;
    }
}

bool InputDebouncer::isChanged() {
    if (mode == Rising) {
        return state == LOW;
    } else {
        return state == HIGH;
    }
}

int InputDebouncer::newState() {
    if (mode == Rising) {
        return HIGH;
    } else {
        return LOW;
    }
}

int InputDebouncer::defaultState() {
    if (mode == Rising) {
        return LOW;
    } else {
        return HIGH;
    }
}
