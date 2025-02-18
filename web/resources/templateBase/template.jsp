<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Template Page</title>
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
                <!-- Main content goes here -->
            </div>
            <%@ include file="footer.jsp" %>
        </main>
    </div>
</body>
</html>
