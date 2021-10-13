<%@ page import="ru.javawebinar.topjava.model.Meal" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Meal</title>
</head>
<body>
<h3><a href="meals">Meals</a></h3>
<hr>
<h2>Edit meal</h2>
<form id="saveMeal">
<p>Date Time:      <input type="datetime-local" class="form-row" class="form-control" id="inputDateTime">
</p>
<p>Description:    <input type="text" class="form-control" id="inputDescription" placeholder="Description">
</p>
<p>Calories:       <input type="number" class="form-control" id="inputCalories" placeholder="Calories">
</p>
<button type="button" onclick="function processSave(contextPath) {

        }
        processSave('${pageContext.request.contextPath}')" class="btn btn-success">
    Save
</button>
<button type="button" onclick="function processCancel(contextPath) {

        }
        processCancel('${pageContext.request.contextPath}')" class="btn btn-success">
    Cancel
</button>

</form>
</body>
</html>
