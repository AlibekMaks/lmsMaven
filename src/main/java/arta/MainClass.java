package arta;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.poi.hssf.usermodel.*;
import com.sun.corba.se.spi.activation.Server;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.swing.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

import arta.common.logic.util.Date;


public class MainClass  {

    public static void main(String[] args) throws Exception{
// create a new file
        FileOutputStream out = new FileOutputStream("workbook.xls");
// create a new workbook
        HSSFWorkbook wb = new HSSFWorkbook();
// create a new sheet
        HSSFSheet s = wb.createSheet();
// declare a row object reference
        HSSFRow r = null;
// declare a cell object reference
        HSSFCell c = null;
// create 3 cell styles
        HSSFCellStyle cs = wb.createCellStyle();
        HSSFCellStyle cs2 = wb.createCellStyle();
        HSSFCellStyle cs3 = wb.createCellStyle();
        HSSFDataFormat df = wb.createDataFormat();
// create 2 fonts objects
        HSSFFont f = wb.createFont();
        HSSFFont f2 = wb.createFont();

//set font 1 to 12 point type
        f.setFontHeightInPoints((short) 12);
//make it blue
        f.setColor( (short)0xc );
// make it bold
//arial is the default font
        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

//set font 2 to 10 point type
        f2.setFontHeightInPoints((short) 10);
//make it red
        f2.setColor( (short)HSSFFont.COLOR_RED );
//make it bold
        f2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        f2.setStrikeout( true );

//set cell stlye
        cs.setFont(f);
//set the cell format
        cs.setDataFormat(df.getFormat("#,##0.0"));

//set a thin border
        cs2.setBorderBottom(cs2.BORDER_THIN);
//fill w fg fill color
        cs2.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
        cs2.setFillForegroundColor( (short)6);   //3 yad  zelen , 4 sin  , 5 zhelt
//set the cell format to text see HSSFDataFormat for a full list
        cs2.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));

// set the font
        cs2.setFont(f2);

// set the sheet name in Unicode
        wb.setSheetName(0, "\u0422\u0435\u0441\u0442\u043E\u0432\u0430\u044F " +
                           "\u0421\u0442\u0440\u0430\u043D\u0438\u0447\u043A\u0430"
        );
// in case of compressed Unicode
// wb.setSheetName(0, "HSSF Test", HSSFWorkbook.ENCODING_COMPRESSED_UNICODE );
// create a sheet with 30 rows (0-29)

        short rownum;
        for (rownum = (short) 0; rownum < 256; rownum++)
        {
            r = s.createRow(rownum);
            c = r.createCell((short)0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(rownum);
            c.setCellStyle(style);
            c.setCellValue(new HSSFRichTextString(rownum+"\r\nasdasd"));
        }

//draw a thick black border on the row at the bottom using BLANKS
// advance 2 rows
        rownum++;
        rownum++;

        r = s.createRow(rownum);

// define the third style to be the default
// except with a thick black border at the bottom
        cs3.setBorderBottom(cs3.BORDER_THICK);

//create 50 cells
        for (short cellnum = (short) 0; cellnum < 50; cellnum++)
        {
            //create a blank type cell (no value)
            c = r.createCell(cellnum);
            // set it to the thick black border style
            c.setCellStyle(cs3);
        }

//end draw thick black border


// demonstrate adding/naming and deleting a sheet
// create a sheet, set its title then delete it
        s = wb.createSheet();
        wb.setSheetName(1, "DeletedSheet");
        wb.removeSheetAt(1);
//end deleted sheet

// write the workbook to the output stream
// close our file (don't blow out our file handles
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        wb.write(bout);
        out.write(bout.toByteArray());
        out.flush();
        out.close();
    }

}
