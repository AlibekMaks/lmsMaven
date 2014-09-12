package arta.common.hssf;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFFont;


public class HSSFStyles {

    /**
     * certical alignment -center, alignment - center, size-14, bold
     * @param workbook
     * @return
     */
    public HSSFCellStyle getHeaderCellStyle(HSSFWorkbook workbook){
        if (workbook == null)
            return null;
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();

        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setFontHeightInPoints((short)14);

        cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * certical alignment -center, alignment - center
     * @param workbook
     * @return
     */
    public HSSFCellStyle getCommonCellStyle(HSSFWorkbook workbook){
        if (workbook == null)
            return null;
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFont(font);
        return cellStyle;
    }

    public HSSFCellStyle getTableCellStyle(HSSFWorkbook workbook){
        if (workbook == null)
            return null;
        HSSFCellStyle style = workbook.createCellStyle();

        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        style.setWrapText(false);

        return style;
    }


    public HSSFCellStyle getTableHeaderCellStyle(HSSFWorkbook workbook){
        if (workbook == null)
            return null;
        HSSFCellStyle style = workbook.createCellStyle();

        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;
    }

    public HSSFCellStyle getTableHeaderCellStyleWithVertBoundaries(HSSFWorkbook workbook){
        if (workbook == null)
            return null;
        HSSFCellStyle style = workbook.createCellStyle();

        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;
    }

    public HSSFCellStyle getTableHeaderCellStyleWithHorBoundaries(HSSFWorkbook workbook){
        if (workbook == null)
            return null;
        HSSFCellStyle style = workbook.createCellStyle();

        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        return style;
    }

}
