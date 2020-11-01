package tw.idv.angus;

import tw.idv.angus.docker.ContainerConfig;
import tw.idv.angus.docker.DockerClient;
import tw.idv.angus.docker.exception.MyDockerClientException;

import java.time.Instant;
import java.util.Arrays;

public class RunDocker {
    public static void main(String[] args) throws MyDockerClientException {
        DockerClient dockerClient = new DockerClient();

        dockerClient.pullImage("alpine/git", "latest");

        ContainerConfig config = new ContainerConfig()
                .withContainerName("gitclone" + Instant.now().toEpochMilli())
                .withHostName("gitclone")
                .withImage("alpine/git:latest")
                .withVolume("/opt/git/:/opt/git/")
                .withAutoRemove(true)
                .withCommand(Arrays.asList("clone", "-b", "develop", "--depth", "1", "https://github.com/ajdfajdfl2003/target_of_update_maven.git", "/opt/git/target_of_update_maven"));
        dockerClient.run(config);
    }
}
