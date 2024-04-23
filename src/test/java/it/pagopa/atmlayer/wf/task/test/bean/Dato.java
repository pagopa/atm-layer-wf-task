package it.pagopa.atmlayer.wf.task.test.bean;

import java.util.ArrayList;

public class Dato {
    String paragrafo;

    ArrayList<String> circuits;

    /**
     * Return circuits value or reference.
     *
     * @return circuits value or reference.
     */
    public ArrayList<String> getCircuits() {
        return circuits;
    }

    /**
     * Set circuits value or reference.
     *
     * @param circuits Value to set.
     */
    public void setCircuits(ArrayList<String> circuits) {
        this.circuits = circuits;
    }

    public String getParagrafo() {
        return paragrafo;
    }

    public void setParagrafo(String paragrafo) {
        this.paragrafo = paragrafo;
    }

}
