package es.ies.puerto.model;

import java.util.Objects;

/**
 * @author Nabil L. A.
 */
public class Monster {

    private int monsterId;
    private String monsterName;
    private String position;
    private boolean captured;

    /**
     * Default constructor of the class
     */
    public Monster() {
        monsterId = 1;
        monsterName = "";
        position = "";
        captured = false;
    }

    public Monster(int monsterId, String monsterName) {
        this.monsterId = monsterId;
        this.monsterName = monsterName;
        position = "";
        captured = false;
    }

    /**
     * Constructor of the class
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
     * @param monsterId of the monster
     * @param monsterName of the monster
     * @param position of the monster
     * @param captured of the monster
     */
    public Monster(int monsterId, String monsterName, String position, boolean captured) {
        this.monsterId = monsterId;
        this.monsterName = monsterName;
        this.position = position;
        this.captured = captured;
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
