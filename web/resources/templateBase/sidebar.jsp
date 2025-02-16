
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/sidebar.css">
    <script src="<%=request.getContextPath()%>/assets/js/sidebar.js" defer></script>
</head>
<body>
<aside class="sidebar">
    <div class="sidebar-logo">
        <img src="<%=request.getContextPath()%>/assets/images/logo.png" alt="Logo" />
    </div>
    <ul class="nav-menu">
        <li><a href="#">Home</a></li>
        <li><a href="#">About</a></li>
        <li>
            <a href="#">Services</a>
            <ul class="sub-menu">
                <li><a href="#">Service 1</a></li>
                <li><a href="#">Service 2</a></li>
                <li><a href="#">Service 3</a></li>
            </ul>
        </li>
        <li><a href="#">Contact</a></li>
    </ul>
</aside>

</body>
</html>