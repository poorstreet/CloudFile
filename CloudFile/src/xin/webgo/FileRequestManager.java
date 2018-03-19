package xin.webgo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FileRequestManager
 */
@WebServlet("/FileRequestManager")
public class FileRequestManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileRequestManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		request.setCharacterEncoding("utf-8");
		String file = request.getParameter("file");
		String action = request.getParameter("action");
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8"); 
		PrintWriter writer = response.getWriter();
		if(action.equals("play")) {
			writer.write("<video width=\"100%\" height=\"100%\" autoplay=\"true\" controls>\r\n" + 
					" <source src=\"VideoPlaySevlet?video="+file+"\" type=\"video/mp4\">\r\n" + 
					"您的浏览器不支持Video标签。\r\n" + 
					"</video>");
		}else {
			writer.write(file+action);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
