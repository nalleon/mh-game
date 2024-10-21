package es.ies.puerto.controller;

import es.ies.puerto.model.Hunter;
import es.ies.puerto.model.MapGame;
import es.ies.puerto.model.Monster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MonsterHunter {

    MapGame mapGame;
    List<Monster> monsterList;
    List<Hunter> hunterList;

    public MonsterHunter() {
        mapGame = new MapGame();
        monsterList = new CopyOnWriteArrayList<>();
        hunterList = new CopyOnWriteArrayList<>();
    }


    /**
     * Getters/setters
     */
    public MapGame getMapGame() {
        return mapGame;
    }

    public void setMapGame(MapGame mapGame) {
        this.mapGame = mapGame;
    }

    public List<Monster> getMonsterList() {
        return monsterList;
    }

    public void setMonsterList(List<Monster> monsterList) {
        this.monsterList = monsterList;
    }

    public List<Hunter> getHunterList() {
        return hunterList;
    }

    public void setHunterList(List<Hunter> hunterList) {
        this.hunterList = hunterList;
    }


    public static void main(String[] args) throws InterruptedException {
        MapGame mapGame = new MapGame(5);

        Hunter hunter1 = new Hunter("Hunter1", mapGame);
        Hunter hunter2 = new Hunter("Hunter2", mapGame);
        Monster monster1 = new Monster(1, "Monster1", mapGame);
        Monster monster2 = new Monster(2, "Monster2", mapGame);

        hunter1.setMapGame(mapGame);
        hunter2.setMapGame(mapGame);

        hunter1.setPosition(mapGame.generateLocations());
        hunter2.setPosition(mapGame.generateLocations());
        monster1.setPosition(mapGame.generateLocations());
        monster2.setPosition(mapGame.generateLocations());

        List<Monster> monsterList = new ArrayList<>(Arrays.asList(monster1, monster2));
        List<Hunter> hunterList = new ArrayList<>(Arrays.asList(hunter1, hunter2));

        MonsterHunter monsterHunterGame = new MonsterHunter();
        monsterHunterGame.setMapGame(mapGame);
        monsterHunterGame.setHunterList(hunterList);
        monsterHunterGame.setMonsterList(monsterList);

        for (Hunter hunter : hunterList) {
            monsterHunterGame.getMapGame().addHunter(hunter, hunter.getPosition());
        }
        for (Monster monster : monsterList) {
            monsterHunterGame.getMapGame().addMonster(monster, monster.getPosition());
        }

        mapGame.addEvents(mapGame.getTypeTraps(), mapGame.getSize()/2);


        Thread hunter1Thread = new Thread(hunter1);
        Thread hunter2Thread = new Thread(hunter2);
        Thread monster1Thread = new Thread(monster1);
        Thread monster2Thread = new Thread(monster2);

        hunter1Thread.start();
        hunter2Thread.start();
        monster1Thread.start();
        monster2Thread.start();

    }

}
