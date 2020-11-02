package tw.idv.angus.docker;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import tw.idv.angus.docker.exception.MyDockerClientException;

import java.util.concurrent.TimeUnit;

public class DockerClient {

    private final com.github.dockerjava.api.DockerClient dockerClient;

    public DockerClient() {
        DefaultDockerClientConfig clientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        dockerClient = DockerClientBuilder.getInstance().withDockerHttpClient(
                new JerseyDockerHttpClient.Builder()
                        .dockerHost(clientConfig.getDockerHost())
                        .sslConfig(clientConfig.getSSLConfig())
                        .connectTimeout(30 * 1000)
                        .build()).build();

    }

    public void pullImage(String repository, String tag) throws MyDockerClientException {
        try {
            dockerClient.pullImageCmd(repository)
                    .withTag(tag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new MyDockerClientException(e.getMessage(), e);
        }
    }

    public void run(ContainerConfig config) throws MyDockerClientException {
        CreateContainerCmd containerCmd = dockerClient
                .createContainerCmd(config.getImageName())
                .withHostConfig(config.buildHostConfig())
                .withCmd(config.getCommands());
        config.getContainerName().ifPresent(containerCmd::withName);
        config.getHostName().ifPresent(containerCmd::withHostName);
        config.getWorkDir().ifPresent(containerCmd::withWorkingDir);
        CreateContainerResponse container = containerCmd.exec();
        dockerClient.startContainerCmd(container.getId()).exec();

        WaitContainerResultCallback waitCallback = new WaitContainerResultCallback();
        dockerClient.waitContainerCmd(container.getId()).exec(waitCallback);
        try {
            waitCallback.awaitCompletion();
        } catch (InterruptedException e) {
            throw new MyDockerClientException(e.getMessage(), e);
        }
    }
}
