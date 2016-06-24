<%--
  Created by IntelliJ IDEA.
  User: santos
  Date: 4/18/16
  Time: 10:52 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0);
%>
<html>
<head>
    <link type="text/css" media="all" rel="stylesheet" href="css/tree.css" />
    <link type="text/css" media="all" rel="stylesheet" href="css/bootstrap.css" />
    <link type="text/css" media="all" rel="stylesheet" href="css/bootstrap.min.css" />
    <script type="text/javascript" src="js/jquery.js"></script>
    <script type="text/javascript" src="js/tree.js"></script>
</head>
<body>
<section class="my-container">
    <section id="buttons">
        <a href="output.jsp"><button class="btn btn-primary" type="button">back to result</button></a>
    </section>
    <section id="tree"></section>
</section>
</body>
</html>
