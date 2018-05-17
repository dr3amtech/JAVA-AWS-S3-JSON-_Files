package com.test.Utilities;



import java.util.Date;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * Utility method to send simple HTML email
 * 
 * 
 */
public class EmailUtil {
	
	public static void sendEmail(Session session, String toEmail, String subject,String body) {
		
		try{
			MimeMessage msg = new MimeMessage(session);
			//set message Headers
			msg.addHeader("Content-type", "text/Html; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding","8bit");
			
			msg.setFrom(new InternetAddress("RanibowData@No-Reply.com","Rainbow Object Error"));
			
			msg.setReplyTo(InternetAddress.parse("RanibowData@No-Reply.com",false));
			
			msg.setSubject(subject,"UTF-8");
			msg.setText(body,"UTF-8");
			
			
			msg.setSentDate(new Date());
			
			
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail,false));
			
			System.out.println("Message is ready");
			Transport.send(msg);
			System.out.println("Message sent successfully");
			
	
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		
	}
	

}
