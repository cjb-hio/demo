package com.example.aptcompiler;

import com.example.annotation.BindView;
import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.example.annotation.BindView"})
public class AnnotationProcesser extends AbstractProcessor {

    private Map<String,List<Element>> map=new HashMap<>();
    public Messager mMessager;
    public Elements mElements;
    public Filer mFiler;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("--------------------------->");
        mFiler = processingEnv.getFiler();//文件相关的辅助类
        mElements = processingEnv.getElementUtils();//元素相关的辅助类
        mMessager = processingEnv.getMessager();//日志相关的辅助类

        //筛选注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element:elements){
            VariableElement variableElement= (VariableElement) element;//获取成员变量节点
            //获取activity名称
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            String key=typeElement.getSimpleName().toString();
            if(map.get(key)==null){
                ArrayList list=new ArrayList();
                map.put(key,list);
            }
            List<Element> list = map.get(key);
            list.add(element);
        }
        //创建文件
        createFiles(map);

        return false;
    }

    private void createFiles(Map<String, List<Element>> map) {
        Iterator<String> iterator=map.keySet().iterator();
    }
}
