package EmployeeManagementSystem.service;

import java.awt.Color;
import java.io.*;
import java.util.*;
import java.util.List;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/** Repeating company identity, wrapped tables and page numbers for generated PDFs. */
final class BluethinkPdfReport implements AutoCloseable {
    private final PDDocument document=new PDDocument();
    private final PDFont font;
    private final PDImageXObject logo;
    private final PDRectangle size;
    private final String title, context;
    private final String[] headings;
    private final float[] widths;
    private PDPage page;
    private PDPageContentStream stream;
    private float y;
    private final Color ink=new Color(29,57,79), muted=new Color(102,121,136), navy=new Color(21,61,96);
    private BluethinkPdfReport(String title,String context,String[] headings,float[] weights)throws IOException {
        this.title=title;this.context=context;this.headings=headings;
        size=headings.length>5?new PDRectangle(PDRectangle.A4.getHeight(),PDRectangle.A4.getWidth()):PDRectangle.A4;
        font=PdfFontSupport.regular(document);
        logo=PDImageXObject.createFromByteArray(document,BluethinkReportBranding.logo(),"Bluethink");
        widths=new float[weights.length];for(int i=0;i<weights.length;i++)widths[i]=weights[i]*(size.getWidth()-64);
        nextPage();
    }
    static byte[] render(String title,String context,String[] headings,float[] weights,List<List<String>> rows)throws IOException {
        try(BluethinkPdfReport report=new BluethinkPdfReport(title,context,headings,weights);ByteArrayOutputStream out=new ByteArrayOutputStream()){
            int index=0;for(List<String> row:rows)report.row(row,index++);
            if(rows.isEmpty())report.row(List.of("No authorized records for the selected period."),0);
            report.stream.close();report.stream=null;
            int total=report.document.getNumberOfPages();
            for(int i=0;i<total;i++){try(PDPageContentStream footer=new PDPageContentStream(report.document,report.document.getPage(i),PDPageContentStream.AppendMode.APPEND,true,true)){
                footer.setStrokingColor(new Color(220,228,234));footer.moveTo(32,33);footer.lineTo(report.size.getWidth()-32,33);footer.stroke();
                report.write(footer,"BLUETHINK HRMS | CONFIDENTIAL",32,21,7,report.muted);
                report.write(footer,"Page "+(i+1)+" of "+total,report.size.getWidth()-100,21,7,report.muted);
            }}
            report.document.getDocumentInformation().setTitle(title);report.document.getDocumentInformation().setAuthor("Bluethink IT Consulting Pvt. Ltd.");report.document.save(out);return out.toByteArray();
        }
    }
    private String printable(String value)throws IOException {
        StringBuilder out=new StringBuilder();for(int cp:Objects.toString(value,"").codePoints().toArray()){
            String character=new String(Character.toChars(cp));if(cp=='\n'||cp=='\r'){out.append(' ');continue;}
            try{font.encode(character);out.append(character);}catch(IllegalArgumentException ex){throw new IOException("Unsupported glyph; configure BLUETHINK_PDF_FONT_PATH before exporting this record",ex);}
        }return out.toString();
    }
    private void write(PDPageContentStream out,String value,float x,float baseline,float fontSize,Color color)throws IOException {
        out.beginText();out.setFont(font,fontSize);out.setNonStrokingColor(color);out.newLineAtOffset(x,baseline);out.showText(printable(value));out.endText();
    }
    private List<String> wrap(String value,float fontSize,float width)throws IOException {
        List<String> lines=new ArrayList<>();StringBuilder line=new StringBuilder();float used=0;
        for(int cp:printable(value).codePoints().toArray()){
            String character=new String(Character.toChars(cp));float next=font.getStringWidth(character)*fontSize/1000;
            if(used+next>width&&line.length()>0){lines.add(line.toString());line.setLength(0);used=0;}
            line.append(character);used+=next;
        }lines.add(line.toString());return lines;
    }
    private void nextPage()throws IOException {
        if(stream!=null)stream.close();page=new PDPage(size);document.addPage(page);stream=new PDPageContentStream(document,page);
        float top=size.getHeight()-32;
        stream.drawImage(logo,32,top-42,154,42);
        write(stream,"BLUETHINK IT CONSULTING PVT. LTD.",202,top-15,11,navy);
        write(stream,"B-12, SEC-2, Noida, U.P.-201301 | Think beyond",202,top-32,8,muted);
        stream.setStrokingColor(navy);stream.moveTo(32,top-54);stream.lineTo(size.getWidth()-32,top-54);stream.stroke();
        y=top-78;write(stream,title,32,y,15,ink);y-=18;
        for(String line:wrap(context,8,size.getWidth()-64)){write(stream,line,32,y,8,muted);y-=12;}
        write(stream,"Generated: "+BluethinkReportBranding.generated()+" | Role-authorized HRMS records",32,y,7,muted);y-=18;
        float x=32;stream.setNonStrokingColor(navy);stream.addRect(x,y-26,size.getWidth()-64,26);stream.fill();
        for(int i=0;i<headings.length;i++){write(stream,headings[i],x+6,y-16,8,Color.WHITE);x+=widths[i];}y-=26;
    }
    private void row(List<String> values,int index)throws IOException {
        List<List<String>> cells=new ArrayList<>();int lines=1;
        for(int c=0;c<headings.length;c++){List<String> parts=wrap(c<values.size()?values.get(c):"",8,widths[c]-12);cells.add(parts);lines=Math.max(lines,parts.size());}
        int offset=0;
        while(offset<lines){
            int capacity=(int)((y-48-12)/11);if(capacity<1){nextPage();continue;}
            int take=Math.min(capacity,lines-offset);float height=take*11+12;
            stream.setNonStrokingColor(index%2==0?Color.WHITE:new Color(240,245,249));stream.addRect(32,y-height,size.getWidth()-64,height);stream.fill();
            float x=32;for(int c=0;c<headings.length;c++){for(int l=0;l<take;l++){int n=offset+l;if(n<cells.get(c).size())write(stream,cells.get(c).get(n),x+6,y-13-l*11,8,ink);}x+=widths[c];}
            stream.setStrokingColor(new Color(220,228,234));stream.moveTo(32,y-height);stream.lineTo(size.getWidth()-32,y-height);stream.stroke();y-=height;offset+=take;if(offset<lines)nextPage();
        }
    }
    @Override public void close()throws IOException {try{if(stream!=null)stream.close();}finally{document.close();}}
}
