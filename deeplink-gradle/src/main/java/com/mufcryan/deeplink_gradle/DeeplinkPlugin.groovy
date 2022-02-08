import com.mufcryan.deeplink_annotation.DeepLinkBean
import groovy.xml.XmlParser
import groovy.xml.XmlUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

class DeeplinkPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        target.afterEvaluate {
            // 获取 Android 拓展
            def android = target.getExtensions().getByName("android")
            android.applicationVariants.all { variant ->
                //variant：ApplicationVariant 代表每一种构建版本,如debug，release ，根据ApplicationVariant 我们可以得知 /签名信息
                def variantName = variant.name.capitalize()
                def processManifestTask = target.tasks.getByName("process${variantName}Manifest")

                //找到一个名如processDebugManifest的task,通过给他添加last闭包完成hook操作
                processManifestTask.doLast {
                    // 最终 AndroidManifest.xml 生成的地方
                    String manifestPath = target.getBuildDir().getAbsolutePath() + "intermediates/merged_manifests/${variant.name}/AndroidManifest.xml"

                    // 通过 XmlParser Deeplink.xml 并解析 Activity 对应的 data 信息
                    HashMap<String, DeepLinkBean> deepLinks = new HashMap<>()
                    String deeplinkXml = target.projectDir.getAbsolutePath() + "Deeplink.xml"
                    File file = new File(deeplinkXml)
                    XmlParser parser = new XmlParser()
                    Node rootNode = parser.parseText(file.getText())
                    List list = rootNode.children()
                    for (i in 0..<list.size()) {
                        Node child = (Node)list.get(i)
                        println("child: " + child.name())
                        String path = child.attribute("path")
                        String host = child.attribute("host")
                        String scheme = child.attribute("scheme")
                        String pathPrefix = child.attribute("pathPrefix")
                        DeepLinkBean bean = new DeepLinkBean(host, scheme, pathPrefix)
                        deepLinks.put(path, bean)
                    }

                    // 通过 XmlParser 修改 AndroidManifest.xml --> 为每个 activity 标签添加 data 信息
                    File manifestFile = new File(manifestPath)
                    String manifest = manifestFile.getText()
                    Node manifestNode = parser.parseText(manifest)
                    Node applicationNode = manifestNode.get("application")[0]
                    NodeList activityNodeList = applicationNode.get("activity")
                    List<String> hasExit = new ArrayList<>()

                    for (i in 0..<activityNodeList.size()) {
                        Node activityNode = (Node)activityNodeList.get(i)
                        Map attrs = activityNode.attributes()
                        Iterator iterator = attrs.iterator()
                        while (iterator.hasNext()) {
                            Map.Entry<Object, Object> entries = iterator.next()
                            String activityPath = entries.value
                            if (deepLinks.containsKey(activityPath)) {
                                NodeList intentFilterList = activityNode.get("intent-filter")
                                if (intentFilterList.size() > 0) {
                                    Node intentFilter = (Node)intentFilterList.get(0)
                                    DeepLinkBean bean = deepLinks.get(activityPath)
                                    HashMap<String, String> dataMap = new HashMap<>()
                                    dataMap.put("android:host", bean.getHost())
                                    dataMap.put("android:scheme", bean.getScheme())
                                    dataMap.put("android:path", bean.getPathPrefix())
                                    intentFilter.appendNode("data", dataMap)

                                    // 如果当前没有 activity view 则需添加 <action android:name="android.intent.action.VIEW"/>
                                    NodeList actionList = intentFilter.get("action")
                                    if (actionList.size() == 0) {
                                        HashMap<String, String> actionMap = new HashMap<>()
                                        actionMap.put("android:name", "android.intent.category.VIEW")
                                        intentFilter.appendNode("action", actionMap)
                                    }

                                    // 检查添加 category 信息
                                    NodeList categoryList = intentFilter.get("category")
                                    if (categoryList.size() < 2) {
                                        HashMap<String, String> categoryMap = new HashMap<>()
                                        categoryMap.put("android:name", "android.intent.category.DEFAULT")
                                        intentFilter.appendNode("category", categoryMap)
                                        categoryMap.clear()
                                        categoryMap.put("android:name", "android.intent.category.BROWSABLE")
                                        intentFilter.appendNode("category", categoryMap)
                                    }

                                    def config = XmlUtil.serialize(manifestNode)
                                    manifestFile.write(config)
                                    hasExit.add(activityPath)
                                } else {
                                    applicationNode.remove(attrs)
                                }
                            }
                        }
                    }

                    for (entry in deepLinks) {
                        if (!hasExit.contains(entry.key)) {
                            HashMap<String, String> maps = new HashMap<>()
                            maps.put("android:name", entry.key)
                            Node activityNode = applicationNode.appendNode("activity", maps)
                            Node filterNode = activityNode.appendNode("intent-filter")

                            NodeList actionList = filterNode.get("action")
                            if (actionList.size() == 0) {
                                HashMap<String, String> actionMap = new HashMap<>()
                                actionMap.put("android:name", "android.intent.action.VIEW")
                                filterNode.appendNode("action", actionMap)
                            }

                            NodeList categoryList = filterNode.get("category")
                            if (categoryList.size() < 2) {
                                HashMap<String, String> categoryMap = new HashMap<>()
                                categoryMap.put("android:name", "android.intent.category.DEFAULT")
                                filterNode.appendNode("category", categoryMap)
                                categoryMap.clear()
                                categoryMap.put("android:name", "android.intent.category.BROWSABLE")
                                filterNode.appendNode("category", categoryMap)
                            }

                            HashMap<String, String> dataMap = new HashMap<>()
                            dataMap.put("android:host", entry.value.getHost())
                            dataMap.put("android:scheme", entry.value.getScheme())
                            dataMap.put("android:path", entry.value.getPathPrefix())
                            filterNode.appendNode("data", dataMap)
                            def config = XmlUtil.serialize(manifestNode)
                            manifestFile.write(config)
                        }
                    }
                    boolean bl = file.delete()
                    println "delete success: " + bl
                }
            }
        }
    }
}