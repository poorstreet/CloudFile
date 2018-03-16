package xin.webgo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import  com.alibaba.fastjson.JSON;
/**
 * Servlet implementation class ListVideos
 */
@WebServlet("/ListFiles")
public class ListFiles extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListFiles() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		String fileDir = File.separator +"WEB-INF"+File.separator +"UploadFile"+File.separator;
		ServletContext context = request.getServletContext();
		String realPath = context.getRealPath(fileDir);
		if(realPath == null) {
			return;
		}
		HashMap<String,String> filesMap = new HashMap<String, String>();
		//获取文件夹下文件文件名及类型<name,type>
		filesMap = (HashMap<String, String>) FileOperator.readDir(realPath);
		//转成json字符串发送
		String jsonString = JSON.toJSONString(filesMap);
		//设置编码格式utf-8
		response.setCharacterEncoding("utf-8");
		//通知浏览器使用utf-8
		response.setContentType("text/html;charset=UTF-8"); 
		PrintWriter writer = response.getWriter();
		writer.write(jsonString);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
