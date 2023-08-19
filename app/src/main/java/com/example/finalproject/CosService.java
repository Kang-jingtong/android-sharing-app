package com.example.finalproject;

import android.content.Context;
import android.widget.Toast;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLDownloadTask;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;
import com.tencent.qcloud.core.auth.BasicLifecycleCredentialProvider;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials;
import com.tencent.qcloud.core.auth.SessionQCloudCredentials;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import com.tencent.qcloud.core.common.QCloudClientException;

import java.io.File;

import Adapter.DownloadListener;

public class CosService {
    //Reference: learn from tencent Cos SDK file;
    //https://cloud.tencent.com/document/product/436/12159

    private static final String BUCKET_NAME = "kjt-1304037201";
    private static final String SECRET_ID = "AKIDB8uaHjLFxPaWg7wKflcDjDi1A5mPM4zj"; //永久密钥 secretId
    private static final String SECRET_KEY = "d4TjcI5d6LNRTFCty86E9WqQ9cx6C8pI"; //永久密钥 secretKey

    private final Context context;
    private CosXmlService cosXmlService;

    public CosService(Context context) {
        this.context = context;
    }

    public void initCos() {

        // keyDuration 为请求中的密钥有效期，单位为秒
        QCloudCredentialProvider myCredentialProvider = new ShortTimeCredentialProvider(SECRET_ID, SECRET_KEY, 300);

        // 存储桶所在地域简称，例如广州地区是 ap-guangzhou
        String region = "ap-beijing";

        // 创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(region)
                .isHttps(true) // 使用 HTTPS 请求, 默认为 HTTP 请求
                .builder();

        // 初始化 COS Service，获取实例
        cosXmlService = new CosXmlService(context, serviceConfig, myCredentialProvider);
    }


    public void upload(final Context context, String photoPath, String fileName){
        // 初始化 TransferConfig，这里使用默认配置，如果需要定制，请参考 SDK 接口文档
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        // 初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService,
                transferConfig);

        // 上传文件
        //fileName:The location identifier of an object in the bucket is called the object.
        //photoPath: The absolute path to the local file
        //null stands for interrupting and continue I don't have that function so null.
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(BUCKET_NAME, fileName /*对象在存储桶中的位置标识符，即称对象*/,
                photoPath /*本地文件的绝对路径*/, null); //filename 和 photoPath 都是String类型File类型报错

        //设置上传进度回调
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
            }
        });

        //设置返回结果回调
        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
               // Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
            }

            //上传回掉如果成功upload就返回
            @Override
            public void onFail(CosXmlRequest request,
                               CosXmlClientException clientException,
                               CosXmlServiceException serviceException) {
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });

        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
            }
        });
    }

    //imageId应该是存在数据库里的字符串 将看到的内容下载到储存桶里
    //imageId应该是filename;
    public void download(String storagePath, String imageId, final DownloadListener downloadListener){
        // 初始化 TransferConfig，这里使用默认配置，如果需要定制，请参考 SDK 接口文档
        TransferConfig transferConfig = new TransferConfig.Builder().build();

        //初始化 TransferManager
        TransferManager transferManager = new TransferManager(cosXmlService,
                transferConfig);

        // 检查 文件是否已️下载savePathDir, imageId
        COSXMLDownloadTask cosxmlDownloadTask = transferManager.download(
                context, BUCKET_NAME, imageId, storagePath, imageId);

        //设置下载进度回调
        cosxmlDownloadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                // todo Do something to update progress...
            }
        });

        //设置返回结果回调
        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                downloadListener.onDownloadSuccess();
            }

            @Override
            public void onFail(CosXmlRequest request,
                               CosXmlClientException clientException,
                               CosXmlServiceException serviceException) {
                if (clientException != null) {
                    clientException.printStackTrace();
                } else {
                    serviceException.printStackTrace();
                }
            }
        });

        //设置任务状态回调，可以查看任务过程
        cosxmlDownloadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
            }
        });
    }
}