package io.github.vino42;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private Integer maxPassenger = 29;
    /**
     * 司机
     */
    private Driver dirver;
    /**
     * 乘客
     */
    private List<User> passengers = Collections.synchronizedList(new ArrayList<>());
    /**
     * 正向公交站点路线
     */
    private Deque<Station> zhengXiangDire;

    /**
     * 反向公交站点路线
     */
    private Deque<Station> fanXiangDire;

    private Long startTime;

    public void setZhengXiangDire(Deque<Station> zhengXiangDire) {
        this.zhengXiangDire = zhengXiangDire;
    }

    public Deque<Station> getFanXiangDire() {
        return fanXiangDire;
    }

    public void setFanXiangDire(Deque<Station> fanXiangDire) {
        this.fanXiangDire = fanXiangDire;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    /**
     * 公交站路线是正向还是反向，正向为1反向为2
     */
    private Integer direction;

    private PassengerStrategy passengerStrategy = new PassengerStrategy();

    public PassengerStrategy getPassengerStrategy() {
        return passengerStrategy;
    }

    public void setPassengerStrategy(PassengerStrategy passengerStrategy) {
        this.passengerStrategy = passengerStrategy;
    }


    public Integer getMaxPassenger() {
        return maxPassenger;
    }

    public void setMaxPassenger(Integer maxPassenger) {
        this.maxPassenger = maxPassenger;
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

    public Deque<Station> getZhengXiangDire() {
        return zhengXiangDire;
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

    public static ScheduledThreadPoolExecutor getZhengxiang() {
        return zhengxiang;
    }

    public static void setZhengxiang(ScheduledThreadPoolExecutor zhengxiang) {
        Bus.zhengxiang = zhengxiang;
    }

    @Override
    public void run() {

        System.out.println("当前时间："+ DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + (this.getDirection() == 1 ? "正向" : "反向") + "当前公交开始运行| 车号：" + this.getBusNumber());
        Long seg = 300 * 60 * 60 * 1000L;
        long now = System.currentTimeMillis();
        while (now - startTime < seg) {
            //正向的车
            if (this.getDirection() == 1) {

                try {
                    zhengXiang();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                //反向的车
                try {
                    fanXiang();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Thread.currentThread().interrupt();
    }

    private void fanXiang() throws InterruptedException {
        Iterator<Station> fan = fanXiangDire.iterator();

        while (fan.hasNext()) {
            Station currentStation = fan.next();
            System.out.println("当前时间："+DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "反向行驶" + "车号：" + this.getBusNumber() + "停靠站台： " + currentStation.getIndex() + "站台等待乘客数: "
                    + currentStation.getWaitingPassenger().size() + "车上乘客数：" + this.getPassengers().size());
            //到站下车上车
            daozhan(currentStation);
            System.out.println("当前时间："+DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + "下车上车完毕 继续出发" + "车上乘客数:" + this.getPassengers().size());
            Integer forwardDirectTime = currentStation.getForwardDirectTime();
            if (forwardDirectTime != null && currentStation.getNext() != null) {
                //运行到下一站
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("当前时间："+DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + (this.getDirection() == 1 ? "正向" : "反向") + "运行到下一站| 下一站 站号" + currentStation.getNext().getName() + "车上乘客数:" + this.getPassengers().size());
            } else {
                //到终点站了 直接跳出循环进行调向
                break;
            }
        }
        System.out.println("当前时间："+DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + (this.getDirection() == 1 ? "正向" : "反向") + "当前公交到达反向终点，开始正向运行| 车号：" + this.getBusNumber() + "车上乘客数:" + this.getPassengers().size());
        this.setDirection(1);
        zhengXiang();
    }

    private void zhengXiang() throws InterruptedException {
        Iterator<Station> it = zhengXiangDire.iterator();

        while (it.hasNext()) {
            Station currentStation = it.next();
            System.out.println("当前时间："+DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "正向行驶" + "车号：" + this.getBusNumber() + "停靠站台： " + currentStation.getIndex() + "站台等待乘客数: "
                    + currentStation.getWaitingPassenger().size() + "车上乘客数：" + this.getPassengers().size());

            daozhan(currentStation);
            System.out.println("当前时间："+DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + "下车上车完毕 继续出发" + "车上乘客数:" + this.getPassengers().size());

            Integer forwardDirectTime = currentStation.getForwardDirectTime();
            if (forwardDirectTime != null && currentStation.getNext() != null) {
                //运行到下一站
                try {
                    TimeUnit.SECONDS.sleep(forwardDirectTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("当前时间："+DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + (this.getDirection() == 1 ? "正向" : "反向") + "运行到下一站| 下一站 站号" + currentStation.getNext().getName() + "车上乘客数:" + this.getPassengers().size());
            } else {
                //到终点站了 直接跳出循环进行调向
                break;
            }
        }
        System.out.println("当前时间："+DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + (this.getDirection() == 1 ? "正向" : "反向") + "当前公交到达正向终点，开始反向运行| 车号：" + this.getBusNumber() + "车上乘客数:" + this.getPassengers().size());
        this.setDirection(2);
        fanXiang();
    }

    private void daozhan(Station currentStation) throws InterruptedException {
        //正向到站 先下后上
        //模拟汽车故障 10分之一概率
        int brokenFlag = RandomUtil.randomInt(1, 11);
        if (brokenFlag == 1) {
            //公交故障拉
            System.out.println("当前时间："+DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + " 故障！全体乘客下车,乘客上车站改为当前站 " + currentStation.getIndex());
            passengerStrategy.busBroken(currentStation, this);
            System.out.println("当前时间："+DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN)+"| "+"线程号：" + Thread.currentThread().getId() + " " + "车号：" + this.getBusNumber() + " 故障！当前修车30秒！ 在站台: " + currentStation.getIndex());
            TimeUnit.SECONDS.sleep(30);
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
