package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

        List<UserMealWithExcess> mealsTo2 = filteredByOneCycle(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo2.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        final Map<LocalDate, Integer> sumCaloriesPerDay = new HashMap<>();

        meals.forEach(userMeal -> sumCaloriesPerDay.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum));

        final List<UserMealWithExcess> result = new ArrayList<>();
        meals.forEach(userMeal -> {
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)){
                result.add(fromUserMealToUserMealWithExcess(userMeal
                        , sumCaloriesPerDay.get(LocalDate.from(userMeal.getDateTime()))
                        , caloriesPerDay));
            }
        });

        return result;
    }

    public static List<UserMealWithExcess> filteredByOneCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> result = new ArrayList<>();
        ArrayList<UserMeal> mealArrayList = new ArrayList<>(meals);
        Map<LocalDate, Integer> sumCaloriesPerDay = new HashMap<>();
        for (int i = 0; i < mealArrayList.size(); i++) {
            if (i < meals.size()) {
                sumCaloriesPerDay.put(LocalDate.from(mealArrayList.get(i).getDateTime())
                        , mealArrayList.get(i).getCalories() + sumCaloriesPerDay.getOrDefault(LocalDate.from(mealArrayList.get(i).getDateTime())
                                , 0));
                mealArrayList.add(mealArrayList.get(i));
            } else {
                if (TimeUtil.isBetweenHalfOpen(mealArrayList.get(i).getDateTime().toLocalTime(), startTime, endTime)){
                    result.add(fromUserMealToUserMealWithExcess(mealArrayList.get(i)
                            , sumCaloriesPerDay.get(LocalDate.from(mealArrayList.get(i).getDateTime()))
                            , caloriesPerDay));
                }
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> sumCaloriesPerDay = meals.stream().collect(Collectors.groupingBy(userMeal -> userMeal.getDateTime().toLocalDate(),
                Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                        sumCaloriesPerDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
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

    public static UserMealWithExcess fromUserMealToUserMealWithExcess(UserMeal userMeal,int sumCaloriesOfUserMealPerDay, int caloriesPerDay) {
        if (sumCaloriesOfUserMealPerDay > caloriesPerDay) {
            return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), true);
        }
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), false);
    }
}
