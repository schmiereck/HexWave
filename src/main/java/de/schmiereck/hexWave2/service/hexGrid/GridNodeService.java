package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.ProbabilityService;
import de.schmiereck.hexWave2.math.ProbabilityVector;

import java.util.List;
import java.util.Optional;

public final class GridNodeService {
    private GridNodeService() {
    }

    static List<Part> searchParticlePartListByParticle(final GridNode gridNode, final int cellArrPos, final Particle particle) {
        final List<Part> partList = gridNode.getPartList(cellArrPos);

        return partList.stream().
                filter(part ->
                        (part.getParticle() == particle)).
                toList();
    }

    static List<Part> searchParticlePartListByParticleAndTypes(final GridNode gridNode, final int cellArrPos, final Particle particle,
                                                               final Particle.PartType partType, final Particle.PartSubType partSubType) {
        final List<Part> partList = gridNode.getPartList(cellArrPos);

        return partList.stream().
                filter(part ->
                        (part.getParticle() == particle) &&
                                (part.getParticle().getPartType() == partType) &&
                                (part.getParticle().getPartSubType() == partSubType)).
                toList();
    }

    static List<Part> searchParticlePartListByTypeAndSubType(final GridNode gridNode, final int cellArrPos, final Particle.PartType partType, final Particle.PartSubType partSubType) {
        final List<Part> partList = gridNode.getPartList(cellArrPos);

        return partList.stream().
                filter(part ->
                        (part.getParticle().getPartType() == partType) &&
                                (part.getParticle().getPartSubType() == partSubType)).
                toList();
    }

    static Optional<Part> searchParticlePart(final GridNode gridNode, final int cellArrPos, final Particle particle, final ProbabilityVector probabilityVector) {
        final List<Part> partList = gridNode.getPartList(cellArrPos);

        return partList.stream().
                filter(part ->
                        (part.getParticle() == particle) &&
                                ProbabilityService.compare(part.impulseProbabilityVector, probabilityVector)).
                findFirst();
    }
}
