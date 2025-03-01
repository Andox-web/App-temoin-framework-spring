<%@ page import="mg.itu.prom16.validation.BindingResult" %>
<%@ page import="mg.itu.prom16.validation.Field" %>
<%@ page import="mg.etu2624.ticketing.controller.LoginController.UtilisateurLogin" %>
<%
    BindingResult result = (BindingResult) request.getAttribute("value");
    boolean isResult = result != null && result.hasErrors();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/color-admin.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/login.css">
</head>
<body>
    <div class="login-container">
        <h2>Admin Login</h2>
        <form method="post">
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="text" id="email" name="email" value='<%= isResult && result.getErrors("email") != null ? result.getErrors("email").getFieldValue() : "" %>'>
                <% if (isResult) { %>
                    <span class="error-message"><%= result.getErrors("email") != null ? result.getErrors("email").getFirstError() : "" %></span>
                <% } %>
            </div>
            <div class="form-group">
                <label for="text">Password:</label>
                <input type="password" id="password" name="password">
                <% if (isResult) { %>
                    <span class="error-message"><%= result.getErrors("password") != null ? result.getErrors("password").getFirstError() : "" %></span>
                <% } %>
            </div>
            <button type="submit">Login</button>
            <div class="error-message">
                <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
            </div>
        </form>
    </div>
</body>
</html>
