package tw.idv.angus.docker;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContainerConfig {
    private final List<Bind> binds;
    private final String imageName;
    private boolean autoRemove;
    private String containerName;
    private String hostName;
    private List<String> commands;
    private String workDir;

    public ContainerConfig(String imageName) {
        this.imageName = imageName;
        this.binds = new ArrayList<>();
        this.commands = new ArrayList<>();
    }

    public ContainerConfig withVolume(String volumeBind) {
        this.binds.add(Bind.parse(volumeBind));
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

    public Optional<String> getContainerName() {
        return Optional.ofNullable(this.containerName);
    }

    public Optional<String> getHostName() {
        return Optional.ofNullable(this.hostName);
    }

    public String getImageName() {
        return this.imageName;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public ContainerConfig withCommands(List<String> commands) {
        this.commands.addAll(commands);
        return this;
    }

    public ContainerConfig withWorkDir(String workDir) {
        this.workDir = workDir;
        return this;
    }

    public Optional<String> getWorkDir() {
        return Optional.ofNullable(workDir);
    }
}
