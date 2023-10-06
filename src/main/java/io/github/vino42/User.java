package io.github.vino42;

import cn.hutool.core.util.RandomUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * =====================================================================================
 *
 * @Created :   2023/10/5 14:04
 * @Compiler :  jdk 17
 * @Author :    VINO
 * @Email : VINO
 * @Copyright : VINO
 * @Decription :
 * =====================================================================================
 */
public class User {
    /**
     * 乘客名称
     */
    private String name;
    /**
     * 上车站点
     */
    private Station upStation;
    /**
     * 下站站点
     */
    private Station target;
    /**
     * 正向还是反向方向
     */
    private Integer direction;

    private AtomicInteger priority = new AtomicInteger(0);

    public AtomicInteger getPriority() {
        return priority;
    }

    public void setPriority(AtomicInteger priority) {
        this.priority = priority;
    }

    public void addPriority() {
        this.priority.getAndAdd(1);
    }

    public Station getUpStation() {
        return upStation;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Station getTarget() {
        return target;
    }

    public void setTarget(Station target) {
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String name) {
        this.name = name;
    }

    public static User initUser() {
        User user = new User("乘客-" + RandomUtil.randomString(8));
        user.setDirection(RandomUtil.randomInt(1, 3));
        int up = RandomUtil.randomInt(16);
        int down = RandomUtil.randomInt(16);
        if (up == down) {
            down = up + RandomUtil.randomInt(1, 3);
        }
        Station station = new Station();
        station.setIndex(up);
        user.setUpStation(station);
        Station downStation = new Station();
        downStation.setIndex(down);
        user.setTarget(downStation);
        return user;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", upStation=" + upStation +
                ", target=" + target +
                ", direction=" + direction +
                '}';
    }
}
