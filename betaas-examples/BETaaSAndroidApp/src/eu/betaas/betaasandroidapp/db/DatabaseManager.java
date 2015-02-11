/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Sergio García Villalonga. Atos Research and Innovation, Atos SPAIN SA
@email sergio.garciavillalonga@atos.net
**/

package eu.betaas.betaasandroidapp.db;

import java.util.ArrayList;

import eu.betaas.betaasandroidapp.listeners.DBGatewayListener;
import eu.betaas.betaasandroidapp.pojo.Gateway;
import eu.betaas.betaasandroidapp.pojo.Measurement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

	private ArrayList<DBGatewayListener> gatewayListeners;
	
	private static final int   DB_VERSION = 1;
	private static final String DB_NAME = "betaas";
	private static final String GATEWAY_TABLE_NAME = "gateway";
	private static final String SERVICE_TABLE_NAME = "service";
	private static final String TOKEN_TABLE_NAME = "token";
	private static final String LAST_MEASUREMENT_TABLE_NAME = "measurement";
	
	private static final String GATEWAY_TABLE_CREATE =
            "CREATE TABLE \"" + GATEWAY_TABLE_NAME + "\" ("
             +     "id     TEXT, "
             +     "name   TEXT, "
             +     "uri    TEXT, "
             +     "port   INTEGER, "
             +     "app_id TEXT"
             + ");";
	
	private static final String SERVICE_TABLE_CREATE =
            "CREATE TABLE \"" + SERVICE_TABLE_NAME + "\" ("
             +     "id     TEXT, "
             +     "gw_id  TEXT "
             + ");";
	
	private static final String TOKEN_TABLE_CREATE =
            "CREATE TABLE \"" + TOKEN_TABLE_NAME + "\" ("
             +     "content  TEXT, "
             +     "gw_id    TEXT "
             + ");";
	
	private static final String LAST_MEASUREMENT_TABLE_CREATE =
            "CREATE TABLE \"" + LAST_MEASUREMENT_TABLE_NAME + "\" ("
             +     "time       INTEGER,"
             +     "presence   TEXT,"
             +     "service_id TEXT,"
             +     "gw_id      TEXT"
             + ");";
	
	private static final String GET_GATEWAYS =
			"SELECT * FROM \"" + GATEWAY_TABLE_NAME + "\"";
	
	private static final String GET_GATEWAY =
			"SELECT * FROM \"" + GATEWAY_TABLE_NAME + "\" WHERE id = ?";
	
	private static final String GET_GATEWAY_BY_URI =
			"SELECT * FROM \"" + GATEWAY_TABLE_NAME + "\" WHERE uri = ? AND port = ?";
	
	private static final String GET_SERVICES =
			"SELECT * FROM \"" + SERVICE_TABLE_NAME + "\" WHERE gw_id = ?";
	
	private static final String GET_TOKENS =
			"SELECT * FROM \"" + TOKEN_TABLE_NAME + "\" WHERE gw_id = ?";
	
	private static final String GET_LAST_MEASUREMENT =
			"SELECT time, presence FROM \"" + LAST_MEASUREMENT_TABLE_NAME + "\" WHERE gw_id = ? AND service_id = ? ORDER BY time DESC LIMIT 1";
	
	private static DatabaseManager instance;
	
	private DatabaseManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		gatewayListeners = new ArrayList<DBGatewayListener>();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(GATEWAY_TABLE_CREATE);
		db.execSQL(SERVICE_TABLE_CREATE);
		db.execSQL(TOKEN_TABLE_CREATE);
		db.execSQL(LAST_MEASUREMENT_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	public static DatabaseManager getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseManager(context);
		}
	
		return instance;
	}
	
	public ArrayList<Gateway> getGateways() {
		ArrayList<Gateway> gateways = new ArrayList<Gateway>();
		String gwId;
		ArrayList<String> services, tokens;
		SQLiteDatabase db;
		Cursor c;
		
		int idCol, nameCol, uriCol, portCol, appCol;
		
		db = instance.getReadableDatabase();
		c  = db.rawQuery(GET_GATEWAYS, null);
		
		idCol   = c.getColumnIndex("id");
		nameCol = c.getColumnIndex("name");
		uriCol  = c.getColumnIndex("uri");
		portCol = c.getColumnIndex("port");
		appCol  = c.getColumnIndex("app_id");
		
		if (c.moveToFirst()) {
			do {
				gwId = c.getString(idCol);
				
				services = getServices(gwId);
				tokens   = getTokens(gwId);
				
				gateways.add(new Gateway(gwId,
				                          c.getString(nameCol),
				                          c.getString(uriCol),
				                          c.getInt(portCol),
				                          c.getString(appCol),
				                          services,
				                          tokens));
			} while (c.moveToNext());
		}
		
		c.close();
		db.close();
		
		return gateways;
	}
	
	public Gateway getGateway(String id) {
		Gateway gateway = null;
		SQLiteDatabase db;
		Cursor c;
		String gwId;
		ArrayList<String> services, tokens;
		
		int idCol, nameCol, uriCol, portCol, appCol;
		
		String[] selectionArgs = { id };
		
		db = instance.getReadableDatabase();
		c  = db.rawQuery(GET_GATEWAY, selectionArgs);
		
		idCol   = c.getColumnIndex("id");
		nameCol = c.getColumnIndex("name");
		uriCol  = c.getColumnIndex("uri");
		portCol = c.getColumnIndex("port");
		appCol  = c.getColumnIndex("app_id");
		
		if (c.moveToFirst()) {
			gwId = c.getString(idCol);
			
			services = getServices(gwId);
			tokens   = getTokens(gwId);
			
			gateway = new Gateway(gwId,
                    c.getString(nameCol),
                    c.getString(uriCol),
                    c.getInt(portCol),
                    c.getString(appCol),
                    services,
                    tokens);
		}
		
		return gateway;
	}
	
	public ArrayList<Gateway> getGatewayByURI(String host, int port) {
		ArrayList<Gateway> gateways = new ArrayList<Gateway>();
		SQLiteDatabase db;
		Cursor c;
		String gwId;
		ArrayList<String> services, tokens;
		
		int idCol, nameCol, uriCol, portCol, appCol;
		
		String[] selectionArgs = { host, String.valueOf(port) };
		
		db = instance.getReadableDatabase();
		c  = db.rawQuery(GET_GATEWAY_BY_URI, selectionArgs);
		
		idCol   = c.getColumnIndex("id");
		nameCol = c.getColumnIndex("name");
		uriCol  = c.getColumnIndex("uri");
		portCol = c.getColumnIndex("port");
		appCol  = c.getColumnIndex("app_id");
		
		if (c.moveToFirst()) {
			do {
				gwId = c.getString(idCol);
				
				services = getServices(gwId);
				tokens   = getTokens(gwId);
				
				gateways.add(new Gateway(gwId,
				                          c.getString(nameCol),
				                          c.getString(uriCol),
				                          c.getInt(portCol),
				                          c.getString(appCol),
				                          services,
				                          tokens));
			} while (c.moveToNext());
		}
		
		c.close();
		db.close();
		
		return gateways;
	}
	
	public void storeGateway(Gateway gateway) {
		SQLiteDatabase db;
		ContentValues values = new ContentValues(1);
		
		db = instance.getWritableDatabase();
		
		values.put("id", gateway.getId());
		values.put("name", gateway.getName());
		values.put("uri", gateway.getUri());
		values.put("port", gateway.getPort());
		values.put("app_id", gateway.getAppId());
		
		db.insert(GATEWAY_TABLE_NAME, null, values);		
		db.close();
		
		for (String service : gateway.getServices()) {
			storeService(service, gateway.getId());
		}
		
		for (String token : gateway.getTokens()) {
			storeService(token, gateway.getId());
		}
		
		notifyGatewayAdded(gateway);
	}
	
	public void updateGateway (Gateway gateway) {
		SQLiteDatabase db;
		ContentValues values = new ContentValues();

		db = instance.getReadableDatabase();
		
		values.put("name", gateway.getAppId());
		values.put("uri", gateway.getUri());
		values.put("port", gateway.getPort());
		
		String selection = "id" + " LIKE ?";
		String[] selectionArgs = { String.valueOf(gateway.getId()) };
		
		db.update(GATEWAY_TABLE_NAME, values, selection, selectionArgs);
		db.close();
		
		//TODO ? update values for services and tokens?
		
		notifyGatewayModified(gateway);
	}
	
	public void updateGatewayAppId (Gateway gateway) {
		SQLiteDatabase db;
		ContentValues values = new ContentValues();

		db = instance.getReadableDatabase();
		
		values.put("app_id", gateway.getAppId());
		
		String selection = "id" + " LIKE ?";
		String[] selectionArgs = { String.valueOf(gateway.getId()) };
		
		db.update(GATEWAY_TABLE_NAME, values, selection, selectionArgs);
		db.close();
		
		//TODO ? update values for services and tokens?
		
		notifyGatewayModified(gateway);
	}
	
	public void updateGatewayServices (Gateway gateway) {
		for (String service : gateway.getServices()) {
			storeService(service, gateway.getId());
		}
	}
	
	public void updateGatewayTokens (Gateway gateway) {
		for (String token : gateway.getTokens()) {
			storeToken(token, gateway.getId());
		}
	}
	
	public void deleteGateway(Gateway gateway) {
		SQLiteDatabase db;
		
		db = instance.getWritableDatabase();
		
		db.delete(SERVICE_TABLE_NAME, "gw_id='"+ gateway.getId() +"'", null);
		db.delete(TOKEN_TABLE_NAME, "gw_id='"+ gateway.getId() +"'", null);
		db.delete(LAST_MEASUREMENT_TABLE_NAME, "gw_id='"+ gateway.getId() +"'", null);
		db.delete(GATEWAY_TABLE_NAME, "id='"+ gateway.getId() +"'", null);
		db.close();
		
		notifyGatewayDeleted(gateway);
	}
	
	private ArrayList<String> getServices(String gwId) {
		ArrayList<String> services = new ArrayList<String>();
		SQLiteDatabase db;
		Cursor c;
		
		int idCol;
		
		String[] selectionArgs = { gwId };
		
		db = instance.getReadableDatabase();
		c  = db.rawQuery(GET_SERVICES, selectionArgs);
		
		idCol   = c.getColumnIndex("id");
		
		if (c.moveToFirst()) {
			do {
				services.add(c.getString(idCol));
			} while (c.moveToNext());
		}
		
		c.close();
		db.close();
		
		return services;
	}
	
	private void storeService(String id, String gwId) {
		SQLiteDatabase db;
		ContentValues values = new ContentValues(1);
		
		db = instance.getWritableDatabase();
		
		values.put("id", id);
		values.put("gw_id", gwId);
		
		db.insert(SERVICE_TABLE_NAME, null, values);
		db.close();
	}
	
	private ArrayList<String> getTokens(String gwId) {
		ArrayList<String> tokens = new ArrayList<String>();
		SQLiteDatabase db;
		Cursor c;
		
		int idCol;
		
		String[] selectionArgs = { gwId };
		
		db = instance.getReadableDatabase();
		c  = db.rawQuery(GET_TOKENS, selectionArgs);
		
		idCol   = c.getColumnIndex("content");
		
		if (c.moveToFirst()) {
			do {
				tokens.add(c.getString(idCol));
			} while (c.moveToNext());
		}
		
		c.close();
		db.close();
		
		return tokens;
	}
	
	private void storeToken(String content, String gwId) {
		SQLiteDatabase db;
		ContentValues values = new ContentValues(1);
		
		db = instance.getWritableDatabase();
		
		values.put("content", content);
		values.put("gw_id", gwId);
		
		db.insert(TOKEN_TABLE_NAME, null, values);
		db.close();
	}
	
	public Measurement getLastMeasurement(String gatewayId, String serviceId) {
		Measurement measurement = null;
		SQLiteDatabase db;
		Cursor c;
		
		int timeCol, presenceCol;
		
		String[] selectionArgs = { gatewayId, serviceId };
		
		db = instance.getReadableDatabase();
		c  = db.rawQuery(GET_LAST_MEASUREMENT, selectionArgs);
		
		timeCol = c.getColumnIndex("time");
		presenceCol = c.getColumnIndex("presence");
		
		if (c.moveToFirst()) {
			measurement = new Measurement(c.getLong(timeCol),
				                   Boolean.valueOf(c.getString(presenceCol)),
				                   gatewayId,
				                   serviceId);
		}
		
		return measurement;
	}
	
	public void storeMeasurement(Measurement measurement) {
		SQLiteDatabase db;
		ContentValues values = new ContentValues(1);
		
		db = instance.getWritableDatabase();
		
		values.put("time", measurement.getTime());
		values.put("presence", measurement.getMeasurement().toString());
		values.put("app_id", measurement.getGatewayId());
		values.put("service_id", measurement.getServiceId());
		
		db.insert(LAST_MEASUREMENT_TABLE_NAME, null, values);
		db.close();
	}
	
	public void deleteMeasurement(String measurementId) {
		SQLiteDatabase db;
		
		db = instance.getWritableDatabase();
		
		db.delete(LAST_MEASUREMENT_TABLE_NAME,"id='"+ measurementId +"'", null);
		db.close();
	}
	
	public void registerGatewayListener(DBGatewayListener listener) {
		gatewayListeners.add(listener);
	}
	
	public void unRegisterGatewayListener(DBGatewayListener listener) {
		gatewayListeners.remove(listener);
	}
	
	private void notifyGatewayAdded(Gateway gateway) {
		for (DBGatewayListener listener : gatewayListeners) {
			listener.onGatewayAdded(gateway);
		}
	}
	
	private void notifyGatewayModified(Gateway gateway) {
		for (DBGatewayListener listener : gatewayListeners) {
			listener.onGatewayUpdated(gateway);
		}
	}

	private void notifyGatewayDeleted(Gateway gateway) {
		for (DBGatewayListener listener : gatewayListeners) {
			listener.onGatewayDeleted(gateway);
		}
	}
}
