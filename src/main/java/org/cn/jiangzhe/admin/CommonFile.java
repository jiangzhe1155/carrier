package org.cn.jiangzhe.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

/**
 * @author jz
 * @date 2020/06/11
 */
@Data
@Builder
public class CommonFile {
    private String fileName;
    private String fileType;
    private String path;
    @JsonIgnore
    private InputStream in;
}
