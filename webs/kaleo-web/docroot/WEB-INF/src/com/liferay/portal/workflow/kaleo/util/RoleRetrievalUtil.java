/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.kaleo.util;

import com.liferay.portal.DuplicateRoleException;
import com.liferay.portal.NoSuchRoleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class RoleRetrievalUtil {

	public static Role getRole(
			String name, int roleType, boolean autoCreate,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		Role role = null;

		try {
			role = RoleLocalServiceUtil.getRole(
				serviceContext.getCompanyId(), name);

			if (role.getType() != roleType) {
				throw new DuplicateRoleException(
					"Role already exists with name " + name);
			}
		}
		catch (NoSuchRoleException nsre) {
			if (!autoCreate) {
				throw nsre;
			}

			Map<Locale, String> descriptionMap = new HashMap<Locale, String>();

			descriptionMap.put(
				LocaleUtil.getDefault(),
				"Autogenerated role from workflow definition");

			role = RoleLocalServiceUtil.addRole(
				serviceContext.getUserId(), serviceContext.getCompanyId(),
				name, null, descriptionMap, roleType);
		}

		return role;
	}

	public static List<Long> getRoleIds(ServiceContext serviceContext)
		throws SystemException {

		List<Role> roles = RoleLocalServiceUtil.getUserRoles(
			serviceContext.getUserId());

		List<Long> roleIds = new ArrayList<Long>(roles.size());

		for (Role role : roles) {
			roleIds.add(role.getRoleId());
		}

		return roleIds;
	}

	public static int getRoleType(String roleType) {
		if (roleType.equals(RoleConstants.TYPE_ORGANIZATION_LABEL)) {
			return RoleConstants.TYPE_ORGANIZATION;
		}
		else if (roleType.equals(RoleConstants.TYPE_SITE_LABEL) ||
				 roleType.equals(_LEGACY_TYPE_COMMUNITY_LABEL)) {

			return RoleConstants.TYPE_SITE;
		}
		else {
			return RoleConstants.TYPE_REGULAR;
		}
	}

	private static final String _LEGACY_TYPE_COMMUNITY_LABEL = "community";

}