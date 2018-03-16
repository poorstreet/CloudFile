package xin.webgo;

import java.io.*;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class UploadHander_Ajax
 */
@WebServlet("/UploadHander_Ajax")
public class UploadHander_Ajax extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadHander_Ajax() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		ServletContext context = request.getServletContext();
		String uploadTemp =context.getRealPath("/WEB-INF/UploadTemp/");
		String tempPath = context.getRealPath("/WEB-INF/temp/");
		File tmpFile = new File(tempPath);
	     if (!tmpFile.exists()) {
	      //创建临时目录
	    	 tmpFile.mkdir();
	     }
		  //使用Apache文件上传组件处理文件上传步骤：
		   //1、创建一个DiskFileItemFactory工厂
		      DiskFileItemFactory factory = new DiskFileItemFactory();
		      //设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
		      factory.setSizeThreshold(1024*100);//设置缓冲区的大小为100KB，如果不指定，那么缓冲区的大小默认是10KB
		      //设置上传时生成的临时文件的保存目录
		      factory.setRepository(tmpFile);
		      //2、创建一个文件上传解析器
		      ServletFileUpload upload = new ServletFileUpload(factory);
		      upload.setHeaderEncoding("UTF-8"); 
		      //3、判断提交上来的数据是否是上传表单的数据
		      if(!ServletFileUpload.isMultipartContent(request)){
		       //按照传统方式获取数据
		       return;
		      }
		    
		      //设置上传单个文件的大小的最大值，目前是设置为1024*1024字节，也就是1MB
		      upload.setFileSizeMax(10*1024*1024);
		      //设置上传文件总量的最大值，最大值=同时上传的多个文件的大小的最大值的和，目前设置为10MB
		      upload.setSizeMax(10*1024*1024);
		      //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
		      List<FileItem> list;
		      HashMap<String,String> map = new HashMap<String,String>();
		      InputStream in = null;
		      try { 
		      list = upload.parseRequest(request);	      
		      for(FileItem item : list){
		       //如果fileitem中封装的是普通输入项的数据
		    	  if(item.isFormField()){
		    		  String name = item.getFieldName();
		    		  String value = item.getString("UTF-8");
		    		  map.put(name, value);
		    		  System.out.print(name);
		    	  }
		    	  else{//如果fileitem中封装的是上传文件
		    		  in = item.getInputStream();
		    		  String tempSavePath = makePath(uploadTemp,map.get("uuid"),map.get("fileName"),map.get("currChunk"));
		    		  FileOutputStream out = new FileOutputStream(tempSavePath);
		    		  byte buffer[] = new byte[1024];
					        //判断输入流中的数据是否已经读完的标识        
					  int len = 0;
					        //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
					  while((len=in.read(buffer))>0){
					       out.write(buffer, 0, len);
					  }
					 in.close();
					 out.close(); 
					 item.delete();
		    	  }		    	 
		      } 
		      }catch (FileUploadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	public String makePath(String temPath,String uuid,String fileName,String currChunk) {
	     
		 String filename = fileName.substring(0, fileName.lastIndexOf("."));
		//得到上传文件的扩展名
	     String fileExtName = fileName.substring(fileName.lastIndexOf("."));
	     String dir = temPath+uuid+File.separator;
	     File file = new File(dir);
		  //如果目录不存在
		   if(!file.exists()){
		    //创建目录
		    file.mkdirs();
		  }
		return dir+filename+"_"+currChunk+fileExtName;
	}

}
