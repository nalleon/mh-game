package es.ies.puerto.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author Nabil L. A.
 */
public class Hunter extends Thread {
    private String hunterName;
    private String position;
    private MapGame mapGame;
    private static long TIME_TO_CATCH = 20000;

    private boolean isDefeated = false;

    private int monsterCaught = 0;

    private List<String> failedPositons;
    /**
     * Default constructor of the class
     */
    public Hunter (){
        hunterName = "";
        position="0,0";
        mapGame = new MapGame();
        failedPositons = new ArrayList<>();
    }

    /**
     * Constructor of the class
     * @param hunterName
     */
    public Hunter(String hunterName, MapGame mapGame) {
        this.hunterName = hunterName;
        position = "0,0";
        this.mapGame = mapGame;
        failedPositons = new ArrayList<>();
    }


    /**
     * Getters/setters
     */
    public String getHunterName() {
        return hunterName;
    }

    public void setHunterName(String hunterName) {
        this.hunterName = hunterName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public MapGame getMapGame() {
        return mapGame;
    }

    public void setMapGame(MapGame mapGame) {
        this.mapGame = mapGame;
    }

    public boolean isDefeated() {
        return isDefeated;
    }

    public void setDefeated(boolean defeated) {
        isDefeated = defeated;
    }

    public int getMonsterCaught() {
        return monsterCaught;
    }

    public void setMonsterCaught(int monsterCaught) {
        this.monsterCaught = monsterCaught;
    }

    public List<String> getFailedPositons() {
        return failedPositons;
    }

    public void setFailedPositons(List<String> failedPositons) {
        this.failedPositons = failedPositons;
    }

    @Override
    public void run() {
        long initialTime = System.currentTimeMillis();
        long timePassed = 0;

        mapGame.addHunter(this, this.getPosition());

        while (!isDefeated() && !mapGame.getMonsters().isEmpty() && timePassed < TIME_TO_CATCH) {
            long endTime = System.currentTimeMillis();
            timePassed = (endTime - initialTime);

            if (timePassed >= TIME_TO_CATCH) {
                break;
            }


            if (this.isDefeated()) {
                break;
            }

            mapGame.moveHunter(this);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(hunterName + " interrupted");
            }
        }
        System.out.println(this.getHunterName() + " caught: " +  this.getMonsterCaught() + " monsters" );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hunter hunter = (Hunter) o;
        return Objects.equals(hunterName, hunter.hunterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hunterName);
    }

    @Override
    public String toString() {
        return "Hunter{" +
                "hunterName='" + hunterName + '\'' +
                ", position='" + position + '\'' +
                '}';
    }

}
