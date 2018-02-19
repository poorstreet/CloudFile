<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
  <!--导入Mongo驱动包-->
  <%@ page language="java" import="com.mongodb.*,com.mongodb.MongoClient,com.mongodb.client.*,com.mongodb.MongoClientOptions.Builder,org.bson.Document,java.util.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<% 
//MongoClient mongoClient = new MongoClient("localhost",27017);
ServerAddress serverAddress = new ServerAddress("localhost",27017);
//MongoDatabase mongodatabase = mongoClient.getDatabase("CloudFile");
MongoCredential credential = MongoCredential.createCredential("file", "CloudFile", "hadoop".toCharArray());
//mongodatabase.authenticate("file","hadoop".toCharArray());
Builder builder = new Builder();
MongoClientOptions opt = builder.build();
MongoClient mongoClient = new MongoClient(serverAddress,credential,opt);
MongoDatabase mongodatabase = mongoClient.getDatabase("CloudFile");
String name = mongodatabase.getName();
//mongodatabase.createCollection("collection");
MongoCollection<Document> collection = mongodatabase.getCollection("file");
/* Document filenote = new Document("", "MongoDB").  
append("description", "database").  
append("likes", 100).  
append("by", "Fly");  
collection.insertOne(filenote); 
Document document = new Document("title", "MongoDB").  
append("description", "database").  
append("likes", 100).  
append("by", "Fly");  
List<Document> documents = new ArrayList<Document>();  
documents.add(document);  
collection.insertMany(documents); */  
FindIterable<Document> findIterable = collection.find();  
MongoCursor<Document> mongoCursor = findIterable.iterator();  
while(mongoCursor.hasNext()){  
   out.println(mongoCursor.next());  
}  
mongoClient.close();
%>
<%=name%>
</body>
</html>