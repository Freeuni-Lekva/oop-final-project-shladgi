package routes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.UserField;
import databases.implementations.QuizDB;
import databases.implementations.UserDB;
import objects.Quiz;
import objects.user.User;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.QUIZDB;
import static utils.Constants.USERDB;

@WebServlet("/quizzes")
public class QuizzesServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/quizzes.html").forward(request, response);
    }



    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        ServletContext context = req.getServletContext();
        UserDB userDB = (UserDB) context.getAttribute(USERDB);
        QuizDB quizDB = (QuizDB) context.getAttribute(QUIZDB);

        String userIdStr = req.getSession().getAttribute("userid")==null?null:req.getSession().getAttribute("userid").toString();
        Integer userid = userIdStr==null?null:Integer.parseInt(userIdStr);
        int id = userid==null? -1 : userid;

        try {
            // Parse JSON request body
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            Gson gson = new Gson();
            QuizRequest qr = gson.fromJson(requestBody, QuizRequest.class);

            // Validate request parameters
            if (qr.page < 1) qr.page = 1;
            if (qr.pageSize < 1) qr.pageSize = 8;

            List<FilterCondition<QuizField>> filters = getFilters(qr, id);

            List<Quiz> quizes = quizDB.query(filters,QuizField.CREATIONDATE,true,qr.pageSize,(qr.page-1)*qr.pageSize);
            int size = quizDB.query(new FilterCondition<>(QuizField.ID, Operator.NOTEQ,-1)).size();
            int totalPages = (int) Math.ceil((double) size / qr.pageSize);



            JsonObject json = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            for (Quiz quiz : quizes) {
                List<User> users = userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS,quiz.getUserId()));
                if(users.isEmpty()) continue;
                String name = users.getFirst().getUserName();
                JsonObject quizJson = new JsonObject();
                quizJson.addProperty("id", quiz.getId());
                quizJson.addProperty("title", quiz.getTitle());
                quizJson.addProperty("createDate",  quiz.getCreationDate().toString());
                quizJson.addProperty("creatorName", name);
                quizJson.addProperty("creatorid", quiz.getUserId());
                jsonArray.add(quizJson);
            }
            json.add("quizzes", jsonArray);
            json.addProperty("totalPages", totalPages);

            System.out.println(json.toString());
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");

            res.getWriter().write(json.toString());

        }catch (Exception e){
            System.out.println("quizzes servlet"+e.getMessage());
        }

    }

    private static class QuizRequest {
        public int page;
        public int pageSize;
        public String creator;
        public String dateFrom;
        public String dateTo;
        public String searchQuery;
    }

    private static class QuizResponse {
        public List<Quiz> quizzes;
        public int totalPages;
    }

    private List<FilterCondition<QuizField>> getFilters(QuizRequest qr, int userid) {
        List<FilterCondition<QuizField>> ans= new ArrayList<>();

        if(qr.creator.equals("Me")) {
            ans.add(new FilterCondition<>(QuizField.USERID, Operator.EQUALS, userid));
        } else if (qr.creator.equals("other")) {
            ans.add(new FilterCondition<>(QuizField.USERID, Operator.NOTEQ,userid ));
        }
        ans.add(new FilterCondition<>(QuizField.TITLE, Operator.LIKE, "%"+qr.searchQuery+"%"));
        ans.add(new FilterCondition<>(QuizField.DESCRIPTION, Operator.LIKE, "%"+qr.searchQuery+"%"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        if(qr.dateFrom != null && qr.dateFrom.length()>0) {
            String format = formatter.format(qr.dateFrom);
            ans.add(new FilterCondition<>(QuizField.CREATIONDATE, Operator.MOREEQ, format));
        }
        if(qr.dateTo != null && qr.dateTo.length()>0) {
            String format = formatter.format(qr.dateTo);
            ans.add(new FilterCondition<>(QuizField.CREATIONDATE, Operator.LESSEQ,format));
        }

        return ans;
    }




}