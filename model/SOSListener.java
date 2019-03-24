package model;

import simulation.Rescuable;

public interface SOSListener {
    void receiveSOSCall(Rescuable r);
}
