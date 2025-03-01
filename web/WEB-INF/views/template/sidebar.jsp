<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/sidebar.css">
    <script src="<%=request.getContextPath()%>/resources/assets/js/sidebar.js" defer></script>
</head>
<body>
<aside class="sidebar" id="sidebar">
    <div class="sidebar-logo">
        <img src="<%=request.getContextPath()%>/resources/assets/images/logo.png" alt="Logo" />
    </div>
    <ul class="nav-menu">
        <%
            if (session.getAttribute("user") != null) {
                %>
                <li>
                    <a class="list">Gestion des vols <div class="menu-icon"></div></a>
                    <div class="submenu">
                        <ul>
                            <li><a href="<%=request.getContextPath()%>/vols">Lister/Chercher</a></li>
                            <li><a href="<%=request.getContextPath()%>/vols/create">Creer un vol</a></li>
                        </ul>
                    </div>
                </li>
                <li><a href="<%=request.getContextPath()%>/admin/promotions">Promotions</a></li>
                <li>
                    <a class="list">Parametres <div class="menu-icon"></div></a>
                    <div class="submenu">
                        <ul>
                            <li><a href="<%=request.getContextPath()%>/admin/parametres/regles-reservation">Regles de reservation</a></li>
                        </ul>
                    </div>
                </li>
                <%
            } else {
                %>
                <li><a href="<%=request.getContextPath()%>/vols">Liste des vols</a></li>
                <%
            }
        %>
    </ul>
</aside>

</body>
</html>