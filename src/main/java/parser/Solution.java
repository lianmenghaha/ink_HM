package parser;

import shape.Layer;

import java.util.ArrayList;

public class Solution {
    private ArrayList<Layer> layers;
    private int nrLayer;

    private double totDryScore;
    private int totOriPrintScore;
    private int totPrintScore;

    public Solution(int totOriPrintScore) {
        this.totOriPrintScore = totOriPrintScore;
        this.layers = new ArrayList<>();
    }


    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public void addToLayers(Layer layer) {
        this.layers.add(layer);
    }

    public int getNrLayer() {
        return nrLayer;
    }

    public void setNrLayer(int nrLayer) {
        this.nrLayer = nrLayer;
    }

    public double getTotDryScore() {
        return totDryScore;
    }

    public void setTotDryScore(double totDryScore) {
        this.totDryScore = totDryScore;
    }

    public double getTotOriPrintScore() {
        return totOriPrintScore;
    }

    public void setTotOriPrintScore(int totOriPrintScore) {
        this.totOriPrintScore = totOriPrintScore;
    }

    public double getTotPrintScore() {
        return totPrintScore;
    }

    public void setTotPrintScore(int totPrintScore) {
        this.totPrintScore = totPrintScore;
    }
}
