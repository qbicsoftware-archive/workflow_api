package com.qbic.util;

import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.UserServiceUtil;
//import javax.faces.context.FacesContext;

public class UserDetails {
	
	//Define log for this class
	private static Logger log = new Logger(UserDetails.class);

	/**
	 * Convenience function to quickly get the user's Liferay ID (e.g. '10542'). used in Workflow*BackingBean an below
	 */
	public static String getUserLiferayID() {
	//	FacesContext context = FacesContext.getCurrentInstance();
	//	String uid = context.getExternalContext().getRemoteUser(); 
		//log.debug("LiferayID is "+uid);
		return "";//uid;
	}
	
	/**
	 * Convenience function to quickly retrieve the user's short-name (e.g. 'loblum', 'behullar'). used by openbis* and PropertyEditor
	 */
	public static String getUserScreenName() {
		return getScreenNameById(Long.parseLong(getUserLiferayID()));
	}
	
	/**
	 * Returns the screen name of the user with the given id
	 * 
	 * @param id
	 * @return
	 */
	public static String getScreenNameById(long id) {
		String screenName = "";
		try {
			screenName = UserServiceUtil.getUserById(id).getScreenName();
		} catch (Exception e) {
//			Contexter.message("Username fail, plz log in!");
			log.error("Liferay screen name getting failed, go and log in!",e);
			screenName="loblum"; //Lori messsing
		}
		return screenName;
	}
	
	public static List<User> getAllUsers() {
		try{
			List<User> users = UserLocalServiceUtil.getUsers(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			log.debug(users.size() + " users were found.");
			return users;
		} catch (SystemException e) {
			log.error("Failed to retrieve user list",e);
		}
		return new ArrayList<User>();
	}
	
	/**
	 * Retrieves the roles of the current user
	 */
	public static List<Role> getUserRoles() {
		try {
			User user = UserServiceUtil.getUserById(Long.parseLong(getUserLiferayID()));
			return user.getRoles();
		}catch (Exception e) {
//			Contexter.message("Failed to retriev user's roles");
			log.error("Failed to retriev user's roles.",e);
		}
		return new ArrayList<Role>();
	}
	
}
