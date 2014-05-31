package it.mathchallenger.server.controls.version;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
}
