package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.model.MealsDB;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
@WebServlet("/meals")
public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<MealTo> mealsTo = MealsUtil.filteredByStreams(MealsDB.meals
                , LocalTime.of(0, 0), LocalTime.of(23, 59), 2000);

        log.debug("redirect to meals " + mealsTo.size());

        req.setAttribute("list", mealsTo);

        req.getRequestDispatcher("meals.jsp").forward(req, resp);
    }
}
