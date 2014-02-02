package org.shortrip.boozaa.plugins.boomcmmoreward.persistence;

import java.sql.SQLException;
import java.util.List;
import lombok.Getter;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.tables.ORMHistory;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Configuration;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;



public class ORMDatabase {

	private final String TYPE 			= "config.database";
	private final String MYSQL_HOST 	= "config.mysql.host";
	private final String MYSQL_PORT 	= "config.mysql.port";
	private final String MYSQL_DATABASE = "config.mysql.database";
	private final String MYSQL_USER 	= "config.mysql.user";
	private final String MYSQL_PASS 	= "config.mysql.pass";

	@SuppressWarnings("unused")
	private BoomcMMoReward plugin;
	private String databaseUrl;
	@Getter private ConnectionSource _connectionSource;
	@Getter private Dao<ORMHistory, Integer> _history;
	
	
	public ORMDatabase(BoomcMMoReward plugin) throws SQLException{
		
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
		System.setProperty(LocalLog.LOCAL_LOG_FILE_PROPERTY, "plugins/BooSharePlayers/ormlite_log.out");		
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
				_connectionSource = new JdbcConnectionSource(databaseUrl);				
			}else if( dbType.equalsIgnoreCase("mysql") ){
				String host = config.getString( MYSQL_HOST );
				int port = config.getInt( MYSQL_PORT );
				String database = config.getString( MYSQL_DATABASE );
				String user = config.getString( MYSQL_USER );
				String pass = config.getString( MYSQL_PASS );
				databaseUrl = "jdbc:mysql://"+ host + ":" + port + "/" + database;
				_connectionSource = new JdbcConnectionSource(databaseUrl,user,pass);				
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
		_history.update(history);		
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
