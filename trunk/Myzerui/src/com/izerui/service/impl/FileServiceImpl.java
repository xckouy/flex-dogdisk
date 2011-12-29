package com.izerui.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.izerui.service.FileService;
import com.izerui.utils.ExtendFilenameUtils;
import com.izerui.vo.FileItem;
import com.izerui.vo.FileTree;

@Service("fileService")
public class FileServiceImpl implements FileService{

	private static Logger log = Logger.getLogger(FileServiceImpl.class);
	
	@Autowired
	@Value("${filePath}")
	private String filePath;
	
	public List<FileTree> getFolderList() {
		if(!filePath.endsWith(File.separator)){
			filePath += File.separator;
		}
		FileTree root = new FileTree();
		root.setFolderpath(filePath);
		root.setFoldername(ExtendFilenameUtils.getLastFolderPathNoEndSeparator(filePath));
		root.setChildren(getChildrenFolderPaths(root));
		
		List<FileTree> filePathTree = new ArrayList<FileTree>();
		filePathTree.add(root);
		return filePathTree;
	}
	
	private List<FileTree> getChildrenFolderPaths(FileTree parent){
		List<FileTree> children = new ArrayList<FileTree>();
		File[] files = new File(parent.getFolderpath()).listFiles();//列出子文件
		if(files!=null){
			for (File file : files) {
				if(file.isDirectory()){//如果子文件是文件夹
					String path = file.getPath()+File.separator;
					FileTree fileTree = new FileTree();
					fileTree.setFoldername(ExtendFilenameUtils.getLastFolderPathNoEndSeparator(path));
					fileTree.setFolderpath(path);
					List<FileTree> childrenList = getChildrenFolderPaths(fileTree);
					if(childrenList!=null&&childrenList.size()>0){
						fileTree.setChildren(getChildrenFolderPaths(fileTree));
					}
					children.add(fileTree);
					log.info(path);
				}
			}
			return children;
		}
		return null;
	}

	public List<FileItem> listFilesByFolder(String folderPath) {
		List<FileItem> files = new ArrayList<FileItem>();
		File folderFile = new File(folderPath);
		for (File file : folderFile.listFiles()) {
			FileItem fi = new FileItem();
			fi.setLashmodifydate(new Date(file.lastModified()));
			if(file.isDirectory()){
				fi.setIsfolder(true);
				fi.setSize(FileUtils.sizeOfDirectory(file));
				fi.setFilename(ExtendFilenameUtils.getLastFolderPathNoEndSeparator(file.getPath()+File.separator));
				fi.setFolderpath(ExtendFilenameUtils.getFullPath(file.getPath()+File.separator));
			}else{
				fi.setIsfolder(false);
				fi.setFolderpath(ExtendFilenameUtils.getFullPath(file.getPath()));
				fi.setBasename(ExtendFilenameUtils.getBaseName(file.getName()));
				fi.setExtension(ExtendFilenameUtils.getExtension(file.getName()));
				fi.setIshidden(file.isHidden());
				fi.setFilename(file.getName());
				fi.setSize(file.length());
			}
			files.add(fi);
			log.info(file.getName());
		}
		return files;
	}

	public String optFolder(String path,boolean isCreate) throws Exception {
		path = FilenameUtils.normalize(path);
		// TODO Auto-generated method stub
		if(isCreate&&new File(path).mkdir()){//新建
			return path+File.separator;
		}else{
			if(new File(path).list().length>0){
				throw new Exception("文件夹不为空");
			}
			new File(path).delete();
		}
		return null;
	}

	public String renameFolder(String path, String FolderNewName) {
		// TODO Auto-generated method stub
		path = FilenameUtils.normalize(path);
		String pathNoEndSeparator = FilenameUtils.getFullPathNoEndSeparator(path);
		String newPath = pathNoEndSeparator.substring(0, pathNoEndSeparator.lastIndexOf(File.separator)+1)+FolderNewName+File.separator;
		if(new File(path).renameTo(new File(newPath))){
			return newPath;
		}
		return null;
		
	}
	
	public static void main(String[] args) {
		FileServiceImpl imp = new FileServiceImpl();
		imp.renameFolder("D:/data/", "data1");
	}
	


}
