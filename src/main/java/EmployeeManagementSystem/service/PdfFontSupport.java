package EmployeeManagementSystem.service;
import java.io.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.*;
/** Supply an appropriately licensed TrueType font on the deployment host for non-Latin names. */
public final class PdfFontSupport {
 private PdfFontSupport(){}
 public static PDFont regular(PDDocument document)throws IOException{
  String path=System.getenv("BLUETHINK_PDF_FONT_PATH");
  if(path!=null&&!path.isBlank()){try(InputStream in=new FileInputStream(path)){return PDType0Font.load(document,in,true);}}
  return new PDType1Font(Standard14Fonts.FontName.HELVETICA);
 }
}
