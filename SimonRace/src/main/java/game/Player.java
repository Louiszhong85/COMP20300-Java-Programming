package game;



import java.io.Serializable;
import java.util.Objects;
/**
 * Player API
 * The main function is to implement the player class.
 * For example:
 * check if the player is suspended or not.
 * update the status of suspend for the .
 * get Suspend Round, if player in to suspend.
 * CompareTo
 */
public class Player implements Serializable, Comparable<Player> {
    public static final long serialVersionUID = 94673777251578706L;

    public static final String NORMAL = "Normal", SUSPEND = "Suspend", DIED = "Died";

    private String name = "[None]";
    private int blood = 5;
    private String status = "Normal";
    private int position = -1;
    private GameModel.Direction direction = GameModel.Direction.FORWARD;
    /**
     * 0 - [None]
     * 1 - Top 1
     * 2 - Top2
     * ...
     */
    private int rank = 0;
    private int steps = 0;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GameModel.Direction getDirection() {
        return direction;
    }

    public void setDirection(GameModel.Direction direction) {
        this.direction = direction;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public boolean isAlive() {
        return blood > 0 && !Objects.equals(status, DIED);
    }

    /**
     * update the status of the player
     * if blood less than 0 then the player died
     * @param v v
     *
     */
    public void updateBlood(int v) {
        if (Objects.equals(status, DIED)) {
            return;
        }
        blood += v;
        if (blood <= 0) {
            status = DIED;
        }
    }

    /**
     * check if the player is suspended or not
     * @return true if the player is suspended
     */
    public boolean isSuspend() {
        return status.contains(SUSPEND);
    }

    /**
     * update the suspend status of the player
     * @param round round
     */
    public void updateSuspendRound(int round) {
        if (Objects.equals(status, DIED)) {
            return;
        }

        int curRound = getSuspendRound();
        curRound += round;
        if (curRound <= 0) {
            status = NORMAL;
        } else {
            status = SUSPEND + curRound;
        }
    }

    /**
     * getSuspendRound
     * @return 0 if player !isSuspend
     */
    public int getSuspendRound() {
        if (!isSuspend()) {
            return 0;
        }

        try {
            return Integer.parseInt(status.substring(SUSPEND.length()));
        } catch (Exception e) {
            setStatus(SUSPEND + "1");
            return 1;
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "\n\tname='" + name + '\'' +
                ", \n\tblood=" + blood +
                ", \n\tstatus='" + status + '\'' +
                ", \n\tposition=" + position +
                ", \n\tdirection=" + direction +
                ", \n\trank=" + rank +
                ", \n\tsteps=" + steps +
                "\n}";
    }

    @Override
    public int compareTo(Player o) {
        if (getSteps() == o.getSteps()) {
            return o.getBlood() - getBlood();
        }
        return getSteps() - o.getSteps();
    }
}
