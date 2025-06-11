package com.badlogic.yatdp.pet;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.yatdp.core.AppState;
import com.badlogic.yatdp.core.MainApp;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.List;

/**
 * SpinePet 类封装了桌面宠物的 Spine 模型、动画控制与交互逻辑。
 * <p>
 * 该类负责加载 Spine 模型资源、控制动画播放、处理用户点击事件，以及完成模型的实时渲染。
 * 它构成桌宠外观与行为的核心表现层，结合 Spine Runtime 实现动态骨骼动画效果。
 *
 * <h3>主要功能</h3>
 * <ul>
 *   <li>加载指定路径的 Spine 模型（支持二进制 .skel 与 .atlas 格式）</li>
 *   <li>初始化并管理骨骼动画系统</li>
 *   <li>根据程序状态动态调整模型位置（如在菜单模式中左移）</li>
 *   <li>支持用户点击交互：触发互动动画与音效播放</li>
 *   <li>自动处理动画混合，增强自然切换效果</li>
 *   <li>提供资源释放方法，防止内存泄漏</li>
 * </ul>
 *
 * <h3>模块协作结构</h3>
 * <pre>
 * MainApp
 *  └── SpinePet
 *        ├── Skeleton / AnimationState        // 动画控制核心
 *        ├── TwoColorPolygonBatch             // 渲染器
 *        ├── OrthographicCamera               // 视图控制
 *        └── Sound clickSound                 // 交互音效
 * </pre>
 *
 * <h3>典型用法</h3>
 * <pre>{@code
 * // 初始化宠物模型（指定模型目录与模型名）
 * SpinePet pet = new SpinePet("models", "cat");
 *
 * // 每帧调用渲染
 * pet.render(Gdx.graphics.getDeltaTime());
 *
 * // 用户点击事件触发动画与声音
 * pet.onClicked();
 *
 * // 应用关闭时释放资源
 * pet.dispose();
 * }</pre>
 *
 * <h3>动画控制说明</h3>
 * <ul>
 *   <li>默认动画：Relax</li>
 *   <li>点击触发：Interact</li>
 *   <li>所有动画之间使用 0.1s 混合过渡</li>
 *   <li>点击动画播放完毕后自动切换回默认动画</li>
 * </ul>
 *
 * <h3>注意事项</h3>
 * <ul>
 *   <li>模型资源应包含一致命名的 `.atlas`, `.skel`, `.png` 文件</li>
 *   <li>模型缩放比设定为 {@code 0.3f}，请根据分辨率合理调整</li>
 *   <li>动画文件中需包含名为 "Relax" 与 "Interact" 的动作</li>
 * </ul>
 *
 * <h3>日志输出</h3>
 * <ul>
 *   <li>Spine 模型加载完成</li>
 *   <li>点击触发交互行为</li>
 * </ul>
 *
 * @author baiheyufei
 * @version 1.0
 * @see #SpinePet(String, String)
 * @see #render(float)
 * @see #onClicked()
 * @see #dispose()
 */
public class SpinePet {
    private static final Logger logger = new Logger("SpinePet", Logger.DEBUG);
    private static final float MODEL_SCALE = 0.3f;
    private static final String DEFAULT_ANIMATION = "Relax";
    private static final String CLICKED_ANIMATION = "Interact";
    private static final String CLICK_SOUND_PATH = "sounds/mixkit-magic-notification-ring-2344.mp3";

    private final OrthographicCamera camera = new OrthographicCamera();
    private final TwoColorPolygonBatch batch = new TwoColorPolygonBatch();
    private final SkeletonRenderer skeletonRenderer = new SkeletonRenderer();
    private TextureAtlas atlas;
    private Skeleton skeleton;
    private AnimationState animationState;
    private AnimationStateData animationStateData;
    private boolean isPlayingSpecialAnimation = false;
    private Sound clickSound;

    /**
     * 构造函数，初始化 Spine 模型和渲染组件
     *
     * @param modelDir 模型文件所在目录路径（相对于 assets 目录）
     * @param modelName 模型文件名（不带扩展名）,需保持Spine模型的`.atlas`, `.skel`, `.png` 名称一致
     * @see #loadSpineModel(String, String)
     * @see #configureSkeleton()
     */
    public SpinePet(String modelDir, String modelName) {
        Gdx.app.setLogLevel(Logger.INFO);
        skeletonRenderer.setPremultipliedAlpha(true);
        loadSpineModel(modelDir, modelName);
        configureSkeleton();
        initializeAnimations();
        loadSoundEffect();
        logger.info("SpinePet initialized.");
    }

    /**
     * 渲染方法，执行动画更新和模型绘制
     *
     * @param delta 时间增量，用于动画帧率控制
     */
    public void render(float delta) {
        updateSkeletonPosition();
        updateAnimation(delta);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        skeletonRenderer.draw(batch, skeleton);
        batch.end();
    }

    /**
     * 释放所有原生资源
     *
     * <p>在程序退出时调用以避免内存泄漏</p>
     */
    public void dispose() {
//        atlas.dispose();
        batch.dispose();
        if (clickSound != null) {
            clickSound.dispose();
        }
    }

    public void resize() {
        camera.setToOrtho(false);
    }

    public void onClicked() {
        if (isPlayingSpecialAnimation) return;

        isPlayingSpecialAnimation = true;
        logger.info("Clicked: playing animation and sound.");

        if (clickSound != null) clickSound.play();

        animationState.setAnimation(0, CLICKED_ANIMATION, false);
        animationState.addAnimation(0, DEFAULT_ANIMATION, true, 0f);

        animationState.addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (CLICKED_ANIMATION.equals(entry.getAnimation().getName())) {
                    isPlayingSpecialAnimation = false;
                }
            }
        });
    }

    // -------- Private Helpers -------- //

    private void loadSpineModel(String dir, String name) {
        atlas = new TextureAtlas(Gdx.files.internal(dir + "/" + name + ".atlas"));
        SkeletonBinary skeletonBinary = new SkeletonBinary(atlas);
        skeletonBinary.setScale(MODEL_SCALE);

        SkeletonData skeletonData = skeletonBinary.readSkeletonData(Gdx.files.internal(dir + "/" + name + ".skel"));
        skeleton = new Skeleton(skeletonData);
        logger.info("Spine model loaded: " + name);
    }

    private void configureSkeleton() {
        skeleton.updateWorldTransform();
        float screenWidth = Gdx.graphics.getWidth();
        skeleton.setX(screenWidth / 2f);  // default center position
    }

    private void updateSkeletonPosition() {
        if (((MainApp) Gdx.app.getApplicationListener()).getAppState() == AppState.MENU) {
            skeleton.setX(75); // align left in menu
        } else {
            skeleton.setX(Gdx.graphics.getWidth() / 2f); // center normally
        }
    }

    private void initializeAnimations() {
        animationStateData = new AnimationStateData(skeleton.getData());

        Array<Animation> animations = skeleton.getData().getAnimations();
        List<Animation> animationList = new ArrayList<>();
        for (Animation animation : animations) {
            animationList.add(animation);
        }

        for (Animation a1 : animationList) {
            for (Animation a2 : animationList) {
                if (!a1.equals(a2)) {
                    animationStateData.setMix(a1, a2, 0.1f);
                }
            }
        }

        animationState = new AnimationState(animationStateData);
        animationState.setAnimation(0, DEFAULT_ANIMATION, true);
    }

    private void updateAnimation(float delta) {
        animationState.update(delta);
        animationState.apply(skeleton);
        skeleton.update(delta);
        skeleton.updateWorldTransform();
    }

    private void loadSoundEffect() {
        clickSound = Gdx.audio.newSound(Gdx.files.internal(CLICK_SOUND_PATH));
    }
}
