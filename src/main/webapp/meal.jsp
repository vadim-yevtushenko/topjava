<%@ page import="ru.javawebinar.topjava.model.Meal" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Meal</title>
</head>
<body>
<h3><a href="meals">Meals</a></h3>
<hr>
<h2>${param.action == 'create' ? 'Create m' : 'Edit m'}</h2>
<jsp:useBean id="m" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
<form method="post" action="meals">
    <p>Date Time: <input type="datetime-local" value="${m.dateTime}" name="dateTime" required>
    </p>
    <p>Description: <input type="text" value="${m.description}" name="description" placeholder="Description" required>
    </p>
    <p>Calories: <input type="number" value="${m.calories}" name="calories" placeholder="Calories" required>
    </p>
    <button type="submit">Save</button>
    <button type="button" onclick="window.history.back()">
        Cancel
    </button>

</form>
</body>
</html>


