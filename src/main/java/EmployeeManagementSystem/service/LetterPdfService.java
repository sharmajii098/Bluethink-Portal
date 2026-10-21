package EmployeeManagementSystem.service;

import EmployeeManagementSystem.entity.EmployeeLetterRequest;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/** Issued PDFs are stored once; downloads never regenerate from subsequently changed employee records. */
@Service
public class LetterPdfService {
 public record Component(String label,BigDecimal annualAmount,String category){}
 public record Snapshot(String title,String type,String reference,String issueDate,String companyName,String companyAddress,
       String email,String cin,String signatoryName,String signatoryDesignation,String body,List<Component> compensation){}
 public byte[] generate(EmployeeLetterRequest letter){
  if(!"ISSUED".equals(letter.getStatus())||letter.getIssuedPdf()==null)throw new IllegalStateException("The letter has not been issued");
  byte[] bytes=letter.getIssuedPdf();
  if(!digest(bytes).equals(letter.getIssuedPdfHash()))throw new IllegalStateException("Stored letter integrity check failed");
  return bytes.clone();
 }
 public static String digest(byte[] bytes){try{return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(bytes));}catch(Exception e){throw new IllegalStateException(e);}}
 public byte[] render(Snapshot snapshot,boolean draft){
  try(PDDocument document=new PDDocument();ByteArrayOutputStream out=new ByteArrayOutputStream()){
   document.getDocumentInformation().setTitle(snapshot.title());document.getDocumentInformation().setAuthor(snapshot.companyName());
   PDFont font=PdfFontSupport.regular(document);PDImageXObject logo=PDImageXObject.createFromByteArray(document,BluethinkReportBranding.logo(),"BlueThink");
   Layout layout=new Layout(document,font,logo,snapshot,draft);layout.newPage();
   for(String paragraph:snapshot.body().replace("\r","").split("\n",-1)){
    if(paragraph.indexOf('\f')>=0){String[] pages=paragraph.split("\f",-1);for(int n=0;n<pages.length;n++){if(n>0)layout.newPage();if(!pages[n].isBlank())layout.paragraph(pages[n],11);}continue;}
    if(paragraph.trim().equals("@@COMPENSATION@@")){layout.compensation(snapshot.compensation());continue;}
    if(paragraph.startsWith("# ")){layout.gap(7);layout.paragraph(paragraph.substring(2),13);}
    else layout.paragraph(paragraph,11);
   }
   if(layout.y<240)layout.newPage();layout.gap(18);layout.paragraph("For "+snapshot.companyName(),11);layout.gap(24);layout.paragraph(snapshot.signatoryName(),12);layout.paragraph(snapshot.signatoryDesignation(),10);
   layout.paragraph("Authorized through the portal approval workflow. No handwritten or digital signature is applied.",8.5f);layout.close();
   document.save(out);return out.toByteArray();
  }catch(IllegalArgumentException e){throw new IllegalArgumentException("Letter PDF validation failed: "+e.getMessage()+". For unsupported characters, configure BLUETHINK_PDF_FONT_PATH",e);}
  catch(IOException e){throw new IllegalStateException("Unable to generate branded letter",e);}
 }
 private static final class Layout implements AutoCloseable{
  private final PDDocument document;private final PDFont font;private final PDImageXObject logo;private final Snapshot data;private final boolean draft;
  private PDPageContentStream canvas;private float y;private int page;
  private static final float LEFT=52,WIDTH=491,BOTTOM=112;
  Layout(PDDocument document,PDFont font,PDImageXObject logo,Snapshot data,boolean draft){this.document=document;this.font=font;this.logo=logo;this.data=data;this.draft=draft;}
  void newPage()throws IOException{
   close();PDPage p=new PDPage(PDRectangle.A4);document.addPage(p);canvas=new PDPageContentStream(document,p);page++;
   canvas.drawImage(logo,LEFT,759,168,46);canvas.setNonStrokingColor(0.08f,0.18f,0.32f);
   headerLine(LEFT,733,10,data.companyName(),WIDTH);text(LEFT,715,9,(draft?"DRAFT - NOT ISSUED  |  ":"")+data.type().replace('_',' '));
   canvas.setStrokingColor(0.1f,0.3f,0.5f);canvas.moveTo(LEFT,701);canvas.lineTo(LEFT+WIDTH,701);canvas.stroke();
   text(LEFT,682,9,"Reference: "+data.reference()+"   |   Date: "+data.issueDate());
   canvas.setNonStrokingColor(0.15f,0.17f,0.2f);y=650;
   // Fixed-height footer with measured wrapping; lengthy legal addresses never overlap the letter body.
   List<String> addressLines=wrap(data.companyAddress().replace('\n',' '),8,WIDTH);if(addressLines.size()>4)throw new IllegalArgumentException("Company address exceeds the four-line letterhead footer; shorten the registered-address display");float footerY=88;for(String line:addressLines){text(LEFT,footerY,8,line);footerY-=10;}
   headerLine(LEFT,34,7.5f,"Verification: "+data.email(),WIDTH);headerLine(LEFT,22,7,"Confidential HR document | Page "+page+(data.cin()==null||data.cin().isBlank()?"":" | CIN: "+data.cin()),WIDTH);
  }
  void headerLine(float x,float yy,float size,String value,float width)throws IOException{while(size>7&&font.getStringWidth(value)/1000*size>width)size-=0.25f;if(font.getStringWidth(value)/1000*size>width)throw new IllegalArgumentException("Letterhead text exceeds the printable width");text(x,yy,size,value);}
  void text(float x,float yy,float size,String value)throws IOException{canvas.beginText();canvas.setFont(font,size);canvas.newLineAtOffset(x,yy);canvas.showText(value);canvas.endText();}
  List<String> wrap(String raw,float size,float width)throws IOException{
   List<String> lines=new ArrayList<>();StringBuilder line=new StringBuilder();
   for(String word:raw.replace('\t',' ').split(" +")){
    if(word.isEmpty())continue;
    String proposed=line.length()==0?word:line+" "+word;
    if(font.getStringWidth(proposed)/1000*size<=width){line.setLength(0);line.append(proposed);continue;}
    if(line.length()>0){lines.add(line.toString());line.setLength(0);}
    for(int pos=0;pos<word.length();){int cp=word.codePointAt(pos);String part=new String(Character.toChars(cp));
     if(font.getStringWidth(line.toString()+part)/1000*size>width&&line.length()>0){lines.add(line.toString());line.setLength(0);}line.append(part);pos+=Character.charCount(cp);}
   }
   if(line.length()>0)lines.add(line.toString());if(lines.isEmpty())lines.add("");return lines;
  }
  void gap(float height)throws IOException{y-=height;if(y<BOTTOM)newPage();}
  void paragraph(String value,float size)throws IOException{
   for(String line:wrap(value,size,WIDTH)){if(y<BOTTOM+size*1.5)newPage();text(LEFT,y,size,line);y-=size*1.45f;}y-=5;
  }
  void compensation(List<Component> components)throws IOException{
   if(components==null||components.isEmpty())throw new IllegalArgumentException("Compensation components missing");
   paragraph("Compensation annexure (INR)",13);tableHeader();
   for(Component c:components){
    List<String> label=wrap(c.label()+" ["+c.category().replace('_',' ')+"]",9,250);float height=Math.max(30,label.size()*13+12);
    if(y-height<BOTTOM){newPage();tableHeader();}
    float rowTop=y;for(String line:label){text(LEFT+6,y-14,9,line);y-=13;}y=rowTop;
    text(LEFT+268,y-14,9,c.annualAmount().toPlainString());text(LEFT+378,y-14,9,c.annualAmount().divide(BigDecimal.valueOf(12),2,RoundingMode.HALF_UP).toPlainString());
    canvas.setStrokingColor(0.8f,0.83f,0.87f);canvas.moveTo(LEFT,y-height);canvas.lineTo(LEFT+WIDTH,y-height);canvas.stroke();y-=height;
   }
   gap(14);paragraph("Monthly equivalent = annual component divided by 12. Employer contributions are not take-home pay. Deductions are shown separately and are excluded from CTC totals.",9);
  }
  void tableHeader()throws IOException{
   if(y<BOTTOM+55)newPage();canvas.setNonStrokingColor(0.92f,0.95f,0.98f);canvas.addRect(LEFT,y-30,WIDTH,30);canvas.fill();canvas.setNonStrokingColor(0.08f,0.18f,0.32f);
   text(LEFT+6,y-19,9,"Component / treatment");text(LEFT+268,y-19,9,"Annual amount");text(LEFT+378,y-19,9,"Monthly equivalent");y-=30;
  }
  @Override public void close()throws IOException{if(canvas!=null){canvas.close();canvas=null;}}
 }
}
