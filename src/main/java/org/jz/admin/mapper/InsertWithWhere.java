package org.jz.admin.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * @author jz
 * @date 2020/07/22
 */
public class InsertWithWhere extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {

        KeyGenerator keyGenerator = new NoKeyGenerator();
        String s = "<script>\nINSERT INTO %s %s SELECT %s FROM DUAL %s\n</script>";
        SqlMethod sqlMethod = SqlMethod.INSERT_ONE;

        String columnScript = SqlScriptUtils.convertTrim(getAllInsertSqlColumnMaybeIf(tableInfo),
                LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);

        String valuesScript = SqlScriptUtils.convertTrim(getAllInsertSqlPropertyMaybeIf(tableInfo),
                null, null, null, COMMA);
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                /** 自增主键 */
                keyGenerator = new Jdbc3KeyGenerator();
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            } else {
                if (null != tableInfo.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(getMethod(sqlMethod), tableInfo, builderAssistant);
                    keyProperty = tableInfo.getKeyProperty();
                    keyColumn = tableInfo.getKeyColumn();
                }
            }
        }

//               String s = "<script>\nINSERT INTO %s %s SELECT %s FROM DUAL %s\n</script>";
        String sql = String.format(s, tableInfo.getTableName(), columnScript, valuesScript,
                  "${ew.customSqlSegment}");
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, getMethod(sqlMethod), sqlSource, keyGenerator,
                keyProperty, keyColumn);
    }

    private String getAllInsertSqlPropertyMaybeIf(TableInfo tableInfo) {
        StringBuilder res = new StringBuilder();

        res.append(tableInfo.getKeyInsertSqlProperty(Constants.ENTITY_DOT, true));
        for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
            final String sqlScript = tableFieldInfo.getInsertSqlColumn();
            if (tableFieldInfo.isWithInsertFill()) {
                return sqlScript;
            }
            String s =
                    SqlScriptUtils.convertIf(SqlScriptUtils.safeParam(Constants.ENTITY_DOT + tableFieldInfo.getProperty())+ ',', String.format("%s != null",
                    Constants.ENTITY_DOT + tableFieldInfo.getProperty()), true);
            res.append(s);
        }
        return res.toString();
    }

    private String getAllInsertSqlColumnMaybeIf(TableInfo tableInfo) {
        StringBuilder res = new StringBuilder();
        res.append(getKeyInsertSqlColumn(tableInfo));
        for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
            final String sqlScript = tableFieldInfo.getInsertSqlColumn();
            if (tableFieldInfo.isWithInsertFill()) {
                return sqlScript;
            }
            String s = SqlScriptUtils.convertIf(sqlScript, String.format("%s != null",
                    Constants.ENTITY_DOT + tableFieldInfo.getProperty()), true);
            res.append(s);
        }
        return res.toString();
    }


    private String getKeyInsertSqlColumn(TableInfo tableInfo) {
        if (tableInfo.havePK()) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                return EMPTY;
            }
            return tableInfo.getKeyColumn() + COMMA + NEWLINE;
        }
        return EMPTY;
    }
}