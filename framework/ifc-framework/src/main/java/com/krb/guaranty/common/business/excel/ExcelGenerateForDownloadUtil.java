package com.krb.guaranty.common.business.excel;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成Excel,直接用于下载
 * @author xuqiang
 */
@Slf4j
public class ExcelGenerateForDownloadUtil {

    private final static String SUFFIX = ".xlsx";
    private ExcelGenerateForDownloadUtil() {}

    public static  void generateXlsx(HttpServletResponse response, String fileName,List<SheetAndData> datas ) {
        try (ServletOutputStream out = response.getOutputStream()){

            ExcelWriter writer = ExcelUtil.getWriter(true);
            for (int i = 0; i < datas.size(); i++) {
                SheetAndData sheetAndData = datas.get(i);

                if (i == 0) {
                    // 设置sheet名称
                    writer.renameSheet(sheetAndData.getSheetName());
                } else {
                    //添加sheet
                    writer.setSheet(sheetAndData.getSheetName());
                }

                writer.getSheet().setDefaultColumnWidth(20);
                List dataList = sheetAndData.getDataList();

                // 头部处理
                List<Pair<String, String>> header = new ArrayList<>();
                if (dataList.isEmpty()) {
                    dataList.add(sheetAndData.getDataType().newInstance() );
                }
                Field[] declaredFields = sheetAndData.getDataType().getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    ColumnTitle annotation = declaredField.getAnnotation(ColumnTitle.class);
                    if (annotation != null) {
                        // 排序
                        header.add(annotation.index(), new Pair<>(declaredField.getName(), annotation.name()));
                    }
                }
                header.forEach(e -> writer.addHeaderAlias(e.getKey(), e.getValue()));
                // 仅设置了header的可以写入.其他忽略.对应的也就是有注解加入没有不加
                writer.setOnlyAlias(true);
                // 写入信息
                writer.write(dataList, true);
                writer.clearHeaderAlias();
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8") + SUFFIX);
            writer.flush(out, true);
            // 关闭writer，释放内存
            writer.close();
            // 关闭输出Servlet流
            IoUtil.close(out);

        } catch (IOException e) {
            log.error("生成Excel失败",e);
        } catch (IllegalAccessException e) {
            log.error("生成Excel失败",e);
        } catch (InstantiationException e) {
            log.error("生成Excel失败",e);
        }
    }


}
