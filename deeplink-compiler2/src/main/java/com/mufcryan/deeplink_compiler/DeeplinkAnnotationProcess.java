package com.mufcryan.deeplink_compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.google.auto.service.AutoService;
import com.mufcryan.deeplink_annotation.DeepLinkBean;
import com.mufcryan.deeplink_annotation.DeeplinkAnnotation;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.mufcryan.deeplink_annotation.DeeplinkAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DeeplinkAnnotationProcess extends AbstractProcessor {
  private Messager mMessager;
  private Elements mElements;
  private HashMap<String, DeepLinkBean> mMap = new HashMap<>();

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    mMessager = processingEnv.getMessager();
    mElements = processingEnv.getElementUtils();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    HashSet<String> supportTypes = new LinkedHashSet<>();
    supportTypes.add(DeeplinkAnnotation.class.getCanonicalName());
    return supportTypes;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(DeeplinkAnnotation.class);
    for (Element element : elements) {
      // 获取当前Activity的全类名
      TypeMirror typeMirror = element.asType();
      DeeplinkAnnotation annotation = element.getAnnotation(DeeplinkAnnotation.class);
      String host = annotation.host();
      String scheme = annotation.scheme();
      String pathPrefix = annotation.pathPrefix();
      DeepLinkBean bean = new DeepLinkBean(host, scheme, pathPrefix);
      mMap.put(typeMirror.toString(), bean);
    }

    String xmlString = XmlUtils.map2xml(mMap, "data");
    mMessager.printMessage(Diagnostic.Kind.NOTE, xmlString);

    // 获取 app module 下的 build.gradle 的位置 --> XML 保存到此处
    try {
      FileObject fileObject =
          processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "com.mufcryan", "test");
      String path = fileObject.toUri().getPath();
      String rootPath = path.split("/build")[0];
      XmlUtils.saveXml(xmlString, rootPath, "Deeplink.xml");
    } catch (Exception e) {
      e.printStackTrace();
      mMessager.printMessage(Diagnostic.Kind.NOTE, "error  :  " + e.getMessage());
    }
    return false;
  }
}