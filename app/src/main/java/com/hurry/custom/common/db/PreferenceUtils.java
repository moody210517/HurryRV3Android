package com.hurry.custom.common.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hurry.custom.R;

public class PreferenceUtils {

	public static final String USERNAME = "user";
	public static final String PASSWORD = "password";
	public static final String NICKNAME = "nickname";


	public static final String TOKEN = "token";
	public static final String EMAIL = "email";
	public static final String IMAGE = "image";

	public static void setLoginUser(Context context, String user, String password, String nickname) {
		getSharedPreferences(context).edit().putString(USERNAME, user).putString(PASSWORD, password)
				.putString(NICKNAME, nickname).commit();
	}

	public static void setTokenUser(Context context, String token){
		getSharedPreferences(context).edit().putString(TOKEN, token).commit();
	}
	public static void setEmail (Context context, String email){
		getSharedPreferences(context).edit().putString(EMAIL, email).commit();
	}
	public static void setImage (Context context, String image){
		getSharedPreferences(context).edit().putString(IMAGE, image).commit();
	}
	public static void setNickname (Context context, String image){
		getSharedPreferences(context).edit().putString(NICKNAME, image).commit();
	}



	public static void setLogIn(Context context){
		getSharedPreferences(context).edit().putBoolean("LOGIN", true).commit();
	}
	public static void setLogOut(Context context){
		getSharedPreferences(context).edit().putBoolean("LOGIN", false).commit();
	}

	public static void setUserId(Context context, String userId){
		getSharedPreferences(context).edit().putString("USER_ID", userId).commit();
	}



	public static void setPassword(Context context, String password){
		getSharedPreferences(context).edit().putString(PASSWORD, password).commit();
	}


	public static String getUser(Context context) {
		String encryptedUser = getSharedPreferences(context).getString(USERNAME, null);
		return encryptedUser != null ? encryptedUser : null;
	}

	public static String getEmail(Context context) {
		return getSharedPreferences(context).getString(EMAIL, "");
	}
	public static String getImage(Context context) {
		return getSharedPreferences(context).getString(IMAGE, "");
	}




	public static String getPassword(Context context) {
		String encryptedPassword = getSharedPreferences(context).getString(PASSWORD, null);
		return encryptedPassword != null ? encryptedPassword : "";
	}
	
	public static String getNickname(Context context) {
		return getSharedPreferences(context).getString(NICKNAME, "");
	}



	public static String getToken(Context context) {
		String userToken = getSharedPreferences(context).getString(TOKEN, null);
		return userToken == null ? null : userToken;
	}

	public static boolean getLogin(Context context) {
		boolean userLogin = getSharedPreferences(context).getBoolean("LOGIN", false);
		return userLogin;
	}



	public static String getUserId(Context context) {
		String  user_id = getSharedPreferences(context).getString("USER_ID", "0");
		return user_id;
	}



	public static void setUserType(Context context, String price){
		getSharedPreferences(context).edit().putString("USER_TYPE", price).commit();
	}
	public static String  getUserType(Context context) {
		String  price = getSharedPreferences(context).getString("USER_TYPE", "");
		return price;
	}

	public static void setAddress1(Context context, String address1){
		getSharedPreferences(context).edit().putString("address1", address1).commit();
	}
	public static String  getAddress1(Context context) {
		String  price = getSharedPreferences(context).getString("address1", "");
		return price;
	}

	public static void setConfPhone(Context context, String con_phone){
		getSharedPreferences(context).edit().putString("con_phone", con_phone).commit();
	}
	public static String  getConfPhone(Context context) {
		String  price = getSharedPreferences(context).getString("con_phone", context.getString(R.string.default_phone));
		return price;
	}



	public static void setAddressId(Context context, String address_id){
		getSharedPreferences(context).edit().putString("address_id", address_id).commit();
	}
	public static String  getAddressId(Context context) {
		String  price = getSharedPreferences(context).getString("address_id", "");
		return price;
	}



	public static void setAddress2(Context context, String address2){
		getSharedPreferences(context).edit().putString("address2", address2).commit();
	}
	public static String  getAddress2(Context context) {
		String  price = getSharedPreferences(context).getString("address2", "");
		return price;
	}


	public static void setCity(Context context, String city){
		getSharedPreferences(context).edit().putString("city", city).commit();
	}
	public static String  getCity(Context context) {
		String  price = getSharedPreferences(context).getString("city", "");
		return price;
	}


	public static void setState(Context context, String state){
		getSharedPreferences(context).edit().putString("state", state).commit();
	}
	public static String  getState(Context context) {
		String  price = getSharedPreferences(context).getString("state", "");
		return price;
	}

	public static void setPincode(Context context, String pincode){
		getSharedPreferences(context).edit().putString("pincode", pincode).commit();
	}
	public static String  getPincode(Context context) {
		String  price = getSharedPreferences(context).getString("pincode", "");
		return price;
	}

	public static void setLandMark(Context context, String landmark){
		getSharedPreferences(context).edit().putString("landmark", landmark).commit();
	}
	public static String  getLandMark(Context context) {
		String  price = getSharedPreferences(context).getString("landmark", "");
		return price;
	}


	public static void setPhone(Context context, String phone){
		getSharedPreferences(context).edit().putString("phone", phone).commit();
	}
	public static String  getPhone(Context context) {
		String  price = getSharedPreferences(context).getString("phone", "");
		return price;
	}


	public static void setLast(Context context, String last_name){
		getSharedPreferences(context).edit().putString("last_name", last_name).commit();
	}
	public static String  getLastName(Context context) {
		String  price = getSharedPreferences(context).getString("last_name", "");
		return price;
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}


	public static void setQuote(Context context, boolean flag){
		getSharedPreferences(context).edit().putBoolean("quote", flag).commit();
	}

	public static boolean getQuote(Context context){
		return  getSharedPreferences(context).getBoolean("quote", false);
	}

	public static void setMode(Context context, int  mode){
		getSharedPreferences(context).edit().putInt("mode", mode).commit();
	}
	public static int getMode(Context context){
		return  getSharedPreferences(context).getInt("mode", 0);
	}


	public static void setOrderId(Context context, String  order_id){
		getSharedPreferences(context).edit().putString("order_id", order_id).commit();
	}

	public static String getOrderId(Context context){
		return  getSharedPreferences(context).getString("order_id", "0");
	}


	public static void setTrackId(Context context, String  track_id){
		getSharedPreferences(context).edit().putString("track_id", track_id).commit();
	}
	public static String getTrackId(Context context){
		return  getSharedPreferences(context).getString("track_id", "0");
	}



	public static void setCustomerQuestion(Context context, String  question){
		getSharedPreferences(context).edit().putString("question", question).commit();
	}
	public static String getCustomerQuestion(Context context){
		return  getSharedPreferences(context).getString("question", "");
	}


	public static void setCorporateQuestion(Context context, String  corporate_question){
		getSharedPreferences(context).edit().putString("corporate_question", corporate_question).commit();
	}
	public static String getCorporateQuestion(Context context){
		return  getSharedPreferences(context).getString("corporate_question", "");
	}


	public static void setAnswer(Context context, String  answer){
		getSharedPreferences(context).edit().putString("answer", answer).commit();
	}
	public static String getAnswer(Context context){
		return  getSharedPreferences(context).getString("answer", "");
	}

	public static void setTerm(Context context, String  term){
		getSharedPreferences(context).edit().putString("term", term).commit();
	}
	public static String getTerm(Context context){
		return  getSharedPreferences(context).getString("term", "");
	}

	public static void setPolicy(Context context, String  policy){
		getSharedPreferences(context).edit().putString("policy", policy).commit();
	}
	public static String getPolicy(Context context){
		return  getSharedPreferences(context).getString("policy", "");
	}


	public static void setFeedBackId(Context context, String feedback_id){
		getSharedPreferences(context).edit().putString("feedback_id", feedback_id).commit();
	}
	public static String  getFeedBackId(Context context) {
		String  price = getSharedPreferences(context).getString("feedback_id", "0");
		return price;
	}


	public static void setCorporateUserId(Context context, String corporate_user_id){
		getSharedPreferences(context).edit().putString("corporate_user_id", corporate_user_id).commit();
	}
	public static String  getCorporateUserId(Context context) {
		String  price = getSharedPreferences(context).getString("corporate_user_id", "");
		return price;
	}

	public static void setCorEmail(Context context, String cor_email){
		getSharedPreferences(context).edit().putString("cor_email", cor_email).commit();
	}
	public static String  getCorEmail(Context context) {
		String  price = getSharedPreferences(context).getString("cor_email", "");
		return price;
	}



	public static void setCorPassword(Context context, String cor_password){
		getSharedPreferences(context).edit().putString("cor_password", cor_password).commit();
	}
	public static String  getCorPassword(Context context) {
		String  price = getSharedPreferences(context).getString("cor_password", "");
		return price;
	}


	public static void setCorOrderId(Context context, String cor_order_id){
		getSharedPreferences(context).edit().putString("cor_order_id", cor_order_id).commit();
	}
	public static String  getCorOrderId(Context context) {
		String  price = getSharedPreferences(context).getString("cor_order_id", "0");
		return price;
	}


	public static void setDeviceToken(Context context, String cor_order_id){
		getSharedPreferences(context).edit().putString("device_token", cor_order_id).commit();
	}
	public static String  getDeviceToken(Context context) {
		String  price = getSharedPreferences(context).getString("device_token", "0");
		return price;
	}

	public static void setCityId(Context context, int city_id){
		getSharedPreferences(context).edit().putInt("city_id", city_id).commit();
	}
	public static int  getCityId(Context context) {
		int  price = getSharedPreferences(context).getInt("city_id", 0);
		return price;
	}



	public static void setFirstStart(Context context, boolean first_start){
		getSharedPreferences(context).edit().putBoolean("first_start", first_start).commit();
	}
	public static boolean getFirstStart(Context context) {
		boolean  price = getSharedPreferences(context).getBoolean("first_start", true);
		return price;
	}

	public static void setBusinessType(Context context, String business_type){
		getSharedPreferences(context).edit().putString("business_type", business_type).commit();
	}
	public static String getBusinessType(Context context) {
		String  price = getSharedPreferences(context).getString("business_type", "0");
		return price;
	}
}