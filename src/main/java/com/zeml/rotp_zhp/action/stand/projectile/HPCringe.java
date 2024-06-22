package com.zeml.rotp_zhp.action.stand.projectile;

import com.github.standobyte.jojo.action.stand.StandEntityAction;

public class HPCringe extends StandEntityAction {
    public HPCringe(StandEntityAction.Builder builder){
        super(builder);
    }


    @Override
    public boolean enabledInHudDefault() {
        return false;
    }

}