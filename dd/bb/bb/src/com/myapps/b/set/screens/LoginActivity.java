package com.myapps.b.set.screens;

import java.io.ByteArrayInputStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.myapps.b.set.R;
import com.myapps.b.set.ActivityListener;
import com.myapps.b.set.BaseActivity;
import com.myapps.b.set.BaseAppData;
import com.myapps.b.set.communication.HttpRequestConstants;
import com.myapps.b.set.data.RequestData;
import com.myapps.b.set.model.Ldap;
import com.myapps.b.set.model.LoginCredentials;
import com.myapps.b.set.parsers.LdapParser;
import com.myapps.b.set.parsers.LoginListParser;
import com.myapps.b.set.parsers.MobileUserLogParser;
import com.myapps.b.set.preferences.AppLaunchPreferences;
import com.myapps.b.set.preferences.AppPreferences;
import com.myapps.b.set.utils.AppUtil;
import com.myapps.b.set.utils.Constants;

/**
 * @Class LoginActivity 
 * @author 324520
 * @Description used to Login to the application. It uses c username and password
 */
public class LoginActivity extends BaseActivity implements ActivityListener, OnClickListener
{

	EditText					edtxtUserName, edtxtPassword;
	TextView					txtSigninProblem;
	Button						btnLogin;
	private static final String	TAG			= LoginActivity.class.getSimpleName();
	String						username	= "";
	String						password	= "";
	String[]					params		= { "", "", "" };

	/**
	 * @author 324520
	 * @Function name : onCreate
	 * @param
	 * @Description : Creating the LoginActivity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.login1);
			initViews();
		}
		catch (Exception e)
		{
			AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
			e.printStackTrace();
		}
	}

	/**
	 * @author 324520
	 * @Function name : initViews
	 * @param
	 * @Description : Initialize the UI
	 */
	private void initViews()
	{
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.hide();

		btnLogin = (Button) findViewById(R.id.btnLogin);
		edtxtUserName = (EditText) findViewById(R.id.edtxtUserName);
		edtxtPassword = (EditText) findViewById(R.id.edtxtPassword);
		txtSigninProblem = (TextView) findViewById(R.id.txtSigninProblem);

		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		edtxtUserName.setTypeface(font);
		edtxtPassword.setTypeface(font);

		btnLogin.setOnClickListener(this);
		txtSigninProblem.setOnClickListener(this);

		SharedPreferences prefs 	= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
		String userName 			= AppPreferences.getPreviousUsername(prefs);
		if (userName != null)
		{
			edtxtUserName.setText(userName);
		}
		else
		{
			edtxtUserName.setText("");
		}

		edtxtUserName.setOnEditorActionListener(new EditText.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_NEXT)
				{
					edtxtPassword.requestFocus();
					return true;
				}
				return false;
			}
		});
		edtxtPassword.setOnEditorActionListener(new EditText.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_DONE)
				{
					btnLogin.performClick();
					InputMethodManager imm 		= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(edtxtPassword.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * @author 324520
	 * @Function name : onClick
	 * @param
	 * @Description : LOGIN BUTTON CLICK FUNCTIONALITY. Validating login
	 *              username and passwords
	 */
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnLogin:
				InputMethodManager imm 		= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtxtPassword.getWindowToken(), 0);
				if (edtxtUserName.getText().toString().equals("") && edtxtPassword.getText().toString().equals(""))
				{
					AppUtil.alertMsg(LoginActivity.this, "Alert", "Please enter credentials");
					edtxtUserName.requestFocus();
				}
				else if (edtxtUserName.getText().toString().equals(""))
				{
					AppUtil.alertMsg(LoginActivity.this, "Alert", "Please enter username");
					edtxtUserName.requestFocus();
				}
				else if (edtxtPassword.getText().toString().equals(""))
				{
					AppUtil.alertMsg(LoginActivity.this, "Alert", "Please enter password");
					edtxtPassword.requestFocus();
				}
				else
				{
					this.showProgress("", Constants.MESSEGE_VERIFY_CREDENTIAL);
					callLoginService();
				}
				break;
			case R.id.txtSigninProblem:
				AlertDialog.Builder appListingDialog 	= new AlertDialog.Builder(LoginActivity.this);
				appListingDialog.setTitle("Contact us");
				appListingDialog.setMessage("Mail us your queries @ SETappsupport@c.com");
				appListingDialog.setPositiveButton("Ok", null);
				appListingDialog.show();

				break;
			default:
				break;

		}
	}

	/**
	 * @author 324520
	 * @Function name : onFinish
	 * @param BaseAppData
	 *            : webservice response data for login service
	 * @Description : webservice response for login service is parsing and
	 *              calling next activity
	 */
	@Override
	public void onFinish(BaseAppData data)
	{
		try
		{
			if (data.getResponse().getResponseData() == null)
			{
				this.dismissProgress();
			}
			if (data.getRequest().getRequestCode() == HttpRequestConstants.REQ_LOGIN_LIST)
			{
				try
				{
					if (data.getResponse().isSuccess())
					{
						if (!data.getResponse().getResponseData().equals(null))
						{						
							/** Parsing the response data */
							LoginListParser parser 			= new LoginListParser(new ByteArrayInputStream(data.getResponse().getResponseData().getBytes()));
							if (parser.isLoginStatus())
							{
								/**
								 * Save user authentication details in shared
								 * preferences
								 */
								SharedPreferences prefs 	= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
								Editor editor 				= prefs.edit();
								AppPreferences.saveUserPreferences(editor, username, password);
								// set the username as the previous username
								AppPreferences.savePreviousUsername(editor, username);
								Constants.IS_APP_LOGGED_IN 	= true;
								/** Launching home activity */
								this.dismissProgress();
								callMobileUserLogService();
								// checkAppFirstLaunch();
							}
							else
							{
								this.dismissProgress();
								if (data.getResponse().getResponseMessage() == null) AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR,
										Constants.RESPONSE_AUTHENTICATION_FAILED);
								else
									AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, data.getResponse().getResponseMessage());
							}

						}
						else
						{
							this.dismissProgress();
							AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, data.getResponse().getResponseMessage());
						}
					}
					else
					{
						this.dismissProgress();
						AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, data.getResponse().getResponseMessage());
					}

				}
				catch (Exception e)
				{
					this.dismissProgress();
					AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
					e.printStackTrace();
				}
			}
			else if (data.getRequest().getRequestCode() == HttpRequestConstants.REQ_LOGIN)
			{
				try
				{
					if (data.getResponse().isSuccess())
					{
						if (!data.getResponse().getResponseData().equals(null))
						{							
							/** Parsing the response data */
							LdapParser parser 		= new LdapParser(new ByteArrayInputStream(data.getResponse().getResponseData().getBytes()));
							Ldap ldap 				= (Ldap) parser.getData();
							if (ldap.isAuthenticated())
							{
								/**
								 * Save user authentication details in shared
								 * preferences
								 */
								SharedPreferences prefs 	= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
								Editor editor 				= prefs.edit();
								AppPreferences.saveUserPreferences(editor, username, edtxtPassword.getText().toString());
								// set the username as the previous username
								AppPreferences.savePreviousUsername(editor, edtxtUserName.getText().toString());
								Constants.IS_APP_LOGGED_IN 	= true;
								finish();
								startActivity(new Intent(LoginActivity.this, DrawerHome.class));
							}
							else
							{
								this.dismissProgress();
								if (ldap.getErrorMessage() == null) AppUtil.alertMsg(LoginActivity.this, "Authentication failed! ", "Please try again."); // changed
																														// #175
								else
									AppUtil.alertMsg(LoginActivity.this, "Alert", ldap.getErrorMessage());
							}
						}
					}
					else
					{
						this.dismissProgress();
						AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
					}

				}
				catch (Exception e)
				{
					this.dismissProgress();
					AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
					e.printStackTrace();
				}
			}
			//
			else if (data.getRequest().getRequestCode() == HttpRequestConstants.REQ_MOBILE_USER_LOG)
			{
				try
				{
					if (data.getResponse().isSuccess())
					{
						if (!data.getResponse().getResponseData().equals(null))
						{							
							/** Parsing the response data */
							MobileUserLogParser parser 		= new MobileUserLogParser(new ByteArrayInputStream(data.getResponse().getResponseData().getBytes()));
							if (parser.isLoginStatus())
							{
								checkAppFirstLaunch();
							}
							else
							{
								this.dismissProgress();
								AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED); // changed																											// #175
							}
						}
					}
					else
					{
						this.dismissProgress();
						AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
					}
				}
				catch (Exception e)
				{
					this.dismissProgress();
					AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			this.dismissProgress();
			AppUtil.alertMsg(LoginActivity.this, Constants.ALERT_TITLE_ERROR, Constants.RESPONSE_ERROR_OCCURRED);
			e.printStackTrace();
		}
	}

	@Override
	public void onCancel(BaseAppData data)
	{
	}

	/**
	 * @author 324520
	 * @Function name : callLoginService
	 * @param
	 * @Description : webservice for login service is calling
	 */

	private void callLoginService()
	{
		try
		{
			username 			= edtxtUserName.getText().toString();
			password 			= edtxtPassword.getText().toString();

			// if c\ is there, remove c\ from userid			
			if (edtxtUserName.getText().toString().startsWith("c\\"))
			{
				username 		= edtxtUserName.getText().toString().replace("c\\", "");
			}
			else if (edtxtUserName.getText().toString().startsWith("c\\"))
			{
				username 		= edtxtUserName.getText().toString().replace("c\\", "");
			}
			
			BaseAppData loginData 					= new BaseAppData();
			RequestData request 					= new RequestData();

			LoginCredentials tempLoginCredentials 	= new LoginCredentials();
			tempLoginCredentials.setUsername(username);
			tempLoginCredentials.setPassword(password);
			tempLoginCredentials.setDomain(Constants.DOMAIN);
			tempLoginCredentials.setLocalHost("");

			//Calling GetList Service for Login...
			addListener(HttpRequestConstants.REQ_LOGIN_LIST, this);
			request.setUrl(Constants.BASE_DATA_URL);
			request.setRequestCode(HttpRequestConstants.REQ_LOGIN_LIST);
			request.setLoginCredentials(tempLoginCredentials);
			request.setParams(params);
			loginData.setRequest(request);
			execute(loginData);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @author 324520
	 * @Function name : checkAppFirstLaunch
	 * @param
	 * @Description : checking whether its is the first launch, and finishing
	 *              this activity and starting home activity
	 */
	private void checkAppFirstLaunch()
	{
		SharedPreferences prefs 		= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
		Editor editor 					= prefs.edit();
		if (AppLaunchPreferences.isAppFirstLaunch(prefs)) AppLaunchPreferences.setAppFirstLaunch(editor, false);
		finish();
		startActivity(new Intent(LoginActivity.this, DrawerHome.class));
	}

	/**
	 * @author 324520
	 * @Function name : callLoginService
	 * @param
	 * @Description : webservice for login service is calling
	 */

	private void callMobileUserLogService()
	{
		try
		{
			showProgress("", "");

			SharedPreferences prefs 	= PreferenceManager.getDefaultSharedPreferences(this);
			String username 			= AppPreferences.getUserName(prefs);
			String password 			= AppPreferences.getUserPassword(prefs);

			BaseAppData loginData 		= new BaseAppData();
			RequestData request 		= new RequestData();

			LoginCredentials tempLoginCredentials 	= new LoginCredentials();
			tempLoginCredentials.setUsername(username);
			tempLoginCredentials.setPassword(password);
			tempLoginCredentials.setDomain(Constants.DOMAIN);
			tempLoginCredentials.setLocalHost("");

			// For using MobileUserLogService 			
			addListener(HttpRequestConstants.REQ_MOBILE_USER_LOG, this);
			request.setUrl(Constants.BASE_MOBILE_USER_LOG_URL);
			request.setRequestCode(HttpRequestConstants.REQ_MOBILE_USER_LOG);
			String[] params 			= { username, Constants.LOGIN_DEVICE };

			request.setLoginCredentials(tempLoginCredentials);
			request.setParams(params);
			loginData.setRequest(request);
			execute(loginData);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
