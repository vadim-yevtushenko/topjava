package ru.javawebinar.topjava.repo;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;
import java.util.List;

public interface MealRepository {

    void save(Meal meal);

    void delete(Long id);

    Meal get(Long id);

    Collection<Meal> getAll();
}
