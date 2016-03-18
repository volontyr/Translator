package servlet;

import compiler.LexicalParser;

import java.io.IOException;
import java.io.InputStream;


import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.*;

/**
 * Created by santos on 17.03.16.
 */
@WebServlet(urlPatterns = "/compile")
@MultipartConfig
public class CompileServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Part file = request.getPart("file");
        InputStream is = file.getInputStream();
        LexicalParser lexicalParser = new LexicalParser(is);
        lexicalParser.parser();
        HttpSession session = request.getSession();
        session.setAttribute("tables",lexicalParser.getTables());
        session.setAttribute("result", lexicalParser.getLexCodesResultArray());
        response.sendRedirect("output.jsp");
        return;

    }

}
