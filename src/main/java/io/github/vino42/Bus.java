package io.github.vino42;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


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
public class Bus implements Runnable {

    private Integer busNumber;
    /**
     * 司机
     */
    private Driver dirver;
    /**
     * 乘客
     */
    private List<User> passengers = new ArrayList<>();
    /**
     * 正向公交站点路线
     */
    private List<Station> busLine;

    /**
     * 开始运行时间
     */
    private Long startTime;

    private Long runTime;


    /**
     * 公交站路线是正向还是反向，正向为1反向为2
     */
    private Integer direction;

    public void setDirection(Integer direction) {
        this.direction = direction;
    }


    private PassengerStrategy passengerStrategy = new PassengerStrategy();

    public PassengerStrategy getPassengerStrategy() {
        return passengerStrategy;
    }

    public void setPassengerStrategy(PassengerStrategy passengerStrategy) {
        this.passengerStrategy = passengerStrategy;
    }



    public Driver getDirver() {
        return dirver;
    }

    public void setDirver(Driver dirver) {
        this.dirver = dirver;
    }

    public List<User> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<User> passengers) {
        this.passengers = passengers;
    }


    public Integer getDirection() {
        return direction;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public void setBusLine(List<Station> busLine) {
        this.busLine = busLine;
    }

    public Long getRunTime() {
        return runTime;
    }

    public void setRunTime(Long runTime) {
        this.runTime = runTime;
    }

    @Override
    public void run() {

        System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + (this.getDirection() == 1 ? "正向" : "反向") + "当前公交开始运行| 车号：" + this.getBusNumber());

        //当前车如果在规定的运行间隔内
        while (System.currentTimeMillis() - startTime < runTime) {
            ListIterator<Station> it = busLine.listIterator();
            //正向的车
            if (this.getDirection() == 1) {

                try {
                    zhengXiang(it);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                //反向的车
                try {
                    fanXiang(it);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //时间到了车停了
        Thread.currentThread().interrupt();
    }

    /**
     * 反向路线行驶
     * @param it
     * @throws InterruptedException
     */
    private void fanXiang(ListIterator<Station> it) throws InterruptedException {

        while (it.hasPrevious()) {
            Station currentStation = it.previous();
            System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "反向行驶" + "车号：" + this.getBusNumber() + "停靠站台： " + currentStation.getIndex() + "站台等待乘客数: "
                    + currentStation.getWaitingPassenger().size() + "车上乘客数：" + this.getPassengers().size());
            //到站下车上车
            daozhan(currentStation);
            System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + "下车上车完毕 继续出发" + "车上乘客数:" + this.getPassengers().size());
            Integer forwardDirectTime = currentStation.getForwardDirectTime();
            if (forwardDirectTime != null && currentStation.getNext() != null) {
                //运行到下一站
                try {
                    //这里为了展示 写了10秒， 这里的10应该是反向路线车站间隔时间
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + (this.getDirection() == 1 ? "正向" : "反向") + "运行到下一站| 下一站 站号" + currentStation.getNext().getName() + "车上乘客数:" + this.getPassengers().size());
            } else {
                //到终点站了 直接跳出循环进行调向
                break;
            }
        }
        System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + (this.getDirection() == 1 ? "正向" : "反向") + "当前公交到达反向终点，开始正向运行| 车号：" + this.getBusNumber() + "车上乘客数:" + this.getPassengers().size());
        this.setDirection(1);
        zhengXiang(it);
    }

    /**
     * 正向路线行驶
     * @param it
     * @throws InterruptedException
     */
    private void zhengXiang(ListIterator<Station> it) throws InterruptedException {
        while (it.hasNext()) {
            Station currentStation = it.next();
            System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "正向行驶" + "车号：" + this.getBusNumber() + "停靠站台： " + currentStation.getIndex() + "站台等待乘客数: "
                    + currentStation.getWaitingPassenger().size() + "车上乘客数：" + this.getPassengers().size());

            daozhan(currentStation);
            System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + "下车上车完毕 继续出发" + "车上乘客数:" + this.getPassengers().size());

            Integer forwardDirectTime = currentStation.getForwardDirectTime();
            if (forwardDirectTime != null && currentStation.getNext() != null) {
                //运行到下一站
                try {
                    //这里为了展示 写了10秒， 这里的10应该是正向路线车站间隔时间
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + (this.getDirection() == 1 ? "正向" : "反向") + "运行到下一站| 下一站 站号" + currentStation.getNext().getName() + "车上乘客数:" + this.getPassengers().size());
            } else {
                //到终点站了 直接跳出循环进行调向
                break;
            }
        }
        System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + (this.getDirection() == 1 ? "正向" : "反向") + "当前公交到达正向终点，开始反向运行| 车号：" + this.getBusNumber() + "车上乘客数:" + this.getPassengers().size());
        this.setDirection(2);
        fanXiang(it);
    }

    private void daozhan(Station currentStation) throws InterruptedException {
        //正向到站 先下后上
        //模拟汽车故障 10分之一概率
        int brokenFlag = RandomUtil.randomInt(1, 11);
        if (brokenFlag == 1) {
            //公交故障拉
            System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + " 故障！全体乘客下车,乘客上车站改为当前站 " + currentStation.getIndex());
            passengerStrategy.busBroken(currentStation, this);
            System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + " 故障！当前修车30秒！ 在站台: " + currentStation.getIndex());
            TimeUnit.SECONDS.sleep(30);
            System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + " 故障修理完毕！ 在站台: " + currentStation.getIndex());
            return;

        }
        //终点站所有乘客下车
        boolean isFinalStation = passengerStrategy.onFinalStation(currentStation, this);
        if (isFinalStation) {
            //终点站不能上车
            return;
        }
        //正常上下车
        passengerStrategy.offPassenger(currentStation, this);
        //正常上车
        passengerStrategy.loadPassenger(currentStation, this);

    }

    public Integer getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(Integer busNumber) {
        this.busNumber = busNumber;
    }

    static ScheduledThreadPoolExecutor zhengxiang = new ScheduledThreadPoolExecutor(5);

    public List<User> getHighUser() {
        List<User> list = this.getPassengers().stream().filter(d -> d.getPriority().get() != 0).toList();
        List<User> list1 = list.stream().sorted(Comparator.comparingInt(o -> o.getPriority().get())).toList();
        return list1;
    }
}
