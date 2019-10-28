
package io.lettuce.core;

import static io.lettuce.core.protocol.CommandKeyword.COUNT;
import static io.lettuce.core.protocol.CommandKeyword.MATCH;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import cn.org.tpeach.nosql.tools.StringUtils;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.internal.LettuceAssert;
import io.lettuce.core.protocol.CommandArgs;
import lombok.Getter;

/**
 * 解决lettuce只支持UTF-8匹配问题
 */
public class ScanArgs implements CompositeArgument {
    @Getter
    private Long count;
    @Getter
    private String match;

    public static class Builder {

        private Builder() {
        }
        public static ScanArgs limit(long count) {
            return new ScanArgs().limit(count);
        }

        public static ScanArgs matches(String matches) {
            return new ScanArgs().match(matches);
        }
    }

    public ScanArgs match(String match) {

        LettuceAssert.notNull(match, "Match must not be null");

        this.match = match;
        return this;
    }

    public ScanArgs limit(long count) {

        this.count = count;
        return this;
    }

    @Override
    public <K, V> void build(CommandArgs<K, V> args) {

        if (match != null) {
//            args.add(MATCH).add(match.getBytes(StandardCharsets.UTF_8));
            args.add(MATCH).add(StringUtils.strToByte(match));
        }

        if (count != null) {
            args.add(COUNT).add(count);
        }
    }
}
