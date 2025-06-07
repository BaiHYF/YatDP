package com.badlogic.yatdp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.XmlReader;

/**
 * ConfigManager 类负责加载和解析应用程序的配置文件。
 * 支持 JSON (*.json) 和 XML (*.xml) 两种格式。
 *
 * <p>配置文件包含系统初始化所需的各种参数，例如：
 * Spine 模型所在目录（modelDirPath）、模型文件名（modelName）、默认窗口尺寸、
 * 图标路径、默认动画名称以及点击后的动画名称等。将这些参数外部化，
 * 可以提高系统的灵活性与可维护性。</p>
 *
 * <p>示例用法：</p>
 * <pre>
 *   // 在应用启动时加载配置文件
 *   ConfigManager config = ConfigManager.loadConfig("config/config.json");
 *   if (config != null) {
 *       SpinePet pet = new SpinePet(config.modelDirPath, config.modelName);
 *   }
 * </pre>
 *
 * <p>注意：配置文件应放在 assets 目录下，例如：
 * assets/config/config.json 或 assets/config/config.xml</p>
 *
 * @author lujy
 *
 * @version 1.0
 */
public class ConfigManager {
    // 日志实例，便于调试
    private static final Logger logger =
        new Logger(ConfigManager.class.getSimpleName(), Logger.DEBUG);

    // 从配置文件中加载的各项配置参数
    public String modelDirPath;               // Spine 模型所在目录路径
    public String modelName;                  // 模型文件名（不带扩展名）
    public int defaultWindowWidth;            // 默认窗口宽度
    public int defaultWindowHeight;           // 默认窗口高度
    public String iconPath;                   // 图标文件路径
    public String defaultAnimationName;       // 默认动画名称
    public String onClickedAnimationName;     // 点击后的动画名称

    // 单例实例
    private static ConfigManager instance;

    /**
     * 私有构造方法，防止外部直接实例化
     */
    private ConfigManager() {
    }

    /**
     * 根据指定文件加载配置。
     * 文件必须位于 assets 文件夹内，支持 JSON 或 XML 格式配置文件。
     *
     * @param filePath 配置文件的相对路径，例如 "config/config.json"
     * @return 加载成功后的 ConfigManager 单例，如果加载失败返回 null
     */
    public static ConfigManager loadConfig(String filePath) {
        instance = new ConfigManager();
        FileHandle fileHandle = Gdx.files.internal(filePath);

        if (filePath.endsWith(".json")) {
            instance.loadJson(fileHandle);
        } else if (filePath.endsWith(".xml")) {
            instance.loadXml(fileHandle);
        } else {
            logger.error("不支持的配置文件格式。仅支持 JSON 与 XML 格式。");
            instance = null;
        }

        return instance;
    }

    /**
     * 解析 JSON 格式的配置文件，并加载各项配置参数。
     *
     * @param fileHandle 配置文件的文件句柄
     */
    private void loadJson(FileHandle fileHandle) {
        try {
            Json json = new Json();
            // 将 JSON 内容转换为一个临时 ConfigManager 对象
            ConfigManager tempConfig = json.fromJson(
                ConfigManager.class, fileHandle.readString("UTF-8")
            );
            // 复制解析后的配置参数
            this.modelDirPath           = tempConfig.modelDirPath;
            this.modelName              = tempConfig.modelName;
            this.defaultWindowWidth     = tempConfig.defaultWindowWidth;
            this.defaultWindowHeight    = tempConfig.defaultWindowHeight;
            this.iconPath               = tempConfig.iconPath;
            this.defaultAnimationName   = tempConfig.defaultAnimationName;
            this.onClickedAnimationName = tempConfig.onClickedAnimationName;

            logger.info("从 JSON 文件加载配置成功: " + fileHandle.path());
        } catch (Exception e) {
            logger.error("读取 JSON 配置出错: " + e.getMessage());
        }
    }

    /**
     * 解析 XML 格式的配置文件，并加载各项配置参数。
     *
     * @param fileHandle 配置文件的文件句柄
     */
    private void loadXml(FileHandle fileHandle) {
        try {
            XmlReader xmlReader = new XmlReader();
            XmlReader.Element root = xmlReader.parse(fileHandle);

            this.modelDirPath = root.getChildByName("modelDirPath").getText();
            this.modelName = root.getChildByName("modelName").getText();
            // 使用 Integer.parseInt() 将文本转换为整型
            this.defaultWindowWidth = Integer.parseInt(
                root.getChildByName("defaultWindowWidth").getText()
            );
            this.defaultWindowHeight = Integer.parseInt(
                root.getChildByName("defaultWindowHeight").getText()
            );
            this.iconPath = root.getChildByName("iconPath").getText();
            this.defaultAnimationName = root.getChildByName("defaultAnimationName").getText();
            this.onClickedAnimationName = root.getChildByName("onClickedAnimationName").getText();

            logger.info("从 XML 文件加载配置成功: " + fileHandle.path());
        } catch (Exception e) {
            logger.error("读取 XML 配置出错: " + e.getMessage());
        }
    }


    /**
     * 获取当前 ConfigManager 单例实例。
     *
     * @return ConfigManager 的单例实例
     */
    public static ConfigManager getInstance() {
        return instance;
    }
}
