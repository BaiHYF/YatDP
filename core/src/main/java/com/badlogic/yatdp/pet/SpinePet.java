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
 * 封装桌面宠物中 Spine 模型与动画的相关操作。
 *
 * <p>{@code SpinePet} 类旨在对 Spine 模型的加载、渲染、动画控制等功能进行封装，
 * 提供简洁且统一的接口供桌宠应用程序调用。通过此类，可以轻松集成和管理桌面宠物的外观及行为。</p>
 *
 * <h3>主要功能</h3>
 * <ul>
 *     <li>加载 Spine 模型资源（支持 `.skel` 二进制格式）</li>
 *     <li>更新模型状态并渲染到桌面</li>
 *     <li>提供资源释放接口防止内存泄漏</li>
 * </ul>
 *
 * <h3>典型用法</h3>
 * <pre>
 * // 初始化
 * SpinePet pet = new SpinePet("models", "cat");
 *
 * // 在 render() 方法中更新和渲染
 * pet.render(Gdx.graphics.getDeltaTime());
 *
 * // 程序退出时释放资源
 * pet.dispose();
 * </pre>
 *
 * @author baiheyufei <BaiHeYuFei@outlook.com>
 * @see #SpinePet(String, String)
 * @see #render(float)
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
