package xin.webgo;

import java.io.File;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class DurationListener
 *
 */
//@WebListener
public class DurationListener implements ServletContextAttributeListener {

    /**
     * Default constructor. 
     */
    public DurationListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextAttributeListener#attributeAdded(ServletContextAttributeEvent)
     */
    public void attributeAdded(ServletContextAttributeEvent event)  { 
         // TODO Auto-generated method stub
    	ServletContext application = event.getServletContext();  
    	//获取添加的属性名和属性值  
    	String name = event.getName();  
        String duration = event.getValue().toString();  
        //String filePath="";
        File file=null;
        System.out.println(application + "范围内添加了名为"+ name + "，值为" + duration + "天的属性!"); 
        if(name.contains("upload")) {
        //filePath = application.getRealPath(name);
        file = new File(name);
        System.out.println("保存路径"+name);
        System.out.println(file.lastModified()+"-"+(new Date()).getTime()+"="+((new Date()).getTime()-file.lastModified()));
        }
        /*while(true) {
        	if(((new Date()).getTime()-file.lastModified())>Long.parseLong(duration)){
        		System.out.print("保存时间结束");
        		//file = new File(name);
        		file.delete();
        		application.removeAttribute(name);
        	}
       }*/
     }


	/**
     * @see ServletContextAttributeListener#attributeRemoved(ServletContextAttributeEvent)
     */
    public void attributeRemoved(ServletContextAttributeEvent event)  { 
         // TODO Auto-generated method stub
    	ServletContext application = event.getServletContext();  
    	//获取添加的属性名和属性值  
    	String name = event.getName();  
        Object value = event.getValue();  
        System.out.println(application + "范围内删除了名为"+ name + "，值为" + value + "的属性!"); 
    }

	/**
     * @see ServletContextAttributeListener#attributeReplaced(ServletContextAttributeEvent)
     */
    public void attributeReplaced(ServletContextAttributeEvent event)  { 
         // TODO Auto-generated method stub
    	ServletContext application = event.getServletContext();  
    	//获取添加的属性名和属性值  
    	String name = event.getName();  
        Object value = event.getValue();  
        System.out.println(application + "范围内更改了名为"+ name + "，值为" + value + "的属性!"); 
    }
	
}
