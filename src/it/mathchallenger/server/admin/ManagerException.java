package it.mathchallenger.server.admin;

import java.io.FileWriter;
import java.util.GregorianCalendar;

public class ManagerException {
	public static synchronized void registraEccezione(Exception e, String filename){
		try{
			StackTraceElement[] stack=e.getStackTrace();
			FileWriter file=new FileWriter(filename, true);
			GregorianCalendar date=new GregorianCalendar();
			file.append(date.getTime().toString()+"\n");
			file.append(e.getMessage()+"\n");
			for(int i=stack.length-1;i>=0;i--){
				file.append(""+stack[i].toString()+"\n");
			}
			file.append("\n\n");
			file.close();
		}
		catch(Exception e1){}
	}
	public static synchronized void registraEccezione(String m, String filename){
		try{
			FileWriter file=new FileWriter(filename, true);
			GregorianCalendar date=new GregorianCalendar();
			file.append(date.getTime().toString()+"\n");
			file.append(m+"\n");
			file.append("\n\n");
			file.close();
		}
		catch(Exception e1){}
	}
}
