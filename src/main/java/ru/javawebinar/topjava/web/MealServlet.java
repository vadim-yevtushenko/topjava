package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.model.MealsDB;
import ru.javawebinar.topjava.repo.MealRepoImpl;
import ru.javawebinar.topjava.repo.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

@WebServlet("/meals")
public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private MealRepository repository = new MealRepoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String paramAction = req.getParameter("action");
//        String paramId = Objects.requireNonNull(req.getParameter("id"));
        String paramId = req.getParameter("id");

        switch (paramAction == null ? "default" : paramAction) {
            case "delete": {
                repository.delete(Long.valueOf(paramId));
                resp.sendRedirect("meals");
                break;
            }
            case "create":
            case "update": {
                final Meal meal = "create".equals(paramAction) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        repository.get(Long.valueOf(paramId));
                req.setAttribute("meal", meal);
                req.getRequestDispatcher("/meal.jsp").forward(req, resp);
                break;
            }
            case "default":
            default: {
                Collection<Meal> list = repository.getAll();
                List<MealTo> mealsTo = MealsUtil.filteredByStreams(list
                        , LocalTime.of(0, 0), LocalTime.of(23, 59), 2000);

                log.debug("redirect to meals " + mealsTo.size());

                req.setAttribute("list", mealsTo);

                req.getRequestDispatcher("/meals.jsp").forward(req, resp);
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String paramId = req.getParameter("id");

        Meal meal = new Meal(paramId.isEmpty() ? null : Long.valueOf(paramId)
                , LocalDateTime.parse(req.getParameter("dateTime"))
                , req.getParameter("description")
                , Integer.parseInt(req.getParameter("calories")));
        repository.save(meal);
        resp.sendRedirect("meals");
    }
}
