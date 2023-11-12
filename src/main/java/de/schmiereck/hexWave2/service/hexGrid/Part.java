package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.HexParticle;

import java.io.Serializable;

public class Part implements Serializable {
    public enum PartType {
        Nothing,
        Air,
        Water,
        Wallpaper,
        Wall,
        Life,
        Sun
    }

    private final Particle particle;
    private final PartType partType;
    private final HexParticle hexParticle;

    private double energy;
    private int probability;
    private int count;
    private boolean propagate;

    public Part(final Particle particle, final PartType partType, final double energy, final int mass, final int probability, final int count) {
        this.particle = particle;
        this.partType = partType;
        this.energy = energy;
        this.probability = probability;
        this.count = count;
        this.hexParticle = new HexParticle(mass);
    }

    public Particle getParticle() {
        return this.particle;
    }

    public PartType getPartType() {
        return this.partType;
    }

    public double getEnergy() {
        return this.energy;
    }

    public void setEnergy(final double energy) {
        this.energy = energy;
    }

    public void addEnergy(final double energy) {
        this.energy += energy;
    }

    public void subEnergy(final double energy) {
        this.energy -= energy;
    }

    public void setProbability(final int probability) {
        this.probability = probability;
    }

    public int getProbability() {
        return this.probability;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public boolean getPropagate() {
        return this.propagate;
    }

    public void setPropagate(boolean propagate) {
        this.propagate = propagate;
    }

    public HexParticle getHexParticle() {
        return this.hexParticle;
    }
}
