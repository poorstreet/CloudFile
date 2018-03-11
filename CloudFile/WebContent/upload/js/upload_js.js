/**
 * 文件上传模块
 * 
 */
var quence = new Array();//待上传的文件队列，包含切块的文件
var uuidArray = new Array();//文件UUID值
var filesArray;//文件数组，保存输入的文件对象
var total;//上传总数据量
var totalProgress=0;//总上传进度

/**
* 用户选择文件之后的响应函数，将文件信息展示在页面，同时对大文件的切块大小、块的起止进行计算、入列等
*/
function showFileList(files) {
	//清空总上传数据
	total = 0;
  if(!files) {
   return;
  }
  var list = $("#filesList");
  list.empty();
  filesArray = files;
  $(filesArray).each(function(idx,e){
	 $("<p>"+e.name+"</p>").appendTo($("#filesList"));
	 console.log(e.name);
	 //重新计算上传数据
	 total += e.size;
  });
  console.log(files.length);  
  }
  
 
/**
 * 从服务器获取文件UUID
 * @returns
 */
function getUUID(){
	var len = document.getElementById("files").files.length;
	console.log("${pageContext.request.contextPath}");
	uuids = $.ajax({
		type:"POST",
		url:"/CloudFile/UuidServlet",
		data:{"filesNum":len},
		async:false
		}).responseText;
	uuidArray = uuids.split(",");
	console.log(uuidArray);
}
/**
 * 文件分片
 * @returns
 */
function tokenFile(){
	 var chunkSize = 5 * 1024 * 1024;  //切块的阀值：5M
	 $(filesArray).each(function(idx,e){
		 //alert(e.name);
	    //切块划分
		 var fileSize = e.size;
	     var chunks = Math.floor(fileSize / chunkSize)+1;//分割块数
	     for(var i=0 ; i<chunks; i++) {
	    	 var startIdx = i*chunkSize;//块的起始位置
	    	 var endIdx = startIdx+chunkSize;//块的结束位置
	    	 if(endIdx > fileSize) {
	    		 endIdx = fileSize;
	    	 }
	    	 //封装成一个task对象，入列
	    	 var task = {
	    			 file:e,
	    			 uuid:uuidArray[idx],//避免文件的重名导致服务端无法定位文件，需要给每个文件生产一个UUID
	    			 startIdx:startIdx,
	    			 endIdx:endIdx,
	    			 currChunk:i,
	    			 totalChunk:chunks
	    	 };
	    	 quence.push(task);
	     }	
	  	});
}
/**
*  上传器，绑定一个XMLHttpRequest对象，处理分配给其的上传任务
**/
function Uploader(name) {
	this.url="/CloudFile/UploadHander_Ajax";    //服务端处理url
	this.req = new XMLHttpRequest();
	this.tasks; //任务队列
	this.taskIdx = 0; //当前处理的tasks的下标
	this.name=name;
	this.status=0;  //状态，0：初始；1：所有任务成功；2：异常
	this.progress=0;
	//上传 动作
	this.upload = function(uploader) {
		this.req.responseType = "json";
		//上传进度监听
		/*this.req.upload.onprogress = function(event){
			uploader.progress=event.loaded;
			//console.log(progress+"  "+total);
		};*/
		//注册load事件（即一次异步请求收到服务端的响应）
		this.req.addEventListener("load", function(){
			//更新对应的进度条
			var preTask = uploader.tasks[uploader.taskIdx-1];
			uploader.progress += preTask.endIdx - preTask.startIdx;
			//progressUpdate(this.response.uuid, this.response.fileSize);
			//从任务队列中取一个再次发送
			var task = uploader.tasks[uploader.taskIdx];
			if(task) {
				console.log(uploader.name + "：当前执行的任务编号：" +uploader.taskIdx);
				this.open("POST", uploader.url);
				this.send(uploader.buildFormData(task));
				uploader.taskIdx++;
			} else {
				console.log("处理完毕");
				uploader.status=1;
			}
     	});

		//处理第一个
		var task = uploader.tasks[uploader.taskIdx];
		if(task) {
			console.log(uploader.name + "：当前执行的任务编号：" +uploader.taskIdx);
			this.req.open("POST", uploader.url);
			this.req.send(uploader.buildFormData(task));
			this.taskIdx++;
		} else {
			uploader.status=1;
		}
	}

	//提交任务给Uploader
	this.submit = function(tasks) {
		this.tasks = tasks;
	}

	//构造表单数据
	this.buildFormData = function(task) {
		var file = task.file;
		var slice = file.slice(task.startIdx, task.endIdx);
		var formData = new FormData();
		formData.append("fileName", file.name);
		formData.append("fileSize", file.size);
		formData.append("uuid", task.uuid);
		formData.append("currChunk", task.currChunk);
		formData.append("totalChunk", task.totalChunk);
		formData.append("data", slice);//截取文件块
		
		return formData;
	}
	
 }
/**
 * 绘制上传进度
 * @returns
 */
function drawProgress(progress){
	var drawing=document.getElementById("drawing");  
	//确定浏览器支持<canvas>元素  
	if(drawing.getContext){  
	//取得绘图上下文对象的引用，“2d”是取得2D上下文对象  
	var context=drawing.getContext("2d");  
	context.strokeStyle="red"; 
	context.clearRect(0,0,drawing.width,drawing.height);
	//绘制进度文本 
	context.font="bold 14px Arial";  
	context.textAlign="center";  
	context.textBaseline="middle";//文本的基线  
	context.fillText(progress+"%",50,50); 
	//开始路径  
	context.beginPath();  
	//绘制外圆
	context.arc(50,50,50,0,2*Math.PI,false);
	//绘制内圆
	context.arc(50,50,43,0,2*Math.PI*progress/100,false);
	context.stroke(); 
	}
}

/**
*“上传”按钮响应函数，
*/
function doUpload() {
	//清空已上传数据
	progress = 0;
	//上传按钮失效
	$("input").attr("disabled",true);
	//请求服务器，获取文件uuid
	getUUID();
	//文件分片
	tokenFile();
	//创建4个Uploader上传器（4条线程）
	
	var uploader0 = new Uploader("uploader0");
	var task0 = new Array();

	var uploader1 = new Uploader("uploader1");
	var task1 = new Array();

	var uploader2 = new Uploader("uploader2");
	var task2 = new Array();

	var uploader3 = new Uploader("uploader3");
	var task3 = new Array();

  //将文件列表取模hash，分配给4个上传器
	for(var i=0 ; i<quence.length; i++) {
		if(i%4==0) {
			task0.push(quence[i]);
		} else if(i%4==1) {
			task1.push(quence[i]);
		} else if(i%4==2) {
			task2.push(quence[i]);
		} else if(i%4==3) {
			task3.push(quence[i]);
		}
	}
  //提交任务，启动线程上传
	uploader0.submit(task0);
	uploader0.upload(uploader0);    
	uploader1.submit(task1);
	uploader1.upload(uploader1);    
	uploader2.submit(task2);
	uploader2.upload(uploader2);    
	uploader3.submit(task3);
	uploader3.upload(uploader3);    
  //注册一个定时任务，每1秒监控文件是否都上传完毕
	uploadCompleteMonitor = setInterval(function(){
		totalProgress = uploader0.progress + uploader1.progress + uploader2.progress + uploader3.progress;
		console.log(totalProgress + "  " + total);
		var rate =Math.floor(totalProgress/ total*100);
		drawProgress(rate);
		$("#message").text("分片正在上传。。。");
		if(uploader0.status==1 && uploader1.status == 1 && uploader2.status == 1 && uploader3.status == 1 && rate == 100){
			$("#message").text("分片上传成功！正在合并文件。。。");
			$.ajax({
				type:"Get",
				url:"/CloudFile/MergeFileServlet",
				data:"",
				async:true,
				success:function(data,textStatus){
					$("#message").text("文件合并成功，成功上传！")	
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					$("#message").text("文件合并失败，请重新上传！")
			       }
				});	
			clearInterval(uploadCompleteMonitor);
		}
	},1000);
 }