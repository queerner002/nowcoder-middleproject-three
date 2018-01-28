package com.nowcoder.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.nowcoder.Util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/1/27 0027.
 */
@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);

    private static String QINIU_IMAGE_DOMAIN = "http://p03t0y570.bkt.clouddn.com/";

    public String saveImage(MultipartFile file) throws IOException {
        Configuration cfg = new Configuration(Zone.zone0());
        String accessKey = "aG_yojZxPfmqDzfBWegEfqtRUe1nhiH3c8kSo2Na";
        String secretKey = "60f1ovd2FS1q3cM4Q5e42pyQucoh1lbAmG2vR4fF";

        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String bucket = "nowcoder";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        //String localFilePath = "/home/qiniu/test.png";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0){
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!ToutiaoUtil.isFileAllowed(fileExt)){
                return null;
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;

            Response response = uploadManager.put(file.getBytes(), fileName, upToken);
            if (response.isOK() && response.isJson()){
                JSONObject jsonObject = JSONObject.parseObject(response.bodyString());
                return QINIU_IMAGE_DOMAIN +jsonObject.get("key");
            }
            else {
                logger.error("七牛异常：" + response.bodyString());
                return null;
            }
            //解析上传成功的结果
            //DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            //System.out.println(putRet.key);
            //System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
                return null;
            }
        }
        return null;
    }

}
