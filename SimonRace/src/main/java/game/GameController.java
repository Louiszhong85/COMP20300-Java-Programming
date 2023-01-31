package game;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * This class mainly implements the controls of the game and displaying game date to Java FX to shown player.
 * For Example：
 * Game start
 * Enable dice, user display area, disable add game controls
 * Initialize map obstacles
 * Refresh the map interface
 * Next turn
 * Execute action (after dice throw, or execute pause state)
 * Get the contents of the previous cell of the player
 * Display log information
 * Get the Object of the next square in the specified direction relative to the current character
 * Get the coordinates of the point to be moved to
 * End of the game, show the end of the game box, disable other controls
 * Update map panel based on gameModel data
 * Clear the map panel
 * Enable or disable all dice related controls
 * Enable the user information box
 * Enable or disable all controls for adding players
 * Set track selectable range (based on whether there is a player in the first column)
 * Initialize the add user panel
 * Get the selectable tracks Determine based on the user's row number
 * Initialize the dice interface
 */
public class GameController implements Initializable {

    @FXML
    private AnchorPane gameLevelPane;
    @FXML
    private Button okLevelButton;
    @FXML
    private TextField mapWidthField;
    @FXML
    private TextField obstacleProbabilityField;
    @FXML
    private GridPane playersGrid;
    @FXML
    private Button execButton;
    @FXML
    private AnchorPane operationPane;
    @FXML
    private Button nextRoundButton;
    @FXML
    private Button restartGameButton;
    @FXML
    private Label gameLogLabel;
    @FXML
    private AnchorPane playerInfoPane;
    @FXML
    private AnchorPane numberDicePane;
    @FXML
    private AnchorPane directionDicePane;
    @FXML
    private AnchorPane addPlayerPane;
    @FXML
    private TextField addPlayerNameField;
    @FXML
    private ComboBox<Integer> addPlayerTrackComboBox;
    @FXML
    private Button addPlayerButton;
    @FXML
    private Button startGameButton;
    @FXML
    private Button stepsDiceButton;
    @FXML
    private Label stepsLabel;
    @FXML
    private Button directionDiceButton;
    @FXML
    private Label directionLabel;
    @FXML
    private Label playerBloodLabel;
    @FXML
    private Label playerStatusLabel;
    @FXML
    private Label playerPositionLabel;
    @FXML
    private Label playerNameLabel;
    @FXML
    private Label playerRankLabel;
    @FXML
    private Label playerStepsLabel;
    @FXML
    private VBox rankPane;
    @FXML
    private GridPane gameMap;

    /**
     * game Status Label
     */
    public enum GameStatus {
        GAME_WAIT,
        GAMING,
        GAME_OVER,
        GAME_START
    }

    private GameStatus gameStatus;
    private GameModel gameModel;

    public GameModel getGameModel() {
        return gameModel;
    }

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;

        switch (gameStatus) {
            case GAME_WAIT:
                gameWait();
                break;
            case GAME_START:
                gameStart();
                break;
            case GAMING:
                gaming();
                break;
            case GAME_OVER:
                gameOver();
                break;
            default:
                break;
        }
    }

    /**
     * 游戏开始
     * 启用骰子,用户显示区域，禁用添加游戏控件
     * 初始化地图障碍物
     * Game start
     * Enable dice, user display area, disable add game controls
     * Initialize map obstacles
     */
    private void gameStart() {
        enableAddPlayerComponents(false);
        enableDiceComponents(true);
        enablePlayerInfoComponents(true);

        // 设置初始数据
        //Set initial data
        gameModel.beginGame();

        // 刷新地图界面
        // update map GUI
        updateMapPane();

        updateRankPane();

        setGameStatus(GameStatus.GAMING);

        nextRoundButton.setVisible(false);
        nextRound();
    }

    /**
     * 下一个回合
     * next Round
     */

    private void nextRound() {
        // 更新数据
        // update data
        List<Player> players = gameModel.getPlayers();

        // 如果全部死亡游戏结束
        //  if player all died then finish game
        boolean allDied = false;
        for (Player player : players) {
            if (player.isAlive()) {
                allDied = false;
                break;
            }
        }
        if (allDied) {
            setGameStatus(GameStatus.GAME_OVER);
            return;
        }

        gameModel.setCurPlayerIndex((gameModel.getCurPlayerIndex() + 1) % players.size());
        Player player = players.get(gameModel.getCurPlayerIndex());

        enableDiceButtons(true);

        if (!player.isAlive()) {
            nextRound();
            return;
        }

        // 不暂停才刷新为null  No pause before refreshing to null
        gameModel.setCurStepDiceValue(null);
        gameModel.setCurDirectionDiceValue(null);

        // 显示当前操作用户并刷新页面
        // Display the current operating user and refresh the page
        updatePlayerInfoPane();
        updateDicePane();

        log("It's player [" + player.getName() + "] round.");


        // 减少暂停的回合
        // when player suspend Reduce the number of suspended turns
        if (player.isSuspend()) {
            player.updateSuspendRound(-1);
            log("Player [" + player.getName() + "] is Suspend! ");
            updatePlayerInfoPane();
            enableDiceButtons(false);

            // 暂停则可以直接执行当前回合
            // if player suspend then can execute the current turn directly
            execButton.setVisible(true);
        }


    }


    private void updatePlayerInfoPane() {
        Player player = getCurPlayer();
        playerBloodLabel.setText(String.valueOf(player.getBlood()));
        playerNameLabel.setText(player.getName());
        playerRankLabel.setText(String.valueOf(player.getRank()));
        playerPositionLabel.setText("(" + getRow(player.getPosition()) + ", " + getCol(player.getPosition()) + ")");
        playerStatusLabel.setText(player.getStatus());
        playerStepsLabel.setText(String.valueOf(player.getSteps()));

        // 给当前角色标注背景色   Mark the background color for the current character
        for (Node child : playersGrid.getChildren()) {
            Label label = (Label) child;
            String name = label.getText();
            if ("".equals(name)) {
                Utils.setBackgroundColorStyle(label, "DCDCDC");
                continue;
            }
            name = name.substring(3, name.length() - 1);
            for (Player p : gameModel.getPlayers()) {
                if (p.getName().equals(name)) {
                    if (!p.isAlive()) {
                        Utils.setBackgroundColorStyle(label, "#CD5C5C");
                    } else if (p.isSuspend()) {
                        Utils.setBackgroundColorStyle(label, "#DEB887");
                    } else if (p == player) {
                        Utils.setBackgroundColorStyle(label, "#98F5FF");
                    } else {
                        Utils.setBackgroundColorStyle(label, "#F5FFFA");
                    }
                }
            }
        }
    }

    private Player getCurPlayer() {
        return gameModel.getPlayers().get(gameModel.getCurPlayerIndex());
    }

    /**
     * 执行操作（骰子选择好后，或者执行暂停状态）
     * Execute the operation (after the dice are selected, or execute the pause state)
     */
    private void execRound() {
        if (gameModel.getCurPlayerIndex() != null & gameModel.getCurStepDiceValue() != null && gameModel.getCurDirectionDiceValue() != null) {
            Player player = gameModel.getPlayers().get(gameModel.getCurPlayerIndex());
            // 死亡的玩家不可以操作    Dead players are not allowed to operate
            if (!player.isAlive()) {
                return;
            }

            int originPosition = player.getPosition();

            // 执行移动操作     move operation
            execMove(player);
            updatePlayerInfoPane();

            int curPosition = player.getPosition();

            // 如果在传送门则随机传送到 空白区域
            // If player in a portal, you are randomly transported to a blank area
            if (gameModel.hasPortal(curPosition)) {
                int randomBlankPosition = gameModel.getRandomBlankPosition();
                movePlayer(player, randomBlankPosition);
                if (gameStatus != GameStatus.GAMING) {
                    return;
                }
            }

            // 如果在火堆上扣除一滴血   If on the fire then deduct one drop of blood
            if (gameModel.hasFilePile(curPosition)) {
                player.updateBlood(-1);
                // 如果玩家死亡，则移除玩家并结束    If the player dies, remove the player and end
                if (!player.isAlive()) {
                    removePlayer(curPosition);
                    player.setStatus(Player.DIED);
                    updatePlayerInfoPane();
                    return;
                }
            }

            // 如果在坑中，暂停一回合    If in the pit, pause for a turn
            if (originPosition != curPosition && gameModel.hasPit(curPosition)) {
                player.updateSuspendRound(1);
            }
        }
        updatePlayerInfoPane();
    }

    private void execMove(Player player) {
        clearStepsTrack();
        enableDiceButtons(false);

        Integer curStepDiceValue = gameModel.getCurStepDiceValue();
        Integer curDirectionDiceValue = gameModel.getCurDirectionDiceValue();
        if (curStepDiceValue == null || curDirectionDiceValue == null) {
            System.err.println("curStepDiceValue == null || curDirectionDiceValue == null");
            enableDiceButtons(true);
            nextRoundButton.setVisible(true);
            return;
        }

        // 添加一回合  Add a round
        player.setSteps(player.getSteps() + 1);

        if (curDirectionDiceValue == -1) {
            log("skip the round~");
            directionLabel.setText("Skip");
            enableDiceButtons(true);
            nextRoundButton.setVisible(true);
            return;
        }

        // 获取移动方向  Get the direction of movement
        GameModel.Direction direction = GameModel.Direction.getDirection(curDirectionDiceValue);


        int steps = curStepDiceValue;
        Random random = new Random();

        while (steps > 0) {
            steps--;

            // 向后时，不转向  No steering when going backwards
            if (direction == GameModel.Direction.BACKWARD) {
                if (isWall(getNextStepObject(player, direction), steps)) {
                    System.err.println("can not back");
                    break;
                }
            } else if (isWall(getNextStepObject(player), steps)) {
                // 更变方向（左或右或后）  Change of direction (left or right or back)
                Object left = getNextStepObject(player, GameModel.Direction.TO_THE_LEFT);
                Object right = getNextStepObject(player, GameModel.Direction.TO_THE_RIGHT);
                Object back = getNextStepObject(player, GameModel.Direction.BACKWARD);
                boolean leftIsWall = isWall(left, steps);
                boolean rightIsWall = isWall(right, steps);
                boolean backIsWall = isWall(back, steps);
                if (leftIsWall && rightIsWall && backIsWall) {
                    logErr("[" + player.getName() + "] no way to go !");
                    break;
                }
                if (leftIsWall && rightIsWall) {
                    player.setDirection(GameModel.Direction.getDirection(player.getDirection().num + GameModel.Direction.BACKWARD.num));
                } else {
                    boolean isLeft = random.nextBoolean();
                    if (isLeft) {
                        player.setDirection(GameModel.Direction.getDirection(player.getDirection().num + GameModel.Direction.TO_THE_LEFT.num));
                    } else {
                        player.setDirection(GameModel.Direction.getDirection(player.getDirection().num + GameModel.Direction.TO_THE_RIGHT.num));
                    }
                }

            }


            movePlayerNextStep(player, direction);

            player.setDirection(GameModel.Direction.FORWARD);

        }

        enableDiceButtons(true);
        log("Player [" + player.getName() + "] exec over, turn to next one!");
        nextRoundButton.setVisible(true);

    }

    private void movePlayerNextStep(Player player, GameModel.Direction direction) {
        int position = player.getPosition();
        direction = GameModel.Direction.getDirection(direction.num + player.getDirection().num);
        int target = getPosition(position, direction, 1);
        if (!inMap(target)) {
            return;
        }
        movePlayer(player, target);
    }

    /**
     * 清理路径
     * Cleanup path
     */
    private void clearStepsTrack() {
        int size = gameModel.getMapCols() * gameModel.getMapRows();
        ObservableList<Node> children = gameMap.getChildren();
        for (int i = 0; i < size; i++) {
            StackPane node = (StackPane) children.get(i);
            node.getChildren().get(2).setStyle("");
        }
    }



    /**
     * 判断某格子 对象 是否是墙
     * Determine if a grid object is a wall
     * @param next
     * @return false is not wall, Ture is wall.
     */
    private boolean isWall(Object next, int steps) {
        boolean isWall = (next == null);
        isWall |= next instanceof Player;
        if (next instanceof Obstacle) {
            Obstacle obstacle = (Obstacle) next;
            isWall = (obstacle == GameModel.FENCE);
            isWall |= ((obstacle == GameModel.PIT) && steps > 0);
            isWall |= ((obstacle == GameModel.PORTAL) && steps > 0);
        }
        return isWall;
    }

    /**
     * 向玩家方向移动一格
     * Move one frame in the direction of the player
     * @param player
     */
    private void movePlayerNextStep(Player player) {
        movePlayerNextStep(player, GameModel.Direction.FORWARD);
    }

    private void enableDiceButtons(boolean flag) {
        directionDiceButton.setDisable(!flag);
        stepsDiceButton.setDisable(!flag);
    }

    /**
     * 显示log信息
     * Display log information
     * @param text log text
     */
    public void log(String text) {
        gameLogLabel.setText(text);
    }


    /**
     * 获取玩家 前面 一格的内容
     * Get the contents of the front row of the player
     * @param player
     * @return 边界返回NULL，玩家则返回对应玩家，障碍物则返回障碍物
     * The boundary returns NULL, the player returns the corresponding player, and the obstacle returns the obstacle
     */
    private Object getNextStepObject(Player player) {
        return getNextStepObject(player, GameModel.Direction.FORWARD);
    }

    /**
     * 获取相对于当前角色 指定方向 的下一个格子的Object
     * Get the Object of the next cell in the specified direction relative to the current character
     * @param player player
     * @param direction direction
     * @return targetPlayer
     */
    public Object getNextStepObject(Player player, GameModel.Direction direction) {
        int position = player.getPosition();
        direction = GameModel.Direction.getDirection(player.getDirection().num + direction.num);

        int target = getPosition(position, direction, 1);
        if (!inMap(target)) {
            return null;
        }
        // 先判断是否有玩家  First determine if there are players
        Player targetPlayer = gameModel.getPlayer(target);
        if (targetPlayer != null) {
            return targetPlayer;
        }
        // 判断是否有障碍物  Determine if there is an obstacle
        if (gameModel.hasObstacle(target)) {
            return gameModel.getMap().get(target);
        }
        return null;
    }

    /**
     * 获取将要移动到的点的坐标
     * Get the coordinates of the point to be moved to
     *  左上角为起点，c轴向右，r轴向下
     *  op left corner is the starting point, c-axis is right, r-axis is down
     *  font 前：c++
     *  back 后：c--
     *  left 左：r--
     *  right 右：r++
     *  如果出界返回-1
     *  Returns -1 if out of bounds
     * @param pos pos
     * @param direction direction
     * @param steps steps
     * @return getPosition
     */
    public int getPosition(int pos, GameModel.Direction direction, int steps) {
        int r = getRow(pos), c = getCol(pos);
        System.out.println(r + "," + c);
        switch (direction) {
            case FORWARD:
                c += steps;
                break;
            case BACKWARD:
                c -= steps;
                break;
            case TO_THE_LEFT:
                r -= steps;
                break;
            case TO_THE_RIGHT:
                r += steps;
                break;
            default:
                System.err.println("direction is err:" + direction);
                break;
        }
        if (r < 0 || r >= gameModel.getMapRows() || c < 0 || c >= gameModel.getMapCols()) {
            return -1;
        }
        return getPosition(r, c);
    }

    public int getPosition(int r, int c) {
        return r * gameModel.getMapCols() + c;
    }

    public int getRow(int position) {
        return position / gameModel.getMapCols();
    }

    public int getCol(int position) {
        return position % gameModel.getMapCols();
    }

    private void removePlayer(int position) {
        Button playerButtonFromGrid = getPlayerButtonFromGrid(position);
        playerButtonFromGrid.setText("");
        playerButtonFromGrid.setVisible(false);
    }

    /**
     * 游戏结束，显示游戏结束框，禁用其他控件
     * Game over, show game over box, disable other controls
     */
    private void gameOver() {
        enableDiceComponents(false);
        enableAddPlayerComponents(false);
        enablePlayerInfoComponents(false);

        Player player = getCurPlayer();

        if (!player.isAlive()) {
            // 全部死亡！  All player Died
            alert("All of players are died!");
            setGameStatus(GameStatus.GAME_WAIT);
            return;
        }

        // 计算成绩并导入库  Calculate grades and import them into the rank.text
        gameModel.addRankPlayer(player);
        // 更新榜单显示  The updated list shows
        updateRankPane();
        // 弹窗  pop-up window
        alert("Player [" + player.getName() + "] reach the finish line!\n\n" + player.toString());

        // 新游戏   new Game
        setGameStatus(GameStatus.GAME_WAIT);
    }



    /**
     * 游戏中
     *
     * Gameing
     *
     */
    private void gaming() {

    }

    /**
     * 清空map中所有元素
     * 更新排行榜
     * 启用 添加玩家框的控件，禁用其他控件
     * Clear all elements in map
     * Update leaderboard
     * Enable Add player box control, disable other controls
     */
    private void gameWait() {
        gameModel.clear();

        updateRankPane();
        clearMapPane();
        clearStepsTrack();

        nextRoundButton.setVisible(false);
        execButton.setVisible(false);


        enableAddPlayerComponents(true);
        enableDiceComponents(false);
        enablePlayerInfoComponents(false);
        enableGameLevelComponents(true);
    }

    /**
     * 根据gameModel数据更新 map面板
     * Update map panel based on gameModel data
     */
    private void updateMapPane() {
        // 清空面板  Emptying the panel
        clearMapPane();
        // 绘制障碍物   Drawing obstacles
        List<Obstacle> map = gameModel.getMap();
        for (int i = 0; i < map.size(); i++) {
            Obstacle obstacle = map.get(i);
            if (obstacle != null && obstacle.getImage() != null) {
                setImage(i, obstacle.getImage());
            }
        }
        // 绘制角色   Drawing player
        for (Player player : gameModel.getPlayers()) {
            setPlayer(player);
        }
    }

    /**
     * 清空map面板
     * Clear map panel
     *
     */
    private void clearMapPane() {
        for (Node node : gameMap.getChildren()) {
            StackPane cell = (StackPane) node;
            ObservableList<Node> children = cell.getChildren();
            ImageView img = (ImageView) children.get(0);
            Button btn = (Button) children.get(1);

            img.setImage(null);
            btn.setText("");
            btn.setVisible(false);
        }
    }

    private void updateRankPane() {
        List<Player> rankPlayers = gameModel.readRankPlayers();
        ObservableList<Node> ranks = rankPane.getChildren();
        for (int i = 1; i <= GameModel.RANK_NUM && i <= rankPlayers.size(); i++) {
            Label rankLabel = (Label) ranks.get(i);
            Player player = rankPlayers.get(i - 1);
            rankLabel.setText(player.getName());
        }
    }

    /**
     * 启用或关闭所有 骰子相关控件
     * Enable or disable all dice-related controls
     * @param flag
     */
    private void enableDiceComponents(boolean flag) {
        numberDicePane.setVisible(flag);
        stepsDiceButton.setDisable(!flag);

        updateDicePane();

        directionDicePane.setVisible(flag);
        directionDiceButton.setDisable(!flag);
    }

    private void updateDicePane() {
        Integer curStepDiceValue = gameModel.getCurStepDiceValue();
        if (curStepDiceValue == null) {
            stepsLabel.setText("");
            Utils.setBackgroundImageStyle(stepsDiceButton, GameModel.IMG_PATH_TETRAHEDRAL_DICE, "90% 90%");
        } else {
            Utils.setBackgroundImageStyle(stepsDiceButton, GameModel.DICE_PATH_IMAGES[curStepDiceValue], "90% 90%");
            stepsLabel.setText(curStepDiceValue + "");
        }

        GameModel.Direction direction = GameModel.Direction.getDirection(gameModel.getCurDirectionDiceValue());
        if (direction == null) {
            directionLabel.setText("");
            //Utils.setBackgroundImageStyle(directionLabel, GameModel.IMG_PATH_TETRAHEDRAL_DICE, "90% 90%");
        } else if (direction == GameModel.Direction.Directionless) {
            directionLabel.setText("Skip the round");
            //Utils.setBackgroundImageStyle(directionLabel, GameModel.DICE_PATH_IMAGES[curStepDiceValue + 1], "90% 90%");
        } else {
            directionLabel.setText(direction.toString());
        }
    }

    /**
     * 启用用户信息框
     * Enable user information box
     * @param flag
     */
    private void enablePlayerInfoComponents(boolean flag) {
        playerInfoPane.setVisible(flag);

        ObservableList<Node> children = playersGrid.getChildren();
        if (flag) {
            for (int i = 0; i < children.size(); i++) {
                Label label = (Label) children.get(i);
                if (i < gameModel.getPlayers().size()) {
                    Player player = gameModel.getPlayers().get(i);
                    label.setText("P" + (i + 1) + "[" + player.getName() + "]");
                    Utils.setBackgroundColorStyle(label, "#F5FFFA");
                } else {
                    label.setText("");
                    Utils.setBackgroundColorStyle(label, "#DCDCDC");
                }
            }
        } else {
            for (Node child : children) {
                Label label = (Label) child;
                label.setText("");
                Utils.setBackgroundColorStyle(label, "#DCDCDC");
            }
        }
    }

    /**
     * 启用或关闭 添加玩家的所有控件
     * Enable or disable all controls for adding players
     * @Param flag
     */
    private void enableAddPlayerComponents(boolean flag) {
        addPlayerPane.setVisible(flag);
        addPlayerNameField.setDisable(!flag);
        addPlayerButton.setDisable(!flag);
        startGameButton.setDisable(!flag);
        addPlayerTrackComboBox.setDisable(!flag);

        // 清空field值  Clear field value
        addPlayerNameField.setText("");
        // 设置可选范围  Set selectable range
        updatePlayerTrackComboBox();
    }

    private void enableGameLevelComponents(boolean visible) {
        gameLevelPane.setVisible(visible);

        boolean disable = !visible;
        okLevelButton.setDisable(disable);
        mapWidthField.setDisable(disable);
        obstacleProbabilityField.setDisable(disable);

        mapWidthField.setText("30");
        obstacleProbabilityField.setText("15");
    }

    /**
     * 设置赛道可选范围（根据第一列是否有玩家来判断）
     * Set the track optional range (based on whether there is a player in the first column)
     */
    private void updatePlayerTrackComboBox() {
        ObservableList<Integer> items = addPlayerTrackComboBox.getItems();
        items.clear();
        items.addAll(getCanSelectTrackList());
    }

    public GameController() {

    }

    private StackPane getPaneFromGrid(int r, int c) {
        return getPaneFromGrid(r * gameModel.getMapCols() + c);
    }

    private ImageView getImageViewFromGrid(int r, int c) {
        return getImageViewFromGrid(r * gameModel.getMapCols() + c);
    }

    public StackPane getPaneFromGrid(int position) {
        return (StackPane) gameMap.getChildren().get(position);
    }

    public ImageView getImageViewFromGrid(int position) {
        return (ImageView) getPaneFromGrid(position).getChildren().get(0);
    }

    private Button getPlayerButtonFromGrid(int r, int c) {
        return getPlayerButtonFromGrid(r * gameModel.getMapCols() + c);
    }

    private Button getPlayerButtonFromGrid(int position) {
        return (Button) getPaneFromGrid(position).getChildren().get(1);
    }

    private void setRank(int rank, String name) {
        Label label = (Label) rankPane.getChildren().get(rank);
        label.setText(name);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameModel = new GameModel(gameMap.getRowConstraints().size(), gameMap.getColumnConstraints().size());
        initAll();
    }

    private void initAll() {
        initOriginMap();

        initGameLevelPane();

        initOperationPane();

        initDices();

        initAddPlayer();

        initGameStatus();
    }

    private void initGameLevelPane() {
        okLevelButton.setOnAction(event -> {
            try {
                double probability = Double.parseDouble(obstacleProbabilityField.getText()) * 100;
                probability = probability < 0 ? 0 : probability > 50 ? 50 : probability;
                int width = Integer.parseInt(mapWidthField.getText());
                width = width < 0 ? 0 : width > 30 ? 30 : width;
                width += 2;
                gameModel.probabilityOfGenratingObstacles = probability;
                gameModel.setMapCols(width);

                gameModel = new GameModel(gameMap.getRowConstraints().size(), width);
                initAll();
            } catch (Exception e) {
                alert(e.getLocalizedMessage());
                return;
            }

            enableGameLevelComponents(false);
        });
    }

    private void initOperationPane() {
        nextRoundButton.setVisible(false);
        execButton.setVisible(false);

        nextRoundButton.setOnAction(e -> {
            nextRoundButton.setVisible(false);
            nextRound();
        });

        restartGameButton.setOnAction(e -> {
            setGameStatus(GameStatus.GAME_WAIT);
        });

        execButton.setOnAction(e -> {
            execButton.setVisible(false);
            execRound();
            nextRoundButton.setVisible(true);
        });
    }

    /**
     * 初始 化添加用户面板
     * Initialize the Add User panel
     */
    private void initAddPlayer() {
        updatePlayerTrackComboBox();
        addPlayerButton.setOnAction(e -> {
            addPlayerButton.setDisable(true);

            // 检测选择的赛道是否合理   Testing whether the chosen track is reasonable
            Integer pos = addPlayerTrackComboBox.getSelectionModel().getSelectedItem();
            if (pos == null || !getCanSelectTrackList().contains(pos)) {
                alert("select track is selected or null! [" + pos + "]");
                addPlayerButton.setDisable(false);
                return;
            }
            // 检测名字是否输入，且不重复   Detects if the name is entered and not duplicated
            String name = addPlayerNameField.getText().trim();
            if ("".equals(name)) {
                alert("name is empty!");
                addPlayerButton.setDisable(false);
                return;
            }
            for (Player player : gameModel.getPlayers()) {
                if (player.getName().equals(name)) {
                    alert("name already exists!");
                    addPlayerButton.setDisable(false);
                    return;
                }
            }
            // 添加玩家  Add player
            Player player = new Player(name);
            player.setPosition(pos * gameModel.getMapCols());
            gameModel.getPlayers().add(player);
            // 显示用户   display player
            setPlayer(player);

            updatePlayerTrackComboBox();
            addPlayerButton.setDisable(false);
        });


        startGameButton.setOnAction(e -> {
            startGameButton.setDisable(true);

            // 检测是否至少有两个玩家  Detect if there are at least two players
            if (gameModel.getPlayers().size() < 2) {
                alert("The number of players < 2!");
                startGameButton.setDisable(false);
                return;
            }
            // Start Game
            setGameStatus(GameStatus.GAME_START);

            startGameButton.setDisable(false);
        });
    }

    /**
     * 获取可以选择的赛道 根据用户所在行号判断
     * Get the tracks that can be selected based on the user's line number
     * @return res
     */
    private List<Integer> getCanSelectTrackList() {
        Set<Integer> set = new HashSet<>();

        for (Player player : gameModel.getPlayers()) {
            int row = player.getPosition() / gameModel.getMapCols();
            set.add(row);
        }

        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < gameModel.getMapRows(); i++) {
            if (!set.contains(i)) {
                res.add(i);
            }
        }

        return res;
    }

    private void alert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("info");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void initGameStatus() {
        setGameStatus(GameStatus.GAME_WAIT);
    }


    /**
     *
     *
     * Initialize dice interface
     */
    private void initDices() {
        Utils.setBackgroundImageStyle(stepsDiceButton, GameModel.IMG_PATH_TETRAHEDRAL_DICE, "90% 90%");
        Utils.setBackgroundImageStyle(directionDiceButton, GameModel.IMG_PATH_TETRAHEDRAL_DICE, "90% 90%");



        stepsDiceButton.setOnAction(e -> {
            if (gameModel.getCurStepDiceValue() == null && !execButton.isVisible()) {
                Random random = new Random();
                gameModel.setCurStepDiceValue(random.nextInt(4) + 1);
            }
            updateDicePane();
        });

        directionDiceButton.setOnAction(e -> {
            if (gameModel.getCurDirectionDiceValue() == null && gameModel.getCurStepDiceValue() != null && !execButton.isVisible()) {
                log("The player [" + getCurPlayer().getName() + "] can to exec round.");

                Random random = new Random();
                int num = random.nextInt(4);
                switch (num) {
                    case 0: case 1:
                        gameModel.setCurDirectionDiceValue(0);
                        break;
                    case 2:
                        gameModel.setCurDirectionDiceValue(2);
                        break;
                    case 3:
                        gameModel.setCurDirectionDiceValue(-1);
                        break;
                    default:
                        String err = "Direction Dice err random number:" + num;
                        logErr(err);
                        break;
                }

                execButton.setVisible(true);
            }
            updateDicePane();
        });


    }

    private void logErr(String err) {
        System.err.println(err);
        log(err);
    }

    /**
     * 初始化原始地图
     * Initialize the original map
     */
    private void initOriginMap() {
        try {
            gameMap.getChildren().clear();
            for (int r = 0; r < gameModel.getMapRows(); r++) {
                for (int c = 0; c < gameModel.getMapCols(); c++) {
                    Parent cell = new FXMLLoader(GameApplication.class.getResource("grid-cell-view.fxml")).load();
                    gameMap.add(cell, c, r);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 第一列为起点   The first column is the starting point
        for (int i = 0; i < gameModel.getMapRows(); i++) {
            StackPane firstPane = getPaneFromGrid(i, 0);
            firstPane.setStyle("-fx-background-color: #00F5FF");
        }

        // 最后一列为终点   last column is the end
        for (int i = 0; i < gameModel.getMapRows(); i++) {
            getPaneFromGrid(i, gameModel.getMapCols() - 1).setStyle("-fx-background-color: #FFE4E1");
        }

    }

    private void setImage(int position, Image image) {
        getImageViewFromGrid(position).setImage(image);
    }

    private void setImage(int position, String url) {
        setImage(position, new Image(url));
    }

    private boolean movePlayer(Player player, int target) {
        // To Do : check target
        if (!inMap(target)) {
            return false;
        }

        Button playerButton = null;

        if (inMap(player.getPosition())) {
            playerButton = getPlayerButtonFromGrid(player.getPosition());
            playerButton.setText("");
            playerButton.setVisible(false);

            setTrack(player.getPosition(), "#FF0000");
        }



        player.setPosition(target);

        playerButton = getPlayerButtonFromGrid(player.getPosition());
        playerButton.setText(player.getName());
        playerButton.setVisible(true);

        // 检测是否到达终点 Detects if the player has reached the end
        int r = getRow(target), c = getCol(target);
        if (c == gameModel.getMapCols() - 1) {
            setGameStatus(GameStatus.GAME_OVER);
        }

        return true;
    }

    private void setTrack(int position, String color) {
        StackPane node = (StackPane) gameMap.getChildren().get(position);
        Label label = (Label) node.getChildren().get(2);
        Utils.setBackgroundColorStyle(label, color);
//        System.out.println(label.getStyle());
    }

    private boolean setPlayer(Player player) {
        if (!player.isAlive()) {
            return false;
        }

        Button playerButton = null;

        if (inMap(player.getPosition())) {
            playerButton = getPlayerButtonFromGrid(player.getPosition());
            playerButton.setText(player.getName());
            playerButton.setVisible(true);

            return true;
        }

        return false;
    }

    private boolean inMap(int position) {
        return position >= 0 && position < gameModel.getMapCols() * gameModel.getMapRows();
    }
}
