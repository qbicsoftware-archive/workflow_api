package de.uni_tuebingen.qbic.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandLine {
	
	private List<String> fullCmd;
	
	public CommandLine(){
		this.fullCmd = new ArrayList<String>();
		this.init();
	}
	
	protected void init(){
		this.fullCmd.clear();

		if(System.getProperty("os.name").indexOf("Win") >= 0)
		{
			this.fullCmd.add("cmd");
			this.fullCmd.add("/c");
		}
		else
		{
			this.fullCmd.add("/bin/sh");
			this.fullCmd.add("-c");
		}
	}
	
	public void addCmd(String cmd){
		this.fullCmd.add(cmd);
	}
	public Process execute(String cmd){
		this.addCmd(cmd);
		return this.execute();
	}
	public Process execute(){
		String[] strarr = new String[this.fullCmd.size()];
		this.fullCmd.toArray(strarr);

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(strarr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
	
	public void clear(){
		this.init();
	}
	
	static public Process executeCmd(String cmd){
		Process p = null;
		try {
			List<String> fullcmd = new ArrayList<String>();
			if(System.getProperty("os.name").indexOf("Win") >= 0)
			{
				fullcmd.add("cmd");
				fullcmd.add("/c");
			}
			else
			{
				fullcmd.add("/bin/sh");
				fullcmd.add("-c");
			}
			fullcmd.add(cmd);
			String[] strarr = new String[fullcmd.size()];
			fullcmd.toArray(strarr);
			p = Runtime.getRuntime().exec(strarr);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
	
}
