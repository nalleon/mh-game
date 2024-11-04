package es.ies.puerto.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilities.TestUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HunterTest extends TestUtilities {
    private Hunter hunter;
    private MapGame mockMapGame;

    @BeforeEach
    public void beforeEach() {
        mockMapGame = new MapGame(5);
        hunter = new Hunter();
        hunter = new Hunter("HunterName", mockMapGame);

       // when(mockMapGame.getMonsters()).thenReturn(new ArrayList<>(List.of(new Monster(1, "Monster1", mockMapGame))));
       // when(mockMapGame.nearCave(hunter)).thenReturn(false);
    }

    @Test
    public void getSetTest() {
        hunter.setHunterName("HunterNameUpdate");
        Assertions.assertEquals("HunterNameUpdate", hunter.getHunterName(), MESSAGE_ERROR);

        hunter.setPosition("2,3");
        Assertions.assertEquals("2,3", hunter.getPosition(), MESSAGE_ERROR);

        mockMapGame = new MapGame(6);
        hunter.setMapGame(mockMapGame);
        Assertions.assertEquals(mockMapGame, hunter.getMapGame(), MESSAGE_ERROR);


        Assertions.assertFalse(hunter.isDefeated(), MESSAGE_ERROR);
        hunter.setDefeated(true);
        Assertions.assertTrue(hunter.isDefeated(), MESSAGE_ERROR);

        Assertions.assertEquals(0, hunter.getMonsterCaught(), MESSAGE_ERROR);
        hunter.setMonsterCaught(3);
        Assertions.assertEquals(3, hunter.getMonsterCaught(), MESSAGE_ERROR);

        List<String> failedPositions = new ArrayList<>(Arrays.asList("0,0"));
        hunter.setFailedPositons(failedPositions);
        Assertions.assertEquals(failedPositions, hunter.getFailedPositons(), MESSAGE_ERROR);

        Assertions.assertTrue(hunter.getFailedPositons().contains("0,0"), MESSAGE_ERROR);

        Cave cave = new Cave();
        hunter.setCave(cave);
        Assertions.assertEquals(cave, hunter.getCave(), MESSAGE_ERROR);
    }


    @Test
    public void equalsTest() {
        Hunter equalsHunter = new Hunter("HunterName", mockMapGame);
        Hunter notEqualsHunter = new Hunter("Hunter2", mockMapGame);

        Assertions.assertEquals(hunter, hunter, MESSAGE_ERROR);
        Assertions.assertEquals(hunter, equalsHunter, MESSAGE_ERROR);
        Assertions.assertEquals(hunter.hashCode(), equalsHunter.hashCode(), MESSAGE_ERROR);
        Assertions.assertNotEquals(hunter, notEqualsHunter, MESSAGE_ERROR);
        Assertions.assertNotEquals(hunter.hashCode(), notEqualsHunter.hashCode(), MESSAGE_ERROR);
        Assertions.assertNotEquals(hunter, null, MESSAGE_ERROR);
        Assertions.assertNotEquals(hunter, "string", MESSAGE_ERROR);
    }


    @Test
    public void toStringTest() {
        String expectedString = "Hunter{hunterName='HunterName', position='0,0'}";
        Assertions.assertEquals(expectedString, hunter.toString(), MESSAGE_ERROR);
    }

    //@Test
    public void runTest() throws InterruptedException {
        hunter.setMonsterCaught(0);
        hunter.setDefeated(false);

        hunter.run();

        verify(mockMapGame, times(1)).
                addHunter(hunter, hunter.getPosition());

        verify(mockMapGame, atLeastOnce()).moveHunter(hunter);


        assertEquals(1, hunter.getMonsterCaught());
    }
}
