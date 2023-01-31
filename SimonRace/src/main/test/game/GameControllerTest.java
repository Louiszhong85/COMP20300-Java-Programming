package game;

import javafx.scene.Parent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.utils.FXTestUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;

public class GameControllerTest {
    public static GuiTest controller;
    public static GameController gameController;

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
        gameController = GameApplication.getGameController();
    }

    @Test
    public void getGameStatus() {
        Assert.assertEquals(GameController.GameStatus.GAME_WAIT, gameController.getGameStatus());
    }

    @Test
    public void setGameStatus() {
        gameController.setGameStatus(GameController.GameStatus.GAMING);
        Assert.assertEquals(GameController.GameStatus.GAMING, gameController.getGameStatus());
    }

    @Test
    public void getNextStepObject() {
        Player player = new Player();
        // 设置玩家在起点位置 此时地图空无一物，因此下一个目标应该为 空白格子 BLANK
        //Set the player at the starting position The map is empty at this point, so the next target should be a blank grid BLANK
        player.setPosition(0);
        player.setDirection(GameModel.Direction.FORWARD);
        // 保存玩家移动方向 传入 FORWARD 不改变玩家方向
        //Save player movement direction Pass in FORWARD does not change player direction
        Assert.assertEquals(GameModel.BLANK, gameController.getNextStepObject(player, GameModel.Direction.FORWARD));
    }

    @Test
    public void getPosition() {
        int rows = gameController.getGameModel().getMapRows();
        int cols = gameController.getGameModel().getMapCols();
        Random random = new Random();
        int r = random.nextInt(rows), c = random.nextInt(cols);
        int target = r * cols + c;
        Assert.assertEquals(target, gameController.getPosition(r, c));
    }

    @Test
    public void getCurPlayer() {
        // 原本没有用户数据，添加两个玩家，并指定当前用户索引为0，应该返回第一个玩家
        // Originally there was no user data, adding two players and specifying the current user index as 0 should return the first player
        GameModel gameModel = gameController.getGameModel();
        Player p1 = new Player();
        Player p2 = new Player();
        gameModel.getPlayers().addAll(Arrays.asList(p1, p2));
        gameModel.setCurPlayerIndex(0);
        try {
            Method getCurPlayer = GameController.class.getDeclaredMethod("getCurPlayer");
            getCurPlayer.setAccessible(true);
            Object invoke = getCurPlayer.invoke(gameController);
            Assert.assertEquals(p1, invoke);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test

    public void getRow() {
        int rows = gameController.getGameModel().getMapRows();
        int cols = gameController.getGameModel().getMapCols();
        Random random = new Random();
        int r = random.nextInt(rows), c = random.nextInt(cols);
        int target = r * cols + c;
        Assert.assertEquals(r, gameController.getRow(target));
    }

    @Test
    public void getCol() {
        int rows = gameController.getGameModel().getMapRows();
        int cols = gameController.getGameModel().getMapCols();
        Random random = new Random();
        int r = random.nextInt(rows), c = random.nextInt(cols);
        int target = r * cols + c;
        Assert.assertEquals(c, gameController.getCol(target));
    }
}