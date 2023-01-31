
package game;


import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static game.GameModel.BLANK;

public class GameModelTest {
    public static GuiTest controller;
    public static GameModel gameModel;

    @BeforeClass
    public static void setUpClass() throws InterruptedException, IOException {
        FXTestUtils.launchApp(GameApplication.class);
        Thread.sleep(2000);
        controller = new GuiTest() {
            @Override
            protected Parent getRootNode() {
                return GameApplication.getStage().getScene().getRoot();
            }
        };

    }

    @Before
    public void before() {
        try {
            Field gameMap = GameController.class.getDeclaredField("gameMap");
            gameMap.setAccessible(true);
            GridPane gp = (GridPane) gameMap.get(GameApplication.getGameController());
            gameModel = new GameModel(gp.getRowConstraints().size(), gp.getColumnConstraints().size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void getRandomBlankPosition() {
        Assert.assertEquals(BLANK, gameModel.getMap().get(gameModel.getRandomBlankPosition()));
    }


    @Test
    public void readRankPlayers() {
        try {
            List<Player> players = gameModel.readRankPlayers();
            Assert.assertNotNull(players);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


    @Test
    public void getRankArr() {
        try {
            String[] players = gameModel.getRankArr();
            Assert.assertNotNull(players);
            Assert.assertEquals(10, players.length);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void setRankArr() {
        Random random = new Random();
        int i = random.nextInt(999);
        gameModel.setRankArr(new String[i]);
        Assert.assertEquals(i, gameModel.getRankArr().length);
    }


    @Test
    public void setPlayers() {
        Random random = new Random();
        int i = random.nextInt(9);
        List<Player>ps = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            ps.add(new Player());
        }
        gameModel.setPlayers(ps);
        Assert.assertEquals(ps, gameModel.getPlayers());
    }

    @Test
    public void getMapCols() {
        gameModel.setMapCols(32);
        Assert.assertEquals(32, gameModel.getMapCols());
    }

    @Test
    public void setMapCols() {
        Random random = new Random();
        int i = random.nextInt(32);
        gameModel.setMapCols(i);
        Assert.assertEquals(i, gameModel.getMapCols());
    }

    @Test
    public void getMapRows() {
        Assert.assertEquals(8, gameModel.getMapRows());
    }

    @Test
    public void setMapRows() {
        Random random = new Random();
        int i = random.nextInt(8);
        gameModel.setMapRows(i);
        Assert.assertEquals(i, gameModel.getMapRows());
    }


    @Test
    public void hasPlayer() {
        Assert.assertFalse(gameModel.hasPlayer(new Random().nextInt(gameModel.getMap().size())));
    }



    @Test
    public void hasObstacle() {
        Assert.assertTrue(gameModel.hasObstacle(new Random().nextInt(gameModel.getMap().size())));
    }

    @Test
    public void hasPlayerOrObstacle() {
        Assert.assertTrue(gameModel.hasPlayerOrObstacle(new Random().nextInt(gameModel.getMap().size())));
    }
}