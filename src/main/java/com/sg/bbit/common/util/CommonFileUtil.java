package com.sg.bbit.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.sg.bbit.common.exception.BizException;
import com.sg.bbit.common.vo.CommonFileVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonFileUtil {
	private static final String DEFAULT_PATH = Paths.get("") + "\\src\\main\\";
	private static final String BASE_PATH = "file://data/files/";

	public static void createFile(CommonFileVO comFile) {
		FileOutputStream outputStream;
		try {
			final String filePath = DEFAULT_PATH + comFile.getPath() + comFile.getName();
			File f = new File(filePath);
			log.info("not overwrite file=>{}", f.exists() && !comFile.isReload());
			if (f.exists() && !comFile.isReload()) {
				return;
			}
			outputStream = new FileOutputStream(filePath);
			byte[] strToBytes = comFile.getContext().getBytes();
			outputStream.write(strToBytes);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getExtension(String name) {
		if (name.contains(".")) {
			return name.substring(0, name.lastIndexOf(".")+1);
		}
		return "";
	}

	public static String saveFile(MultipartFile mf, String path) {
		if (mf == null || mf.isEmpty()) {
			return null;
		}
		String fileName = BASE_PATH + UUID.randomUUID() + "." + CommonFileUtil.getExtension(mf.getOriginalFilename());
		File file = new File(fileName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			Files.copy(mf.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			throw new BizException(e, "파일 저장 시 에러");
		}
		return fileName;
	}

	public static boolean renameFile(String target, String rename) {
		File tFile = new File(BASE_PATH + target);
		if (tFile.exists()) {
			File rFile = new File(BASE_PATH + rename);
			return tFile.renameTo(rFile);
		}
		return false;
	}

	public static boolean deleteFile(String fileName) {
		File delFile = new File(BASE_PATH + fileName);
		if (delFile.exists()) {
			return delFile.delete();
		}
		return true;
	}

	public static boolean deleteFiles(List<String> fileNames) {
		for (String fileName : fileNames) {
			if (fileName == null)
				continue;
			deleteFile(fileName);
		}
		return true;
	}
}
