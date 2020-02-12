package eu.cloudnetservice.cloudnet.ext.npcs.node.listener;


import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.util.DefaultModuleHelper;
import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;
import eu.cloudnetservice.cloudnet.ext.npcs.configuration.NPCConfiguration;

import java.io.File;
import java.util.Arrays;

public class IncludePluginListener {

    private NPCConfiguration configuration;

    public IncludePluginListener(NPCConfiguration configuration) {
        this.configuration = configuration;
    }

    @EventListener
    public void handle(CloudServicePreStartEvent event) {
        if (!event.getCloudService().getServiceConfiguration().getServiceId().getEnvironment().isMinecraftJavaServer()) {
            return;
        }

        boolean installPlugin = this.configuration.getConfigurations().stream()
                .anyMatch(npcConfigurationEntry -> Arrays.asList(event.getCloudService().getServiceConfiguration().getGroups()).contains(npcConfigurationEntry.getTargetGroup()));

        new File(event.getCloudService().getDirectory(), "plugins").mkdirs();
        File file = new File(event.getCloudService().getDirectory(), "plugins/cloudnet-npcs.jar");
        file.delete();

        if (installPlugin && DefaultModuleHelper.copyCurrentModuleInstanceFromClass(IncludePluginListener.class, file)) {
            DefaultModuleHelper.copyPluginConfigurationFileForEnvironment(
                    IncludePluginListener.class,
                    event.getCloudService().getServiceConfiguration().getProcessConfig().getEnvironment(),
                    file
            );
        }
    }

}
