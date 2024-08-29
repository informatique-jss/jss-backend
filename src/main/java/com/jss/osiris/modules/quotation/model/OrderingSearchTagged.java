package com.jss.osiris.modules.quotation.model;

import com.jss.osiris.modules.miscellaneous.model.ActiveDirectoryGroup;

public class OrderingSearchTagged extends OrderingSearch {

    ActiveDirectoryGroup activeDirectoryGroup;
    Boolean isOnlyDisplayUnread;

    public ActiveDirectoryGroup getActiveDirectoryGroup() {
        return activeDirectoryGroup;
    }

    public void setActiveDirectoryGroup(ActiveDirectoryGroup activeDirectoryGroup) {
        this.activeDirectoryGroup = activeDirectoryGroup;
    }

    public Boolean getIsOnlyDisplayUnread() {
        return isOnlyDisplayUnread;
    }

    public void setIsOnlyDisplayUnread(Boolean isOnlyDisplayUnread) {
        this.isOnlyDisplayUnread = isOnlyDisplayUnread;
    }

}
