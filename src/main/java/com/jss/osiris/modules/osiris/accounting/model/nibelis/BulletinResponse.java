package com.jss.osiris.modules.osiris.accounting.model.nibelis;

import java.util.List;

public class BulletinResponse {
    private List<BulletinLigne> data;

    public List<BulletinLigne> getData() {
        return data;
    }

    public void setData(List<BulletinLigne> data) {
        this.data = data;
    }
}