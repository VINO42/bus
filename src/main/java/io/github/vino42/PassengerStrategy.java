package io.github.vino42;

import cn.hutool.core.date.DateUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.github.vino42.Constants.MAX_PASSGNGER_COUNT;

/**
 * =====================================================================================
 *
 * @Created :   2023/10/5 17:48
 * @Compiler :  jdk 17
 * @Author :    VINO
 * @Email : VINO
 * @Copyright : VINO
 * @Decription :
 * =====================================================================================
 */
public class PassengerStrategy implements Strategy {

    @Override
    public void loadPassenger(Station currentStation, Bus bus) throws InterruptedException {
        List<User> waitingPassenger = currentStation.getWaitingPassenger();
        List<User> passengers = bus.getPassengers();
        List<User> list = waitingPassenger.stream().filter(d -> d.getDirection().equals(bus.getDirection())).toList();
        //优先保障故障车乘客
        List<User> toUpPassenger = list.stream().filter(d -> d.getPriority().get() > 0).sorted(Comparator.comparing(o -> o.getPriority().get())).toList();
        //当前车上乘客没满
        List<User> users = new ArrayList<>();
        if (passengers.size() < MAX_PASSGNGER_COUNT) {
            //算出来剩余的空位数
            int shengyu = MAX_PASSGNGER_COUNT - passengers.size();
            //如果故障车乘客有的话
            if (!toUpPassenger.isEmpty()) {
                //剩余空位数大于故障车数优先故障乘客上车
                if (shengyu > toUpPassenger.size()) {
                    users = toUpPassenger;
                    System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + bus.getBusNumber() + (bus.getDirection() == 1 ? "正向" : "反向") + "故障乘客上车| 当前站" + currentStation.getIndex() + "上车故障乘客数" + toUpPassenger.size());
                    //故障乘客上车后,还有空余位置。填充下正常乘客
                    List<User> normalUser = list.stream().filter(d -> !toUpPassenger.contains(d)).toList();
                    List<User> normalUserToLoad = normalUser.subList(0, shengyu - toUpPassenger.size());
                    users.addAll(normalUserToLoad);
                    System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + bus.getBusNumber() + (bus.getDirection() == 1 ? "正向" : "反向") + "正常乘客上车| 当前站" + currentStation.getIndex() + "上车正常乘客数" + normalUserToLoad.size());
                } else {
                    //剩余空位装不下故障乘客,那就把能装的全装上
                    users = toUpPassenger.subList(0, shengyu + 1);
                    System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + bus.getBusNumber() + (bus.getDirection() == 1 ? "正向" : "反向") + "故障乘客上车| 当前站" + currentStation.getIndex() + "上车故障乘客数" + users.size());
                }
            } else {
                if (shengyu > list.size()) {
                    users = list;
                } else {
                    users = list.subList(0, shengyu + 1);
                }
            }
            //上车
            passengers.addAll(users);
            bus.setPassengers(passengers);
            //站台减人
            currentStation.getWaitingPassenger().removeAll(users);
        }
        TimeUnit.SECONDS.sleep(users.size() * 10L);
        System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + bus.getBusNumber() + (bus.getDirection() == 1 ? "正向" : "反向") + "乘客上车| 当前站" + currentStation.getIndex() + "上车乘客数" + users.size());


    }

    @Override
    public void offPassenger(Station currentStation, Bus bus) throws InterruptedException {
        List<User> passengers = bus.getPassengers();
        List<User> offPassenger = passengers.stream().filter(d -> d.getTarget().getIndex().equals(currentStation.getIndex())).toList();
        //下车
        passengers.removeAll(offPassenger);
        bus.setPassengers(passengers);
        //正常来说 该故障车需要停顿
        TimeUnit.SECONDS.sleep(10L);
        System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + bus.getBusNumber() + (bus.getDirection() == 1 ? "正向" : "反向") + "乘客下车| 当前站" + currentStation.getIndex() + "下车乘客数" + offPassenger.size());
    }

    @Override
    public void busBroken(Station currentStation, Bus bus) {
        List<User> passengers = bus.getPassengers();
        //设置当前乘客上车地点为当前站
        List<User> list = passengers.stream().peek(User::addPriority).peek(u -> u.setUpStation(currentStation)).toList();
        //当前站的乘客排队列别添加下车乘客
        currentStation.getWaitingPassenger().addAll(list);
        //全员下车
        bus.setPassengers(new ArrayList<>());

    }

    @Override
    public boolean onFinalStation(Station curretStation, Bus bus) {
        boolean finalStation = curretStation.isFinalStation(bus);
        if (finalStation) {
            //到终点站了 所有乘客要下车。
            bus.setPassengers(new ArrayList<>());
            System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "线程号：" + Thread.currentThread().getId() + " " + "车号：" + bus.getBusNumber() + (bus.getDirection() == 1 ? "正向" : "反向") + "终点站乘客下车| 当前站" + curretStation.getIndex() + "终点站下车乘客数" + bus.getPassengers().size());
            return true;
        }
        return false;
    }
}
