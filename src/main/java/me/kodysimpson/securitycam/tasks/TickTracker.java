package me.kodysimpson.securitycam.tasks;

import lombok.Getter;
import me.kodysimpson.securitycam.SecurityCam;
import org.bukkit.Bukkit;

public class TickTracker {

    @Getter
    private static long currentTick = 0;

    public static void startTracking() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SecurityCam.getSecurityCam(), () -> {
            currentTick++;
        }, 0L, 1L);
    }

}
