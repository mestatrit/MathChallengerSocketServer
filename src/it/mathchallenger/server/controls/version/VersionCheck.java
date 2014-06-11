package it.mathchallenger.server.controls.version;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class VersionCheck {
	private static VersionCheck instance;
	private ArrayList<Integer> versioni_valide;
	
	public static VersionCheck getInstance(){
		if(instance==null)
			instance=new VersionCheck();
		return instance;
	}
	private VersionCheck(){
		versioni_valide=new ArrayList<Integer>(5);
		loadFile();
	}
	public void loadFile(){
		versioni_valide.clear();
		try {
			Scanner file=new Scanner(new FileReader("versioni_available.ver"));
			while(file.hasNextInt()){
				int v=file.nextInt();
				versioni_valide.add(v);
			}
			file.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public boolean isVersionOK(int v){
		for(int i=0;i<versioni_valide.size();i++){
			if(versioni_valide.get(i)==v)
				return true;
		}
		return false;
	}
	public ArrayList<Integer> getValidVersions(){
		return versioni_valide;
	}
	public boolean aggiungiVersione(int v){
		if(!isVersionOK(v)){
			versioni_valide.add(v);
			salvaSuFile();
			return true;
		}
		else
			return false;
	}
	public boolean rimuoviVersione(int v){
		if(isVersionOK(v)){
			removeVersion(v);
			salvaSuFile();
			return true;
		}
		else
			return false;
	}
	private void salvaSuFile(){
		FileWriter f_w=null;
		try {
			f_w=new FileWriter("versioni_available.ver");
			for(int i=0;i<versioni_valide.size();i++)
				f_w.append(+versioni_valide.get(i)+" ");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if(f_w!=null)
				try {
					f_w.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	private void removeVersion(int v){
		for(int i=0;i<versioni_valide.size();i++){
			if(versioni_valide.get(i)==v){
				versioni_valide.remove(i);
				return;
			}
		}
	}
}
