/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/19 23:37
 * @version 1.0
 */

package com.example.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.*;
import com.example.entity.Aspect.Log;
import com.example.service.*;
import lombok.SneakyThrows;
import org.apache.poi.hssf.record.DVALRecord;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/excel")
public class FileDownloadController {


    @Resource
    private SalaryService salaryService;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private LogAspectService logAspectService;

    @Resource
    private ResourcesService resourcesService;

    @Resource
    private FinancialinService financialinService;

    @Resource
    private FinancialoutService financialoutService;


    @PostMapping(value = "/employeeIn")
    public Result employeeIn(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null) return Result.error(ResultCodeEnum.FILE_UP_NULL);
        final int[] num = {0};
        final int[] sum = {0};
        AnalysisEventListener listener = new AnalysisEventListener() {
            @Override
            public void invoke(Object data, AnalysisContext context) {
                //获取到每一行数据，逐行进行处理
                Employee clueDTO = (Employee)data;
                Employee employee = new Employee();
                BeanUtils.copyProperties(clueDTO,employee);
                //这里将获取到的数据封装回实体类对象中，并在数据库持久化
                String mes = employeeService.addByUp(employee);
                sum[0]++;
                if(mes != null){
                    num[0]++;
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                System.out.println("导入数据完毕");
            }
        };
        try {
            EasyExcel.read(file.getInputStream(), Employee.class, listener).sheet(0).doRead();
        } catch (IOException e) {
            return Result.error("5091","导入出错：{}"+e.getMessage());
        }
        if(num[0] != 0){
            if(sum[0] == num[0]){
                return Result.error(ResultCodeEnum.DATA_UP_FAILE);
            }else{
                return Result.error("5099","共"+sum[0]+"条数据,"+"导入失败"+num[0]+"条");
            }
        }else{
            return Result.success("全部员工数据上传成功");
        }
    }



    @PostMapping(value = "/financialOutOut")
    public void exportFinancialout(@RequestBody Financialout financialout, HttpServletResponse response){
        try{
            String filename = "支出表";
            List<Financialout> list = financialoutService.selectAll(null);
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + "导出记录"+ ".xlsx");
            EasyExcel.write(response.getOutputStream())
                    .head(Financialout.class)
                    .sheet(filename)
                    .doWrite(list);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/financialInOut")
    public void exportFinancialin(@RequestBody Financialin financialin, HttpServletResponse response){
        try{
            String filename = "财务收入表";
            List<Financialin> list = financialinService.selectAll(null);
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + "导出记录"+ ".xlsx");
            EasyExcel.write(response.getOutputStream())
                    .head(Financialin.class)
                    .sheet(filename)
                    .doWrite(list);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/resourcesOut")
    public void exportResources(@RequestBody Resources resources , HttpServletResponse response){
        try{
            String filename = "资产表";
            List<Resources> list = resourcesService.selectAll(null);
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + "导出记录"+ ".xlsx");
            EasyExcel.write(response.getOutputStream())
                    .head(Resources.class)
                    .sheet(filename)
                    .doWrite(list);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/logOut")
    public void exportLog(@RequestBody Log log, HttpServletResponse response){
        try{
            String filename = "日志表";
            List<Log> list = logAspectService.selectAll(null);
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + "导出记录"+ ".xlsx");
            EasyExcel.write(response.getOutputStream())
                    .head(Log.class)
                    .sheet(filename)
                    .doWrite(list);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/salaryOut")
    public void exportSalary(@RequestBody Salary salary, HttpServletResponse response){
        try{
            String filename = "薪资表";
            List<Salary> list = salaryService.selectSalaryExcel();
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + "导出记录"+ ".xlsx");
            EasyExcel.write(response.getOutputStream())
                    .head(Salary.class)
                    .sheet(filename)
                    .doWrite(list);
            // 打印日志记录是否成功写入数据
            System.out.println("成功写入数据到 Excel 文件中！");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/employeeOut")
    public void exportEmployee(@RequestBody Employee employee, HttpServletResponse response){
        try{
            String filename = "员工表";
            List<Employee> list = employeeService.selectAll(null);
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + "导出记录"+ ".xlsx");
            EasyExcel.write(response.getOutputStream())
                    .head(Employee.class)
                    .sheet(filename)
                    .doWrite(list);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    @PostMapping(value = "/employeeM")
    public void exportEmployeeM(@RequestBody Employee employee, HttpServletResponse response){
        try{
            String filename = "员工表";
            Employee employee1 = new Employee();
            employee1.setUsername("zhangSan");
            employee1.setName("张三");
            employee1.setPhone("13511110000");
            employee1.setEmail("wangyi@163.com");
            employee1.setDepartmentName("财务部");
            employee1.setLevel(1);
            ArrayList<Employee> list = new ArrayList<>();
            list.add(employee1);
            response.setContentType("application/vnd.ms-excel; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + "导出记录"+ ".xlsx");
            EasyExcel.write(response.getOutputStream())
                    .head(Employee.class)
                    .sheet(filename)
                    .doWrite(list);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }





    @PostMapping("/salaryIn")
    @SneakyThrows
    public Object importSalaryExcel(@RequestPart("file") MultipartFile file) {
        List<Salary> list = EasyExcel.read(file.getInputStream())
                .head(Salary.class).sheet().doReadSync();
        return Result.success();
    }






}
