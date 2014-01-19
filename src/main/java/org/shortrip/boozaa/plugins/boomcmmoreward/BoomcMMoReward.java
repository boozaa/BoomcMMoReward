package org.shortrip.boozaa.plugins.boomcmmoreward;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.shortrip.boozaa.plugins.boomcmmoreward.CommandsExecutor.CommandNullException;
import org.shortrip.boozaa.plugins.boomcmmoreward.listeners.McMMOListener;
import org.shortrip.boozaa.plugins.boomcmmoreward.listeners.MyPlayerListener;
import org.shortrip.boozaa.plugins.boomcmmoreward.persistence.Cache;
import org.shortrip.boozaa.plugins.boomcmmoreward.persistence.Database;
import org.shortrip.boozaa.plugins.boomcmmoreward.persistence.Database.DatabaseException;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Configuration;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.ModifyRewardFiles;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;


public class BoomcMMoReward extends JavaPlugin {
    
	private CommandsExecutor commandExecutor;
	
	private static Plugin _instance;
	public static Plugin getInstance(){ return _instance; }
	
	private static Database database;
	public static Database getDB(){ return database; }
	
	private static Cache pendingCache;
	public static Cache getPendingCache(){ return pendingCache; }
	
	// Fichier yml de config	
	private static Configuration config;
	public static Configuration getYmlConf(){ return config; }
	
	// Vault economy
	private static Economy econ = null;
	public static Economy getEcon(){ return econ; }
	
	// Vault perms
	private static Permission perms = null;
	public static Permission getPerms(){ return perms; }

	
	private static boolean vaultEnabled = false;
	public static boolean isVaultEnabled(){ return vaultEnabled; }
	
	
    @Override
	public void onEnable() {
    	        		
        try {
        	        	
        	// Economy
        	this.hookEconomy();
        	
        	// Permissions
        	this.hookPermissions(); 
        	
        	// Config from config.yml
            this.loadMainConfig();
            
        	// All listeners
            getServer().getPluginManager().registerEvents(new McMMOListener(this), this);  
            getServer().getPluginManager().registerEvents(new MyPlayerListener(this), this);       
            
            // Make database connection with settings in config.yml
            setupDatabase();
            
            // Pending Cache
            pendingCache = new Cache();
            
            // Commands executor
			commandExecutor = new CommandsExecutor(this);
			
			// Singleton for this instance
			_instance = this;
			
			
		} catch (CommandNullException e) {
			// FATAL -> unload plugin
			Log.warning("A fatal problem occured on CommandsExecutor");
	 		Log.warning("Please send your errors.txt content on Boo mcMMO Reward dev.bukkit pages");			
			Log.severe("onEnable() fatal error: CommandNullException", e);
		} catch (DatabaseException e) {
			// FATAL -> unload plugin
			Log.warning("A fatal problem occured on Database");
	 		Log.warning("Please send your errors.txt content on Boo mcMMO Reward dev.bukkit pages");
			Log.severe("onEnable() fatal error: DatabaseException", e);
		} catch (OnConfigCreationException e) {
			// FATAL -> unload plugin
			Log.warning("A fatal problem occured on config or folder handling");
	 		Log.warning("Please send your errors.txt content on Boo mcMMO Reward dev.bukkit pages");
			Log.severe("onEnable() fatal error: OnConfigCreationException", e);
		} catch (HookException e) {
			Log.warning("A problem occured when trying to hook on Vault");
			Log.warning("The plugin is not disabled but problems with economy can occured");
		}
        
        
		
        
    }

	
	@Override
	public void onDisable() {
        // Cleanup statics
		database = null;
		config = null;
		econ = null;
		perms = null;
    }

    
    /**
     * Hook econmy aspects with Vault
     * @throws HookException
     */
    private void hookEconomy() throws HookException{
    	try{
	    	if (getServer().getPluginManager().getPlugin("Vault") == null) {
	    		Log.warning("Vault seems not here, you can't use money rewards");
	    		return;
	        }
	    	
	    	RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
	        if (economyProvider != null) {
	        	econ = economyProvider.getProvider();
	        	Log.info("Economy providing by Vault");
	        	vaultEnabled = true;
	        }else{
	        	Log.warning("Can't hooked Economy with Vault");
	        }   
    	}catch( Exception ex ){
    		throw new HookException("Exception on hookEconomy()", ex);
    	}
    	
    }
    
    /**
     * Hook permissions aspects with Vault
     * @throws HookException
     */
    private void hookPermissions() throws HookException{
    	try{
	    	if (getServer().getPluginManager().getPlugin("Vault") == null) {
	    		Log.warning("Vault seems not here, you can't use permission rewards");
	    		return;
	        }
	    	RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
	        if (permissionProvider != null) {
	        	perms = permissionProvider.getProvider();
	        	Log.info("Permissions providing by Vault");
	        	vaultEnabled = true;
	        }else{
	        	Log.warning("Can't hooked Permissions with Vault");        	
	        }     
    	}catch( Exception ex ){
    		throw new HookException("Exception on hookEconomy()", ex);
    	}
        
    }
    
    
    /**
     * Take database settings from config.yml and initialize it
     * @throws DatabaseException
     */
    private void setupDatabase() throws DatabaseException{
    	// Database type depending on choice in config.yml
        if( config.getString("database.type").equalsIgnoreCase("sqlite")  ) {            	
        	database = new Database(new File(this.getDataFolder() + File.separator + "BoomcMMoReward.db")); 	
        }else if( config.getString("database.type").equalsIgnoreCase("mysql")  ){            	
        	// Database(String host, String database, String username, String password)
        	database = new Database(config.getString("database.mysql.server"),
        							config.getString("database.mysql.base"),
					        		config.getString("database.mysql.user"),
					        		config.getString("database.mysql.pass") );            	
        }
        
        database.initialise();                    
        Log.info("Database ready");
    }
    
    
    @Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
    	Boolean result = true;
		try {
			result = commandExecutor.handleCommand(sender, command, commandLabel, args);
		} catch (org.shortrip.boozaa.plugins.boomcmmoreward.CommandsExecutor.CommandHandlerException e) {
			// SEVERE -> disable plugin
			Log.severe("onCommand() fatal error: CommandHandlerException", e.get_Throwable());
		}
		return result;	
    }
    

    
    /**
     * Create config.yml and folder structure
     * @throws OnConfigCreationException
     */
    private void loadMainConfig() throws OnConfigCreationException {
		// Creation ou chargement config principale
    	makeConfig();
    	// Création des dossiers si inexistants
    	makeFolders();
	}

    
    
    /**
     * Create and/or load config.yml dynamically
     * @throws OnConfigCreationException
     */
    private void makeConfig() throws OnConfigCreationException{
    	try{
	    	Boolean mustBackup = false;
	    	String oldVersion = "";
	    	String configPath = getDataFolder() + File.separator + "config.yml";
	    	config = new Configuration(configPath);
			
	    	List<String> messages = new ArrayList<String>();
	    	Boolean updated = false;
	    	
	    	
	    	if( !config.exists() ){    		
	    		config.save();
	    	}    		   				
	    		
			config.load();
			
	    	// debugMode
			if( config.get("config.debugMode") == null ) {
				config.set("config.debugMode", true);
				updated = true;
				messages.add("config.debugMode - Set debug mode ON or OFF");
			}
						
			// diceFaces
			if( config.get("config.diceFaces") == null  ) {
				config.set("config.diceFaces", 10);
				updated = true;
				messages.add("config.diceFaces - Number of faces = number max for lottery purpose");
			}
			
			// Log to database
			if( config.get("config.logInDatabase") == null  ) {
				config.set("config.logInDatabase", true);
				updated = true;
				messages.add("config.logInDatabase - Enable or not storing rewards per player in database");
			}
			
			
			// Database choix Sqlite ou MySQL
			if( config.get("database.type") == null  ) {
				config.set("database.type", "sqlite");			
				config.set("database.mysql.server", "localhost");
				config.set("database.mysql.base", "minecraft");
				config.set("database.mysql.user", "boozaa");
				config.set("database.mysql.pass", "password");			
				updated = true;
				messages.add("database.type - To choose between SQLite or MySQL database for storage");
			}
					
			//version
			if( config.get("config.version") == null ) {    			
				// Il n'existe pas on le fixe
				config.set("config.version", getDescription().getVersion());
				updated = true;
				messages.add("config.version - the version of the config");
			}else{
				// On vérifie si a jour
				oldVersion = config.getString("config.version");
				// On vérifie si à jour
				if( !getDescription().getVersion().equalsIgnoreCase(oldVersion) ){
					config.set("config.version", getDescription().getVersion());   
					updated = true;
					mustBackup = true;
					messages.add("config.version - updated");
				}					
			}
					
			
			
			if( updated ) {	
				config.save();
				config.load();
				Log.info("v" + this.getDescription().getVersion() + " plugins/BoomcMMoReward/config.yml");				
				for(String str : messages){
					Log.info(str);
				}
			
			}
			
			
			if(mustBackup){
				new ModifyRewardFiles(this,oldVersion );			
			}

		}catch(Exception ex){
			throw new OnConfigCreationException("Exception on makeConfig()", ex);
		}
			
    	
    }

    
    /**
     * Create folder's structure if not exists
     * @throws OnConfigCreationException
     */
    private void makeFolders() throws OnConfigCreationException{
    	try{
	    	// Dossier REWARD
			String power = getDataFolder() + File.separator + "POWER" + File.separator;
			String powerOne = power + File.separator + "ONE" + File.separator;
			String powerEvery = power + File.separator + "EVERY" + File.separator;
			String skills = getDataFolder() + File.separator + "SKILLS" + File.separator;
			String abilities = getDataFolder() + File.separator + "ABILITIES" + File.separator;
			
			// Creation du dossier POWER/ONE
			File dir = new File ( powerOne );
			if( !dir.exists() ){
				dir.mkdirs();
			}
			// Creation du dossier POWER/EVERY
			dir = new File ( powerEvery );
			if( !dir.exists() ){
				dir.mkdirs();
			}
			
			// Creation des dossiers skills
			for( SkillType s : SkillType.values()){							
				String skillsOne = skills + s.name() + File.separator + "ONE" + File.separator;
				String skillsEvery = skills + s.name() + File.separator + "EVERY" + File.separator;				
				// Creation du dossier SKILLS/SKILL/ONE
				dir = new File ( skillsOne);
				if( !dir.exists() ){
					dir.mkdirs();
				}
				// Creation du dossier SKILLS/SKILL/EVERY
				dir = new File ( skillsEvery);
				if( !dir.exists() ){
					dir.mkdirs();
				}
			}
			
			// Creation des dossiers abilities
			for( AbilityType s : AbilityType.values()){							
				String ab = abilities + s.name() + File.separator;					
				// Creation du dossier ABILITIES/ABILITY/
				dir = new File ( ab);
				if( !dir.exists() ){
					dir.mkdirs();
				}
				String powerFolder = ab + File.separator + "POWER" + File.separator;
				dir = new File ( powerFolder);
				if( !dir.exists() ){
					dir.mkdirs();
				}
				String powerONEFolder = powerFolder + File.separator + "ONE" + File.separator ;
				dir = new File ( powerONEFolder);
				if( !dir.exists() ){
					dir.mkdirs();
				}
				String powerEVERYFolder = powerFolder + File.separator + "EVERY" + File.separator ;
				dir = new File ( powerEVERYFolder);
				if( !dir.exists() ){
					dir.mkdirs();
				}
				
				
				
				String skillsFolder = ab + File.separator + "SKILLS" + File.separator;
				dir = new File ( skillsFolder);
				if( !dir.exists() ){
					dir.mkdirs();
				}
				String skillsONEFolder = skillsFolder + File.separator + "ONE" + File.separator ;
				dir = new File ( skillsONEFolder);
				if( !dir.exists() ){
					dir.mkdirs();
				}
				String skillsEVERYFolder = skillsFolder + File.separator + "EVERY" + File.separator ;
				dir = new File ( skillsEVERYFolder);
				if( !dir.exists() ){
					dir.mkdirs();
				}
				
			}

		}catch(Exception ex){
			throw new OnConfigCreationException("Exception on makeFolders()", ex);
		}
    }


    
	
	public class OnConfigCreationException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public OnConfigCreationException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}

	public class HookException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public HookException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}
	
    
}

