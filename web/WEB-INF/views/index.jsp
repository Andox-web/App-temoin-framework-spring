<%@ page import="mg.etu2624.ticketing.model.Utilisateur" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ticketing</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/index.css">
</head>
<body>
    <div class="page-index">
        <div class="container">
            <h1>Bienvenue sur Ticketing</h1>
            <p class="message"><%=request.getAttribute("message")%></p>
        </div>
    </div>
</body>
</html>

