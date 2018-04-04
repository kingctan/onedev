package io.onedev.server.security.permission;

import org.apache.shiro.authz.Permission;

import io.onedev.server.security.ProjectPrivilege;

public class PublicPermission implements Permission {

	@Override
	public boolean implies(Permission p) {
		if (p instanceof ProjectPermission) {
			ProjectPermission projectPermission = (ProjectPermission) p;
			return projectPermission.getProject().isPublicRead() 
					&& projectPermission.getPrivilege() == ProjectPrivilege.READ;
		} else {
			return p instanceof PublicPermission;
		}
	}

}