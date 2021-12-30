package org.mybatis.generator.lemon;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.internal.util.StringUtility;
import tk.mybatis.mapper.MapperException;

import java.util.*;

/**
 * @author yuzhou
 * @date 2018/6/8
 * @time 18:21
 * @since 3.0.0
 */
public class LemonPlugin extends PluginAdapter {
    private Set<String> mappers = new HashSet<>();
    private Set<String> ignoreModelFields = new HashSet<>();
    
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 生成的Mapper接口
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //获取实体类
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        //获取主键
        FullyQualifiedJavaType primaryKeyJavaType = null;
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
        if (null != introspectedColumns) {
            if (introspectedColumns.size() == 1) {
                primaryKeyJavaType = introspectedColumns.get(0).getFullyQualifiedJavaType();
            } else {
                primaryKeyJavaType = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            }
        }
        if (null == primaryKeyJavaType) {
            primaryKeyJavaType = new FullyQualifiedJavaType("java.lang.Object");
        }
        //import接口
        for (String mapper : mappers) {
            interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
            interfaze.addImportedType(primaryKeyJavaType);
            interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ", " + primaryKeyJavaType.getShortName() + ">"));
        }
        //import实体类
        interfaze.addImportedType(entityType);
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        interfaze.addAnnotation("@Mapper");
        return true;
    }
    
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String mappers = this.properties.getProperty("mappers");
        if (StringUtility.stringHasValue(mappers)) {
            for (String mapper : mappers.split(",")) {
                this.mappers.add(mapper);
            }
        } else {
            throw new MapperException("Mapper插件缺少必要的mappers属性!");
        }
        String ignoreModelFields = this.properties.getProperty("ignoreModelFields");
        if (StringUtility.stringHasValue(ignoreModelFields)) {
            for (String ignoreModelField : ignoreModelFields.split(",")) {
                this.ignoreModelFields.add(ignoreModelField);
            }
        }
    }
    
    //下面所有return false的方法都不生成。这些都是基础的CRUD方法，使用通用Mapper实现
    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectAllMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectAllMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {

        topLevelClass.addImportedType("com.galaxy.lemon.framework.annotation.DataObject");
        topLevelClass.addAnnotation("@DataObject");
        return true;
    }

    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        if (introspectedTable.getContext().getDefaultModelType().equals(ModelType.LEMONFLAT)) {
            if (introspectedTable.getPrimaryKeyColumns().size() == 1) {
                return false;
            }
        }
        return true;
    }

    public boolean modelFieldGenerated(Field field,
                                       TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       Plugin.ModelClassType modelClassType) {
        if (ignoreModelFields.contains(field.getName())) {
            return false;
        }
        return true;
    }

    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              Plugin.ModelClassType modelClassType) {
        if (this.ignoreModelFields.contains(introspectedColumn.getJavaProperty())) {
            return false;
        }
        return true;
    }

    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              Plugin.ModelClassType modelClassType) {
        if (this.ignoreModelFields.contains(introspectedColumn.getJavaProperty())) {
            return false;
        }
        return true;
    }

    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        if (introspectedTable.getPrimaryKeyColumns() != null
                && introspectedTable.getPrimaryKeyColumns().size() > 1
                && isLemonFlat(introspectedTable)) {
            List<Attribute> attributes = element.getAttributes();
            for (Iterator<Attribute> iterator = attributes.iterator(); iterator.hasNext(); ) {
                if ("parameterType".equals(iterator.next().getName())) {
                    iterator.remove();
                    break;
                }
            }
            element.addAttribute(new Attribute("parameterType", introspectedTable.getPrimaryKeyType()));
        }
        return true;
    }

    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        if (introspectedTable.getPrimaryKeyColumns() != null
                && introspectedTable.getPrimaryKeyColumns().size() > 1
                && isLemonFlat(introspectedTable)) {
            List<Attribute> attributes = element.getAttributes();
            for (Iterator<Attribute> iterator = attributes.iterator(); iterator.hasNext(); ) {
                if ("parameterType".equals(iterator.next().getName())) {
                    iterator.remove();
                    break;
                }
            }
            element.addAttribute(new Attribute("parameterType", introspectedTable.getPrimaryKeyType()));
        }
        return true;
    }

    private boolean isLemonFlat(IntrospectedTable introspectedTable) {
        return introspectedTable.getContext().getDefaultModelType().equals(ModelType.LEMONFLAT);
    }
}
