package game;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static game.Player.*;


public class PlayerTest {
    private Player player;

    @Before
    public void before() {
        player = new Player();
    }

    @Test
    public void isAlive() {
        player.setBlood(1);
        player.setStatus(Player.NORMAL);
        Assert.assertTrue(player.isAlive());

        player.setBlood(1);
        player.setStatus(DIED);
        Assert.assertFalse(player.isAlive());

        player.setBlood(-1);
        player.setStatus(Player.NORMAL);
        Assert.assertFalse(player.isAlive());
    }

    @Test
    public void updateBlood() {
        int b = player.getBlood();
        for (int i = 0; i < 99; i++) {
            Random random = new Random();
            int addB = random.nextInt(9999) - 5000;
            b = b + addB;
            player.updateBlood(addB);
            if (player.getBlood() <= 0) {
                Assert.assertEquals(DIED, player.getStatus());
            } else {
                Assert.assertEquals(b, player.getBlood());
            }
        }
    }

    @Test
    public void isSuspend() {
        player.setStatus(NORMAL);
        Assert.assertFalse(player.isSuspend());

        player.setStatus(DIED);
        Assert.assertFalse(player.isSuspend());

        player.setStatus(SUSPEND);
        Assert.assertTrue(player.isSuspend());

        for (int i = 0; i < 99; i++) {
            player.setStatus(SUSPEND + "" + i);
            Assert.assertTrue(player.isSuspend());
        }
    }

    @Test
    public void updateSuspendRound() {

        player.setStatus(DIED);
        player.updateSuspendRound(9);
        Assert.assertEquals(0, player.getSuspendRound());
        player.setStatus(SUSPEND);
        int r = player.getSuspendRound();
        player.updateSuspendRound(99);
        Assert.assertEquals(r + 99, player.getSuspendRound());


    }

    @Test
    public void getSuspendRound() {
        player.setStatus(DIED);
        player.updateSuspendRound(9);
        Assert.assertEquals(0, player.getSuspendRound());
        player.setStatus(SUSPEND);
        int r = player.getSuspendRound();
        player.updateSuspendRound(99);
        Assert.assertEquals(r + 99, player.getSuspendRound());
    }

    @Test
    public void testToString() {
        for (int i = 0; i < 55; i++) {
            player = new Player("" + i);
            String s = player.toString();
            Assert.assertEquals("Player{" +
                    "\n\tname='" + player.getName() + '\'' +
                    ", \n\tblood=" + player.getBlood() +
                    ", \n\tstatus='" + player.getStatus() + '\'' +
                    ", \n\tposition=" + player.getPosition() +
                    ", \n\tdirection=" + player.getDirection() +
                    ", \n\trank=" + player.getRank() +
                    ", \n\tsteps=" + player.getSteps() +
                    "\n}", s);
        }
    }

    @Test
    public void compareTo() {
        Player a = new Player(), b = new Player();

        Random r = new Random();
        for (int i = 0; i < 55; i++) {
            a.setBlood(r.nextInt( 5) + 1);
            b.setBlood(r.nextInt(5) + 1);
            a.setSteps(r.nextInt(10000) + 1);
            b.setSteps(r.nextInt(10000) + 1);

            int cp = a.getSteps() - b.getSteps();
            if (cp == 0) {
                cp = a.getBlood() - b.getBlood();
            }
            Assert.assertEquals(cp, a.compareTo(b));
        }
    }
}