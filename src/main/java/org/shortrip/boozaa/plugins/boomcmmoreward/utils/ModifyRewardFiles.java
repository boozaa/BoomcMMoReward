package org.shortrip.boozaa.plugins.boomcmmoreward.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.plugin.Plugin;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;


public class ModifyRewardFiles {

	private Plugin plugin;
	private String oldVersion;
	
	public ModifyRewardFiles(Plugin plugin, String oldVersion){		
		this.plugin = plugin;
		this.oldVersion = oldVersion;		
		
		if( 	oldVersion.equalsIgnoreCase("1.1.0") || 
				oldVersion.equalsIgnoreCase("1.1.5") || 
				oldVersion.equalsIgnoreCase("1.1.6-PEX-bPerms-modifications") || 
				oldVersion.equalsIgnoreCase("1.1.7-Beta") ){
			
			// On fait le backup
			if( this.makeBackup(this.oldVersion) ){
				this.ModifFromV11xToV2();		// 1.1 to v2
				this.ModifFromV200xToV201();	// 2.0.0 to v2.0.1
				this.ModifFromV202xToV203();	// 2.0.2 to v2.0.3
			}
						
		}
		
		if( oldVersion.equalsIgnoreCase("2.0.0b") ){
			// On fait le backup
			if( this.makeBackup(this.oldVersion) ){
				this.ModifFromV200xToV201();				
			}
		}
		

		if( oldVersion.equalsIgnoreCase("2.0.2b") ){
			// On fait le backup
			if( this.makeBackup(this.oldVersion) ){
				this.ModifFromV202xToV203();				
			}
		}
		
	}
	
	
	private void ModifFromV202xToV203() {
		Log.info("This new release needs some changes on your config file");
		Log.info("To be on a safe side a backup of your current config file will be in :");
		Log.info("your plugins/BoomcMMoReward/backups/" + oldVersion + "/config.yml");
		
				
		try {
			
			File config = new File( this.plugin.getDataFolder() + File.separator + "config.yml" );
			
			if( config.exists() ){
				
				// Backup old file
				File to = new File( this.plugin.getDataFolder() + File.separator + "backups" + File.separator + oldVersion + File.separator + "config.yml" );			
				backupConfigFile( to );
				
				// Make changes
				FileInputStream fis =  new FileInputStream(config);
				File nouveau = new File(config + ".new");
				FileOutputStream fos = new FileOutputStream(nouveau);				

				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
				BufferedWriter out = new BufferedWriter( new OutputStreamWriter(fos));
				
				Boolean modified = false;
				String retdata = null;
				
				// On parcours config.yml ligne par ligne
				while((retdata = in.readLine())!= null){
					
					if( retdata.contains("informUpdate") || retdata.contains("allowMetricsStats") ){						
						Log.info("Remove unecessary setting in config.yml: " + retdata);
					}else{
						out.write(retdata);
						out.newLine();
						modified = true;
					}
					
				}																	 
	 			in.close();
			 	out.close();
			 	
			 	if( modified ){
			 		// On supprime original
			 		config.delete();
				 	// On renomme copie
				 	new File( config + ".new" ).renameTo(config);
			 	}
			 	
			 	Log.info("Thank you for using BoomcMMoReward, have fun");
			 	
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void ModifFromV11xToV2(){
	
		Log.info("This new release needs some changes on your rewards files");
		Log.info("To be on a safe side a backup of your current rewards files will be in :");
		Log.info("your plugins/BoomcMMoReward/backups/" + oldVersion + "/ folder");
		
		// <ancien terme, nouveau terme>
		Map<String, String> maps = new HashMap<String, String>();
		maps.put("items:", "item:");
		maps.put("messages:", "message:");
		maps.put("perms:", "perm:");
		maps.put("groups:", "group:");
		maps.put("luckyItems:", "luckyItem:");
		maps.put("lotteryItems:", "lotteryItem:");
		maps.put("commands:", "command:");
				
		File powerFolder = new File(this.plugin.getDataFolder() + File.separator + "POWER" + File.separator);			
		modifyFiles(powerFolder, maps);
		File skillsFolder = new File(this.plugin.getDataFolder() + File.separator + "SKILLS" + File.separator);			
		modifyFiles(skillsFolder, maps);
		
	}
	
	private void ModifFromV200xToV201(){
		
		Log.info("This new release corrected some changes on your rewards files on SKILLS folder");
		Log.info("To be on a safe side a backup of your current rewards files will be in :");
		Log.info("your plugins/BoomcMMoReward/backups/" + oldVersion + "/ folder");
		
		// <ancien terme, nouveau terme>
		Map<String, String> maps = new HashMap<String, String>();
		maps.put("items:", "item:");
		maps.put("messages:", "message:");
		maps.put("perms:", "perm:");
		maps.put("groups:", "group:");
		maps.put("luckyItems:", "luckyItem:");
		maps.put("lotteryItems:", "lotteryItem:");
		maps.put("commands:", "command:");
				
		File skillsFolder = new File(this.plugin.getDataFolder() + File.separator + "SKILLS" + File.separator);			
		modifyFiles(skillsFolder, maps);
		
	}
	
	
	private void modifyFiles(File folder, Map<String, String> maps){
		
		//File repertoire = new File(this.plugin.getDataFolder() + File.separator + "POWER");
		if ( folder.isDirectory ( ) ) {
            File[] list = folder.listFiles();
            if (list != null){
                for ( int i = 0; i < list.length; i++) {
                    
                	// Si fichier
					if( list[i].isFile()){	
						
						// Si fichier .yml
						if(getFileExtension(list[i].getName()).equalsIgnoreCase("yml")){
							
							// On va le lire et modifier son contenu
							try{
								 FileInputStream fis =  new FileInputStream(list[i]);
								 File nouveau = new File(list[i] + ".new");
								 FileOutputStream fos = new FileOutputStream(nouveau);

								 BufferedReader in = new BufferedReader(new InputStreamReader(fis));
								 BufferedWriter out = new BufferedWriter( new OutputStreamWriter(fos));
								 
								 Boolean modified = false;
								 StringBuffer contenu = new StringBuffer();
								 String nl = System.getProperty("line.separator");
								 String retdata = null;
								 while((retdata = in.readLine())!= null){
									 for (String mapKey : maps.keySet()) {	 
										 retdata = retdata.replaceAll(mapKey, maps.get(mapKey));	
										 modified = true;
									 }
									 contenu.append("    " + retdata);
									 contenu.append(nl);
								 }
								 
								// On a l'ancien contenu ici on va donc reconstruire le nouveau:
								if( modified ){
									out.write("all:");
									out.newLine();
									out.write("  Your First schema:");
									out.newLine();
									out.write(contenu.toString());								
								}														 
					 			in.close();
							 	out.close();
							 	
							 	if( modified ){
							 		// On supprime original
								 	list[i].delete();
								 	// On renomme copie
								 	new File( list[i] + ".new" ).renameTo(list[i]);
							 	}
							 	
							 	
						 	}catch(Exception e){	
						 		Log.warning("A problem occured on rewards refactoring process");
						 		Log.warning("Please send your errors.txt content on Boo mcMMO Reward dev.bukkit pages");
								Log.severe("modifyFiles", e);
						 	}
							
						}											
						
					}
                	// Appel récursif sur les sous-répertoires si dossier
                	modifyFiles( list[i], maps);
                } 
            } else {
            	System.err.println(folder + " : Erreur de lecture.");
            }
		}
		
	}
	
	
	private String getFileExtension(String NomFichier) {
	    File tmpFichier = new File(NomFichier);
	    tmpFichier.getName();
	    int posPoint = tmpFichier.getName().lastIndexOf('.');
	    if (0 < posPoint && posPoint <= tmpFichier.getName().length() - 2 ) {
	        return tmpFichier.getName().substring(posPoint + 1);
	    }    
	    return "";
	}
	
	
	private Boolean backupConfigFile(File destination) throws IOException{
		
		File originalConfigFile = new File(this.plugin.getDataFolder() + File.separator + "config.yml");
		if( originalConfigFile.exists() ){
			copyFile( originalConfigFile, destination);
		}		
		return true;
		
	}
	
	
	
	private Boolean makeBackup(String folderName){
		
		// Dossier REWARD
		String originalRoot = this.plugin.getDataFolder() + File.separator;
		File originalPOWERRoot = new File(this.plugin.getDataFolder() + File.separator + "POWER");
		File originalSKILLSRoot = new File(this.plugin.getDataFolder() + File.separator + "SKILLS");		
		String backupRoot = this.plugin.getDataFolder() + File.separator + "backups";
		
		if( !new File(originalRoot).exists()){
			Log.warning("Strange thing, you must have plugins/BoomcMMoReward but i can't find it");
			return false;
		}
		
		// Création du dossier backup si n'existe pas
		File backupDir = new File ( backupRoot );
		if( !backupDir.exists() ){
			backupDir.mkdirs();
		}
		
		// Création du dossier dédié à ce backup
		File folderDir = new File ( backupRoot + File.separator + folderName + File.separator );
		File folderDirPower = new File ( backupRoot + File.separator + folderName + File.separator + "POWER" + File.separator );
		File folderDirSkills = new File ( backupRoot + File.separator + folderName + File.separator + "SKILLS" + File.separator );
		if( !folderDir.exists() ){
			folderDir.mkdirs();	
			if( !folderDirPower.exists() ){
				folderDirPower.mkdirs();
			}
			if( !folderDirSkills.exists() ){
				folderDirSkills.mkdirs();
			}				
		}
	
		try {
			// On copie l'existant vers ce nouveau dossier
			this.copyDirectory(originalPOWERRoot, folderDirPower);
			// On copie l'existant vers ce nouveau dossier
			this.copyDirectory(originalSKILLSRoot, folderDirSkills);
		} catch (IOException e) {
			Log.warning("A problem occured on backup reward files process");
			Log.warning("Please send your errors.txt content on Boo mcMMO Reward dev.bukkit pages");
			Log.severe("makeBackup", e);
			return false;
		}
		
		return true;
	}

	
	private void copyDirectory(final File from, final File to) throws IOException {
		 if (! to.exists()) {
			 to.mkdir();
		 }
		 final File[] inDir = from.listFiles();
		 for (int i = 0; i < inDir.length; i++) {
			  final File file = inDir[i];
			  copy(file, new File(to, file.getName()));
		 }
	}
	
	public void copy(final File from, final File to) throws IOException {
		 if (from.isFile()) {
			 copyFile(from, to);
		 } else if (from.isDirectory()){
			 copyDirectory(from, to);
		 } else {
			 throw new FileNotFoundException(from.toString() + " does not exist" );
		 }
	} 

	public static void copyFile(final File from, final File to) throws IOException {
		 final InputStream inStream = new FileInputStream(from);
		 final OutputStream outStream = new FileOutputStream(to);
		 copy(inStream, outStream, (int) Math.min(from.length(), 4*1024));
		 inStream.close();
		 outStream.close();
	}
	
	public static void copy(final InputStream inStream, final OutputStream outStream, final int bufferSize) throws IOException {
		 final byte[] buffer = new byte[bufferSize];
		 int nbRead;
		 while ((nbRead = inStream.read(buffer)) != -1) {
			 outStream.write(buffer, 0, nbRead);
		 }
	}
	
	

	
}
