<%@ page import="compiler.Tables" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="compiler.Lexeme" %><%--
  Created by IntelliJ IDEA.
  User: santos
  Date: 17.03.16
  Time: 17:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Result</title>
    <link type="text/css" media="all" rel="stylesheet" href="css/bootstrap.css" />
    <link type="text/css" media="all" rel="stylesheet" href="css/bootstrap.min.css" />
    <link type="text/css" media="all" rel="stylesheet" href="css/main.css" />
</head>
<body>
<%
    Tables tables = (Tables) session.getAttribute("tables");
    ArrayList<Lexeme> result = (ArrayList<Lexeme>) session.getAttribute("result");
    ArrayList<String> errors = (ArrayList<String>) session.getAttribute("errors");
    HashMap<String, Integer> keyWord = tables.getKeyWords();
    HashMap<String, Integer> identifiers = tables.getIdentifiersTable();
    HashMap<String, Integer> constants = tables.getConstTable();
    HashMap<String, Integer> multiDelimiters = tables.getMultiCharDelimiters();

    Set<String> keyWordKeys = keyWord.keySet();
    Set<String> identifierKeys = identifiers.keySet();
    Set<String> constKeys = constants.keySet();
    Set<String> delimiterKeys = multiDelimiters.keySet();
%>
<section class="my-container">
    <div class="my-table">
        <h4>Key words</h4>
        <table>
            <tr>
                <th>lexeme</th>
                <th>code</th>
            </tr>
            <%for (String key: keyWordKeys){%>
                <tr>
                    <td><%=key%></td>
                    <td><%=keyWord.get(key)%></td>
                </tr>
            <%}%>
        </table>
    </div>
    <div class="my-table">
        <h4>Identifiers</h4>
        <table>
            <tr>
                <th>lexeme</th>
                <th>code</th>
            </tr>
            <%for (String key: identifierKeys){%>
            <tr>
                <td><%=key%></td>
                <td><%=identifiers.get(key)%></td>
            </tr>
            <%}%>
        </table>
    </div>
    <div class="my-table">
        <h4>Constants</h4>
        <table>
            <tr>
                <th>lexeme</th>
                <th>code</th>
            </tr>
            <%for (String key: constKeys){%>
            <tr>
                <td><%=key%></td>
                <td><%=constants.get(key)%></td>
            </tr>
            <%}%>
        </table>
    </div>
    <div class="my-table">
        <h4>Multi char delimiters</h4>
        <table>
            <tr>
                <th>lexeme</th>
                <th>code</th>
            </tr>
            <%for (String key: delimiterKeys){%>
            <tr>
                <td><%=key%></td>
                <td><%=multiDelimiters.get(key)%></td>
            </tr>
            <%}%>
        </table>
    </div>
    <div class="result">
        <%for (Lexeme code: result) {%>
            <span><%=code.getLexCode()%> :
                [<%=code.getStringIndex()%>, <%=code.getCharIndex()%>]</span>
        <%}%>
    </div>

    <div class="result">
        <%for (String error: errors) {%>
            <span>error: <%=error%></span><br/>
        <%}%>
        <a href="index.jsp"><button class="btn btn-primary" type="button">to home page</button></a>
        <%if (errors.size() == 0) {%>
        <a href="tables.jsp"><button class="btn btn-success" type="button">view tables</button></a>
        <a href="tree.jsp"><button class="btn btn-success" type="button">view tree</button></a>
        <%}%>
    </div>

</section>
</body>
</html>
