package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.DiscoveringOrigin;

public interface DiscoveringOriginService {
    public List<DiscoveringOrigin> getDiscoveringOrigins();

    public DiscoveringOrigin getDiscoveringOrigin(Integer id);

    public DiscoveringOrigin addOrUpdateDiscoveringOrigin(DiscoveringOrigin discoveringOrigin);
}
