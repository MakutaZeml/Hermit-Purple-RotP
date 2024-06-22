package com.zeml.rotp_zhp.util;

import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.general.GeneralUtil;
import com.github.standobyte.jojo.util.general.GraphAdjacencyList;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineBarrierEntity;
import com.zeml.rotp_zhp.entity.stand.stands.HermitPurpleEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HPBarriersNet {
    private Map<HPVineBarrierEntity, ShootingPoints> placedBarriers = new HashMap<>();
    private GraphAdjacencyList<Vector3d> closePoints = new GraphAdjacencyList<>();
    private double lastShotGap = -1;
    private boolean canShoot;

    public void tick() {
        Iterator<Map.Entry<HPVineBarrierEntity, ShootingPoints>> iter = placedBarriers.entrySet().iterator();
        while (iter.hasNext()) {
            HPVineBarrierEntity barrier = iter.next().getKey();
            if (!barrier.isAlive() || barrier.wasRipped()) {
                onRemoved(barrier);
                iter.remove();
            }
        }
        canShoot = true;
    }

    public void add(HPVineBarrierEntity barrier) {
        placedBarriers.put(barrier, generateShootingPoints(barrier));
        onUpdate();
    }

    private static final double SHOOTING_POINTS_GAP = 8;
    private ShootingPoints generateShootingPoints(HPVineBarrierEntity entity) {
        Vector3d posA = entity.position();
        Vector3d posB = entity.getOriginPoint(1.0F);
        Vector3d vecAToB = posB.subtract(posA);

        List<Vector3d> shootingPoints = new ArrayList<>();
        if (vecAToB.lengthSqr() <= SHOOTING_POINTS_GAP * SHOOTING_POINTS_GAP * 4) {
            shootingPoints.add(posA.add(vecAToB.scale(0.5)));
        }
        else {
            int steps = MathHelper.floor(vecAToB.length() / SHOOTING_POINTS_GAP);
            Vector3d nextPoint = posA;
            Vector3d stepVec = vecAToB.normalize().scale(SHOOTING_POINTS_GAP);
            for (int i = 0; i < steps; i++) {
                nextPoint = nextPoint.add(stepVec);
                shootingPoints.add(nextPoint);
            }
        }

        return new ShootingPoints(shootingPoints);
    }

    private void onRemoved(HPVineBarrierEntity barrier) {
        onUpdate();
    }

    private void onUpdate() {
        if (lastShotGap > -1) {
            closePoints.clear();
            lastShotGap = -1;
        }
    }

    public int getSize() {
        return placedBarriers.size();
    }

    public void shootStringsFromBarriers(IStandPower standPower, HermitPurpleEntity stand,
                                         Vector3d targetPos, int tick, double maxStrings, float staminaPerString, double minGap, boolean breakBlocks) {
        if (!canShoot) return;
        List<Vector3d> shootingPoints = placedBarriers.values().stream().flatMap(points ->
                points.shootingPoints.stream()).collect(Collectors.toCollection(LinkedList::new));
        if (lastShotGap != minGap) {
            closePoints.create((pointA, pointB) -> pointA.distanceToSqr(pointB) < minGap * minGap, shootingPoints);
            lastShotGap = minGap;
        }

        Set<Vector3d> pointsToShootThisTick = new HashSet<>();
        GeneralUtil.doFractionTimes(() -> {
            Vector3d point = shootingPoints.stream().min(Comparator.comparingDouble(p -> p.distanceToSqr(targetPos))).get();
            pointsToShootThisTick.add(point);
            shootingPoints.remove(point);
            closePoints.getAllAdjacent(point).forEach(closePoint -> shootingPoints.remove(closePoint));
        }, maxStrings, () -> shootingPoints.isEmpty());

        canShoot = false;
    }

    public Stream<Vector3d> wasRippedAt() {
        return placedBarriers.keySet().stream()
                .flatMap(barrier -> barrier.wasRippedAt().map(point -> Stream.of(point)).orElse(Stream.empty()));
    }

    public enum PointsChoice {
        RANDOM,
        CLOSEST
    }

    private class ShootingPoints {
        private final List<Vector3d> shootingPoints;

        private ShootingPoints(List<Vector3d> shootingPoints) {
            this.shootingPoints = shootingPoints;
        }
    }
}
