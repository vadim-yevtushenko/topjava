package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class MyCollector implements Collector<UserMeal, Map.Entry<List<UserMeal>, Map<LocalDate, Integer>>, List<UserMealWithExcess>> {

    private LocalTime startTime;
    private LocalTime endTime;
    private int caloriesPerDay;

    public MyCollector(LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.caloriesPerDay = caloriesPerDay;
    }

    @Override
    public Supplier<Map.Entry<List<UserMeal>, Map<LocalDate, Integer>>> supplier() {
        return () -> new AbstractMap.SimpleImmutableEntry<>(
                new ArrayList<>(), new HashMap<>());
    }

    @Override
    public BiConsumer<Map.Entry<List<UserMeal>, Map<LocalDate, Integer>>, UserMeal> accumulator() {
        return (map, meal) -> {
            map.getKey().add(meal);
            map.getValue().merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        };
    }

    @Override
    public BinaryOperator<Map.Entry<List<UserMeal>, Map<LocalDate, Integer>>> combiner() {
        return (map1, map2) -> {
            map1.getKey().addAll(map2.getKey());
            return map1;
        };
    }

    @Override
    public Function<Map.Entry<List<UserMeal>, Map<LocalDate, Integer>>, List<UserMealWithExcess>> finisher() {
        return c -> {
            ArrayList<UserMealWithExcess> result = new ArrayList<>();
            for (UserMeal userMeal : c.getKey()) {
                if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                    if (c.getValue().get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay){
                        result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), true));
                    }
                    else {
                        result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), false));
                    }
                }
            }
            return result;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }
}
