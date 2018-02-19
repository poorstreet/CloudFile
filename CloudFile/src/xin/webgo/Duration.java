package xin.webgo;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * Application Lifecycle Listener implementation class Duration
 *监听数据库Mongo.file判断其保存日期是否已到，已到则删除文件
 */
//@WebListener
public class Duration implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public Duration() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event)  { 
         // TODO Auto-generated method stub
    	Timer timer = new Timer();
    	Task task = new Task(event);
    	timer.schedule(task, 1000, 60000);
    }
	class Task extends TimerTask{
        ServletContextEvent event = null;
        public Task(ServletContextEvent event) {
        	this.event = event;
        }
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//ServletContext context = event.getServletContext();
			//Enumeration<String> attributes = context.getAttributeNames();
			 ServerAddress serverAddress = new ServerAddress("localhost",27017);
		     MongoCredential credential = MongoCredential.createCredential("file", "CloudFile", "hadoop".toCharArray());
		     Builder builder = new Builder();
		     MongoClientOptions opt = builder.build();
		     //密码验证
		     MongoClient mongoClient = new MongoClient(serverAddress,credential,opt);
		     MongoDatabase mongodatabase = mongoClient.getDatabase("CloudFile");
		     MongoCollection<Document> collection = mongodatabase.getCollection("file");
		     FindIterable<Document> findIterable = collection.find();
		     if(findIterable!=null) {
		    	 MongoCursor<Document> mongoCursor = findIterable.iterator();  
		    	 while(mongoCursor.hasNext()){ 
		    		 String Path = mongoCursor.next().getString("path");
		    		 File file = new File(Path);
		    		 if(((new Date()).getTime() - file.lastModified())>Long.parseLong(mongoCursor.next().getString("duration").toString())*60000) {
							System.out.println("时间耗尽");
							Document query = new Document("path",Path);
							collection.findOneAndDelete(query);
							file.delete();
		            }
		
	            }
           }
		     
		   mongoClient.close();
      }
	}
	}

		