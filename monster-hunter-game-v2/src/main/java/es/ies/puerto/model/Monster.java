package es.ies.puerto.model;

import java.util.Objects;

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

    /**
     * Default constructor of the class
     */
    public Monster() {
        monsterId = 1;
        monsterName = "";
        position = "0,0";
        captured = false;
        mapGame = new MapGame();
    }

    public Monster(int monsterId, String monsterName, MapGame mapGame) {
        this.monsterId = monsterId;
        this.monsterName = monsterName;
        position = "0,0";
        captured = false;
        this.mapGame = mapGame;
    }

    /**
     * Constructor of the class
     *
     * @param monsterId of the monster
     */
    public Monster(int monsterId) {
        this.monsterId = monsterId;
        monsterName = "";
        position = "";
        captured = false;
    }

    /**
     * Constructor of the class
     *
     * @param monsterId   of the monster
     * @param monsterName of the monster
     * @param position    of the monster
     * @param captured    of the monster
     */
    public Monster(int monsterId, String monsterName, String position, boolean captured) {
        this.monsterId = monsterId;
        this.monsterName = monsterName;
        this.position = position;
        this.captured = captured;
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
        System.out.println();
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
