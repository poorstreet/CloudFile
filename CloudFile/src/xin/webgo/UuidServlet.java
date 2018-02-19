package xin.webgo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UuidServlet
 */
@WebServlet(description = "为每个上传文件提供UUID", urlPatterns = { "/UuidServlet" })
public class UuidServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UuidServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		//读取提交的数据
		int fileNum=Integer.parseInt(request.getParameter("filesNum"));
		StringBuilder stringBuilder=new StringBuilder();
		while(fileNum>0) {
			String uuid = UUID.randomUUID().toString();	
			stringBuilder.append(uuid).append(",");
			fileNum--;
		}
		String uuids = stringBuilder.substring(0, stringBuilder.length()-1);
		PrintWriter writer = response.getWriter();
		writer.write(uuids);
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
