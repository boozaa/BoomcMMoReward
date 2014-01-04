package org.shortrip.boozaa.plugins.boomcmmoreward;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.shortrip.boozaa.plugins.boomcmmoreward.commands.BooCommands;
import org.shortrip.boozaa.plugins.boomcmmoreward.commands.ConsoleCommands;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.BooSystemException;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.CommandException;
import org.shortrip.boozaa.plugins.boomcmmoreward.listeners.McMMOListener;
import org.shortrip.boozaa.plugins.boomcmmoreward.listeners.MyPlayerListener;
import org.shortrip.boozaa.plugins.boomcmmoreward.persistence.Cache;
import org.shortrip.boozaa.plugins.boomcmmoreward.persistence.Database;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Configuration;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.ModifyRewardFiles;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.StoreErrors;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;




public class BoomcMMoReward extends JavaPlugin {
    
	
	private static Plugin _instance;
	public static Plugin getInstance(){
		return _instance;
	}
		
	
	// Base de données
	/*
	private static EbeanServer database;
	public static EbeanServer getDb(){
		return database;
	}
	*/
	private static Database database;
	public static Database getDB(){
		return database;
	}
	
	private static Cache pendingCache;
	public static Cache getPendingCache(){
		return pendingCache;
	}
	
	// Fichier yml de config	
	private static Configuration config;
	public static Configuration getYmlConf(){
		return config;
	}
	
	// Vault economy
	private static Economy econ = null;
	public static Economy getEcon(){
		return econ;
	}
	
	// Vault perms
	private static Permission perms = null;
	public static Permission getPerms(){
		return perms;
	}

	
	// Logger
	private static Logger logger = Logger.getLogger("Minecraft");
	public static void log(Level level, String message) {
		logger.log(level, Const.PLUGIN_NAME + message);
	}
	// Debug si activé
	public static void debug(String message) {
		
		if( config.getBoolean("config.debugMode") ) {			
			logger.log(Level.INFO, Const.PLUGIN_NAME + "- DEBUG - " + message);
		}
		
	}

	private static StoreErrors storeErrors;
	public static StoreErrors getStoreErrors(){return storeErrors;}
	
	
	@Override
	public void onDisable() {
        // Cleanup statics
		database = null;
		config = null;
		econ = null;
		perms = null;
		logger = null;
    }

	
	
    @Override
	public void onEnable() {
    	
    	// storeErrors
        storeErrors = new StoreErrors(this);
    	
    	// Economy
    	this.hookEconomy();
    	
    	// Permissions
    	this.hookPermissions(); 
    	
    	// On charge la config initiale
        try {
			this.loadMainConfig();
		} catch (BooSystemException e) { }
        
    	// Les listeners
    	//getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new McMMOListener(this), this);  
        getServer().getPluginManager().registerEvents(new MyPlayerListener(this), this);       
        
        // Chargement ou creation database
        log(Level.INFO, "Connecting to database");
        
        //this.setupDatabase();        
        //database = this.getDatabase();
        
        // Choix selon fichier config
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
        
        // Pending Cache
        pendingCache = new Cache();
        		
		// Singleton
		_instance = this;
        
    }

    
    private void hookEconomy(){
    	
    	if (getServer().getPluginManager().getPlugin("Vault") == null) {
    		log(Level.WARNING, "Vault seems not here, you can't use money rewards");
    		return;
        }
    	
    	RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
        	econ = economyProvider.getProvider();
        	log(Level.INFO, "Economy providing by Vault");
        }else{
        	log(Level.WARNING, "Can't hooked Economy with Vault");
        }   
    	
    }
    
    private void hookPermissions(){
    	
    	if (getServer().getPluginManager().getPlugin("Vault") == null) {
    		log(Level.WARNING, "Vault seems not here, you can't use permission rewards");
    		return;
        }
    	RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
        	perms = permissionProvider.getProvider();
        	log(Level.INFO, "Permissions providing by Vault");
        }else{
        	log(Level.WARNING, "Can't hooked Permissions with Vault");        	
        }   
        
    }
    
    
    
    
    @Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
    	// Pas d'arguments dans la commande on sort
    	if( args.length == 0 ){return true;}    	
    	try {   		
    		if(command.getName().equalsIgnoreCase("boomcmmoreward")){	
	    		
		    		if( !( sender instanceof Player ) ) {    		
			    		// Sends form console		    		
							new ConsoleCommands(args);					
			    	}
			    	// Sends from game
			    	new BooCommands(sender, command, commandLabel, args);		    	
	    	}
    	} catch (CommandException e) {}    	
    	return true;
    }
    

    
    private void loadMainConfig() throws BooSystemException {
		
    	// Creation ou chargement config principale
    	makeConfig();
    	// Création des dossiers si inexistants
    	makeFolders();
    					
	}

    
    
    private void makeConfig(){
    	
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
		
		// Allow Update notifier
		if( config.get("config.informUpdate") == null ) {
			config.set("config.informUpdate", true);
			updated = true;
			messages.add("config.informUpdate - Enable or not new update release on console");
		}
		
		// allowMetricsStats
		if( config.get("config.allowMetricsStats") == null ) {
			config.set("config.allowMetricsStats", true);
			updated = true;
			messages.add("config.allowMetricsStats - Allow/Deny sending infos to http://mcstats.org for usage stats purpose only");
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
			log(Level.INFO, "v" + this.getDescription().getVersion() + " plugins/BoomcMMoReward/config.yml");				
			for(String str : messages){
				log(Level.INFO, str);
			}
		
		}
		
		
		if(mustBackup){
			new ModifyRewardFiles(this,oldVersion );			
		}
    	
			
    	
    }

    
    private void makeFolders(){
    	
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
		}
    	
    }


    
}

