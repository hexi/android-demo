package com.hexi;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hexi.model.DaBai;
import com.hexi.model.LineType;
import com.hexi.model.Navigation;
import com.hexi.model.Person;
import com.hexi.model.ServerDomain;
import com.hexi.model.Xiong2;

import org.apache.http.conn.ConnectTimeoutException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hexi on 15/7/3.
 */
public class UnitTest {

    class LastAvg {
        public float value = Float.NaN;
    }

    @Test
    public void testUnit() {
        LastAvg f = new LastAvg();
        f.value = 10;
        update(f);
        System.out.println(f.value);
    }

    private void update(LastAvg f) {
        f.value = 20;
    }

    @Test
    public void test1() {
        List<Integer> ages = Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Optional<Integer> first = FluentIterable.from(ages).firstMatch(new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input > 0;
            }
        });
        System.out.println(first.get());
    }

    @Test
    public void test2() throws InterruptedException {
        LineType currentLineType = LineType.avg;
        final String lineType = currentLineType.value;
        currentLineType = LineType.avg2d;
        final LineType fLineType = currentLineType;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("lineType: " + lineType + ", currentLineType: " + fLineType);
            }
        }, 2000);

        Thread.sleep(3000);
    }

    @Test
    public void test3() {
        DaBai daBai = newInstance(DaBai.class);
        System.out.println(daBai.getClass().getSimpleName());
    }

    @Test
    public void test4() {
        Xiong2 xiong2 = newInstance(Xiong2.class);
        System.out.println(xiong2.getClass().getSimpleName());
    }

    private static <T extends Person> T newInstance(Class<T> clazz) {
        T t = null;
        try {
            t = (T) clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Test
    public void test5() {
        DateTime now = DateTime.now().withMonthOfYear(4);
        System.out.println(now);
        DateTime dateTime = now.plusMonths(1).withDayOfMonth(1).plusDays(-1);

        System.out.println(dateTime);
    }

    @Test
    public void test6() throws InterruptedException {
        long now = System.currentTimeMillis();
        String s = String.format("%1$TY-%1$Tm-%1$Td %1$TH:%1$TM:%1$TS", now);
        System.out.println(s);
    }

    @Test
    public void test7() {
        DateTime now = DateTime.now();
        System.out.println(now);
        addOneDay(now);
        System.out.println(now);
    }

    private void addOneDay(DateTime now) {
        now = now.plusDays(1);
        System.out.println("addOneDay:" + now);
    }

    @Test
    public void test8() {
        Throwable throwable = new ConnectTimeoutException();
        System.out.println(throwable.getClass().getSimpleName());
        boolean isInterruptedIOException = throwable.getClass().getSimpleName().equals("InterruptedIOException");
        System.out.println(isInterruptedIOException);
    }

    @Test
    public void test9() {
        int minutesOfLineType = 120;
        DateTime dateTime = new DateTime(1447223986977L);
        System.out.println(dateTime);
        dateTime = dateTime.withSecondOfMinute(0).withMillisOfSecond(0);
        int minutes = dateTime.getMinuteOfHour();
        System.out.println(minutes);
        minutes = minutes - (minutes % minutesOfLineType) + minutesOfLineType;
        System.out.println(minutes);
        dateTime = dateTime.withMinuteOfHour(0).plusMinutes(minutes);
        System.out.println(dateTime);

    }

    @Test
    public void test10() throws InterruptedException {
        //结论多线程的情况下，一个线程遍历，另一个线程同时对这个arrayList新增或删除都会导致ConcurrentModificationException
        final List<Integer> ids = new ArrayList<>();
        ids.add(0);
        ids.add(1);
        ids.add(2);
        ids.add(3);

        DateTime timestamp = DateTime.now().plusSeconds(1);
        CountDownLatch countDownLatch = new CountDownLatch(2);

        concurrentTest(new Function() {
            @Override
            public void apply() {
                for (Integer id : ids) {
                    System.out.println(id + DateTime.now().toString());
                }
            }
        }, timestamp, countDownLatch);

        concurrentTest(new Function() {
            @Override
            public void apply() {
                System.out.println(DateTime.now().toString());
                ids.add(5);
                ids.add(6);
                ids.add(6);
            }
        }, timestamp, countDownLatch);
        countDownLatch.await();
        System.out.println(ids.size());
    }

    @Test
    public void test11() throws InterruptedException {
        //结论多线程的情况下，一个线程遍历，另一个线程同时对这个arrayList新增或删除都会导致ConcurrentModificationException
        final List<Integer> ids = new CopyOnWriteArrayList<>();
        ids.add(0);
        ids.add(1);
        ids.add(2);
        ids.add(3);

        DateTime timestamp = DateTime.now().plusSeconds(1);
        CountDownLatch countDownLatch = new CountDownLatch(2);

        concurrentTest(new Function() {
            @Override
            public void apply() {
                Iterator<Integer> it = ids.iterator();
                while (it.hasNext()) {
                    System.out.println(it.next() + "," + DateTime.now().toString());
                }
            }
        }, timestamp, countDownLatch);

        concurrentTest(new Function() {
            @Override
            public void apply() {
                System.out.println(DateTime.now().toString());
                ids.add(5);
                ids.add(6);
                ids.add(6);
            }
        }, timestamp, countDownLatch);
        countDownLatch.await();
        System.out.println(ids.size());
    }

    @Test
    public void test12() {
        long start = System.currentTimeMillis();
        new Date();
        System.out.println("time:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void test13() {
        long start = System.currentTimeMillis();
//        new DateTime(System.currentTimeMillis());
        DateTime.now();
        System.out.println("time:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void test14() throws JSONException {
        Navigation navigation = new Navigation();
        System.out.println(new Gson().toJson(navigation));
    }

    static interface Function {
        public void apply();
    }

    private void concurrentTest(final Function task, DateTime dateTime, final CountDownLatch countDownLatch) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                task.apply();
                countDownLatch.countDown();
            }
        }, dateTime.toDate());
    }

    @Test
    public void test15() {
        String s = "20151221105045";
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        DateTime dt = formatter.parseDateTime(s);
        System.out.println(dt);
    }

    @Test
    public void test16() {
        double value1 = 100;
        double value2 = 2;
        double value3 = 5;
        double value4 = 10;
        double ret = new FluentBigDecimal()
                .with(value1)
                .div(value2)
                .add(new FluentBigDecimal().with(value4).div(value3).value())
                .value();
        System.out.println(ret);
    }

    @Test
    public void test17() {
        double value1 = 100;
        double value2 = 2;
        double value3 = 5;
        double value4 = 10;
        System.out.println(value1 / value2 + value4 / value3);
    }

    @Test
    public void test18() {
        String a = "3";
        System.out.println(Double.parseDouble(a));
    }

    @Test
    public void test19() {
        double value = 12.3;
        int a = 5;
        String s = String.format("value:%f, int:%d", value, a);
        System.out.println(s);
    }

    @Test
    public void test20() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String clazz = "com.hexi.model.DaBai";
        Constructor constructor = Class.forName(clazz).getConstructor(new Class[]{String.class});
        Person person = (Person)constructor.newInstance("方灿");
        System.out.println(new Gson().toJson(person));
    }

    @Test
    public void test21() {
        String value = String.format("快跟我躺着赚钱！加入一周%s%%收益计划，这个分析师有点准！", "16");
        System.out.println(value);
    }

    @Test
    public void test22() {
        Map<String, String> map = new HashMap();
        map.put("1", "a");
        map.put("2", "b");

        Collection<String> values = map.values();
        String json = new Gson().toJson(values);
        System.out.println(json);
        List<String> list = new Gson().fromJson(json, new TypeToken<List<String>>() {
        }.getType());
        System.out.println(new Gson().toJson(list));
    }

    @Test
    public void test23() {
        String a = "android.resource://%s/raw/";
        String b = String.format(a, "com.baidao.ytxmobile");
        System.out.println(b);
    }

    @Test
    public void test24() {
        String json = "{\"quotes\":\"http://weixin2.baidao.com/assets/openAccountYG\",\"www\":\"http://az.mobile-static-service.baidao.com/activity.list/activity.list.html\"}";
        ServerDomain serverDomain = new Gson().fromJson(json, ServerDomain.class);
        System.out.println(new Gson().toJson(serverDomain));
    }

    public static int[] toArray(List<Integer> value) {
        if (value == null) {
            return new int[0];
        }
        Integer[] boxedArray = value.toArray(new Integer[0]);
        int len = boxedArray.length;
        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            array[i] = boxedArray[i].intValue();
        }
        return array;
    }

    @Test
    public void testMath() {
        double result = 1D / 3D;
        System.out.println(result);
    }

    @Test
    public void testAdd() {
        double d0 = 0.06D;
        double d1 = 0.01D;
        System.out.println(d0+d1);
    }
}
