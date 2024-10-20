package es.ies.puerto.model;

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


    /**
     * Default constructor of the class
     */
    public Hunter (){
        hunterName = "";
        position="0,0";
        mapGame = new MapGame();
    }

    /**
     * Constructor of the class
     * @param hunterName
     */
    public Hunter(String hunterName, MapGame mapGame) {
        this.hunterName = hunterName;
        position = "0,0";
        this.mapGame = mapGame;
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

    @Override
    public void run() {
        long initialTime = System.currentTimeMillis();
        long timePassed = 0;

        int monsterCaught = 0;
        boolean isOver = false;

        mapGame.addHunter(this, this.getPosition());

        while (!isOver && !mapGame.getMonsters().isEmpty() && timePassed < TIME_TO_CATCH) {
            mapGame.moveHunter(this);

            long endTime = System.currentTimeMillis();
            timePassed = (endTime - initialTime);

            if (timePassed >= TIME_TO_CATCH){
                System.out.println("Time is up!");
                System.out.println(hunterName + " caught " + monsterCaught + " monsters");
                isOver = true;
            }


            for (Monster monster : mapGame.getMonsters()) {
                if (monster.getPosition().equals(this.getPosition()) && !monster.isCaptured()) {
                    monster.setCaptured(true);
                    System.out.println(this.getHunterName() + " caught " + monster.getMonsterName());
                    mapGame.removeMonster(monster, monster.getPosition());
                    mapGame.getMonsters().remove(monster);
                    monsterCaught++;
                    break;
                }
            }

            for (Monster monster : mapGame.getMonsters()) {
                if (!monster.isCaptured() && timePassed >= 10000 && timePassed < TIME_TO_CATCH) {
                    mapGame.removeMonster(monster, monster.getPosition());
                    System.out.println(monster.getMonsterName() + " has fled the field!");
                }
            }



            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(hunterName + " interrupted");
            }
        }
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
