package routes;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserField;
import databases.filters.fields.UserTokenField;
import databases.implementations.UserDB;
import databases.implementations.UserTokenDB;
import objects.user.User;
import objects.user.UserToken;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static utils.Constants.USERDB;
import static utils.Constants.USERTOKENDB;

@WebFilter("/*")
public class RememberMeFilter implements Filter {
    @Override
    public void doFilter(ServletRequest requestt, ServletResponse responset, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) requestt;
        HttpServletResponse response = (HttpServletResponse) responset;

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userid") == null) {
            String token = getRememberMeToken(request);
            if (token != null) {
                User user = validateTokenAndGetUser(request, token);
                if (user != null) {
                    session = request.getSession(true);
                    session.setAttribute("userid", user.getId());
                    session.setAttribute("username", user.getUserName());
                    session.setAttribute("type", user.getType());
                }
            }
        }

        chain.doFilter(request, response);
    }

    private String getRememberMeToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("rememberMe".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private User validateTokenAndGetUser(HttpServletRequest request, String token) {
        UserTokenDB tokenDB = (UserTokenDB) request.getServletContext().getAttribute(USERTOKENDB);
        UserDB userDB = (UserDB) request.getServletContext().getAttribute(USERDB);

        List<UserToken> tokens = tokenDB.query(List.of(
                new FilterCondition<>(UserTokenField.TOKEN, Operator.EQUALS, token),
                new FilterCondition<>(UserTokenField.EXPIREDATE, Operator.MOREEQ, LocalDateTime.now().toString())
        ));

        if (tokens.isEmpty()) return null;

        int userId = tokens.get(0).getUserid();
        List<User> users = userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS, userId));
        return users.isEmpty() ? null : users.get(0);
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy(){}
}

