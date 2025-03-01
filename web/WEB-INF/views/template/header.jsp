<%@ page import="mg.etu2624.ticketing.model.Utilisateur" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/resources/assets/images/logo.png" type="image/x-icon">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/header.css">
</head>
<body>

    <div class="header">
        <%
            if(session.getAttribute("user") != null){
                %>
                <div><%=((Utilisateur)session.getAttribute("user")).getNom()%></div>
                <%
            }else{
                %>
                <div>Ticketing</div>
                <%
            }
        %>
        <nav>
            <%
                if(session.getAttribute("user") != null){
                    %>
                    <a href="<%=request.getContextPath()%>/admin/connexion">Connexion</a>
                    <a href="<%=request.getContextPath()%>/admin/deconnexion">Deconnexion</a>   
                    <%
                }
            %>
        </nav>
    </div>

</body>
</html>