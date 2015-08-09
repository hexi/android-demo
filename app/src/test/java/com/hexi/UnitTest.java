package com.hexi;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hexi.model.LineType;
import com.hexi.model.Person;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
    public void testGson() {
        String json = "{age: 1}";
        Person person = new Gson().fromJson(json, Person.class);

        System.out.println(person.getAge());
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
}
