package EmployeeManagementSystem.service;

import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

/** Shared presentation for authorized, application-generated workbook exports. */
final class BluethinkReportBranding {
    private BluethinkReportBranding() {}
    static byte[] logo() throws IOException {
        try (InputStream in = BluethinkReportBranding.class.getResourceAsStream("/branding/bluethink-reference.png")) {
            if (in == null) throw new IOException("Bluethink report logo is missing");
            BufferedImage reference = ImageIO.read(in);
            BufferedImage wordmark = reference.getSubimage(25, 440, 1203, 328);
            ByteArrayOutputStream out = new ByteArrayOutputStream(); ImageIO.write(wordmark, "png", out); return out.toByteArray();
        }
    }
    static String generated() { return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")); }
    static void workbook(Workbook book, Sheet sheet, String title, String context, boolean table) throws IOException {
        int last = sheet.getLastRowNum(); int columns = 1;
        for (Row row : sheet) columns = Math.max(columns, row.getLastCellNum());
        int layoutColumns = Math.max(columns, 6);
        sheet.shiftRows(0, last, 8);
        for (int c=columns;c<layoutColumns;c++) sheet.setColumnWidth(c, 18*256);
        Font titleFont=book.createFont(); titleFont.setFontName("Calibri"); titleFont.setBold(true); titleFont.setFontHeightInPoints((short)16); titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
        Font small=book.createFont(); small.setFontName("Calibri"); small.setFontHeightInPoints((short)10); small.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        CellStyle titleStyle=book.createCellStyle(); titleStyle.setFont(titleFont);
        CellStyle infoStyle=book.createCellStyle();infoStyle.setFont(small);infoStyle.setWrapText(true);
        Row company=sheet.createRow(0);company.setHeightInPoints(34);Cell companyCell=company.createCell(2);companyCell.setCellValue("BLUETHINK IT CONSULTING PVT. LTD.");companyCell.setCellStyle(titleStyle);sheet.addMergedRegion(new CellRangeAddress(0,0,2,layoutColumns-1));
        Row address=sheet.createRow(1);address.setHeightInPoints(22);Cell addressCell=address.createCell(2);addressCell.setCellValue("B-12, SEC-2, Noida, U.P.-201301 | Think beyond");addressCell.setCellStyle(infoStyle);sheet.addMergedRegion(new CellRangeAddress(1,1,2,layoutColumns-1));
        int image=book.addPicture(logo(),Workbook.PICTURE_TYPE_PNG);ClientAnchor anchor=book.getCreationHelper().createClientAnchor();anchor.setCol1(0);anchor.setRow1(0);Picture picture=sheet.createDrawingPatriarch().createPicture(anchor,image);picture.resize(190d/1203d);
        Row heading=sheet.createRow(3);heading.setHeightInPoints(30);Cell headingCell=heading.createCell(0);headingCell.setCellValue(title);headingCell.setCellStyle(titleStyle);sheet.addMergedRegion(new CellRangeAddress(3,3,0,layoutColumns-1));
        for(int r=4;r<=5;r++){Row row=sheet.createRow(r);row.setHeightInPoints(24);Cell cell=row.createCell(0);cell.setCellValue(r==4?context:"Generated: "+generated()+" | Source: role-authorized HRMS records");cell.setCellStyle(infoStyle);sheet.addMergedRegion(new CellRangeAddress(r,r,0,layoutColumns-1));}
        Font headerFont=book.createFont();headerFont.setBold(true);headerFont.setColor(IndexedColors.WHITE.getIndex());headerFont.setFontName("Calibri");
        CellStyle header=book.createCellStyle();header.setFont(headerFont);header.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());header.setFillPattern(FillPatternType.SOLID_FOREGROUND);header.setWrapText(true);
        CellStyle[] body=new CellStyle[2];
        for(int i=0;i<2;i++){body[i]=book.createCellStyle();body[i].setWrapText(true);body[i].setVerticalAlignment(VerticalAlignment.TOP);body[i].setBorderBottom(BorderStyle.HAIR);body[i].setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());if(i==1){body[i].setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());body[i].setFillPattern(FillPatternType.SOLID_FOREGROUND);}}
        for(int r=8;r<=last+8;r++){Row row=sheet.getRow(r);if(row==null)continue;int height=table&&r==8?30:26;for(Cell cell:row){cell.setCellStyle(table&&r==8?header:body[(r-8)%2]);String value=cell.toString();int chars=Math.max(12,sheet.getColumnWidth(cell.getColumnIndex())/256);height=Math.max(height,Math.min(240,(value.length()/chars+1)*15+8));}row.setHeightInPoints(height);}
        if(table){sheet.createFreezePane(0,9);sheet.setAutoFilter(new CellRangeAddress(8,last+8,0,columns-1));sheet.setRepeatingRows(new CellRangeAddress(0,8,-1,-1));}
        Row footer=sheet.createRow(last+11);Cell foot=footer.createCell(0);foot.setCellValue("CONFIDENTIAL | Bluethink HRMS | For authorized business use only");foot.setCellStyle(infoStyle);sheet.addMergedRegion(new CellRangeAddress(last+11,last+11,0,layoutColumns-1));
        sheet.getFooter().setLeft("Bluethink HRMS | Confidential");sheet.getFooter().setRight("Page &P of &N");sheet.setFitToPage(true);sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);sheet.getPrintSetup().setLandscape(columns>6);sheet.getPrintSetup().setFitWidth((short)1);sheet.getPrintSetup().setFitHeight((short)0);
    }
}
