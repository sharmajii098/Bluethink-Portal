package EmployeeManagementSystem.service;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.zip.*;
/** Bounded structural checks supplement, but never replace, malware scanning. */
public final class ChatFileRules {
 private ChatFileRules(){}
 public static boolean matches(String ext,byte[] bytes){
  if(bytes==null||bytes.length==0||bytes.length>10*1024*1024)return false;
  if(ext.equals("pdf"))return bytes.length>=5&&bytes[0]=='%'&&bytes[1]=='P'&&bytes[2]=='D'&&bytes[3]=='F'&&bytes[4]=='-';
  if(ext.equals("png")){byte[] sig={(byte)137,80,78,71,13,10,26,10};return bytes.length>=8&&java.util.Arrays.equals(java.util.Arrays.copyOf(bytes,8),sig);}
  if(ext.equals("jpg")||ext.equals("jpeg"))return bytes.length>=3&&(bytes[0]&255)==255&&(bytes[1]&255)==216&&(bytes[2]&255)==255;
  if(ext.equals("txt")||ext.equals("csv")){try{for(byte b:bytes)if(b==0)return false;StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes));return true;}catch(CharacterCodingException e){return false;}}
  if(!ext.equals("docx")&&!ext.equals("xlsx"))return false;
  boolean manifest=false,document=false;int entries=0;long total=0;
  try(ZipInputStream zip=new ZipInputStream(new ByteArrayInputStream(bytes))){
   ZipEntry e;byte[] buffer=new byte[8192];
   while((e=zip.getNextEntry())!=null){
    if(++entries>2000)return false;
    String name=e.getName();if(name.startsWith("/")||name.contains("..")||name.contains("\\")||name.toLowerCase(java.util.Locale.ROOT).endsWith("vbaproject.bin"))return false;
    if(name.equals("[Content_Types].xml"))manifest=true;
    if(name.equals(ext.equals("docx")?"word/document.xml":"xl/workbook.xml"))document=true;
    int n;while((n=zip.read(buffer))!=-1){total+=n;if(total>50L*1024*1024)return false;}
   }
   return manifest&&document;
  }catch(IOException e){return false;}
 }
}
