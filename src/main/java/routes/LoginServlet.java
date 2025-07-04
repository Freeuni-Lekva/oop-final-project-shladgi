package routes;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //TODO
        //handle login
        System.out.println("aaa");
    }

}
