package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collector;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> result = new ArrayList<>();
        Map<LocalDate, Integer> sumCaloriesPerDay = new HashMap<>();
        for (UserMeal meal : meals) {
            sumCaloriesPerDay.put(LocalDate.from(meal.getDateTime()), meal.getCalories() + sumCaloriesPerDay.getOrDefault(LocalDate.from(meal.getDateTime()), 0));
        }
        for (UserMeal userMeal : meals) {
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)){
                result.add(fromUserMealToUserMealWithExcess(userMeal
                        , sumCaloriesPerDay.get(LocalDate.from(userMeal.getDateTime()))
                        , caloriesPerDay));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        return meals.stream()
                .collect(new MyCollector(startTime, endTime, caloriesPerDay));
    }

    public static UserMealWithExcess fromUserMealToUserMealWithExcess(UserMeal userMeal,int sumCaloriesOfUserMealPerDay, int caloriesPerDay) {
        if (sumCaloriesOfUserMealPerDay > caloriesPerDay) {
            return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), true);
        }
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), false);
    }

    public static Collector<UserMeal, ?, List<UserMealWithExcess>> partitioningByLimitCaloriesPerDay(LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return Collector.<UserMeal, Map.Entry<List<UserMeal>, Map<LocalDate, Integer>>, List<UserMealWithExcess>>of(
                () -> new AbstractMap.SimpleImmutableEntry<>(
                        new ArrayList<>(), new HashMap<>()),
                (map, meal) -> {
                    map.getKey().add(meal);
                    map.getValue().merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
                },
                (map1, map2) -> {
                    map1.getKey().addAll(map2.getKey());
                    return map1;
                },
                c -> {
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
                });
    }
}
