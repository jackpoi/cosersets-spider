package io.github.beastars1.cosersets;

import io.github.beastars1.cosersets.verticle.CosVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.TimeUnit;

public class MainLauncher {
  public static final Logger log = LoggerFactory.getLogger("[MainLauncher]");

  public static void main(String[] args) {
    final var localConfigStore = new ConfigStoreOptions()
      .setType("file")
      .setOptional(true)
      .setFormat("json")
      .setConfig(new JsonObject()
        .put("path", "config.json")
      );
    final ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions()
      .addStore(localConfigStore);
    final Vertx vertx = Vertx.vertx(new VertxOptions().setBlockedThreadCheckIntervalUnit(TimeUnit.DAYS));
    ConfigRetriever retriever = ConfigRetriever.create(vertx, configRetrieverOptions);
    retriever.getConfig(ar -> {
      if (ar.failed()) {
        log.error("Failed to get configuration");
      } else {
        JsonObject config = ar.result();
        deployCos(vertx, config);
      }
    });
  }

  private static void deployCos(Vertx vertx, JsonObject config) {
    Future.<Void>succeededFuture()
      .compose(v -> {
        DeploymentOptions options = new DeploymentOptions().setWorker(true).setConfig(config);
        return vertx.deployVerticle(CosVerticle.class.getName(), options);
      })
      .onSuccess(deployId -> log.info("CosVerticle deploy ID: " + deployId))
      .onFailure(err -> {
        log.error(err.getMessage(), err.getCause());
        System.exit(0);
      });
  }
}
