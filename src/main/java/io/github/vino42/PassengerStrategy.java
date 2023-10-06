package io.github.vino42;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        List<User> toUpPassenger = list.stream().sorted(Comparator.comparingInt(o -> o.getPriority().get())).toList();

        if (passengers.size() < 29) {
            List<User> users;
            int shengyu = 29 - passengers.size();
            if (!toUpPassenger.isEmpty()) {

                if (shengyu > toUpPassenger.size()) {
                    users = toUpPassenger;
                } else {
                    users = toUpPassenger.subList(0, shengyu + 1);
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
            TimeUnit.SECONDS.sleep(users.size() * 10L);
            System.out.println("线程号："+Thread.currentThread().getId()+"车号：" + bus.getBusNumber() + (bus.getDirection() == 1 ? "正向" : "反向") + "乘客上车| 当前站" + currentStation.getIndex() + "上车乘客数" + users.size());


        }


    }

    @Override
    public void offPassenger(Station currentStation, Bus bus) throws InterruptedException {
        List<User> passengers = bus.getPassengers();
        List<User> offPassenger = passengers.stream().filter(d -> d.getTarget().getIndex().equals(currentStation.getIndex())).toList();
        //下车
        passengers.removeAll(offPassenger);
        bus.setPassengers(passengers);
        //正常来说 该故障车需要停顿
//        TimeUnit.SECONDS.sleep(offPassenger.size() * 10L);
        System.out.println("线程号："+Thread.currentThread().getId()+"车号：" + bus.getBusNumber() + (bus.getDirection() == 1 ? "正向" : "反向") + "乘客下车| 当前站" + currentStation.getIndex() + "下车乘客数" + offPassenger.size());
    }

    @Override
    public void busBroken(Station currentStation, Bus bus) {
        List<User> passengers = bus.getPassengers();
        //设置当前乘客上车地点为当前站
        List<User> list = passengers.stream().peek(d -> d.addPriority()).map(u -> {
            u.setUpStation(currentStation);
            return u;
        }).toList();
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
            return true;
        }
        return false;
    }
}
