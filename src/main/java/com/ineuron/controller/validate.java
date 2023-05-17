package com.ineuron.controller;

import com.mysql.cj.jdbc.Driver;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "validate", value = "/validate",
initParams ={
        @WebInitParam(name="URL",value = "jdbc:mysql://localhost:3306/ineuron"),
        @WebInitParam(name="username",value = "root"),
        @WebInitParam(name="password",value ="Password")
})
public class validate extends HttpServlet {
    private Connection connection = null ;
    Driver driver = null ;
    static {
        System.out.println("INITIALIZING THE SERVLET .");
    }

    @Override
    public void init() throws ServletException {
        System.out.println("CONNECTING TO DATABASE ");
        try {
            driver = new Driver();
            DriverManager.registerDriver(driver);

            connection = DriverManager.getConnection(getInitParameter("URL"),getInitParameter("username"),getInitParameter("password"));

        } catch ( SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(connection!=null){
            PreparedStatement preparedStatement = null;
                try {

                    if(request.getHeader("referer").equals("http://localhost:8080/LoginValidatorUsingServletJDBC_war_exploded/Login.html")){
                        preparedStatement = connection.prepareStatement("select COUNT(*) from login where id = ? and password = ?");
                        preparedStatement.setString(1,request.getParameter("usern"));
                        preparedStatement.setString(2,request.getParameter("pass"));
                        ResultSet rs = preparedStatement.executeQuery();
                        if(rs.next())
                            ValidateAndRedirect(response,rs.getInt(1));


                    }else {
                        preparedStatement = connection.prepareStatement("insert into login values(?,?);");
                        preparedStatement.setString(1,request.getParameter("usern"));
                        preparedStatement.setString(2,request.getParameter("pass"));
                        ValidateAndRedirect(response,preparedStatement.executeUpdate());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();


                }


            }


    }
    public void ValidateAndRedirect(HttpServletResponse response,int row_count) throws IOException {
        if(row_count > 0 ){
            response.sendRedirect("./Success.html");
        }else{
            response.sendRedirect("./Error.html");
        }
    }
}