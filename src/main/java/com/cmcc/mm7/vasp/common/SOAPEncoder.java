/**File Name:SOAPEncoder.java
 * Company:  中国移动集团公司
 * Date  :   2004-1-8
 * */

package com.cmcc.mm7.vasp.common;

import java.io.*;
import java.util.*;
import java.text.*;
import sun.misc.BASE64Encoder;
import com.cmcc.mm7.vasp.message.*;
import com.cmcc.mm7.vasp.conf.*;

public class SOAPEncoder
{
  private MM7VASPReq mm7VaspReq;
  private boolean bMessageExist;
  private boolean bEncoder;
  private ByteArrayOutputStream byteOutput;
  private MM7Config mm7Config;

  /**默认构造方法*/
  public SOAPEncoder()
  {
    reset();
  }

  public SOAPEncoder(MM7Config config)
  {
    mm7Config = config;
  }

  public void reset()
  {
    mm7VaspReq = null;
    bMessageExist = false;
    bEncoder = false;
    byteOutput = null;
    mm7Config = null;
  }
  /**设置MM7VASPReq类型的消息*/
  public void setMessage(MM7VASPReq mm7vaspreq)
  {
    mm7VaspReq = mm7vaspreq;
    bMessageExist = true;
  }
  /**得到byte[]形式的消息*/
  public byte[] getMessage()
  {
    if(bEncoder)
      return(byteOutput.toByteArray());
    else
      return(null);
  }
  /**进行消息的编码*/
  public void encodeMessage() throws SOAPEncodeException
  {
    if(!bMessageExist)
      throw new SOAPEncodeException("No Multimedia Messages set in the encoder!");
    
    try
    {
      byteOutput = new ByteArrayOutputStream();
      bEncoder = false;
      StringBuffer sb = new StringBuffer();
      StringBuffer ContentBuffer = new StringBuffer();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      if(mm7VaspReq instanceof MM7SubmitReq)
      {
        MM7SubmitReq req = (MM7SubmitReq)mm7VaspReq;
        if(req.isContentExist())
        {
          sb.append("this is a multi-part message in MIME format").append("\r\n");
          sb.append("\r\n");
          sb.append("\r\n");
          sb.append("----NextPart_0_2817_24856").append("\r\n");
          sb.append(String.format("Content-Type:text/xml;charset=\"%s\"\r\n", mm7Config.getCharSet()));
          sb.append("Content-Transfer-Encoding:8bit\r\n");
          sb.append("Content-ID:</tnn-200102/mm7-vasp>\r\n");
          sb.append("\r\n");
        }
      }
      else if(mm7VaspReq instanceof MM7ReplaceReq)
      {
        MM7ReplaceReq req = (MM7ReplaceReq)mm7VaspReq;
        if(req.isContentExist())
        {
          sb.append("this is a multi-part message in MIME format\r\n");
          sb.append("\r\n");
          sb.append("\r\n");
          sb.append("----NextPart_0_2817_24856\r\n");
          sb.append(String.format("Content-Type:text/xml;charset=\"%s\"\r\n", mm7Config.getCharSet()));
          sb.append("Content-Transfer-Encoding:8bit\r\n");
          sb.append("Content-ID:</tnn-200102/mm7-vasp>\r\n");
          sb.append("\r\n");
        }
      }
      
      //end add by hudm 2004-06-21
      sb.append("<?xml version=\"1.0\" encoding=\"").append(mm7Config.getCharSet()).append("\"?><env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\"><env:Header>");//
      if(mm7VaspReq.isTransactionIDExist())  //
        sb.append("<mm7:TransactionID xmlns:mm7=\"http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-6-MM7-1-0\" env:mustUnderstand=\"1\">").append(mm7VaspReq.getTransactionID()).append("</mm7:TransactionID>");
      else
        System.out.println("TransactionID 不许为空！");
      sb.append("</env:Header><env:Body>");
      /**
       * 若发送的消息为MM7SubmitReq
       * */
      if(mm7VaspReq instanceof MM7SubmitReq){
        MM7SubmitReq submitReq = (MM7SubmitReq) mm7VaspReq;
        sb.append("<SubmitReq xmlns=\"http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-6-MM7-1-0\">");
        if(submitReq.isMM7VersionExist())
          sb.append(String.format("<MM7Version>%s</MM7Version>", submitReq.getMM7Version()));
        else
          System.out.println("MM7Version 不许为空！");
        
        if(submitReq.isVASPIDExist() || submitReq.isVASIDExist() || submitReq.isSenderAddressExist())
        {
          sb.append("<SenderIdentification>");
          if (submitReq.isVASPIDExist())
        	sb.append(String.format("<VASPID>%s</VASPID>", submitReq.getVASPID()));
          else
            System.out.println("SP代码VASPID不许为空！");
          
          if(submitReq.isVASIDExist())
          	sb.append(String.format("<VASID>%s</VASID>", submitReq.getVASID()));
          else
            System.out.println("服务代码VASID不许为空！");
          
          if(submitReq.isSenderAddressExist())
          	sb.append(String.format("<SenderAddress>%s</SenderAddress>", submitReq.getSenderAddress()));
          
          sb.append("</SenderIdentification>");
        }
        else
          System.out.println("SP代码VASPID和服务代码VASID均不允许为空！");

        if(submitReq.isToExist() || submitReq.isCcExist() || submitReq.isBccExist())
        {
          sb.append("<Recipients>");
          if (submitReq.isToExist()) {
            sb.append("<To>");
            List<String> ToList = new ArrayList<String>();
            ToList = submitReq.getTo();
            for (int i = 0; i < ToList.size(); i++)
            {
              String strto = ToList.get(i);
              if(strto.indexOf('@') > 0)
                sb.append(String.format("<RFC2822Address>%s</RFC2822Address>", strto));
              else
            	sb.append(String.format("<Number>%s</Number>", strto));
            }
            sb.append("</To>");
          }
          
          if(submitReq.isCcExist()){
            sb.append("<Cc>");
            List<String> CcList = new ArrayList<String>();
            CcList = submitReq.getCc();
            for (int i = 0; i < CcList.size(); i++)
            {
              String strcc = (String)CcList.get(i);
              if(strcc.indexOf('@') > 0)
              	sb.append(String.format("<RFC2822Address>%s</RFC2822Address>", strcc));
              else
            	sb.append(String.format("<Number>%s</Number>", strcc));
            }
            sb.append("</Cc>");
          }
          
          if(submitReq.isBccExist()){
            sb.append("<Bcc>");
            List<String> BccList = new ArrayList<String>();
            BccList = submitReq.getBcc();
            for(int i=0;i<BccList.size();i++)
            {
              String strbcc = (String)BccList.get(i);
              if(strbcc.indexOf('@') > 0)
            	sb.append(String.format("<RFC2822Address>%s</RFC2822Address>", strbcc));
              else
            	sb.append(String.format("<Number>%s</Number>", strbcc));
            }
            sb.append("</Bcc>");
          }
          sb.append("</Recipients>");
        }
        else
          System.out.println("接收方地址To、抄送方地址Cc和密送方地址Bcc中至少需要有一个不为空！");

        if(submitReq.isServiceCodeExist())
          sb.append(String.format("<ServiceCode>%s</ServiceCode>", submitReq.getServiceCode()));
        else
          System.out.println("业务代码ServiceCode不许为空！");

        if(submitReq.isLinkedIDExist())
          sb.append(String.format("<LinkedID>%s</LinkedID>", submitReq.getLinkedID()));
        
        if(submitReq.isMessageClassExist())
          sb.append(String.format("<MessageClass>%s</MessageClass>", submitReq.getMessageClass()));
        
        if(submitReq.isTimeStampExist())
          sb.append(String.format("<TimeStamp>%s</TimeStamp>", submitReq.getTimeStamp()));
        
        if(submitReq.isReplyChargingExist())
        {
          sb.append("<ReplyCharging");
          if(submitReq.isReplyChargingSizeExist())
          	sb.append(String.format(" replyChargingSize=\"%s\"", submitReq.getReplyChargingSize()));
          	
          if(submitReq.isReplyDeadlineExist())
        	sb.append(String.format(" replyDeadline=\"%s+08:00\"", sdf.format(new Date(submitReq.getReplyDeadlineRelative()))));
          
          if(submitReq.isReplyDeadlineAbsoluteExist())
        	sb.append(String.format(" replyDeadline=\"%s+08:00\"", sdf.format(submitReq.getReplyDeadlineAbsolute())));          
          sb.append("></ReplyCharging>");
        }
        
        if(submitReq.isEarliestDeliveryTimeExist())
        {
          Date dd = new Date(submitReq.getEarliestDeliveryTimeRelative());
          sb.append(String.format("<EarliestDeliveryTime>%s+08:00</EarliestDeliveryTime>", sdf.format(dd)));
        }
        
        if(submitReq.isEarliestDeliveryTimeAbsolute())
          sb.append(String.format("<EarliestDeliveryTime>%s+08:00</EarliestDeliveryTime>", sdf.format(submitReq.getEarliestDeliveryTimeAbsolute())));
        
        if(submitReq.isExpiryDateExist())
          sb.append(String.format("<ExpiryDate>%s+08:00</ExpiryDate>", sdf.format(submitReq.getExpiryDateRelative())));
        
        if(submitReq.isExpiryDateAbsolute())
          sb.append(String.format("<ExpiryDate>%s+08:00</ExpiryDate>", sdf.format(submitReq.getExpiryDateAbsolute())));
        
        if(submitReq.isDeliveryReportExist())
          sb.append(String.format("<DeliveryReport>%s</DeliveryReport>",submitReq.getDeliveryReport()));
        
        if(submitReq.isReadReplyExist())
          sb.append(String.format("<ReadReply>%s</ReadReply>",submitReq.getReadReply()));

        if(submitReq.isPriorityExist())
        {
          if(submitReq.getPriority() == (byte)0)
            sb.append("<Priority>Low</Priority>");
          else if(submitReq.getPriority() == (byte)1)
            sb.append("<Priority>Normal</Priority>");
          else if(submitReq.getPriority() == (byte)2)
            sb.append("<Priority>High</Priority>");
        }
        
        if(submitReq.isSubjectExist())
          sb.append(String.format("<Subject>%s</Subject>",submitReq.getSubject()));
        
        if(submitReq.isChargedPartyExist())
        {
          if(submitReq.getChargedParty() == (byte)0)
            sb.append("<ChargedParty>Sender</ChargedParty>");
          else if(submitReq.getChargedParty() == (byte)1)
            sb.append("<ChargedParty>Recipient</ChargedParty>");
          else if(submitReq.getChargedParty() == (byte)2)
            sb.append("<ChargedParty>Both</ChargedParty>");
          else if(submitReq.getChargedParty() == (byte)3)
            sb.append("<ChargedParty>Neither</ChargedParty>");
          else if(submitReq.getChargedParty() == (byte)4)
            sb.append("<ChargedParty>ThirdParty</ChargedParty>");
        }
        
        if(submitReq.isChargedPartyIDExist())
          sb.append(String.format("<ChargedPartyID>%s</ChargedPartyID>",submitReq.getChargedPartyID()));
        
        if(submitReq.isDistributionIndicatorExist())
          sb.append(String.format("<DistributionIndicator>%s</DistributionIndicator>",submitReq.getDistributionIndicator()));
        
        if(submitReq.isContentExist()) {
          sb.append("</SubmitReq>");
          sb.append("</env:Body></env:Envelope>");
          sb.append("\r\n");
          sb.append("----NextPart_0_2817_24856\r\n");
          MMContent parentContent = submitReq.getContent();
          if(parentContent.getContentType() != null) {
            String strSubType = "";
            String strtempID = "<START>";
            strSubType = parentContent.getContentType().getSubType();
            if(strSubType.equalsIgnoreCase("related")) {
              if(parentContent.isMultipart())
              {
                List<MMContent> tempSub = new ArrayList<MMContent>();
                tempSub = parentContent.getSubContents();
                for(int x=0;x<tempSub.size();x++)
                {
                  MMContent tempCon = tempSub.get(x);
                  if(tempCon.getContentType().getSubType().equalsIgnoreCase("smil"))
                  {
                    if(tempCon.isContentIDExist())
                      strtempID = tempCon.getContentID();
                    else
                      strtempID = "<START>";
                      break;
                  }
                }
              }
              sb.append("Content-Type:multipart/related;start=\"".concat(strtempID).concat("\";type=\"application/smil\";boundary=\"SubPart_7452684322002_77645\"\r\n"));
            }
            else
            {
              sb.append("Content-Type:".concat(parentContent.getContentType().getPrimaryType()).concat("/").concat(parentContent.getContentType().getSubType()).concat(";boundary=\"SubPart_7452684322002_77645\"\r\n"));
            }
          }
          else
            sb.append("Content-Type:multipart/mixed;boundary=\"SubPart_7452684322002_77645\"\r\n");
          
          if(parentContent.isContentIDExist())
            sb.append("Content-ID:".concat(parentContent.getContentID()).concat("\r\n"));
          else
            sb.append("Content-ID:<SaturnPics-01020930>\r\n");
          
          sb.append("Content-Transfer-Encoding:8bit\r\n");
          
          if(parentContent.isContentLocationExist())
            sb.append("Content-Location:".concat(parentContent.getContentLocation()).concat("\r\n"));
          sb.append("\r\n");
          
          ByteArrayOutputStream Subbaos = new ByteArrayOutputStream();
          if(parentContent.isMultipart())
          {
            List<MMContent> subContent = new ArrayList<MMContent>();
            subContent = parentContent.getSubContents();
            for(int i=0;i<subContent.size();i++)
            {
              ContentBuffer = new StringBuffer();
              ContentBuffer.append("--SubPart_7452684322002_77645\r\n");
              MMContent content = subContent.get(i);
              if(content.getContentType() != null)
              {
                ContentBuffer.append("Content-Type:".concat(content.getContentType().getPrimaryType()).concat("/").concat(content.getContentType().getSubType()));
                if(content.getContentType().getPrimaryType().equalsIgnoreCase("text")) {
                  if(content.getCharset() == null || content.getCharset().length() == 0)
                    ContentBuffer.append(";charset=".concat(mm7Config.getCharSet()));
                  else
                    ContentBuffer.append(";charset=".concat(content.getCharset()));
                }
                ContentBuffer.append("\r\n");
              } else {
                if (content.isContentIDExist()) {
                  String strContentID = content.getContentID();
                  int index = strContentID.indexOf(".");
                  String type = strContentID.substring(index + 1);
                  type = type.toLowerCase();
                  if (type.equals("txt")) {
                    ContentBuffer.append("Content-Type:text/plain;charset=".concat(mm7Config.getCharSet()).concat("\r\n"));
                  }
                  else if (type.equals("jpg")) {
                    ContentBuffer.append("Content-Type:image/jpeg\r\n");
                  }
                  else if (type.equals("gif")) {
                    ContentBuffer.append("Content-Type:image/gif\r\n");
                  }
                }
              }
              ContentBuffer.append("Content-Transfer-Encoding:8bit\r\n");
              if(content.getContentType().getSubType().equalsIgnoreCase("related"))
              {
                if(content.isContentIDExist())
                  ContentBuffer.append("Content-ID:".concat(content.getContentID()).concat("\r\n"));
                else
                  ContentBuffer.append("Content-ID:<START>\r\n");
              }
              else
              {
                if (content.isContentIDExist())
                  ContentBuffer.append("Content-ID:".concat(content.getContentID()).concat("\r\n"));
              }
              if (content.isContentLocationExist())
            	ContentBuffer.append("Content-Location:".concat(content.getContentLocation()).concat("\r\n"));
              ContentBuffer.append("\r\n");
              
              try{
              Subbaos.write(ContentBuffer.toString().getBytes());
              Subbaos.write(content.getContent());
              Subbaos.write("\r\n\r\n".getBytes());
              }catch(IOException e){
                e.printStackTrace();
              }
            }
            Subbaos.write("--SubPart_7452684322002_77645--\r\n".getBytes());
            Subbaos.write("----NextPart_0_2817_24856--\r\n".getBytes());
            byteOutput.write(sb.toString().getBytes(mm7Config.getCharSet()));
            byteOutput.write(Subbaos.toByteArray());
          }
        }
        else
        {
          sb.append("</SubmitReq>");
          sb.append("</env:Body></env:Envelope>");
          byteOutput.write(sb.toString().getBytes());
        }

      }
      /**
       * 若发送的消息为MM7CancelReq
       * */
      else if(mm7VaspReq instanceof MM7CancelReq){
        MM7CancelReq cancelReq = (MM7CancelReq)mm7VaspReq;
        sb.append("<CancelReq xmlns:mm7=\"http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-6-MM7-1-0\" env:mustUnderstand=\"1\">");
        if(cancelReq.isMM7VersionExist())
          sb.append(String.format("<MM7Version>%s</MM7Version>",cancelReq.getMM7Version()));
        else
          System.out.println("MM7Version 不许为空！");
        
        if(cancelReq.isVASPIDExist() || cancelReq.isVASIDExist())
        {
          sb.append("<SenderIdentification>");
          if (cancelReq.isVASPIDExist())
        	sb.append(String.format("<VASPID>%s</VASPID>",cancelReq.getVASPID()));
          
          if(cancelReq.isVASIDExist())
        	sb.append(String.format("<VASID>%s</VASID>",cancelReq.getVASID()));
          sb.append("</SenderIdentification>");
        }
        
        if(cancelReq.isSenderAddressExist())
          sb.append(String.format("<SenderAddress>%s</SenderAddress>",cancelReq.getSenderAddress()));
        
        if(cancelReq.isMessageIDExist())
          sb.append(String.format("<MessageID>%s</MessageID>",cancelReq.getMessageID()));
        else
          System.out.println("待取消的消息的标识符MessageID不许为空！");
        
        sb.append("</CancelReq>");
        sb.append("</env:Body></env:Envelope>");
        byteOutput.write(sb.toString().getBytes());
      }
      /**
       * 若发送的消息为MM7ReplaceReq
       * */
      else if(mm7VaspReq instanceof MM7ReplaceReq){
        MM7ReplaceReq replaceReq = (MM7ReplaceReq) mm7VaspReq;
        sb.append("<ReplaceReq xmlns:mm7=\"http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-6-MM7-1-0\" env:mustUnderstand=\"1\">");
        if(replaceReq.isMM7VersionExist())
          sb.append(String.format("<MM7Version>%s</MM7Version>",replaceReq.getMM7Version()));

        if(replaceReq.isVASPIDExist() || replaceReq.isVASIDExist())
        {
          sb.append("<SenderIdentification>");
          if (replaceReq.isVASPIDExist())
        	sb.append(String.format("<VASPID>%s</VASPID>",replaceReq.getVASPID()));

          if(replaceReq.isVASIDExist())
            sb.append(String.format("<VASID>%s</VASID>",replaceReq.getVASID()));
          sb.append("</SenderIdentification>");
        }
        
        if(replaceReq.isMessageIDExist())
          sb.append(String.format("<MessageID>%s</MessageID>",replaceReq.getMessageID()));
        else
          System.out.println("被当前消息所替换的消息的标识符MessageID 不许为空！");
        
        if(replaceReq.isServiceCodeExist())
          sb.append(String.format("<ServiceCode>%s</ServiceCode>",replaceReq.getServiceCode()));
        
        if(replaceReq.isTimeStampExist())
          sb.append(String.format("<TimeStamp>%s</TimeStamp>",replaceReq.getTimeStamp()));
        
        if(replaceReq.isEarliestDeliveryTimeExist())
        {
          String earliestTime = sdf.format(new Date(
              replaceReq.getEarliestDeliveryTimeRelative()))+"+08:00";
          sb.append("<EarliestDeliveryTime>" + earliestTime +"</EarliestDeliveryTime>");
        }
        if(replaceReq.isEarliestDeliveryTimeAbsoluteExist())
          sb.append("<EarliestDeliveryTime>"+
                    sdf.format(replaceReq.getEarliestDeliveryTimeAbsolute())+"+08:00"+
                    "</EarliestDeliveryTime>");
        if(replaceReq.isReadReplyExist())
          sb.append("<ReadReply>"+replaceReq.getReadReply()+"</ReadReply>");
        if(replaceReq.isDistributionIndicatorExist())
          sb.append("<DistributionIndicator>"+replaceReq.getDistributionIndicator()
                    +"</DistributionIndicator>");
        if(replaceReq.isContentExist())
        {
          sb.append("</ReplaceReq>");
          sb.append("</env:Body></env:Envelope>" + "\r\n");
          sb.append("----NextPart_0_2817_24856\r\n");
          MMContent parentContent = replaceReq.getContent();
          if(parentContent.getContentType()!=null)
          {
              String strSubType = "";
              String strtempID = "<START>";
              strSubType = parentContent.getContentType().getSubType();
              if (strSubType.equalsIgnoreCase("related")) {
                if (parentContent.isMultipart()) {
                  List<MMContent> tempSub = new ArrayList<MMContent>();
                  tempSub = parentContent.getSubContents();
                  for (int x = 0; x < tempSub.size(); x++) {
                    MMContent tempCon = tempSub.get(x);
                    if (tempCon.getContentType().getSubType().equalsIgnoreCase("smil")) {
                      if (tempCon.isContentIDExist())
                        strtempID = tempCon.getContentID();
                      else
                        strtempID = "<START>";
                      break;
                    }
                  }
                }
                sb.append("Content-Type:" + "multipart/related;" +
                          "start=\"" + strtempID + "\";type=\"application/smil\"" +
                          ";boundary=\"SubPart_7452684322002_77645\"" + "\r\n");
              }
              else {
                sb.append("Content-Type:" +
                          parentContent.getContentType().getPrimaryType()
                          + "/" + parentContent.getContentType().getSubType() +
                          ";boundary=\"SubPart_7452684322002_77645\"" + "\r\n");
              }

          }
          else
          {
            sb.append("Content-Type:" + "multipart/mixed" +
                      ";boundary=\"SubPart_7452684322002_77645\"" + "\r\n");
          }
         sb.append("Content-Transfer-Encoding:8bit" + "\r\n");
         if(parentContent.isContentIDExist())
           sb.append("Content-ID:"+parentContent.getContentID()+"\r\n");
         else
           sb.append("Content-ID:<SaturnPics-01020930>"+"\r\n");
        if(parentContent.isContentLocationExist())
          sb.append("Content-Location:"+parentContent.getContentLocation()+"\r\n");
         sb.append("\r\n");
          ByteArrayOutputStream Subbaos = new ByteArrayOutputStream();
         if (parentContent.isMultipart()) {
            List<MMContent> subContent = new ArrayList<MMContent>();
            subContent = parentContent.getSubContents();
            for (int i = 0; i < subContent.size(); i++) {
              ContentBuffer = new StringBuffer();
              ContentBuffer.append("----SubPart_7452684322002_77645"+"\r\n");
              MMContent content = subContent.get(i);
              if (content.getContentType() != null)
                ContentBuffer.append("Content-Type:" +
                                     content.getContentType().getPrimaryType() +
                                     "/" +
                                     content.getContentType().getSubType() +
                                     "\r\n");
              else {
                if (content.isContentIDExist()) {
                  String strContentID = content.getContentID();
                  int index = strContentID.indexOf(".");
                  String type = strContentID.substring(index + 1);
                  type = type.toLowerCase();
                  if (type.equals("txt")) {
                    ContentBuffer.append("Content-Type:text/plain" + "\r\n");
                  }
                  else if (type.equals("jpg")) {
                    ContentBuffer.append("Content-Type:image/jpeg" + "\r\n");
                  }
                  else if (type.equals("gif")) {
                    ContentBuffer.append("Content-Type:image/gif" + "\r\n");
                  }
                }
              }
              ContentBuffer.append("Content-Transfer-Encoding:8bit" + "\r\n");
              if (content.isContentIDExist())
                ContentBuffer.append("Content-ID:" + content.getContentID() +
                                     "\r\n");
              if(content.isContentLocationExist())
                ContentBuffer.append("Content-Location:"+content.getContentLocation()+"\r\n");
              ContentBuffer.append("\r\n");
              try{
              Subbaos.write(ContentBuffer.toString().getBytes());
              Subbaos.write(content.getContent());
              Subbaos.write("\r\n\r\n".getBytes());
              }catch(IOException e){
                e.printStackTrace();
              }
            }
            Subbaos.write("--SubPart_7452684322002_77645--\r\n".getBytes());
            Subbaos.write("----NextPart_0_2817_24856--".getBytes());
            byteOutput.write(sb.toString().getBytes());
            byteOutput.write(Subbaos.toByteArray());
          }
        }
        else
        {
          sb.append("</ReplaceReq>");
          sb.append("</env:Body></env:Envelope>");
          byteOutput.write(sb.toString().getBytes());
        }
      }
      bEncoder = true;
    }
    catch (Exception e) {
      System.err.println(e);
    }
  }
  /**进行BASE64编码*/
  public static String getBASE64(String value)
  {
    if(value == null)
      return null;
    BASE64Encoder BaseEncode = new BASE64Encoder();
    return(BaseEncode.encode(value.getBytes()));
  }
}
