package org.cn.jiangzhe.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.util.Date;

/**
 * @author jz
 * @date 2020/06/11
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonFile {
    private String fileName;
    private String fileType;
    private String path;
    @JsonIgnore
    private InputStream in;
    private Boolean isDir;
    private String size;
    private Date lastModifyTime;
}
