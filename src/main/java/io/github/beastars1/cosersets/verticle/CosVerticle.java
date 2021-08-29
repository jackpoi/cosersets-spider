package io.github.beastars1.cosersets.verticle;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.beastars1.cosersets.constants.CosConstants;
import io.github.beastars1.cosersets.entity.FileEntity;
import io.github.beastars1.cosersets.property.CosProperty;
import io.github.beastars1.cosersets.utils.JsonUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClient;

import java.util.List;

public class CosVerticle extends AbstractVerticle {
  public static final Logger log = LoggerFactory.getLogger("[CosVerticle]");
  private static WebClient client;
  private CosProperty cosProperty;
  private String url;
  private FileSystem fs;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    this.init()
      .compose(property -> {
        this.cosProperty = property;
        this.url = "https://" + this.cosProperty.getDomain() + this.cosProperty.getSuffix();
        return this.begin();
      })
      .onSuccess(startPromise::complete)
      .onFailure(startPromise::fail);
  }

  private Future<Void> begin() {
    client.getAbs(this.url)
      .send()
      .onSuccess(res -> {
        JsonNode tree = JsonUtil.toTree(res.bodyAsString());
        JsonNode node = tree.get("data").get("files");
        loop(JsonUtil.toList(node.toString(), FileEntity.class));
      })
      .onFailure(t -> log.error("unexpected exception: ", t));
    return Future.succeededFuture();
  }

  private void loop(List<FileEntity> files) {
    try {
      // 每一组文件的path都一样，在这里创建文件夹
      if (!files.isEmpty()
        && !fs.existsBlocking(this.cosProperty.getPath() + files.get(0).getPath())) {
        log.info("create dir : " + this.cosProperty.getPath() + files.get(0).getPath());
        fs.mkdirBlocking(this.cosProperty.getPath() + files.get(0).getPath());
      }

      String path = this.cosProperty.getPath() + files.get(0).getPath();
      for (FileEntity entity : files)  {
        if (CosConstants.FOLDER.equals(entity.getType())) {
          client.getAbs(this.url)
            .addQueryParam("path", entity.getPath() + entity.getName())
            .send()
            .onSuccess(res -> {
              JsonNode node = JsonUtil.toTree(res.bodyAsString()).get("data").get("files");
              loop(JsonUtil.toList(node.toString(), FileEntity.class));
            });
        } else {
          String name = path + entity.getName();
          client.getAbs(entity.getUrl())
            .send()
            .onSuccess(res -> {
              Buffer buffer = res.bodyAsBuffer();
              fs.writeFile(name, buffer, ar -> {
                if (ar.succeeded()) {
                  log.info("download successful : " + name);
                } else {
                  log.error("download failed : " + name);
                }
              });
            });
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Future<CosProperty> init() {
    try {
      fs = vertx.fileSystem();
      client = WebClient.create(vertx);
      return Future.succeededFuture(this.config().mapTo(CosProperty.class));
    } catch (Exception e) {
      return Future.failedFuture(e);
    }
  }
}
