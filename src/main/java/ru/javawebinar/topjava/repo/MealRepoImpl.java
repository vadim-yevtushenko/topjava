package ru.javawebinar.topjava.repo;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealsDB;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MealRepoImpl implements MealRepository{

    private final Map<Long, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    {
        MealsDB.meals.forEach(this::save);
    }

    @Override
    public void save(Meal meal) {
        if (meal.getId() == null){
            meal.setId(counter.incrementAndGet());
        }
        repository.put(meal.getId(), meal);
    }

    @Override
    public void delete(Long id) {
        repository.remove(id);
    }

    @Override
    public Meal get(Long id) {
        return repository.get(id);
    }

    @Override
    public Collection<Meal> getAll() {
        return repository.values();
    }
}
