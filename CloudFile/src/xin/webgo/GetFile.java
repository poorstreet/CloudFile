package xin.webgo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 获取服务器文件 定义了两种获取方式 getFileInline(File file, HttpServletRequest request,
 * HttpServletResponse response) getFileOffline(File file, HttpServletRequest
 * request, HttpServletResponse response) 都支持部分文件获取，用于支持断点续传，文件在线打开等功能
 * 
 * @author chc
 *
 */
public class GetFile {
	//
	public static void getFileInline(File file, HttpServletRequest request, HttpServletResponse response) {
		getFile(file, request, response, false);

	}

	public static void getFileOffline(File file, HttpServletRequest request, HttpServletResponse response) {
		getFile(file, request, response, false);
	}

	private static boolean getFile(File file, HttpServletRequest request, HttpServletResponse response,
			boolean isOnline) {
		long pos = headerSetting(file, request, response, isOnline);
		ServletOutputStream os = null;
		BufferedOutputStream out = null;
		RandomAccessFile raf = null;
		byte b[] = new byte[1024];// 暂存容器
		try {
			os = response.getOutputStream();
			out = new BufferedOutputStream(os);
			raf = new RandomAccessFile(file, "r");
			raf.seek(pos);
			try {
				int n = 0;
				while ((n = raf.read(b, 0, 1024)) != -1) {
					out.write(b, 0, n);
				}
				out.flush();
			} catch (IOException ie) {
				// 记录异常

			}
		} catch (Exception e) {
			// log.error(e.getMessage(), e);

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// log.error(e.getMessage(), e);
				}
			}
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					// log.error(e.getMessage(), e);
				}
			}
		}
		return true;
	}

	private static long headerSetting(File file, HttpServletRequest request, HttpServletResponse response,
			boolean isDownload) {
		long len = file.length();// 文件长度
		if (null == request.getHeader("Range")) {
			setResponse(new RangeSettings(len), file.getName(), response);
			return 0;
		}
		String range = request.getHeader("Range").replaceAll("bytes=", "");
		RangeSettings settings = getSettings(len, range, isDownload);
		setResponse(settings, file.getName(), response);
		return settings.getStart();
	}

	private static void setResponse(RangeSettings settings, String fileName, HttpServletResponse response) {
		if (settings.isDownload()) {
			response.addHeader("Content-Disposition", "attachment; filename=\"" + IoUtil.toUtf8String(fileName) + "\"");
		} else {
			response.addHeader("Content-Disposition", "inline; filename=\"" + IoUtil.toUtf8String(fileName) + "\"");
		}
		response.setContentType(IoUtil.setContentType(fileName));// set the MIME type.
		// 如果没有rang，请求整个文件
		if (!settings.isRange()) {
			response.addHeader("Content-Length", String.valueOf(settings.getTotalLength()));
		} else {
			long start = settings.getStart();
			long end = settings.getEnd();
			long contentLength = settings.getContentLength();
			response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);// 通知客户端为部分数据
			response.addHeader("Content-Length", String.valueOf(contentLength));// 本次传输的总数据
			// Content-Range: bytes (unit first byte pos) - [last byte pos]/[entity legth]
			String contentRange = new StringBuffer("bytes ").append(start).append("-").append(end).append("/")
					.append(settings.getTotalLength()).toString();
			response.setHeader("Content-Range", contentRange);
		}
	}

	/**
	 * 
	 * @param len
	 *            文件总长度
	 * @param range
	 *            http请求的rang字段
	 * @param isDownload
	 *            是否下载
	 * @return 返回服务器响应的报文头设置对象
	 */
	private static RangeSettings getSettings(long len, String range, boolean isDownload) {
		long contentLength = 0;
		long start = 0;
		long end = 0;
		if (range.startsWith("-"))// 例子：-500，最后500个(http协议)
		{
			contentLength = Long.parseLong(range.substring(1));// 要下载的量
			end = len - 1;
			start = len - contentLength;
		} else if (range.endsWith("-"))// 从哪个开始
		{
			start = Long.parseLong(range.replace("-", ""));
			end = len - 1;
			contentLength = len - start;
		} else// 从a到b
		{
			String[] se = range.split("-");
			start = Long.parseLong(se[0]);
			end = Long.parseLong(se[1]);
			contentLength = end - start + 1;
		}
		return new RangeSettings(start, end, contentLength, len, isDownload);
	}
}

/**
 * 
 * @author chc 响应rang的报文头相应字段设置对象
 */

class RangeSettings {

	private long start;// 响应数据开始位置
	private long end;// 响应数据结束位置
	private long contentLength;// 本次传输的数据量
	private long totalLength;// 请求文件的总大小
	private boolean range;// 请求是否包含rang字段
	private boolean download;// 请求是否为下载或者直接浏览器打开

	public RangeSettings() {
		super();
	}

	public RangeSettings(long start, long end, long contentLength, long totalLength, boolean isDownload) {
		this.start = start;
		this.end = end;
		this.contentLength = contentLength;
		this.totalLength = totalLength;
		this.range = true;
		this.download = isDownload;
	}

	public RangeSettings(long totalLength) {
		this.totalLength = totalLength;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public long getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}

	public boolean isRange() {
		return range;
	}

	public boolean isDownload() {
		return download;
	}
}

class IoUtil {
	public static String setContentType(String returnFileName) {
		String contentType = "application/octet-stream";
		if (returnFileName.lastIndexOf(".") < 0)
			return contentType;
		returnFileName = returnFileName.toLowerCase();
		returnFileName = returnFileName.substring(returnFileName.lastIndexOf(".") + 1);
		if (returnFileName.equals("html") || returnFileName.equals("htm") || returnFileName.equals("shtml")) {
			contentType = "text/html";
		} else if (returnFileName.equals("css")) {
			contentType = "text/css";
		} else if (returnFileName.equals("xml")) {
			contentType = "text/xml";
		} else if (returnFileName.equals("gif")) {
			contentType = "image/gif";
		} else if (returnFileName.equals("jpeg") || returnFileName.equals("jpg")) {
			contentType = "image/jpeg";
		} else if (returnFileName.equals("js")) {
			contentType = "application/x-javascript";
		} else if (returnFileName.equals("atom")) {
			contentType = "application/atom+xml";
		} else if (returnFileName.equals("rss")) {
			contentType = "application/rss+xml";
		} else if (returnFileName.equals("mml")) {
			contentType = "text/mathml";
		} else if (returnFileName.equals("txt")) {
			contentType = "text/plain";
		} else if (returnFileName.equals("jad")) {
			contentType = "text/vnd.sun.j2me.app-descriptor";
		} else if (returnFileName.equals("wml")) {
			contentType = "text/vnd.wap.wml";
		} else if (returnFileName.equals("htc")) {
			contentType = "text/x-component";
		} else if (returnFileName.equals("png")) {
			contentType = "image/png";
		} else if (returnFileName.equals("tif") || returnFileName.equals("tiff")) {
			contentType = "image/tiff";
		} else if (returnFileName.equals("wbmp")) {
			contentType = "image/vnd.wap.wbmp";
		} else if (returnFileName.equals("ico")) {
			contentType = "image/x-icon";
		} else if (returnFileName.equals("jng")) {
			contentType = "image/x-jng";
		} else if (returnFileName.equals("bmp")) {
			contentType = "image/x-ms-bmp";
		} else if (returnFileName.equals("svg")) {
			contentType = "image/svg+xml";
		} else if (returnFileName.equals("jar") || returnFileName.equals("var") || returnFileName.equals("ear")) {
			contentType = "application/java-archive";
		} else if (returnFileName.equals("doc")) {
			contentType = "application/msword";
		} else if (returnFileName.equals("pdf")) {
			contentType = "application/pdf";
		} else if (returnFileName.equals("rtf")) {
			contentType = "application/rtf";
		} else if (returnFileName.equals("xls")) {
			contentType = "application/vnd.ms-excel";
		} else if (returnFileName.equals("ppt")) {
			contentType = "application/vnd.ms-powerpoint";
		} else if (returnFileName.equals("7z")) {
			contentType = "application/x-7z-compressed";
		} else if (returnFileName.equals("rar")) {
			contentType = "application/x-rar-compressed";
		} else if (returnFileName.equals("swf")) {
			contentType = "application/x-shockwave-flash";
		} else if (returnFileName.equals("rpm")) {
			contentType = "application/x-redhat-package-manager";
		} else if (returnFileName.equals("der") || returnFileName.equals("pem") || returnFileName.equals("crt")) {
			contentType = "application/x-x509-ca-cert";
		} else if (returnFileName.equals("xhtml")) {
			contentType = "application/xhtml+xml";
		} else if (returnFileName.equals("zip")) {
			contentType = "application/zip";
		} else if (returnFileName.equals("mid") || returnFileName.equals("midi") || returnFileName.equals("kar")) {
			contentType = "audio/midi";
		} else if (returnFileName.equals("mp3")) {
			contentType = "audio/mpeg";
		} else if (returnFileName.equals("ogg")) {
			contentType = "audio/ogg";
		} else if (returnFileName.equals("m4a")) {
			contentType = "audio/x-m4a";
		} else if (returnFileName.equals("ra")) {
			contentType = "audio/x-realaudio";
		} else if (returnFileName.equals("3gpp") || returnFileName.equals("3gp")) {
			contentType = "video/3gpp";
		} else if (returnFileName.equals("mp4")) {
			contentType = "video/mp4";
		} else if (returnFileName.equals("mpeg") || returnFileName.equals("mpg")) {
			contentType = "video/mpeg";
		} else if (returnFileName.equals("mov")) {
			contentType = "video/quicktime";
		} else if (returnFileName.equals("flv")) {
			contentType = "video/x-flv";
		} else if (returnFileName.equals("m4v")) {
			contentType = "video/x-m4v";
		} else if (returnFileName.equals("mng")) {
			contentType = "video/x-mng";
		} else if (returnFileName.equals("asx") || returnFileName.equals("asf")) {
			contentType = "video/x-ms-asf";
		} else if (returnFileName.equals("wmv")) {
			contentType = "video/x-ms-wmv";
		} else if (returnFileName.equals("avi")) {
			contentType = "video/x-msvideo";
		}
		return contentType;
	}

	// UTF8转码
	public static String toUtf8String(String s) {
		StringBuffer sb = new StringBuffer();
		int len = s.toCharArray().length;
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("utf-8");
				} catch (Exception ex) {
					System.out.println(ex);
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		String s_utf8 = sb.toString();
		sb.delete(0, sb.length());
		sb.setLength(0);
		sb = null;
		return s_utf8;
	}

	public static InputStream skipFully(InputStream in, long howMany) throws Exception {
		long remainning = howMany;
		long len = 0;
		while (remainning > 0) {
			len = in.skip(len);
			remainning -= len;
		}
		return in;
	}
}