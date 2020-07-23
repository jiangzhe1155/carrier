package org.jz.admin.ddd.application.dto;

import cn.hutool.core.io.FileUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author jz
 * @date 2020/07/23
 */
public class FileNameValidator implements ConstraintValidator<FileName, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !FileUtil.containsInvalid(s);
    }
}
