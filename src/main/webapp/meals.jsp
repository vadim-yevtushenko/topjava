<%@ page import="ru.javawebinar.topjava.model.MealTo" %>
<%@ page import="java.util.List" %>
<%@ page import="ru.javawebinar.topjava.util.MealsUtil" %>
<%@ page import="ru.javawebinar.topjava.model.MealsDB" %>
<%@ page import="java.time.LocalTime" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="ru">
<head>
    <title>Meal list</title>
    <style type="text/css">
        TABLE {
            border-collapse: collapse; /* Убираем двойные линии между ячейками */
        }

        TD, TH {
            padding: 3px; /* Поля вокруг содержимого таблицы */
            border: 1px solid black; /* Параметры рамки */
        }
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>


<a href="meal?action=create"/>Add Meal</a>
<br>
<br>


<table>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th>&nbsp;</th>
        <th>&nbsp;</th>
    </tr>


    <jsp:useBean id="list" scope="request" type="java.util.List"/>
    <c:forEach var="meal" items="${list}">

        <c:if test="${meal.excess}">
            <c:set var="color" value="color:#FF0000"/>
        </c:if>

        <c:if test="${!meal.excess}">
            <c:set var="color" value="color:#00FF00"/>
        </c:if>

        <tr style="${color}">
            <td>

                <fmt:parseDate value="${meal.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime"
                               type="both"/>
                <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${parsedDateTime}"/>

            </td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><a href="meal?action=update">Update </a></td>
            <td><a href="meals?action=delete">Delete</a></td>
        </tr>

    </c:forEach>

</table>

</body>
</html>