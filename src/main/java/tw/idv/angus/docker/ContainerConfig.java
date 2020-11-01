package tw.idv.angus.docker;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;

import java.util.ArrayList;
import java.util.List;

public class ContainerConfig {
    private List<Bind> binds;
    private boolean autoRemove;
    private String containerName;
    private String hostName;
    private String imageName;
    private List<String> commands;

    public ContainerConfig() {
        binds = new ArrayList<>();
    }

    public ContainerConfig withVolume(String volumeBind) {
        binds.add(Bind.parse(volumeBind));
        return this;
    }

    public ContainerConfig withAutoRemove(boolean autoRemove) {
        this.autoRemove = autoRemove;
        return this;
    }

    public HostConfig buildHostConfig() {
        return HostConfig.newHostConfig().withAutoRemove(autoRemove).withBinds(binds);
    }

    public ContainerConfig withContainerName(String containerName) {
        this.containerName = containerName;
        return this;
    }

    public ContainerConfig withHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public ContainerConfig withImage(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getHostName() {
        return hostName;
    }

    public String getImageName() {
        return imageName;
    }

    public List<String> getCommands() {
        return commands;
    }

    public ContainerConfig withCommand(List<String> commands) {
        this.commands = commands;
        return this;
    }
}
