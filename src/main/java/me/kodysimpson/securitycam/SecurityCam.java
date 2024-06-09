package me.kodysimpson.securitycam;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.kodysimpson.securitycam.commands.CreateCameraCommand;
import me.kodysimpson.securitycam.commands.ReplayCommand;
import me.kodysimpson.securitycam.commands.StartRecordingCommand;
import me.kodysimpson.securitycam.commands.StopRecordingCommand;
import me.kodysimpson.securitycam.database.MongoService;
import me.kodysimpson.securitycam.database.dao.CameraDao;
import me.kodysimpson.securitycam.database.dao.RecordableDao;
import me.kodysimpson.securitycam.database.dao.RecordingDao;
import me.kodysimpson.securitycam.listeners.BukkitListener;
import me.kodysimpson.securitycam.listeners.CameraCreationListener;
import me.kodysimpson.securitycam.listeners.ReplayListener;
import me.kodysimpson.securitycam.listeners.SecurityCamPacketListener;
import me.kodysimpson.securitycam.services.CameraService;
import me.kodysimpson.securitycam.services.RecordingService;
import me.kodysimpson.securitycam.services.ReplayService;
import me.kodysimpson.securitycam.tasks.TickTracker;
import me.kodysimpson.simpapi.menu.MenuManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public final class SecurityCam extends JavaPlugin {

    @Getter
    private static SecurityCam securityCam;
    @Getter
    private MongoService mongoService;
    @Getter
    private CameraService cameraService;
    @Getter
    private RecordingService recordingService;
    @Getter
    private ReplayService replayService;

    @Getter
    private CameraDao cameraDao;
    @Getter
    private RecordingDao recordingDao;
    @Getter
    private RecordableDao recordableDao;

    private BukkitAudiences adventure;

    public BukkitAudiences getAdventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(true)
                .bStats(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        securityCam = this;
        this.adventure = BukkitAudiences.create(this);

        //services
        this.mongoService = new MongoService();
        this.cameraDao = new CameraDao(mongoService);
        this.recordingDao = new RecordingDao(mongoService);
        this.recordableDao = new RecordableDao(mongoService);
        this.cameraService = new CameraService(cameraDao);
        this.recordingService = new RecordingService(recordingDao, recordableDao);
        this.replayService = new ReplayService(recordableDao, cameraDao);

        MenuManager.setup(this.getServer(), this);

        PacketEvents.getAPI().getEventManager().registerListener(new SecurityCamPacketListener(), PacketListenerPriority.NORMAL);
        PacketEvents.getAPI().init();

        //commands
        getCommand("createcam").setExecutor(new CreateCameraCommand());
        getCommand("startrecording").setExecutor(new StartRecordingCommand());
        getCommand("stoprecording").setExecutor(new StopRecordingCommand());
        getCommand("replay").setExecutor(new ReplayCommand());

        //Start tickin'
        TickTracker.startTracking();

        //listeners
        getServer().getPluginManager().registerEvents(new CameraCreationListener(), this);
        getServer().getPluginManager().registerEvents(new BukkitListener(), this);
        getServer().getPluginManager().registerEvents(new ReplayListener(replayService), this);
    }

    @Override
    public void onDisable() {

        replayService.stopAllReplays();

        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();

        if (this.adventure != null){
            this.adventure.close();
            this.adventure = null;
        }
    }

}
