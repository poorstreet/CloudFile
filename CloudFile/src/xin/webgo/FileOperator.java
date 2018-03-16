/**
 * 
 */
package xin.webgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public class FileOperator {
	/**
	 * 读取某个文件夹下的所有文件及目录,
	 */
	public static Map<String, String> readDir(String filepath) throws FileNotFoundException, IOException {
		Map<String, String> fileMap = new HashMap<String, String>();
		File file = new File(filepath);
		//非文件夹直接返回空的fileMap
		if (!file.isDirectory()) {
			return fileMap;
		} else if (file.isDirectory()) {
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++) {
				File readfile = new File(filepath + File.separator + filelist[i]);
				fileMap.put(readfile.getName(), getFileType(readfile.getName()));
			}
		}
		return fileMap;
	}

	/**
	 * 删除某个文件夹下的所有文件夹和文件
	 */

	public static boolean deletefiles(String delpath) throws FileNotFoundException, IOException {
		try {

			File file = new File(delpath);
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + File.separator + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
						System.out.println("删除文件成功");
					} else if (delfile.isDirectory()) {
						deletefiles(delpath + File.separator + filelist[i]);
					}
				}
				file.delete();

			}

		} catch (FileNotFoundException e) {
			System.out.println("deletefile()   Exception:" + e.getMessage());
		}
		return true;
	}

	/**
	 * 根据文件名获取文件的类型
	 * 
	 * @param fileName
	 * @return String (dir,mp4,pdf... )
	 */
	public static String getFileType(String fileName) {
		if(fileName == null) {
			return null;
		}
		String type = "";
		int idx = fileName.lastIndexOf(".");
		if (idx == -1) {
			type = "dir";
		} else {
			type = fileName.substring(idx + 1, fileName.length());
		}
		return type;
	}
	public static boolean transcoding(String inPath,String outPath) {
		
		return true;
	}
}
