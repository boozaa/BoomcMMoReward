package org.shortrip.boozaa.plugins.boomcmmoreward.persistence;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import lombok.Getter;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.tables.ORMHistory;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Configuration;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
//import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;



public class ORMDatabase {

	private final String TYPE 			= "database.type";
	private final String MYSQL_HOST 	= "database.mysql.server";
	private final String MYSQL_DATABASE = "database.mysql.base";
	private final String MYSQL_USER 	= "database.mysql.user";
	private final String MYSQL_PASS 	= "database.mysql.pass";

	@SuppressWarnings("unused")
	private BoomcMMoReward plugin;
	private String databaseUrl;
	@Getter private ConnectionSource _connectionSource;
	@Getter private Dao<ORMHistory, Integer> _history;
	
	
	public ORMDatabase(BoomcMMoReward plugin) throws SQLException{
		
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
		System.setProperty(LocalLog.LOCAL_LOG_FILE_PROPERTY, plugin.getDataFolder() + File.separator + "ormlite_log.out");		
		this.plugin = plugin;		
		
		this.updateConnectionInfos();
		
		this.initDatabase();
		
	}
	
	
	public void updateConnectionInfos() throws SQLException{
		
		Log.debug("Enter in updateConnectionInfos()");
		Configuration config = BoomcMMoReward.getYmlConf();
		
		
		if( config.contains( TYPE ) ){
						
			String dbType = config.getString( TYPE );	
			
			if( dbType.equalsIgnoreCase("sqlite") ){
				databaseUrl = "jdbc:sqlite:plugins/BoomcMMoReward/BoomcMMoReward.db";
				_connectionSource = new JdbcPooledConnectionSource(databaseUrl);				
			}else if( dbType.equalsIgnoreCase("mysql") ){
				String host = config.getString( MYSQL_HOST );
				//int port = config.getInt( MYSQL_PORT );
				String database = config.getString( MYSQL_DATABASE );
				String user = config.getString( MYSQL_USER );
				String pass = config.getString( MYSQL_PASS );
				//databaseUrl = "jdbc:mysql://"+ host + ":" + port + "/" + database;
				databaseUrl = "jdbc:mysql://"+ host + "/" + database;
				_connectionSource = new JdbcPooledConnectionSource(databaseUrl,user,pass);				
			}
			
		}		
	}
	
	
	private void initDatabase() throws SQLException{    	

		Log.debug("Enter in initDatabase()");
		// Le DAO pour PlayerInventoryDAO
		_history = DaoManager.createDao(_connectionSource, ORMHistory.class);			
		// Create the table		
        TableUtils.createTableIfNotExists(_connectionSource, ORMHistory.class);
			
    }
	
	
	public void saveORMHistory( ORMHistory history ) throws Exception{
		Log.debug("Enter in saveORMHistory()");	
		// Update in database
		_history.create(history);		
	}
	

	public List<ORMHistory> getORMHistory( String playerName ) throws Exception{
		QueryBuilder<ORMHistory, Integer> statementBuilder = _history.queryBuilder();
		statementBuilder.where().eq(ORMHistory.PLAYERNAME, playerName);
		return _history.query(statementBuilder.prepare() );
	}
		
	/*
	private PlayerProfile getPlayerDAO( Player player) throws SQLException{
		QueryBuilder<PlayerProfile, Integer> statementBuilder = this._playerProfiles.queryBuilder();
        // shouldn't find anything: name LIKE 'hello" does not match our account
        statementBuilder.where().eq(PlayerProfile.PLAYERNAME, player.getName());
		List<PlayerProfile> results = this._playerProfiles.query(statementBuilder.prepare() );
		if( !results.isEmpty() )
			return this._playerProfiles.query(statementBuilder.prepare()).get(0);
		
		return null;
	}
	*/

}
