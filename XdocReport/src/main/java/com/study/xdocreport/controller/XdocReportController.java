package com.study.xdocreport.controller;

import com.study.xdocreport.pojo.User;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: chandlerHuang
 * @Date: 2023/05/14/10:28
 * @Description:
 */
@RestController
@RequestMapping("/xdoc")
@Slf4j
public class XdocReportController {

    @GetMapping(value = "/summary")
    public void createXdocReport(HttpServletResponse response) throws IOException {
        InputStream inputStream = null;
        ServletOutputStream outputStream = response.getOutputStream();
        try {
            //读取取resource目录下的模板　　　　　　　
            inputStream = XdocReportController.class.getClassLoader().getResourceAsStream("templates/test.docx");
            IXDocReport report = XDocReportRegistry
                    .getRegistry()
                    .loadReport(inputStream, TemplateEngineKind.Freemarker);
            IContext context = report.createContext();
            context.put("title", "年终总结大会");
            context.put("time", "2023年5月6日");
            context.put("address", "深圳市南山区软件产业基地4b");
            List<User> users = new ArrayList<>();
            users.add(new User("周星星", "男", "刑警"));
            users.add(new User("吴孟达", "男", "大队长"));
            users.add(new User("孙悟空", "雄", "保镖"));

            context.put("users", users);

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/msword");
            String fileName = "会议纪要.docx";
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
            report.process(context, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            log.info("生成纪要文件发生异常：<{}>", e.getMessage());
        }
    }
}
