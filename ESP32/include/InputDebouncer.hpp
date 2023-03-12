#ifndef INPUT_DEBOUNCER_H
#define INPUT_DEBOUNCER_H

#include <Arduino.h>

enum InputMode {
    Rising,
    Falling
};

class InputDebouncer {

private:
    int state;
    InputMode mode;
    uint8_t pin;
    unsigned long debounce;
    unsigned long actionTime;
    void (*callback)(unsigned long);

    bool isTriggered(int state);
    bool isChanged();
    int newState();
    int defaultState();

public:
    InputDebouncer(uint8_t pin, unsigned long debounce, InputMode mode);
    void setCallback(void (*callback)(unsigned long));
    void loop();
};

#endif