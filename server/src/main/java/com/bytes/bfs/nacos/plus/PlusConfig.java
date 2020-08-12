package com.bytes.bfs.nacos.plus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties(prefix = "nacos.plus")
public class PlusConfig {

    @Builder.Default
    private Integer expire = 10_000;

    @Builder.Default
    private Integer heartbeat = 5_000;

}
