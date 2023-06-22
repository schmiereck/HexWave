package de.schmiereck.hexWave.service.hexGrid;

import de.schmiereck.hexWave.math.HexParticle;

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

    private final PartType partType;
    private final HexParticle hexParticle;

    private double energy;

    public Part(final PartType partType, final double energy, final int mass) {
        this.partType = partType;
        this.energy = energy;
        this.hexParticle = new HexParticle(mass);
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

    public HexParticle getHexParticle() {
        return this.hexParticle;
    }
}
