package sky.it.com.stock_statistics.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import sky.it.com.stock_statistics.database.bean.StockBean;
import sky.it.com.stock_statistics.entity.PullToRefreshBean;

/**
 * @company Name:
 * @author: sky
 * @creatDate: 2019/3/31 12:43 PM
 * @className: ExcelUtil
 * @description:
 * @modified By:
 * @modifyDate:
 */
public class ExcelUtil {

    private static WritableFont arial14font = null;

    private static WritableCellFormat arial14format = null;
    private static WritableFont arial10font = null;
    private static WritableCellFormat arial10format = null;
    private static WritableFont arial12font = null;
    private static WritableCellFormat arial12format = null;
    private final static String UTF8_ENCODING = "UTF-8";

    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    private static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            //对齐格式
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            //设置边框
            arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Excel
     *
     * @param fileName 导出excel存放的地址（目录）
     * @param colName  excel中包含的列名（可以有多个）
     */
    public static void initExcel(String fileName, String sheetName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            //设置表格的名字
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            //创建标题栏
            sheet.addCell((WritableCell) new Label(0, 0, fileName, arial14format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            //设置行高
            sheet.setRowView(0, 340);
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void writeObjListToExcel(List<T> objList, String fileName, Context c) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName), workbook);
                WritableSheet sheet = writebook.getSheet(0);

                for (int j = 0; j < objList.size(); j++) {
                    PullToRefreshBean projectBean = (PullToRefreshBean) objList.get(j);
                    List<String> list = new ArrayList<>();
                    list.add(projectBean.getNumber());
                    list.add(projectBean.getStockNum());
                    list.add(projectBean.getTotal() + "");
                    list.add(projectBean.getLeft() + "");

                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j + 1, list.get(i), arial12format));
                        if (list.get(i).length() <= 4) {
                            //设置列宽
                            sheet.setColumnView(i, list.get(i).length() + 8);
                        } else {
                            //设置列宽
                            sheet.setColumnView(i, list.get(i).length() + 5);
                        }
                    }
                    //设置行高
                    sheet.setRowView(j + 1, 350);
                }

                writebook.write();
                Toast.makeText(c, "导出Excel成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 导出Excel（样品）
     *
     * @param sheetName sheet名称
     * @param title     标题
     * @param wb        HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(Context context, String sheetName,
                                               String[] title, List<StockBean> list,
                                               List<Map<String, String>> sums,
                                               HSSFWorkbook wb) {
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if (wb == null) {
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);
        row.setHeight((short) 650);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for (int i = 0; i < title.length; i++) {
            sheet.setColumnWidth(i, 7000);
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            HSSFFont font = wb.createFont();
            font.setFontName("黑体");
            font.setFontHeightInPoints((short) 12);//设置字体大小
            style.setFont(font);
            cell.setCellStyle(style);
        }

        try {
            //创建内容
            HSSFCellStyle styleCon = wb.createCellStyle();
            // 创建一个居中格式
            styleCon.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            styleCon.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                row.setHeight((short) 600);
                StockBean stockBean = list.get(i);

                String iconPath = list.get(i).getMark();
                String[] split = new String[0];
                if (!TextUtils.isEmpty(iconPath)) {
                    split = iconPath.split(",");
                }

                for (int n = 0; n < split.length; n++) {
                    //将内容按顺序赋给对应的列对象
                    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                    HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
                    //将两张图片读到BufferedImage
//                    InputStream inputStream =
//                            context.getResources().openRawResource(Integer.parseInt(split[n]));
                    //从本机路径获取图片流
                    InputStream inputStream = new FileInputStream(split[n]);

                    //读取缓存
                    byte[] buffer = new byte[2048];
                    int length = 0;
                    while ((length = inputStream.read(buffer)) != -1) {
                        byteArrayOut.write(buffer, 0, length);//写入输出流
                    }
                    inputStream.close();//读取完毕，关闭输入流
                    //图片一导出到单元格B2中
                    HSSFClientAnchor anchor = new HSSFClientAnchor(250 + (n * 220), 30, 470 + (n * 220), 250,
                            (short) 6, i + 1, (short) 6, i + 1);
                    // 插入图片
                    patriarch.createPicture(anchor, wb.addPicture(byteArrayOut
                            .toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
                }


                //设置列内容
                //仓库
                cell = row.createCell(0);
                cell.setCellValue(stockBean.getName());
                cell.setCellStyle(styleCon);
                //嫌疑人姓名
                cell = row.createCell(1);
                cell.setCellValue(stockBean.getManager());
                cell.setCellStyle(styleCon);
                //编号
                cell = row.createCell(2);
                cell.setCellValue(stockBean.getNumber());
                cell.setCellStyle(styleCon);
                //名称
                cell = row.createCell(3);
                cell.setCellValue(stockBean.getProduct_name());
                cell.setCellStyle(styleCon);
                //数量
                cell = row.createCell(4);
                cell.setCellValue(stockBean.getAmount() + " " + stockBean.getUnit());
                cell.setCellStyle(styleCon);
                //特征
                cell = row.createCell(5);
                cell.setCellValue(stockBean.getType());
                cell.setCellStyle(styleCon);
            }

            //统计数据
            for (int i = 0; i < sums.size(); i++) {
                row = sheet.createRow(list.size() + 2 + i);
                row.setHeight((short) 600);
                Map<String, String> map = sums.get(i);

                //设置列内容
                //仓库
                cell = row.createCell(0);
                cell.setCellValue(map.get("name"));
                cell.setCellStyle(styleCon);
                //嫌疑人姓名
                cell = row.createCell(1);
                cell.setCellValue(map.get("sum"));
                cell.setCellStyle(styleCon);
            }
            return wb;
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println(e.getMessage());
        }
        return wb;
    }


}
