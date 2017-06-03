package org.epe.core;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.epe.annotations.ECell;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 授权说明：
 * <p>
 * <p>
 * Copyright(c) 2011 ERE1.2 mec(北京神农微电子计算机工作室)
 * <p>
 * ERE为一个开源项目，目前我重构了ERE解析部份，待久化模块仍在进行中，ERE遵循GPL协议，
 * 用户可以自由修改本软件,如在互联网上传或者发表ERE，请注明出处。
 * <p>
 * ERE1.2支持office2003与office2007,基于POI3.8,为目前最新版本，ERE1.2只重构了逐行读取，
 * 逐列读取与复杂表头算法还没有完成重构，将在后续完成。ERE1.2可以满足项目中简单的表格
 * 导入导出，以及多Sheet的导入导出，不支持VBA，如果您的Excel中有VBA，ERE可能无法读取， 出现这种情况，请将VBA删除。
 * ERE成功应用于山西移动内审管理系统、浙江移动需求管理平台。
 * <p>
 * 改变日志
 * <p>
 * 1、重构逐行读取 2、增加注解配置，在需要进行EOM的类中标注注解可以映射指定字段 3、支持多sheet
 * 4、增加EOM，ERE1.2以前的版本不支持EOM，即Excel对象关系映射，可以方便的映射对象。
 * 5、逐列读取与复杂表头暂未重构，将在后续版本推出，ERE1.2以前的版本支持。 6、暂不支持Excel公式
 * 7、EDM（Excel数据库映射）暂未重构，ERE1.2以前的版本支持。
 * <p>
 * 项目下载路径 http://code.google.com/p/excelresolveengine/downloads/list
 * <p>
 * ERE核心解析类，实现了EOM（Excel object Mapping）,OEM(Object Excel mapping)
 * 譔类可以导出List<T>中的数据，也可以有将Excel中的数据以List<T>的形式返回。 list中的对象可由用户自行定义。
 * 在目标实体中标注ECell可以将对象映射到Excel中
 *
 * @author 李声波
 */
public class ExcelResolve extends AbstractExcelResolve {

    private short headBackgroundColor;

    public ExcelResolve() {
        super();
    }

    /**
     * 设置值
     *
     * @param headCell 存储数据的单元格对象
     * @param cell     单元格
     */
    private void setCellValues(HeadCell headCell, Cell cell) {
        if (headCell.getFieldType() == Integer.class) {
            cell.setCellValue(Integer.parseInt(headCell.getValue() == null ? "0" : headCell.getValue().toString()));
        }
        if (headCell.getFieldType() == String.class) {
            cell.setCellValue(headCell.getValue() == null ? "" : headCell.getValue().toString());
        }
        if (headCell.getFieldType() == Double.class) {
            cell.setCellValue(Double.parseDouble(headCell.getValue() == null ? "0" : headCell.getValue().toString()));
        }
        if (headCell.getFieldType() == Date.class) {
            if (headCell.getValue() == null) {
                cell.setCellValue((double) 0);
            } else {

                cell.setCellValue(DateFromat.formatDate((Date) headCell.getValue(), headCell.getPaten()));
            }

        }
        if (headCell.getFieldType() == Float.class) {
            cell.setCellValue(Float.parseFloat(headCell.getValue() == null ? "0" : headCell.getValue().toString()));
        }
        if (headCell.getFieldType() == int.class) {
            cell.setCellValue(Integer.parseInt(headCell.getValue() == null ? "0" : headCell.getValue().toString()));
        }
    }

    /**
     * 执行EOM时设值的方法
     *
     * @param field
     * @param obj
     * @param headCell
     */
    private void setObjectValues(Field field, Object obj, HeadCell headCell) throws ResolveException {
        try {
            if (headCell.getFieldType() == Integer.class && field.getType() == Integer.class) {
                field.set(obj, Integer.parseInt(headCell.getValue() == null ? "" : headCell.getValue().toString()));
            }
            if (headCell.getFieldType() == String.class && field.getType() == String.class) {
                field.set(obj, headCell.getValue() == null ? "" : headCell.getValue().toString());
            }
            if (headCell.getFieldType() == Double.class && field.getType() == Double.class) {
                field.setDouble(obj, Double.parseDouble(headCell.getValue() == null ? "" : headCell.getValue().toString()));
            }
            if (headCell.getFieldType() == Date.class) {
                Date date = (Date) headCell.getValue();
                field.set(obj, date);
            }
            if (field.getType() == Float.class) {
                field.set(obj, new Float(headCell.getValue().toString()));
            }
            if (headCell.getFieldType() == int.class) {
                field.setInt(obj, Integer.parseInt(headCell.getValue() == null ? "" : headCell.getValue().toString()));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            if (e.getMessage().indexOf("Integer") != -1) {
                throw new ResolveException(headCell.getName() + "字段不为合法的整型");
            } else if (e.getMessage().indexOf("Date") != -1) {
                throw new ResolveException(headCell.getName() + "字段不是合法的日期格式");
            } else if (e.getMessage().indexOf("Float") != -1) {
                throw new ResolveException(headCell.getName() + "字段不是合法的小数类型");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到目标单元格，该方法将跟据ECell注解生成相应的Excel字段
     *
     * @param listObject
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected List<HeadCell> getTargetCells(List<?> listObject) throws IllegalArgumentException, IllegalAccessException {
        fieldCount = 0;
        List<HeadCell> list = new ArrayList<HeadCell>();
        if (listObject.size() < 0) return null;
        for (int i = 0; i < listObject.size(); i++) {
            Class<?> classObj = listObject.get(i).getClass();

            fields = classObj.getDeclaredFields();
            for (Field field : fields) {

                field.setAccessible(true);
                ECell ecell = field.getAnnotation(ECell.class);
                if (ecell == null) {
                    continue;
                }
                HeadCell hc = new HeadCell();
                hc.setName(ecell.name());
                hc.setColumnName(field.getName());
                hc.setValue(field.get(listObject.get(i)));
                hc.setHidden(ecell.isHidden());
                hc.setFieldType(field.getType());
                list.add(hc);
                if (i == 0) fieldCount++;
            }
        }
        return list;
    }

    protected <T extends Object> T getTargetObject(Class<T> clss) throws IllegalArgumentException, IllegalAccessException {
        Object obj_instance = null;
        Constructor[] constructors = clss.getDeclaredConstructors();
        try {
            for (int i = 0; i < constructors.length; i++) {
                constructors[i].setAccessible(true);
                obj_instance = constructors[i].newInstance(new Object[0]);
            }
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }
        return (T) obj_instance;
    }

    /**
     * 得到表头背景色
     *
     * @return
     */
    public short getHeadBackgroundColor() {
        return headBackgroundColor;
    }

    /**
     * 设置表头背景色
     *
     * @param headBackgroundColor
     */
    public void setHeadBackgroundColor(short headBackgroundColor) {
        this.headBackgroundColor = headBackgroundColor;
    }

    @Override
    public List<HeadCell> dataProcessFactory(Object obj) {

        // return listValue;
        return null;
    }

    @Override
    public <T extends Object> List<SheetItem> inputExcel() {
        List<SheetItem> listSheet = new ArrayList<SheetItem>();
        this.createWorkbook();
        List<String> columnNames = null;
        int cellCount = 0;
        for (Workbook workbook : this.workbook) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                columnNames = new ArrayList<String>();
                Sheet sheet = workbook.getSheetAt(i);
                SheetItem si = new SheetItem();
                si.setName(sheet.getSheetName());
                List<HeadCell> headCells = new ArrayList<HeadCell>();
                for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
                    Row row = sheet.getRow(j);
                    if (j == 0) cellCount = row.getPhysicalNumberOfCells();
                    for (int h = 0; h < cellCount; h++) {
                        String name = "";
                        Cell cell = row.getCell(h);
                        HeadCell headCell = new HeadCell();
                        if (j == 0) {
                            name = cell.getStringCellValue();
                            columnNames.add(name);
                            continue;
                        }
                        headCell.setName(columnNames.get(h));
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case HSSFCell.CELL_TYPE_STRING:
                                    headCell.setFieldType(String.class);
                                    headCell.setValue(cell.getStringCellValue());
                                    break;
                                case HSSFCell.CELL_TYPE_NUMERIC:

                                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                        headCell.setFieldType(Date.class);
                                        headCell.setValue(cell.getDateCellValue());
                                    } else {
                                        double value = cell.getNumericCellValue();
                                        String str_value = new String(value + "");
                                        String float_value = str_value.substring(str_value.lastIndexOf(".") + 1);
                                        // 处理科学计数法
                                        if (str_value.indexOf("E") != -1) {
                                            BigDecimal bd = new BigDecimal(str_value);
                                            headCell.setFieldType(String.class);
                                            headCell.setValue(bd.toPlainString());
                                        }
                                        // 处理整型数据
                                        else if ("0".equals(float_value)) {
                                            headCell.setFieldType(Integer.class);
                                            headCell.setValue(str_value.substring(0, str_value.lastIndexOf(".")));
                                        } else {
                                            headCell.setFieldType(Double.class);
                                            headCell.setValue(value);
                                        }

                                    }
                                    break;
                                case HSSFCell.CELL_TYPE_BOOLEAN:
                                    headCell.setFieldType(Boolean.class);
                                    headCell.setValue(cell.getBooleanCellValue());

                                    break;

                            }

                        } else {
                            headCell.setFieldType(String.class);
                            headCell.setValue("");
                        }
                        headCells.add(headCell);
                    }
                }
                si.setHeadCells(headCells);
                listSheet.add(si);
            }
        }
        return listSheet;
    }

    /**
     * excel object 映射方法
     *
     * @return
     */
    public <T extends Object> Map<String, List<T>> excelObjectMapping(List<HeadCell> list, Class<T> clss) {
        Map<String, List<T>> map = new HashMap<String, List<T>>();
        List<T> listMessage = new ArrayList<T>();
        List<T> list_new = new ArrayList<T>();
        StringBuffer message = null;
        // 初化始字段总数
        fieldCount = 0;
        Field[] fields_new = clss.getDeclaredFields();
        fields = new Field[fields_new.length];
        for (Field field : fields_new) {
            field.setAccessible(true);
            ECell ecell = field.getAnnotation(ECell.class);
            if (ecell == null) continue;
            else {
                fields[fieldCount] = field;
                fieldCount++;

            }
        }
        int x = 0;
        T obj = null;

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < fieldCount; j++) {
                if (x == 0 || x % fieldCount == 0) {
                    try {
                        obj = getTargetObject(clss);
                        message = new StringBuffer();
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    }

                }
                if (x >= list.size()) {
                    break;

                }
                // 从List<HeadCell>拿出读到的Excel数据
                HeadCell headCell = list.get(x);
                // 映射对象

                ECell ecell = fields[j].getAnnotation(ECell.class);
                try {
                    if (ecell != null && headCell.getName().equals(ecell.name())) {

                        setObjectValues(fields[j], obj, headCell);
                    }
                } catch (ResolveException e) {
                    e.printStackTrace();
                    //if ("message".equals(fields[j].getName())) {
                    message.append(e.getMessage() + ",");
                    //try {
                    //fields[j].set(obj, message.toString());
                    //} catch (IllegalArgumentException e1) {
                    //	e1.printStackTrace();
                    //	} catch (IllegalAccessException e1) {
                    //	e1.printStackTrace();
                    //	}

                }
                //}

                x++;
                if (x % fieldCount == 0) {
                    list_new.add(obj);
                    listMessage.add((T) message);
                }
            }
        }
        map.put("list", list_new);
        map.put("message", listMessage);
        return map;
    }

    /**
     * 导入EXCEL
     */
    @Override
    public void exportExcel(SheetItem... sheetItems) {
        this.createWorkbook();
        System.out.println("##" + workbook.length);
        for (Workbook workbook : this.workbook) {
            // 多个sheet处理方式
            //if (getSheetItems() != null) {
            for (SheetItem sheetItem : sheetItems) {
                // Sheet s = wb.createSheet();
                List<HeadCell> listValue = null;
                if (sheetItem.getList().getClass() == ArrayList.class) {
                    ArrayList<?> list = (ArrayList<?>) sheetItem.getList();
                    try {
                        listValue = getTargetCells(list);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                genericExcel(workbook, listValue, sheetItem.getName());
            }
            //} else {
            //	genericExcel(workbook, getHeadCells(), getSheetName());
            //}

        }
        genericDataSource(this.getServerPath());
    }

    private void genericExcel(Workbook workbook, List<HeadCell> list, String sheetName) {

        Sheet sheet = null;
        if (sheetName == null || "".equals(sheetName)) {
            sheet = workbook.createSheet();
        } else {
            sheet = workbook.createSheet(sheetName);
        }
        Row headRow = sheet.createRow(0);
        CellStyle cs = workbook.createCellStyle();
        // cs.setFillForegroundColor(headBackgroundColor);
        // cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cs.setBorderBottom((short) 1);
        cs.setBorderLeft((short) 1);
        cs.setBorderRight((short) 1);
        cs.setBorderTop((short) 1);
        cs.setAlignment(CellStyle.ALIGN_LEFT);
        int x = 0;
        for (int j = 1; j <= list.size(); j++) {
            Row row_value = sheet.createRow(j);
            for (int i = 0; i < fieldCount; i++) {
                // 在获取数据之前生成表头
                if (j == 1) {
                    Cell cell = headRow.createCell(i);
                    HeadCell hc = list.get(i);
                    if (hc == null || list.get(x) == null || list.get(x).getName() == null) continue;
                    else {
                        cell.setCellValue(hc.getName());
                        cell.setCellStyle(cs);
                        //合并单元格
                        //						CellRangeAddress cra=new CellRangeAddress(2, 2, 0, 15);
                        //					    sheet.addMergedRegion(cra);
                        sheet.setColumnWidth(i, list.get(x).getName() == null ? 1000 : (list.get(x).getName().length() > (list.get(x).getValue() == null ? 0 : list.get(x).getValue().toString().length()) ? list.get(x).getName().length() : list.get(x).getValue().toString().length()) * 500);
                    }
                }
                // 不能大于list集合大小，否则跳出循环
                if (x >= list.size()) {
                    break;
                } else {
                    // 生成数据
                    Cell cell_value = row_value.createCell(i);
                    cell_value.setCellStyle(cs);
                    setCellValues(list.get(x), cell_value);
                }
                // 自增取出数据的索引
                ++x;
            }
        }
    }

    @Override
    public <T> boolean exportReport(Sheet sheet) {
        return false;
    }

    @Override
    public List<Sheet> getSheet(String sheetName) {
        createWorkbook();
        List<Sheet> sheets = new ArrayList<Sheet>();
        for (Workbook workbk : this.workbook) {
            Sheet sheet = workbk.createSheet(sheetName);
            sheets.add(sheet);
        }
        return sheets;
    }

    /**
     * 测试导出EXCEl文件格式
     */
    //	public static void main(String[] args) {
    //		String serverPath = "c:\\test.xls";
    //
    //		AbstractExcelResolve aer = new ExcelResolve();
    //		aer.setServerPath(serverPath);
    //		List<Sheet> sheets = aer.getSheet("老龄事业发展情况");
    //		for (Sheet sheet : sheets) {
    //			Row row = null;
    //			Cell cell = null;
    //			for (int j = 0; j < 10; j++) {
    //				row = sheet.createRow(j);
    //				for (int j2 = 0; j2 < 32; j2++) {
    //					cell = row.createCell(j2);
    //					cell.setCellValue(j2);
    //				}
    //			}
    //			/*********************设置格式**************************/
    //			CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
    //
    //			//对齐方式
    //			cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    //			//自动换行
    //			cellStyle.setWrapText(true);
    //			//边框颜色
    //			cellStyle.setBorderBottom((short) 1);
    //			cellStyle.setBorderLeft((short) 1);
    //			cellStyle.setBorderRight((short) 1);
    //			cellStyle.setBorderTop((short) 1);
    //			//背景颜色
    //			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    //			cellStyle.setFillForegroundColor(HSSFColor.TURQUOISE.index);
    //			for (int i = 1; i < 6; i++) {
    //				row = sheet.createRow(i);
    //				for (int j = 0; j < 32; j++) {
    //					row.createCell(j).setCellStyle(cellStyle);
    //				}
    //			}
    //			/******第一行******/
    //			CellRangeAddress cra00 = new CellRangeAddress(0, 0, 0, 1);
    //			sheet.addMergedRegion(cra00);
    //			CellRangeAddress cra02 = new CellRangeAddress(0, 0, 2, 30);
    //			sheet.addMergedRegion(cra02);
    //			sheet.getRow(0).getCell(0).setCellValue("区(县):石景山");
    //			sheet.getRow(0).getCell(31).setCellValue("单位:个");
    //			/******第二行******/
    //			CellRangeAddress cra10 = new CellRangeAddress(1, 1, 0, 31);
    //			sheet.addMergedRegion(cra10);
    //			sheet.getRow(1).getCell(0).setCellValue("2010年度北京市区县老龄工作基本情况调查表(六)");
    //			/******第三行******/
    //			CellRangeAddress cra20 = new CellRangeAddress(2, 4, 0, 0);
    //			sheet.addMergedRegion(cra20);
    //			sheet.getRow(2).getCell(0).setCellValue("指标名称 ");
    //			CellRangeAddress cra21 = new CellRangeAddress(2, 3, 1, 5);
    //			sheet.addMergedRegion(cra21);
    //			sheet.getRow(2).getCell(1).setCellValue("老龄人口发展情况 ");
    //			CellRangeAddress cra26 = new CellRangeAddress(2, 2, 6, 31);
    //			sheet.addMergedRegion(cra26);
    //			sheet.getRow(2).getCell(6).setCellValue("老龄事业发展情况");
    //			/******第四行******/
    //			CellRangeAddress cra36 = new CellRangeAddress(3, 3, 6, 9);
    //			sheet.addMergedRegion(cra36);
    //			sheet.getRow(3).getCell(6).setCellValue("老年维权");
    //			CellRangeAddress cra310 = new CellRangeAddress(3, 3, 10, 11);
    //			sheet.addMergedRegion(cra310);
    //			sheet.getRow(3).getCell(10).setCellValue("老年服务设施");
    //			sheet.getRow(3).getCell(12).setCellValue("老年福利");
    //			CellRangeAddress cra313 = new CellRangeAddress(3, 3, 13, 17);
    //			sheet.addMergedRegion(cra313);
    //			sheet.getRow(3).getCell(13).setCellValue("老年医疗护理机构");
    //			CellRangeAddress cra318 = new CellRangeAddress(3, 3, 18, 29);
    //			sheet.addMergedRegion(cra318);
    //			sheet.getRow(3).getCell(18).setCellValue("老年群众组织");
    //			CellRangeAddress cra330 = new CellRangeAddress(3, 3, 30, 31);
    //			sheet.addMergedRegion(cra330);
    //			sheet.getRow(3).getCell(30).setCellValue("老年群众组织");
    //			/******第五行******/
    //			sheet.getRow(4).getCell(1).setCellValue("60岁以上老年人口数");
    //			sheet.getRow(4).getCell(2).setCellValue("70岁以上老年人口数");
    //			sheet.getRow(4).getCell(3).setCellValue("80岁以上老年人口数");
    //			sheet.getRow(4).getCell(4).setCellValue("100岁以上老年人口数");
    //			sheet.getRow(4).getCell(5).setCellValue("纯老年家庭人口数");
    //			sheet.getRow(4).getCell(6).setCellValue("老龄系统接待来信访问次数");
    //			sheet.getRow(4).getCell(7).setCellValue("老年法律援救中心");
    //			sheet.getRow(4).getCell(8).setCellValue("涉老案件数");
    //			sheet.getRow(4).getCell(9).setCellValue("维权协调组织数");
    //			sheet.getRow(4).getCell(10).setCellValue("老年活动站/中心/室数");
    //			sheet.getRow(4).getCell(11).setCellValue("老年人参加人数");
    //			sheet.getRow(4).getCell(12).setCellValue("享受高龄补贴的老年人数");
    //			sheet.getRow(4).getCell(13).setCellValue("老年医院数");
    //			sheet.getRow(4).getCell(14).setCellValue("其中床位数 ");
    //			sheet.getRow(4).getCell(15).setCellValue("临终关怀医院数");
    //			sheet.getRow(4).getCell(16).setCellValue("床位数");
    //			sheet.getRow(4).getCell(17).setCellValue("年底在院人数");
    //			sheet.getRow(4).getCell(18).setCellValue("老年协会");
    //			sheet.getRow(4).getCell(19).setCellValue("其中参加人数");
    //			sheet.getRow(4).getCell(20).setCellValue("居、村老年协会");
    //			sheet.getRow(4).getCell(21).setCellValue("其中参加人数");
    //			sheet.getRow(4).getCell(22).setCellValue("街、乡老年协会");
    //			sheet.getRow(4).getCell(23).setCellValue("其中参加人数");
    //			sheet.getRow(4).getCell(24).setCellValue("市、县老年协会");
    //			sheet.getRow(4).getCell(25).setCellValue("其中参加人数");
    //			sheet.getRow(4).getCell(26).setCellValue("老年基金会");
    //			sheet.getRow(4).getCell(27).setCellValue("事业投入经费");
    //			sheet.getRow(4).getCell(28).setCellValue("其他老年社团组织");
    //			sheet.getRow(4).getCell(29).setCellValue("参加人数");
    //			sheet.getRow(4).getCell(30).setCellValue("老年学校");
    //			sheet.getRow(4).getCell(31).setCellValue("在校人数");
    //			/******第六行******/
    //			sheet.getRow(5).getCell(0).setCellValue("单位");
    //			sheet.getRow(5).getCell(1).setCellValue("人");
    //			sheet.getRow(5).getCell(2).setCellValue("人");
    //			sheet.getRow(5).getCell(3).setCellValue("人");
    //			sheet.getRow(5).getCell(4).setCellValue("人");
    //			sheet.getRow(5).getCell(5).setCellValue("人");
    //			sheet.getRow(5).getCell(6).setCellValue("次");
    //			sheet.getRow(5).getCell(7).setCellValue("个");
    //			sheet.getRow(5).getCell(8).setCellValue("件");
    //			sheet.getRow(5).getCell(9).setCellValue("个");
    //			sheet.getRow(5).getCell(10).setCellValue("个");
    //			sheet.getRow(5).getCell(11).setCellValue("人");
    //			sheet.getRow(5).getCell(12).setCellValue("人");
    //			sheet.getRow(5).getCell(13).setCellValue("个");
    //			sheet.getRow(5).getCell(14).setCellValue("张 ");
    //			sheet.getRow(5).getCell(15).setCellValue("个");
    //			sheet.getRow(5).getCell(16).setCellValue("张");
    //			sheet.getRow(5).getCell(17).setCellValue("人");
    //			sheet.getRow(5).getCell(18).setCellValue("个");
    //			sheet.getRow(5).getCell(19).setCellValue("人");
    //			sheet.getRow(5).getCell(20).setCellValue("个");
    //			sheet.getRow(5).getCell(21).setCellValue("人");
    //			sheet.getRow(5).getCell(22).setCellValue("个");
    //			sheet.getRow(5).getCell(23).setCellValue("人");
    //			sheet.getRow(5).getCell(24).setCellValue("个");
    //			sheet.getRow(5).getCell(25).setCellValue("人");
    //			sheet.getRow(5).getCell(26).setCellValue("个");
    //			sheet.getRow(5).getCell(27).setCellValue("万元");
    //			sheet.getRow(5).getCell(28).setCellValue("个");
    //			sheet.getRow(5).getCell(29).setCellValue("人");
    //			sheet.getRow(5).getCell(30).setCellValue("个");
    //			sheet.getRow(5).getCell(31).setCellValue("人");
    //
    //
    //			aer.genericDataSource(serverPath);
    //		}
    //		System.out.println("ExcelResolve.main(程序结束)");
    //	}
}
