package com.izerui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DownloadController {
	
	private int length = 4096 ;
	private byte[] array=new byte[length];
	
	@RequestMapping(value="/download",method=RequestMethod.GET)
	public void download(@RequestParam("path") String filepath,@RequestParam("name") String filename,HttpServletResponse response){
		try {
			filepath = new String(filepath.getBytes("ISO-8859-1"),"UTF-8");
			filepath += new String(filename.getBytes("ISO-8859-1"),"UTF-8");
			File file = new File(filepath);
			if(file.isFile()){//存在文件
				response.reset();  
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");  
				response.addHeader("Content-Length", "" + file.length());  
				response.setContentType("application/octet-stream;charset=UTF-8");  
				OutputStream output = response.getOutputStream();
				outputFileStream(filepath, output);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	public void outputFileStream(String filePath, OutputStream output)
	{
		FileInputStream input = null;
		try {
			int len = 0;
			int pos = 0;
			input = new FileInputStream(filePath);
			while(( len = input.read(array)) != -1)
			{
				output.write( array, 0, len );
				pos += len;
			}
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			System.out.println("文件不存在:"+e.getMessage());
		} catch (IOException e) {
			System.out.println("文件IO错误:"+e.getMessage());
		}finally{
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
