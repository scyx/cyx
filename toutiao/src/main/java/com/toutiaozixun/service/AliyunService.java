package com.toutiaozixun.service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectResult;
import com.toutiaozixun.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

/**
 * 阿里云服务
 */
@Service
public class AliyunService {
    private static final Logger logger = LoggerFactory.getLogger(AliyunService.class);
    //设置好账号的ACCESS_KEY和SECRET_KEY
    String endpoint = "";
    String  accessKeyId   = "";
    String  accessKeySecret   =  "";
    //要上传的空间
    String bucketname = "";
    String headfilename = "";


    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
//    public String getUpToken() {
//        return auth.uploadToken(bucketname);
//    }
    File f = null;
    public String saveImage(MultipartFile file) throws IOException {
        try {

            if(file.equals("")||file.getSize()<=0){
                file = null;
            }else{
                InputStream ins = file.getInputStream();
                f=new File(file.getOriginalFilename());
                inputStreamToFile(ins, f);
            }
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            //通过扩展名名判断这个文件是不是图片 是否允许上传
            //png","jpg","bmp","jpeg"都允许
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;
            }
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;

            PutObjectResult res = ossClient.putObject(bucketname, fileName, f);
            //putObject上传
            ossClient.shutdown();
            //打印返回的信息
            if (res != null) {
                return headfilename+"/"+fileName;
            } else {
                logger.error("阿里云异常");
                return null;
            }
        } catch (OSSException e) {
            // 请求失败时打印的异常的信息
            logger.error("阿里云异常:" + e.getMessage());
            return null;
        }finally{
            File del = new File(f.toURI());
            del.delete();
        }
    }
    public static void inputStreamToFile(InputStream ins,File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
