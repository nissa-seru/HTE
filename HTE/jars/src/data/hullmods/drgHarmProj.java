package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class drgHarmProj extends BaseHullMod {
    
    public static String HP_ICON = "graphics/icons/drgHarmProjBuff.png";
    public static String HP_BUFFID1 = "drg_harmproj1";
    public static String HP_BUFFID2 = "drg_harmproj2";
    public static String HP_NAME = "Harmonic Energy Projector";
    public static float RANGE_BOOST = 25f;
    public static float PROJ_BOOST_MULT = 1.32f; // 33% max boost
    public static float MAX_BOOST_FLUX = 0.9f;
    
    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0)
            return (int)RANGE_BOOST + "%";
        if (index == 1)
            return (int)(RANGE_BOOST * PROJ_BOOST_MULT) + "%";
        if (index == 2)
            return (int)(100f * (MAX_BOOST_FLUX)) + "%";
        return null;
    }
    
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }
    
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (Global.getCombatEngine().isPaused() || ship.isHulk()) {
            return;
        }
        
        FluxTrackerAPI fluxTracker = ship.getFluxTracker();
        float harProjBonus = RANGE_BOOST * (fluxTracker.getCurrFlux() / (fluxTracker.getMaxFlux() * MAX_BOOST_FLUX));

        if (fluxTracker.getHardFlux() == 0)
        {
            harProjBonus = 0;
        } else if (harProjBonus > RANGE_BOOST)
        {
            harProjBonus = RANGE_BOOST;
        }

        ship.getMutableStats().getEnergyWeaponRangeBonus().modifyPercent("harProj", harProjBonus);
        ship.getMutableStats().getProjectileSpeedMult().modifyPercent("harProj", harProjBonus * PROJ_BOOST_MULT);
        
        if (ship == Global.getCombatEngine().getPlayerShip() && fluxTracker.getHardFlux() > 0) {
            Global.getCombatEngine().maintainStatusForPlayerShip(HP_BUFFID1, HP_ICON, HP_NAME, "Energy weapon range increased by " + (int)harProjBonus + "%", false);
            Global.getCombatEngine().maintainStatusForPlayerShip(HP_BUFFID2, HP_ICON, HP_NAME, "Projectile velocity increased by " + (int)(harProjBonus * PROJ_BOOST_MULT) + "%", false);
        }
    }
}
