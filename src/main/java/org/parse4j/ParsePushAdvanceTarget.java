package org.parse4j;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.parse4j.command.ParsePostCommand;
import org.parse4j.command.ParseResponse;

public class ParsePushAdvanceTarget {

	private Date expirationTime = null;
	private Date pushTime = null;
	private Long expirationTimeInterval = null;
	private JSONObject pushData = new JSONObject();
	private JSONObject allData = new JSONObject();

	public void setPushTime(Date time) {
		this.pushTime = time;
	}

	public void setExpirationTime(Date time) {
		this.expirationTime = time;
		this.expirationTimeInterval = null;
	}

	public void setExpirationTimeInterval(long timeInterval) {
		this.expirationTime = null;
		this.expirationTimeInterval = Long.valueOf(timeInterval);
	}

	public void clearExpiration() {
		this.expirationTime = null;
		this.expirationTimeInterval = null;
	}

	public void setMessage(String message) {
		this.pushData.put("alert", message);
	}

	public void setBadge(String badge) {
		if (badge == null || badge.length() == 0) {
			badge = "Increment";
		}
		this.pushData.put("badge", badge);
	}

	public void setSound(String sound) {
		this.pushData.put("sound", sound);
	}

	public void setTitle(String title) {
		this.pushData.put("title", title);
	}

	public void setData(String key, String value) {
		this.pushData.put(key, value);
	}

	public void set(String key, JSONObject value)
	{
		this.allData.put(key, value);
	}
	
	public void setWhere(JSONObject value)
	{
		this.allData.put("where", value);
	}
	
	public void setData(JSONObject data)
	{
		this.pushData = data;
	}
	
	public void send() throws ParseException {
		ParsePostCommand command = new ParsePostCommand("push");
		JSONObject requestData = getJSONData();
		command.setData(requestData);
		ParseResponse response = command.perform();
		if (response.isFailed()) {
			throw response.getException();
		}
	}

	public void sendInBackground(String message, List<String> channels) {
		SendPushInBackgroundThread event = new SendPushInBackgroundThread();
		ParseExecutor.runInBackground(event);
	}

	class SendPushInBackgroundThread extends Thread {

		public void run() {
			try {
				send();
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private JSONObject getJSONData() {
		JSONObject data = this.allData;
		data.put("data", this.pushData);

		if (pushTime != null) {
			data.put("push_time", Parse.encodeDate(pushTime));
		}

		if (expirationTimeInterval != null) {
			data.put("expiration_interval", expirationTimeInterval);
		}

		if (expirationTime != null) {
			data.put("expiration_time", expirationTime);
		}

		return data;
	}
}
