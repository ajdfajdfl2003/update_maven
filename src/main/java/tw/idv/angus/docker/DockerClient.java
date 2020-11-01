package tw.idv.angus.docker;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
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
                    .awaitCompletion(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new MyDockerClientException(e.getMessage(), e);
        }
    }

    public void run(ContainerConfig config) {
        CreateContainerResponse container
                = dockerClient
                .createContainerCmd(config.getImageName())
                .withName(config.getContainerName())
                .withHostName(config.getHostName())
                .withHostConfig(config.buildHostConfig())
                .withCmd(config.getCommands()).exec();
        dockerClient.startContainerCmd(container.getId()).exec();
    }
}
