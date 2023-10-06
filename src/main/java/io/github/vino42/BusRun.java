package io.github.vino42;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * =====================================================================================
 *
 * @Created :   2023/10/5 13:29
 * @Compiler :  jdk 17
 * @Author :    VINO
 * @Email : VINO
 * @Copyright : VINO
 * @Decription :
 * =====================================================================================
 */
public class BusRun {
    private static List<Station> busLine;

    private static Integer MAX_NUM = 15;

    private static Random RANDOM = new Random();

    static ScheduledThreadPoolExecutor zhengxiang = new ScheduledThreadPoolExecutor(10);
    static ScheduledThreadPoolExecutor fanxiang = new ScheduledThreadPoolExecutor(10);
    static Map<Integer, Integer> direct = new HashMap<>() {
        {
            put(1, 5);
            put(2, 6);
            put(3, 7);
            put(4, 8);
            put(5, 4);
            put(6, 3);
            put(7, 6);
            put(8, 5);
            put(9, 6);
            put(10, 7);
            put(11, 4);
            put(12, 3);
            put(13, 6);
            put(14, 3);
        }
    };
    static Map<Integer, Integer> reverse = new HashMap<>() {
        {
            put(2, 4);
            put(3, 7);
            put(4, 5);
            put(5, 6);
            put(6, 3);
            put(7, 4);
            put(8, 5);
            put(9, 3);
            put(10, 7);
            put(11, 4);
            put(12, 5);
            put(13, 4);
            put(14, 5);
            put(15, 4);

        }
    };
    static ScheduledThreadPoolExecutor busLinePassenger = new ScheduledThreadPoolExecutor(15);

    static {

        List<Station> list = IntStream.range(1, 16).mapToObj(index -> {
            Station station = new Station();
            station.setIndex(index);
            station.setName(String.valueOf(index));

            if (index == 1) {
                station.setBeforeIndex(null);
            } else {
                station.setBeforeIndex(index - 1);
            }

            if (index == MAX_NUM) {
                station.setNextIndex(null);
            } else {
                station.setNextIndex(index + 1);
            }
            if (direct.get(index) != null) {
                station.setForwardDirectTime(direct.get(index) + RANDOM.nextInt(2));
            }
            if (reverse.get(index) != null) {
                station.setReverseDirectTime(reverse.get(index) + RANDOM.nextInt(2));
            }
            return station;
        }).toList();
        for (int i = 0; i < list.size() - 1; i++) {
            Station station = list.get(i);
            if (i != 0) {
                station.setBefore(list.get(i + 1));
            }
            if (i != list.size() - 1) {
                station.setNext(list.get(i + 1));
            }
        }
        busLine = list;

        //每五分钟 生成10个随机站点有目的地的乘客分配到各个车站
        busLinePassenger.scheduleAtFixedRate(() -> {
            List<User> users = IntStream.range(1, 101).mapToObj(num -> {
                User user = User.initUser();
                return user;
            }).toList();
            ListIterator<Station> it = busLine.listIterator();
            while (it.hasNext()) {
                Station next = it.next();
                List<User> users1 = users.stream().filter(d -> d.getUpStation().getIndex().equals(next.getIndex()) && d.getDirection() == 1).toList();
                int size = next.getWaitingPassenger().size();
                next.getWaitingPassenger().addAll(users1);
                System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "乘客排队等车中| 当前站" + next.getIndex() + "正向添加等待上车乘客数" + users1.size() + " 原有乘客等待数： " + size);
            }
            while (it.hasPrevious()) {
                Station next = it.previous();
                List<User> user2 = users.stream().filter(d -> d.getUpStation().getIndex().equals(next.getIndex()) && d.getDirection() == 2).toList();
                int size = next.getWaitingPassenger().size();
                next.getWaitingPassenger().addAll(user2);
                System.out.println("当前时间：" + DateUtil.format(new Date(), Constants.CUSTOM_NORM_TIME_PATTERN) + "| " + "乘客排队等车中| 当前站" + next.getIndex() + "反向添加等待上车乘客数" + user2.size() + " 原有乘客等待数： " + size);
            }

        }, 1L, 60L, TimeUnit.SECONDS);
    }


    public static void main(String[] args) {


        List<Bus> fiveBus = IntStream.range(1, 21).mapToObj(index -> {
            Bus bus = new Bus();
            bus.setBusNumber(index);
            bus.setDirver(new Driver("司机-" + RandomUtil.randomString(4)));
            bus.setPassengers(new ArrayList<>());
            bus.setDirection(1);
            bus.setBusLine(busLine);
            return bus;
        }).toList();
        List<Bus> nextFiveBus = IntStream.range(20, 31).mapToObj(index -> {
            Bus bus = new Bus();
            bus.setBusNumber(index);
            bus.setDirver(new Driver("司机-" + RandomUtil.randomString(4)));
            bus.setPassengers(new ArrayList<>());
            bus.setDirection(2);
            bus.setBusLine(busLine);
            return bus;
        }).toList();
        //每15分钟发正向一辆车  这里是为了测试改为30秒跑一次
        fiveBus.forEach(bus -> {
            bus.setStartTime(System.currentTimeMillis());
            zhengxiang.schedule(bus, 30, TimeUnit.SECONDS);
        });
        //每15分钟发反向一辆车 这里是为了测试改为30秒跑一次
        nextFiveBus.forEach(bus -> {
            bus.setStartTime(System.currentTimeMillis());
            fanxiang.schedule(bus, 30, TimeUnit.SECONDS);
        });
    }
}
