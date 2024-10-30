package es.ies.puerto.model;

import java.util.Objects;
import es.ies.puerto.model.Cave;

/**
 * @author Nabil L. A.
 */
public class Monster extends Thread {

    private int monsterId;
    private String monsterName;
    private String position;
    private boolean captured;
    private MapGame mapGame;
    private static long TIME_TO_ESCAPE = 20000;
    private int huntersDefeated = 0;
    private Cave cave;

    /**
     * Default constructor of the class
     */
    public Monster() {
        monsterId = 1;
        monsterName = "";
        position = "0,0";
        captured = false;
        mapGame = new MapGame();
        cave = new Cave();
    }

    public Monster(int monsterId, String monsterName, MapGame mapGame) {
        this.monsterId = monsterId;
        this.monsterName = monsterName;
        this.position = "0,0";
        this.captured = false;
        this.mapGame = mapGame;
        this.cave = new Cave();
    }


    @Override
    public void run() {
        long initialTime = System.currentTimeMillis();
        long timePassed = 0;

        mapGame.addMonster(this, this.getPosition());

        while (!isCaptured() && !mapGame.getHunters().isEmpty() && timePassed < TIME_TO_ESCAPE) {
            long endTime = System.currentTimeMillis();
            timePassed = (endTime - initialTime);

                if (timePassed >= TIME_TO_ESCAPE) {
                    break;
                }

                if (this.isCaptured()){
                    break;
                }

                mapGame.moveMonster(this);

                if (!cave.isOccupied()){
                    try {
                        cave.enterCave(this, mapGame);

                        Thread.sleep(4000);

                        cave.exitCave(this, mapGame);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }


                if (timePassed >= 10000 && timePassed < TIME_TO_ESCAPE) {
                    if (!this.isCaptured()) {
                        this.setCaptured(true);
                        mapGame.monsterFleeFromMap(this);
                        System.out.println(this.getMonsterName() + " has fled the field!");
                    }
                }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(this.getMonsterName() + " interrupted");
            }
        }
        System.out.println(this.getMonsterName() + " defeated: " + this.getHuntersDefeated() + " hunters");
    }



    /**
     * Getters and setters
     */

    public int getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(int monsterId) {
        this.monsterId = monsterId;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    public MapGame getMapGame() {
        return mapGame;
    }

    public void setMapGame(MapGame mapGame) {
        this.mapGame = mapGame;
    }

    public int getHuntersDefeated() {
        return huntersDefeated;
    }

    public void setHuntersDefeated(int huntersDefeated) {
        this.huntersDefeated = huntersDefeated;
    }

    public Cave getCave() {
        return cave;
    }

    public void setCave(Cave cave) {
        this.cave = cave;
    }

    /**
     * Method to string
     */

    @Override
    public String toString() {
        return "Monster{" +
                "monsterId=" + monsterId +
                ", monsterName='" + monsterName + '\'' +
                ", position='" + position + '\'' +
                ", captured=" + captured +
                '}';
    }

    /**
     * Equals and hashcode
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Monster monster = (Monster) o;
        return monsterId == monster.monsterId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(monsterId);
    }
}
