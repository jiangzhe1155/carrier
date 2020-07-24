package org.jz.admin.aspect;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author jz
 * @date 2020/07/23
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FileNameValidator.class)
public @interface FileName {
    String message() default "文件名不合法";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
