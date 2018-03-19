package xin.webgo;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FileDownload
 */
@WebServlet("/FileDownload")
public class FileDownload extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileDownload() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		String fileName = request.getParameter("file"); 
		String fileSaveRootPath=this.getServletContext().getRealPath("/WEB-INF/UploadFile/");
		//得到要下载的文件
		File file = new File(fileSaveRootPath + fileName);
		//如果文件不存在
		if(!file.exists()){
		request.setAttribute("message", "您要下载的资源已被删除！！");
		request.getRequestDispatcher("/message.jsp").forward(request, response);
		return;
		}
		 GetFile.getFileOffline(file, request, response);
		}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
