package game;

import javafx.scene.image.Image;

import java.io.*;
import java.util.*;

/**
 * The main function is to initialize each model.
 * For example.
 * Set the image of each model.
 * Initialize the map data.
 * Importing player history rank.
 * Direction.
 * Initializes the obstacle on the map.
 * Generate random obstacles on the map by chance.
 * player position.
 */
public class GameModel {

    public static final String RANK_FILE_PATH = "src/rank.txt";

    public static final String IMG_PATH_FENCE = "file:src/main/resources/img/R-C (1).jpg";
    public static final String IMG_PATH_FIRE_PILE = "file:src/main/resources/img/fire_img.png";
    public static final String IMG_PATH_PIT = "file:src/main/resources/img/OIP-C (1).jpg";
    public static final String IMG_PATH_PORTAL = "file:src/main/resources/img/OIP-C.jpg";
    public static final String IMG_PATH_TETRAHEDRAL_DICE = "file:src/main/resources/img/question.jpg";
    public static final String IMG_PATH_ONE = "file:src/main/resources/img/1.png",
            IMG_PATH_TWO = "file:src/main/resources/img/2.png",
            IMG_PATH_THREE = "file:src/main/resources/img/3.png",
            IMG_PATH_FOUR = "file:src/main/resources/img/4.png";

    public static final Image IMG_ONE = new Image(IMG_PATH_ONE), IMG_TWO = new Image(IMG_PATH_TWO), IMG_THREE = new Image(IMG_PATH_THREE), IMG_FOUR = new Image(IMG_PATH_FOUR);
    public static final String[] DICE_PATH_IMAGES = new String[] {
            IMG_PATH_TETRAHEDRAL_DICE, IMG_PATH_ONE, IMG_PATH_TWO, IMG_PATH_THREE, IMG_PATH_FOUR
    };

    public static final Image[] DICE_IMAGES = new Image[] {
            null, IMG_ONE, IMG_TWO, IMG_THREE, IMG_FOUR
    };

    public static final Image IMG_FENCE = new Image(IMG_PATH_FENCE);
    public static final Image IMG_FIRE_PILE = new Image(IMG_PATH_FIRE_PILE);
    public static final Image IMG_PIT = new Image(IMG_PATH_PIT);
    public static final Image IMG_PORTAL = new Image(IMG_PATH_PORTAL);

    public static final Image IMG_TETRAHEDRAL_DICE = new Image(IMG_PATH_TETRAHEDRAL_DICE);

    public static Obstacle
            FENCE = new Obstacle(IMG_FENCE, "fence", 30),
            FIRE_PILE = new Obstacle(IMG_FIRE_PILE, "fire pile", 30),
            PIT = new Obstacle(IMG_PIT, "pit", 30),
            PORTAL = new Obstacle(IMG_PORTAL, "portal", 10),
            BLANK = new Obstacle(null, "blank", 0);
    public double probabilityOfGenratingObstacles = 0.15;

    public static final Obstacle[] OBSTACLES = new Obstacle[]{
            FENCE,
            FIRE_PILE,
            PIT,
            PORTAL
    };


    public static final int RANK_NUM = 10;

    public static final String FILE_PATH_RANK = "src/rank.txt";

    private String[] rankArr = new String[10];

    private List<Player> players = new ArrayList<>();

    private int mapCols = 0, mapRows = 0;

    private List<Obstacle> map;

    private Integer curPlayerIndex;
    private Integer curStepDiceValue;
    private Integer curDirectionDiceValue;


    public Integer getCurPlayerIndex() {
        return curPlayerIndex;
    }

    public void setCurPlayerIndex(Integer curPlayerIndex) {
        this.curPlayerIndex = curPlayerIndex;
    }

    public Integer getCurStepDiceValue() {
        return curStepDiceValue;
    }

    public void setCurStepDiceValue(Integer curStepDiceValue) {
        this.curStepDiceValue = curStepDiceValue;
    }

    public Integer getCurDirectionDiceValue() {
        return curDirectionDiceValue;
    }

    public void setCurDirectionDiceValue(Integer curDirectionDiceValue) {
        this.curDirectionDiceValue = curDirectionDiceValue;
    }

    /**
     * 初始化游戏数据  Initialize game data
     * 初始化地图数据  Initialize game map data
     * 初始化 Initialize curPlayerIndex curStepDiceValue curDirectionDiceValue
     */
    public void beginGame() {
        initRandomMap();

        curPlayerIndex = 0;

        curStepDiceValue = null;
        curDirectionDiceValue = null;
    }

    public GameModel() {
        try {
            File file = new File(RANK_FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(out);
                ArrayList<Player> obj = new ArrayList<>();
                obj.add(new Player("asdf"));
                oos.writeObject(obj);
                oos.flush();
                out.close();
                oos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasFilePile(int position) {
        return map.get(position) == FIRE_PILE;
    }

    public boolean hasPit(int pos) {
        return map.get(pos) == PIT;
    }

    public boolean hasPortal(int pos) {
        return map.get(pos) == PORTAL;
    }

    public int getRandomBlankPosition() {
        List<Integer> blanks = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i) == BLANK) {
                blanks.add(i);
            }
        }
        for (Player player : players) {
            if (player.isAlive()) {
                blanks.remove(player.getPosition());
            }
        }
        Random random = new Random();
        return blanks.get(random.nextInt(blanks.size()));
    }


    /**
     * 导入成绩  inport player ra
     *
     * @param player player
     * @return 成功返回NULL Success returns NULL
     */
    public String addRankPlayer(Player player) {
        try {
            if (!player.isAlive()) {
                return "player [" + player.getName() + "] is died, can't get rank.";
            }
            if (player.getPosition() % mapCols != mapCols - 1) {
                return "player [" + player.getName() + "] is not in finish line, can't get rank.";
            }

            List<Player> rankPlayers = readRankPlayers();
            boolean hasPlayer = false;
            for (int i = 0; i < rankPlayers.size(); i++) {
                Player rankPlayer = rankPlayers.get(i);
                if (rankPlayer.getName().equals(player.getName())) {
                    hasPlayer = true;
                    if (player.compareTo(rankPlayer) < 0) {
                        rankPlayers.set(i, player);
                    }
                    break;
                }
            }
            if (!hasPlayer) {
                rankPlayers.add(player);
            }
            Collections.sort(rankPlayers);
            for (int i = 0; i < rankPlayers.size(); i++) {
                Player rankPlayer = rankPlayers.get(i);
                rankPlayer.setRank(i + 1);
            }
            writeRankPlayers(rankPlayers);

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    /**
     * 读取历史记录，并更新player的rank值
     * Read the history and update the player's rank value
     *
     * @return Player player
     */
    public List<Player> readRankPlayers() {
        try {
            FileInputStream in = new FileInputStream(RANK_FILE_PATH);
            ObjectInputStream ois = new ObjectInputStream(in);
            List<Player> players = (List<Player>) ois.readObject();
            for (Player rankPlayer : players) {
                for (Player player : this.players) {
                    if (player.getName().equals(rankPlayer.getName())) {
                        player.setRank(rankPlayer.getRank());
                    }
                }
            }
            in.close();
            ois.close();
            return players;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean writeRankPlayers(List<Player> players) {
        try {
            FileOutputStream out = new FileOutputStream(RANK_FILE_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(players);
            oos.flush();
            out.close();
            oos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Direction for the players
     */
    public enum Direction {
        FORWARD(0), TO_THE_RIGHT(1), BACKWARD(2), TO_THE_LEFT(3), Directionless(-1);

        int num;

        Direction(int num) {
            this.num = num;
        }

        /**
         * get the Direction
         * @param i i
         * @return Direction
         */
        public static Direction getDirection(Integer i) {
            if (i == null) {
                return null;
            }
            if (i == -1) {
                return Directionless;
            }
            i %= 4;
            switch (i) {
                case 0:
                    return FORWARD;
                case 1:
                    return TO_THE_RIGHT;
                case 2:
                    return BACKWARD;
                case 3:
                    return TO_THE_LEFT;
            }
            return null;
        }
    }

    /**
     * initialize the game
     * @param mapRows  map rows
     * @param mapCols mapCols
     */
    public GameModel(int mapRows, int mapCols) {
        this();
        this.mapCols = mapCols;
        this.mapRows = mapRows;

        initObstacleMap();

    }

    /**
     *  Initializes the obstacle map
     */
    private void initObstacleMap() {
        int size = this.mapRows * this.mapCols;
        this.map = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.map.add(BLANK);
        }
    }

    public String[] getRankArr() {
        return rankArr;
    }

    public void setRankArr(String[] rankArr) {
        this.rankArr = rankArr;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getMapCols() {
        return mapCols;
    }

    public void setMapCols(int mapCols) {
        this.mapCols = mapCols;
    }

    public int getMapRows() {
        return mapRows;
    }

    public void setMapRows(int mapRows) {
        this.mapRows = mapRows;
    }


    public List<Obstacle> getMap() {
        return map;
    }

    public void setMap(List<Obstacle> map) {
        this.map = map;
    }

    /**
     * 随机生成地图障碍物（第一列，第二列和最后一列不生成）
     * - 0.15 概率生成障碍物
     * [0.3]栅栏：纯障碍物，不可通过
     * [0.3]火堆: 停留时扣除1血量
     * [0.3]坑：踩中暂停一回合，超过则停在坑前一格（然后随机转弯 优先前，其次左和右，实在走不了就后）
     * [0.1]传送门：随机传送到一个空白区域
     * Random map obstacle generation (first column, second and last columns are not generated)
     * - 0.15 probability of generating obstacles
     * [0.3] Fence: pure obstacle, not passable
     * [0.3] Fire: 1 blood is deducted on stopping
     * [0.3] Pit: Pause for one turn if stepped on, stop one frame before the pit if exceeded (then turn randomly Priority front, followed by left and right, really can't go after)
     * [0.1] Portal: randomly teleport to a blank area
     */
    public void initRandomMap() {
        // 清空当前障碍物  Clear the current obstacle
        initObstacleMap();

        // 生成新的随机障碍物  Generate new random obstacles
        Random random = new Random();
        int range = 0;
        for (Obstacle obstacle : OBSTACLES) {
            range += obstacle.getOccurrenceRatio();
        }
        for (int row = 0; row < mapRows; row++) {
            for (int col = 2; col < mapCols - 1; col++) {
                if (random.nextInt(100) < 100.0 * probabilityOfGenratingObstacles) {
                    // 需要生成障碍物  Need to generate obstacles
                    int v = random.nextInt(range);
                    for (Obstacle obstacle : OBSTACLES) {
                        if (v < obstacle.getOccurrenceRatio()) {
                            map.set(row * mapCols + col, obstacle);
                            break;
                        }
                        v -= obstacle.getOccurrenceRatio();
                    }
                }
            }
        }
    }

    public void clear() {
        players.clear();
        initObstacleMap();
    }

    /**
     * check has players
     * @param position position
     * @return true if has players
     */
    public boolean hasPlayer(int position) {
        for (Player player : players) {
            if (player.getPosition() == position) {
                return true;
            }
        }
        return false;
    }

    /**
     *  get player position
     * @param position position
     * @return player player
     */
    public Player getPlayer(int position) {
        for (Player player : players) {
            if (player.getPosition() == position) {
                return player;
            }
        }
        return null;
    }

    public boolean hasObstacle(int position) {
        return map.get(position) != null;
    }

    public boolean hasPlayerOrObstacle(int position) {
        return hasObstacle(position) || hasPlayer(position);
    }

}
