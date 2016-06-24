package servlet;

import compiler.Generator;
import compiler.LexicalParser;
import compiler.SyntaxParser;

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
        
        if (lexicalParser.getErrors().size() == 0) {
            SyntaxParser syntaxParser = new SyntaxParser(lexicalParser.getLexCodesResultArray(),
                    lexicalParser.getTables());
            syntaxParser.parser();
            session.setAttribute("identifierTable", syntaxParser.getIdentifiersTable());
            session.setAttribute("constTable", syntaxParser.getConstTable());
            session.setAttribute("errors", syntaxParser.getErrors());
            if (syntaxParser.getErrors().size() == 0) {
                Generator generator = new Generator(syntaxParser.getParseTree(), lexicalParser.getTables());
                generator.generate();
            }
        } else {
            session.setAttribute("errors", lexicalParser.getErrors());
        }
        session.setAttribute("tables", lexicalParser.getTables());
        session.setAttribute("result", lexicalParser.getLexCodesResultArray());
        response.sendRedirect("output.jsp");
        return;

    }

}
