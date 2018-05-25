package com.xinqing.boy.pipeline;

import com.xinqing.boy.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * 结果保存到文本
 *
 * @author xuan
 * @since 1.0.0
 */
public class TextPipeline implements Pipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TextPipeline.class);

    private final int order = 100;

    /**
     * 文件路径
     */
    private String path;

    public TextPipeline(String path) {
        this.path = path;
    }

    @Override
    public void process(Item item) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(path, true);
            append(writer, item);
        } catch (IOException e) {
            LOG.error("write error.", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOG.error("writer close error.", e);
                }
            }
        }
    }

    @Override
    public int order() {
        return order;
    }

    private void append(FileWriter writer, Item item) throws IOException {
        for (Map.Entry<String, Object> entry : item.getAll().entrySet()) {
            writer.append(entry.getKey()).append(":").append("\r\n");
            writer.append(entry.getValue().toString()).append("\r\n");
        }
        writer.append("\r\n");
    }

}
