package io.github.vino42;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * =====================================================================================
 *
 * @Created :   2023/10/5 13:20
 * @Compiler :  jdk 17
 * @Author :    VINO
 * @Email : VINO
 * @Copyright : VINO
 * @Decription :
 * =====================================================================================
 */
public class Station {
    /**
     * 当前站名
     */
    private String name;

    private Integer index;
    /**
     * 前一站
     */
    private Station before;
    private Integer beforeIndex;
    /**
     * 下一站
     */
    private Station next;
    private Integer nextIndex;
    /**
     * 车站等车人员
     */
    private List<User> waitingPassenger = Collections.synchronizedList(new ArrayList<>());

    public List<User> getWaitingPassenger() {
        return waitingPassenger;
    }

    public void setWaitingPassenger(List<User> waitingPassenger) {
        this.waitingPassenger = waitingPassenger;
    }

    /**
     * 正向下一站到站间隔时间
     */
    private Integer forwardDirectTime;

    /**
     * 反向下一站到站间隔时间
     */
    private Integer reverseDirectTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Station getBefore() {
        return before;
    }

    public void setBefore(Station before) {
        this.before = before;
    }

    public Integer getBeforeIndex() {
        return beforeIndex;
    }

    public void setBeforeIndex(Integer beforeIndex) {
        this.beforeIndex = beforeIndex;
    }

    public Station getNext() {
        return next;
    }

    public void setNext(Station next) {
        this.next = next;
    }

    public Integer getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(Integer nextIndex) {
        this.nextIndex = nextIndex;
    }

    public Integer getForwardDirectTime() {
        return forwardDirectTime;
    }

    public void setForwardDirectTime(Integer forwardDirectTime) {
        this.forwardDirectTime = forwardDirectTime;
    }

    public Integer getReverseDirectTime() {
        return reverseDirectTime;
    }

    public void setReverseDirectTime(Integer reverseDirectTime) {
        this.reverseDirectTime = reverseDirectTime;
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", index=" + index +
                ", before=" + before +
                ", beforeIndex=" + beforeIndex +
                ", next=" + next +
                ", nextIndex=" + nextIndex +
                ", forwardDirectTime=" + forwardDirectTime +
                ", reverseDirectTime=" + reverseDirectTime +
                '}';
    }

    public boolean isFinalStation(Bus bus) {
        if (bus.getDirection() == 1 && this.index == 15) {
            return true;
        }
        if (bus.getDirection() == 2 && this.index == 1) {
            return true;
        }
        return false;
    }
}
