package com.example.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/files")
public class FileController {

    // 文件上传存储路径
    private static final String filePath = System.getProperty("user.dir") + "/files/";
    private static final String filePath1 = System.getProperty("user.dir") + "/files/reimbursement/";

    @Value("${server.port:9090}")
    private String port;

    @Value("${ip:localhost}")
    private String ip;




    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        String flag;
        synchronized (FileController.class) {
            flag = System.currentTimeMillis() + "";
            ThreadUtil.sleep(1L);
        }
        String fileName = file.getOriginalFilename();
        try {
            if (!FileUtil.isDirectory(filePath)) {
                FileUtil.mkdir(filePath);
            }
            // 文件存储形式：时间戳-文件名
            FileUtil.writeBytes(file.getBytes(), filePath + flag + "-" + fileName);  // ***/manager/files/1697438073596-avatar.png
            System.out.println(fileName + "--上传成功");

        } catch (Exception e) {
            System.err.println(fileName + "--文件上传失败");
        }
        String http = "http://" + ip + ":" + port + "/files/";
        return Result.success(http + flag + "-" + fileName);  //  http://localhost:9090/files/1697438073596-avatar.png
    }

    /**
     * 文件上传
     * 这里要用注解
     * 前端做好标注
     */
    @PostMapping("/uploads")
    public Result uploads(MultipartFile[] fileList) {
        String flag = generateFlag();
        List<String> uploadedFiles = new ArrayList<>();
        String http =  "http://" + ip + ":" + port + "/files/reimbursement/"; // 服务器地址
        System.out.println("================================");
        if (fileList == null ) {
            // 如果文件数组为空，返回错误信息
            return Result.error("500", "文件上传为空");
        }
        if (!FileUtil.isDirectory(filePath1)) {
            FileUtil.mkdir(filePath1);
        }

        for (MultipartFile file : fileList) {
            String fileName = file.getOriginalFilename();
            System.out.println("文件名: " + fileName);
            String filePath = filePath1 + flag + "-" + fileName;
            try {
                File dest = new File(filePath);
                file.transferTo(dest); // 保存文件到指定路径
                uploadedFiles.add(fileName);
            } catch (Exception e) {
                System.err.println("--文件上传失败");
                return Result.error("500", http);
            }

        }

        return Result.success(http + flag, uploadedFiles);
    }

    // 生成唯一标识符
    private String generateFlag() {
        return System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }


    /**
     * 获取文件
     *
     * @param flag
     * @param response
     */
    @GetMapping("/reimbursement/{flag}")   //  1697438073596-avatar.png
    public void reimbursementPath(@PathVariable String flag, HttpServletResponse response) {
        OutputStream os;
        try {
            if (StrUtil.isNotEmpty(flag)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(flag, "UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(filePath1 + flag);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
    }

    @GetMapping("/{flag}")   //  1697438073596-avatar.png
    public void avatarPath(@PathVariable String flag, HttpServletResponse response) {
        OutputStream os;
        try {
            if (StrUtil.isNotEmpty(flag)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(flag, "UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(filePath + flag);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
    }
    /**
     * 删除文件
     *
     * @param flag
     */
    @DeleteMapping("/{flag}")
    public void delFile(@PathVariable String flag) {
        FileUtil.del(filePath + flag);
        System.out.println("删除文件" + flag + "成功");
    }


}
