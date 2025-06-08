package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.stand.StandEntityAction;

public class GiveEmperor extends StandEntityAction {

    public GiveEmperor(Builder builder) {
        super(builder);
    }

    @Override
    public boolean enabledInHudDefault() {
        return false;
    }

}
