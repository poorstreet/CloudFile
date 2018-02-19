package xin.webgo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MergeFieServlet
 */
@WebServlet(description = "用于文件分片的合并", urlPatterns = { "/MergeFieServlet" })
public class MergeFieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MergeFieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	/**
	 * 文件合并方法
	 * @param fpath：需合并的文件所在的文件夹路劲
	 * @param resultPath：合并后文件所在的路径
	 * @return 0：输入目录或输出目录有误；1：文件合并成功；2：文件操作异常，合并失败
	 */
	protected  int mergeFiles(String fpath, String resultPath) {
		if(fpath == null|| resultPath == null) {		
			System.out.println("输入或输出目录为空");
			return 0;
		}
		File inputDir = new File(fpath);
		File outputFile = new File(resultPath);
		if(!outputFile.exists()) {
			outputFile.mkdirs();
		}
		if(inputDir.exists() && inputDir.isDirectory()) {
			//获取输入目录下的文件
			String[] files = inputDir.list();
			if(files == null) {
				System.out.println("输入目录没有文件分片");
				return 0;		
			}
			Arrays.sort(files);
			try {
		        FileChannel resultFileChannel = new FileOutputStream(outputFile, true).getChannel();
		        for(String filePath:files ) {
		        	File chunk= new File(filePath);
		        	FileChannel chunkChannel = new FileInputStream(chunk).getChannel();
			        resultFileChannel.transferFrom(chunkChannel, resultFileChannel.size(), chunkChannel.size());
			        chunkChannel.close();
		        }
		        resultFileChannel.close();
		        return 1;
			} catch (FileNotFoundException e) {
		        e.printStackTrace();
		        return 2;
		    } catch (IOException e) {
		        e.printStackTrace();
		        return 2;
		    }
		}else {
			return 0;
		}					
	}
	protected boolean belongToFile(String chunk,String file) {
		
	}
}
