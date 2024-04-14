package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.ActiveDirectoryGroup;

public interface ActiveDirectoryGroupService {
    public List<ActiveDirectoryGroup> getActiveDirectoryGroups();

    public ActiveDirectoryGroup getActiveDirectoryGroup(Integer id);
	
	 public ActiveDirectoryGroup addOrUpdateActiveDirectoryGroup(ActiveDirectoryGroup activeDirectoryGroup);
}
