<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <%
        if(session.getAttribute("user") != null){
            %>
            <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/color-admin.css">
            <%
        }else{
            %>
            <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/color-user.css">
            <%
        }
        if(request.getAttribute("title") != null){
            %>
            <title><%=request.getAttribute("title")%></title>
            <%
        }
    %>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/assets/css/content.css">
</head>
<body>
    <div class="layout">
        <aside>
            <%@ include file="sidebar.jsp" %>
        </aside>
        <main class="main-content">
            <%@ include file="header.jsp" %>
            <div class="content">
                <jsp:include page="content.jsp" />
            </div>
            <%@ include file="footer.jsp" %>
        </main>
    </div>
</body>
</html>
