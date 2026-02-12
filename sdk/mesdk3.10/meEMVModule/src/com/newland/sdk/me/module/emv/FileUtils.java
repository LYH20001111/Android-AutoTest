package com.newland.sdk.me.module.emv;

import android.util.Log;

import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * 简单文件工具
 * 
 *
 *
 * 2009-2-17
 */
public class FileUtils{
	
	private DeviceLogger logger = DeviceLoggerFactory.getLogger(FileUtils.class);
	
	private static final String KEY_PATHSEPERATOR = "path.separator";
	private static final String KEY_CLASSPATH = "java.class.path";
	
    /**文件分隔符**/
    public static final String FILESEPARATOR = System.getProperty("file.separator");  
	
	/**
	 * 获取一个文件文件大小
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static long getFileSize(File file) throws IOException{
		RandomAccessFile raf = null;
		try{
			raf = new RandomAccessFile(file,"r");
			return raf.length();
		}finally{
			if(raf != null){
				try{
					raf.close();
				}catch (Exception e) {
				}
			}
		}
	}
	
	/**
	 * 获取当前启动环境的标准类路径
	 * <p>
	 * @return
	 */
	public static File[] getClassPath(){
		String classpath = System.getProperty(KEY_CLASSPATH);
		String seperator = System.getProperty(KEY_PATHSEPERATOR);
		String [] pathArray = classpath.split(seperator);
		File [] rF = new File[pathArray.length];
		for(int i = 0;i<pathArray.length;i++){
			rF[i] = new File(pathArray[i]);
		}
		return rF;
	}
	
	/**
	 * <p>在当前类路径下查找满足<code>filter</code>的文件</p>
	 * @param filter
	 * @return
	 */
	public static List<File> findFiles(FileFilter filter){
		File [] classpath = getClassPath();
		List <File>  resultList = new ArrayList<File> ();
		for(int i = 0 ;i<classpath.length;i++){
			List <File>  result = findFiles(classpath[i],filter,-1);
			resultList.addAll(result);
		}
		return resultList;		
	}
	
	/**
	 * <p>在对应路径为path，查询名称为name的文件</p>
	 * <p>查询失败返回NULL</p>
	 * @param path
	 * @param name
	 * @return
	 */
	public static List <File>  findFiles(File path,final String name,int level){
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				if(pathname.isFile()){
					if(pathname.getName().equals(name))
						return true;
				}
				return false;
			}
		};
		return findFiles(path,filter,level);
		
	}
	

	
	/**
	 * 
	 * 根据过滤器递归查询对应的文件列表
	 * <p>
	 * <p>
	 * 但通过层级避免过深的文件查询。
	 * 
	 * @param path 被查询的路径。
	 * @param filter 根据过滤器确定允许的文件。
	 * @param level 当level<0时，将无限层级查询，但当level>=0时，将查询到对应层级的文件。
	 * @return
	 */
	public static List <File>  findFiles(File path,FileFilter filter,int level){
		
		class FileContext{
			private File file;
			private int pathlevel;
			
			private FileContext(File file,int pathlevel){
				this.file = file;
				this.pathlevel = pathlevel;
			}
		}
		if(!path.exists())
			throw new IllegalArgumentException("path is not exists!");
		
		List<File> resultList = new ArrayList<File>();
		List<FileContext> undoList = new ArrayList<FileContext>();
		
		FileContext undoContext = null; 
		
		undoContext = new FileContext(path,0);
		
		
		while(true){
			//等于空时不考虑任何文件
			if(filter == null || filter.accept(undoContext.file)){
				if(undoContext.pathlevel != 0) //排除掉第一级的文件夹
					resultList.add(undoContext.file);
			}
			if(undoContext.file.isDirectory()){
				File[] files = undoContext.file.listFiles();
				int pathlevel = undoContext.pathlevel;
				if(level >= pathlevel || level < 0){
					for(File file:files){
						undoList.add(new FileContext(file,pathlevel + 1));
					}
				}
			}
			if(undoList.contains(undoContext))
				undoList.remove(undoContext);
			
			if(undoList.isEmpty())
				break;
			
			undoContext = undoList.get(0);
		}
		return resultList;
	}


	
	/**
	 * 递归的清理一个文件夹
	 * @param path
	 */
	public static boolean removeFiles(File path){
		boolean result = true;
		if(path.isFile()){
			try{
				result = path.delete();
			}catch(Exception e){
				if(path.exists()){
					Log.e("FileUtils","delete file:"+path.getName()+" failed!",e);
					return false;
				}else{
					Log.e("FileUtils","delete file:"+path.getName()+" cause an exception!but delet successfully!",e);
					return true;
				}				
			}
		}else if(path.isDirectory()){
			File files[] =path.listFiles();
			if(files.length>0){
				for(int i=0;i<files.length;i++){
					result = removeFiles(files[i]);
					if(!result) //如果递归删除没有成功，则放弃继续删除过程，直接返回。
						return result;
				}
			}
			try{
				result = path.delete();
			}catch(Exception e){
				if(path.exists()){
					Log.e("FileUtils","delete directory:"+path.getName()+" failed!",e);
					return false;
				}else{
					Log.w("FileUtils","delete directory:"+path.getName()+" cause an exception!but delet successfully!",e);
					return true;
				}
			}
		}
		return result;
	}
	
	/**
	 * 返回文件相对路径描述
	 * <p>
	 * 该方法会尝试获取文件或者文件夹在系统中的绝对路径。所以该文件必须存在。
	 * 
	 * @param dist 要被相对的路径
	 * @param target 将去相对的文件
	 * @return
	 * @throws IOException 当尝试获取系统的绝对路径描述失败时。
	 */
	public static String absoluteToRelative(File dist, File target) throws IOException {
		String dir = dist.getCanonicalPath();
		String fName = target.getCanonicalPath();
		if(fName.startsWith(dir)){
			if(fName.equals(dir))
				return "";
			return fName.substring(dir.length()+1);
		}
		return null;
	}

	/**
	 * 将文件分割符替换成'.'
	 * 
	 * @param filename
	 * @return
	 */
    public static String fileSeparaToDot(String filename) {
        return filename.replace(File.separatorChar, '.').replace('/', '.')
            .replace('\\', '.');
    }

	/**
	 * 判断文件是否存在
	 *
	 * @param filePath 文件路径
	 * @return {@code true}: 存在<br>{@code false}: 不存在
	 */
	public static boolean isFileExists(String filePath) {
		return isFileExists(getFileByPath(filePath));
	}

	/**
	 * 判断文件是否存在
	 *
	 * @param file 文件
	 * @return {@code true}: 存在<br>{@code false}: 不存在
	 */
	public static boolean isFileExists(File file) {
		return file != null && file.exists();
	}

	/**
	 * 根据文件路径获取文件
	 *
	 * @param filePath 文件路径
	 * @return 文件
	 */
	public static File getFileByPath(String filePath) {
		return isSpace(filePath) ? null : new File(filePath);
	}

	private static boolean isSpace(String s) {
		if (s == null) return true;
		for (int i = 0, len = s.length(); i < len; ++i) {
			if (!Character.isWhitespace(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	//
	private static int sBufferSize = 8192;

	/**
	 * 将输入流写入文件
	 *
	 * @param file 文件
	 * @param is   输入流
	 * @return {@code true}: 写入成功<br>{@code false}: 写入失败
	 */
	public static boolean writeFileFromIS(File file, final InputStream is) {
		return writeFileFromIS(file, is, false);
	}

	/**
	 * 将输入流写入文件
	 *
	 * @param file   文件
	 * @param is     输入流
	 * @param append 是否追加在文件末
	 * @return {@code true}: 写入成功<br>{@code false}: 写入失败
	 */
	public static boolean writeFileFromIS(File file, final InputStream is, boolean append) {
		if (!FileUtils.createOrExistsFile(file) || is == null) return false;
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(file, append));
			byte data[] = new byte[sBufferSize];
			int len;
			while ((len = is.read(data, 0, sBufferSize)) != -1) {
				os.write(data, 0, len);
			}
			os.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			closeIO(is, os);
		}
	}


	/**
	 * 关闭IO
	 *
	 * @param closeables closeables
	 */
	private static void closeIO(Closeable... closeables) {
		if (closeables == null) return;
		for (Closeable closeable : closeables) {
			if (closeable != null) {
				try {
					closeable.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 判断文件是否存在，不存在则判断是否创建成功
	 *
	 * @param file 文件
	 * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
	 */
	public static boolean createOrExistsFile(File file) {
		if (file == null) return false;
		// 如果存在，是文件则返回true，是目录则返回false
		if (file.exists()) return file.isFile();
		if (!createOrExistsDir(file.getParentFile())) return false;
		try {
			return file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 判断目录是否存在，不存在则判断是否创建成功
	 *
	 * @param file 文件
	 * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
	 */
	public static boolean createOrExistsDir(File file) {
		// 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
		return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
	}


}

