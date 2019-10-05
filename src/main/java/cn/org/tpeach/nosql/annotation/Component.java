package cn.org.tpeach.nosql.annotation;

import java.lang.annotation.*;

/**
 * 定义注解
 *
 * @Target： 表示该注解可以用于什么地方，可能的ElementType参数有：
 * CONSTRUCTOR：构造器的声明
 * FIELD：域声明（包括enum实例）应用于字段或属性
 * LOCAL_VARIABLE：局部变量声明
 * METHOD：应用于方法级注释
 * PACKAGE：包声明
 * PARAMETER：应用于方法的参数
 * TYPE：类、接口（包括注解类型）或enum声明
 * ANNOTATION_TYPE：应用于注释类型
 * TYPE_PARAMETER：
 * TYPE_USE：
 * @Retention 表示需要在什么级别保存该注解信息。可选的RetentionPolicy参数包括：
 * SOURCE：标记的注释仅保留在源级别中 注解将被编译器丢弃
 * CLASS：注解在class文件中可用，但会被VM丢弃
 * RUNTIME：VM将在运行期间保留注解，因此可以通过反射机制读取注解的信息
 * @Documented 无论何时使用指定的注释，都应使用Javadoc工具记录这些元素
 * 默认情况下，注释不包含在Javadoc中。）有关更多信息，请参阅 Javadoc工具页面。
 * @Inherited 表明注释类型可以从超类继承。当用户查询注释类型并且该类没有此类型的注释时，
 * 将查询类的超类以获取注释类型（默认情况下不是这样）。此注释仅适用于类声明。
 * @Repeatable 表明标记的注释可以多次应用于相同的声明或类型使用
 * (即可以重复在同一个类、方法、属性等上使用)。
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    public enum BeanScope {
        SCOPE_SINGLETON,
        SCOPE_PROTOTYPE;
        BeanScope(){
        }

    }
    String value() ;
    BeanScope scope() default BeanScope.SCOPE_SINGLETON;
    boolean lazy() default true;
}
