<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %><%--
  Created by IntelliJ IDEA.
  User: santos
  Date: 4/20/16
  Time: 4:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link type="text/css" media="all" rel="stylesheet" href="css/bootstrap.css" />
    <link type="text/css" media="all" rel="stylesheet" href="css/bootstrap.min.css" />
    <link type="text/css" media="all" rel="stylesheet" href="css/main.css" />
    <link type="text/css" media="all" rel="stylesheet" href="css/tables.css" />
</head>
<body>
<%
    HashMap<String, Integer> identifiers = (HashMap<String, Integer>) session.getAttribute("identifierTable");
    HashMap<String, Integer> constants = (HashMap<String, Integer>) session.getAttribute("constTable");

    Set<String> identifierKeys = identifiers.keySet();
    Set<String> constKeys = constants.keySet();
%>
<section class="my-container">
    <div class="my-table">
        <h4>Identifiers Table</h4>
        <table>
            <tr>
                <th>identifier</th>
                <th>constant</th>
            </tr>
            <%for (String key: identifierKeys){%>
            <tr>
                <td><%=key%></td>
                <td><%=identifiers.get(key)%></td>
            </tr>
            <%}%>
            <%for (String key: constKeys){%>
            <tr>
                <td><%=key%></td>
                <td><%=constants.get(key)%></td>
            </tr>
            <%}%>
        </table>
    </div>
    <br/>
    <section id="buttons">
        <a href="output.jsp"><button class="btn btn-primary" type="button">back to result</button></a>
    </section>
</section>
</body>
</html>
